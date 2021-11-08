package com.solitare.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PileName {
    FaceDown0("faceDown0"),
    FaceDown1("faceDown1"),
    FaceDown2("faceDown2"),
    FaceDown3("faceDown3"),
    FaceDown4("faceDown4"),
    FaceDown5("faceDown5"),
    FaceDown6("faceDown6"),
    FaceUp0("faceUp0"),
    FaceUp1("faceUp1"),
    FaceUp2("faceUp2"),
    FaceUp3("faceUp3"),
    FaceUp4("faceUp4"),
    FaceUp5("faceUp5"),
    FaceUp6("faceUp6"),
    Win0("win0"),
    Win1("win1"),
    Win2("win2"),
    Win3("win3"),
    DrawUp("drawUp"),
    DrawDown("drawDown");

    private final String pile;
    private final String category;

    PileName(String pile) {
        this.pile = pile;
        category = pile.replaceAll("\\d","");
    }

    @JsonValue
    public String getPile() {
        return pile;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return pile;
    }
}
