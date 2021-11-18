package com.solitare.services;

import com.solitare.models.*;
import com.solitare.models.enums.PileName;
import com.solitare.models.values.DrawResponse;
import com.solitare.models.values.FullResponse;
import com.solitare.models.values.InitialResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

//database proxy for a deck of cards
public interface DeckOfCards {
    //creates a new deck of cards and returns an id
    String createDeck();
    //draws the entirety of the deck of cards
    List<Card> drawFullDeck(String deckId);
    //draws a specified amount of cards from a specified deck
    List<Card> drawFromPile(String deckId, PileName pile, int amount);
    //adds a collection of cards to a pile
    void addToPile(String deckId, PileName pile, Card[] cards);
    //retrieves info about a pile while also retrieving all pile sizes
    CompletableFuture<Map<PileName, ?>> getPileInfoAsync(String deckId, PileName pile);
}