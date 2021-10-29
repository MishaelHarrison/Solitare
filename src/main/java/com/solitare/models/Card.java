package com.solitare.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Card {

    private String image;
    private String value;
    private String suit;
    private String code;

}
