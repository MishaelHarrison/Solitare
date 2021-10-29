package com.solitare.services;

import com.solitare.models.GameBoard;
import com.solitare.models.GameBoardFull;

public interface BoardManagerService {
    Integer newGame(Integer id);
    GameBoard getGame(int id) throws Exception;
    GameBoardFull getGameFull(int id) throws Exception;
}
