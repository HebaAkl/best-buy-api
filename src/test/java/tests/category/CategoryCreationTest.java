package tests.category;

import com.github.javafaker.Faker;
import data_helper.CategoryDataHelper;
import io.restassured.http.ContentType;
import models.category.Category;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static io.restassured.RestAssured.given;

public class CategoryCreationTest {
    Faker faker = new Faker();
    Category expectedCategory;
    String url;

    @BeforeMethod
    public void setUpData() {
         expectedCategory = new Category(
                faker.name().name(),
                faker.number().digits(4)
        );

        url = "http://localhost:3030/categories";
    }

    @Test
    public void testCanCreateCategory(){
        Category actualCategory =
                given().
                        contentType(ContentType.JSON).
                        body(expectedCategory).
                when().
                        post(url).as(Category.class);

        assertThatJson(actualCategory).whenIgnoringPaths("updatedAt", "createdAt").isEqualTo(expectedCategory);
    }


    @Test
    public void testFailToCreateWithNonUniqueId(){
        CategoryDataHelper categoryDataHelper = new CategoryDataHelper();
        Category category = categoryDataHelper.createCategory(expectedCategory);
                given().
                    contentType(ContentType.JSON).
                    body(category).
                when().
                    post(url).
                then().assertThat()
                    .statusCode(400)
                    .body("errors.message[0]", Matchers.equalTo("id must be unique"));
    }


    @Test
    public void testFailToCreateWithLongName(){
        expectedCategory.setName(faker.lorem().characters(101));
        given().
            contentType(ContentType.JSON).
            body(expectedCategory).
        when().
            post(url).
        then().log().body()
            .assertThat()
            .statusCode(400)
            .body("errors[0]", Matchers.equalTo("'name' should NOT be longer than 100 characters"));
    }


}
