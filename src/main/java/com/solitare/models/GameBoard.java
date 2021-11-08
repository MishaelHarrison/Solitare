package com.solitare.models;

import lombok.Data;
import java.util.Map;

@Data
public class GameBoard {

    private int ID;

    private Map<PileName, Object> Board;

}
