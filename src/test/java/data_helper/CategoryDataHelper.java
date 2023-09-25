package data_helper;

import io.restassured.http.ContentType;
import models.category.Category;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class CategoryDataHelper {

    String url = "http://localhost:3030/categories";


    @Test
    public Category createCategory(Category category) {
        Category actualCategory =
                given().
                        contentType(ContentType.JSON).
                        body(category).
                        when().
                        post(url).as(Category.class);

        return category;

    }

}

