package com.solitare.services;

import com.solitare.models.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DeckOfCards {
    InitialResponse createDeck();
    DrawResponse drawFullDeck(String deckId);
    DrawResponse drawFromPile(String deckId, PileName pile, int amount);
    void addToPile(String deckId, PileName pile, Card[] cards);
    CompletableFuture<FullResponse> getPileInfoAsync(String deckId, PileName pile);
}