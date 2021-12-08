package com.solitare.models.values;

import com.solitare.models.Card;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DrawResponse {

    private List<Card> cards;

}
