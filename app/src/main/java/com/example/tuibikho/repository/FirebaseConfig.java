package com.example.tuibikho.repository;

public class FirebaseConfig {
    private static final String PROJECT_ID = "learn-8f041";
    private static final String BASE_URL = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID + "/databases/(default)/documents/";

    public static String getBaseUrl() {
        return BASE_URL;
    }
}
