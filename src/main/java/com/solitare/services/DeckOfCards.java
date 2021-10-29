package com.solitare.services;

import com.solitare.models.Card;
import com.solitare.models.DrawResponse;
import com.solitare.models.FullResponse;
import com.solitare.models.InitialResponse;

import java.util.concurrent.CompletableFuture;

public interface DeckOfCards {
    InitialResponse createDeck();
    DrawResponse drawFullDeck(String deckId);
    void addToPile(String deckId, String pileName, Card[] cards);
    CompletableFuture<FullResponse> getPileInfoAsync(String deckId, String pileName);
}