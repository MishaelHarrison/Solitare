package com.solitare.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PileName {
    FaceDown0("faceDown0", "playDown", 0, false),
    FaceDown1("faceDown1", "playDown", 1, false),
    FaceDown2("faceDown2", "playDown", 2, false),
    FaceDown3("faceDown3", "playDown", 3, false),
    FaceDown4("faceDown4", "playDown", 4, false),
    FaceDown5("faceDown5", "playDown", 5, false),
    FaceDown6("faceDown6", "playDown", 6, false),
    FaceUp0("faceUp0", "playUp", 0, true),
    FaceUp1("faceUp1", "playUp", 1, true),
    FaceUp2("faceUp2", "playUp", 2, true),
    FaceUp3("faceUp3", "playUp", 3, true),
    FaceUp4("faceUp4", "playUp", 4, true),
    FaceUp5("faceUp5", "playUp", 5, true),
    FaceUp6("faceUp6", "playUp", 6, true),
    Win0("win0", "win", 0, true),
    Win1("win1", "win", 1, true),
    Win2("win2", "win", 2, true),
    Win3("win3", "win", 3, true),
    DrawUp("drawUp", "drawUp", null, true),
    DrawDown("drawDown", "drawDown", null, false);

    private final String pile;
    private final String category;
    private final Integer num;
    private final boolean visible;

    PileName(String pile, String category, Integer num, boolean visible) {
        this.pile = pile;
        this.category = category;
        this.num = num;
        this.visible = visible;
    }

    public static PileName fromString(String text) {
        for (PileName i : PileName.values()) {
            if (i.pile.equals(text)) {
                return i;
            }
        }
        return null;
    }

    @JsonValue
    public String getPile() {
        return pile;
    }

    public String getCategory() {
        return category;
    }

    public Integer getNum() {
        return num;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public String toString() {
        return pile;
    }
}
