package com.solitare.services;

import com.solitare.models.enums.PileName;

public interface GameLogicService {
    void makeMove(PileName from, PileName to, int depth, int gameId);
}
