package com.solitare.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class FullResponse {

    private Map<PileName, Pile> piles;

    @Data
    @NoArgsConstructor
    public static class Pile {
        private int remaining;
        private List<Card> cards;
    }

    public List<Card> getPileFromPileName(PileName pile) {
        try {
            return piles.get(pile).getCards();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public int getRemainingFromPileName(PileName pile) {
        try {
            return piles.get(pile).getRemaining();
        } catch (NullPointerException e) {
            return 0;
        }
    }

}