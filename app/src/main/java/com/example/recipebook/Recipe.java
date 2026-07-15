package com.example.recipebook;

public class Recipe {
    private String id;
    private String title;
    private String ingredients;
    private String preparationSteps;
    private String classification;
    private String youtubeVideoURL;
    private String imageUri;
    public Recipe() {
    }
    public Recipe(String id, String title, String ingredients, String preparationSteps, String classification, String youtubeVideoURL, String imageUri) {
        this.id = id;
        this.title = title;
        this.ingredients = ingredients;
        this.preparationSteps = preparationSteps;
        this.classification = classification;
        this.youtubeVideoURL = youtubeVideoURL;
        this.imageUri = imageUri;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getPreparationSteps() {
        return preparationSteps;
    }

    public void setPreparationSteps(String preparationSteps) {
        this.preparationSteps = preparationSteps;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getYoutubeVideoURL() {
        return youtubeVideoURL;
    }

    public void setYoutubeVideoURL(String youtubeVideoURL) {
        this.youtubeVideoURL = youtubeVideoURL;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}