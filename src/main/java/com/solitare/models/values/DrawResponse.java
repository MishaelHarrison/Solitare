package com.solitare.models.values;

import com.solitare.models.Card;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DrawResponse {

    private List<Card> cards;

}
