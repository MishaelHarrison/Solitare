package com.solitare.services;

public interface GameLogicService {
    void makeMove(String from, String to, int depth, int gameId);
}
