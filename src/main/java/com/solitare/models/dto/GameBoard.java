package com.solitare.models.dto;

import com.solitare.models.enums.PileName;
import lombok.Data;
import java.util.Map;

@Data
public class GameBoard {

    private int ID;

    private Map<PileName, Object> Board;

}
