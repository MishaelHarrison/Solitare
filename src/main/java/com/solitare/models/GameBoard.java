package com.solitare.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class GameBoard {

    private int ID;

    private List<Card> faceUp0;
    private List<Card> faceUp1;
    private List<Card> faceUp2;
    private List<Card> faceUp3;
    private List<Card> faceUp4;
    private List<Card> faceUp5;
    private List<Card> faceUp6;

    private int faceDown0;
    private int faceDown1;
    private int faceDown2;
    private int faceDown3;
    private int faceDown4;
    private int faceDown5;
    private int faceDown6;

    private int drawDown;
    private List<Card> drawUp;

    private List<Card> win0;
    private List<Card> win1;
    private List<Card> win2;
    private List<Card> win3;

}
