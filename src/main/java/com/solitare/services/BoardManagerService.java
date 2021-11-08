package com.solitare.services;

import com.solitare.models.GameBoard;

public interface BoardManagerService {
    Integer newGame(Integer id);
    GameBoard getGame(int id);
}
