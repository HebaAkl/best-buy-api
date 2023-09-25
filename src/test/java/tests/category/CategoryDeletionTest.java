package tests.category;

import com.github.javafaker.Faker;
import data_helper.CategoryDataHelper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.category.Category;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

public class CategoryDeletionTest {
    Faker faker = new Faker();
    Category expectedCategory;
    String url;

    @BeforeMethod
    public void setUpData() {
        expectedCategory = new Category(
                faker.name().name(),
                faker.number().digits(4)
        );

        url = "http://localhost:3030/categories/{id}";
    }

    @Test
    public void testCanDeleteCategory() {
        CategoryDataHelper categoryDataHelper = new CategoryDataHelper();
        Category category = categoryDataHelper.createCategory(expectedCategory);
        Category deletedCategory =
                given().
                        contentType(ContentType.JSON).
                        when().
                        delete(url, category.getId()).as(Category.class);

        assertThatJson(deletedCategory).whenIgnoringPaths("createdAt").isEqualTo(category);
    }


    @Test
    public void testFailWhenSendNonExistCategoryId() {
        String wrongCatId = faker.number().digits(7);
        given().
                contentType(ContentType.JSON).
                when().
                delete(url, wrongCatId)
                .then().log().body().assertThat()
                .statusCode(404)
                .body("message", Matchers.equalTo("No record found for id '" + wrongCatId +"'"))
                .body("name", Matchers.equalTo("NotFound"));
    }


}
