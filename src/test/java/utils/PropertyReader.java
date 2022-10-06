package utils;

import com.github.javafaker.Faker;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {
    static Properties properties;
    static Faker faker = new Faker();

    static {
        properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("src/test/resources/config.properties");) {
            properties.load(fileInputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    public static void setProperties() {
        properties.setProperty("firstName", faker.name().firstName());
        properties.setProperty("lastName", faker.name().lastName());
        properties.setProperty("totalPrice", Integer.toString(faker.number().numberBetween(100, 500)));
        properties.setProperty("depositpaid", "true");
        properties.setProperty("additionalneeds", "TV in the room");

    }
}
