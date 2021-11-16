package com.solitare.services;

import com.solitare.models.GameBoard;
import com.solitare.models.PileName;

public interface BoardManagerService {
    Integer newGame(Integer id);
    GameBoard getGame(int id);
    GameBoard getGame(int id, PileName[] piles);
}
