package com.solitare.models;

import lombok.Data;

import java.util.List;

@Data
public class GameBoardFull {

    private int ID;

    private List<Card> faceUp0;
    private List<Card> faceUp1;
    private List<Card> faceUp2;
    private List<Card> faceUp3;
    private List<Card> faceUp4;
    private List<Card> faceUp5;
    private List<Card> faceUp6;

    private List<Card> faceDown0;
    private List<Card> faceDown1;
    private List<Card> faceDown2;
    private List<Card> faceDown3;
    private List<Card> faceDown4;
    private List<Card> faceDown5;
    private List<Card> faceDown6;

    private List<Card> drawDown;
    private List<Card> drawUp;

    private List<Card> winC;
    private List<Card> winS;
    private List<Card> winH;
    private List<Card> winD;

}
