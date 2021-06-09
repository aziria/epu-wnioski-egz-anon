package com.example.util;

import java.io.*;
import java.util.Properties;

public class ApplicationProperties {

    private static String username;
    private static String password;
    private static String apiKey;
    private static String name;
    private static String surname;
    private static String pesel;

    private ApplicationProperties() {}

    static {
        try (Reader reader = new BufferedReader(new InputStreamReader(
                ApplicationProperties.class.getResourceAsStream("/config.properties")))) {
            Properties properties = new Properties();
            properties.load(reader);
            username = properties.getProperty("username");
            password = properties.getProperty("password");
            apiKey = properties.getProperty("apiKey");
            name = properties.getProperty("name");
            surname = properties.getProperty("surname");
            pesel = properties.getProperty("pesel");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static String getName() {
        return name;
    }

    public static String getSurname() {
        return surname;
    }

    public static String getPesel() {
        return pesel;
    }
}
