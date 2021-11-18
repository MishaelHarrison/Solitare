package com.solitare.services;

import com.solitare.models.dto.GameBoard;
import com.solitare.models.enums.PileName;

//manages a solitaire game board in the context of methods that only initiate or read
public interface BoardManagerService {
    //creates a new game instance within the database
    Integer newGame(Integer id);
    //retrieves a fully populated game board instance
    GameBoard getGame(int id);
    //retrieves a partially populated game board instance
    GameBoard getGame(int id, PileName[] piles);
}
