package com.solitare.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FullResponse {

    private Piles piles;

    @Data
    @NoArgsConstructor
    public static class Piles{

        private Pile faceUp0;
        private Pile faceUp1;
        private Pile faceUp2;
        private Pile faceUp3;
        private Pile faceUp4;
        private Pile faceUp5;
        private Pile faceUp6;

        private Pile faceDown0;
        private Pile faceDown1;
        private Pile faceDown2;
        private Pile faceDown3;
        private Pile faceDown4;
        private Pile faceDown5;
        private Pile faceDown6;

        private Pile drawDown;
        private Pile drawUp;

        private Pile win0;
        private Pile win1;
        private Pile win2;
        private Pile win3;

        @Data
        @NoArgsConstructor
        public static class Pile{
            private int remaining;
            private List<Card> cards;
        }

    }

}
