package com.solitare.models.values;

import com.solitare.models.Card;
import com.solitare.models.enums.PileName;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class FullResponse {

    private Map<PileName, Pile> piles;

    @Data
    @NoArgsConstructor
    public static class Pile {
        private int remaining;
        private List<Card> cards;
    }

}