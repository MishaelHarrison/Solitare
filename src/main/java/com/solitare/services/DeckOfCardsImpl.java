package com.solitare.services;

import com.solitare.models.Card;
import com.solitare.models.DrawResponse;
import com.solitare.models.FullResponse;
import com.solitare.models.InitialResponse;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class DeckOfCardsImpl implements DeckOfCards {

    private final Logger logger = LoggerFactory.getLogger(DeckOfCardsImpl.class);

    private final RestTemplate template = new RestTemplate();

    @Override
    public InitialResponse createDeck() {
        String url = "https://deckofcardsapi.com/api/deck/new/shuffle/?deck_count=" + 1;
        val res = template.getForEntity(url, InitialResponse.class);
        logger.trace("status code: "+res.getStatusCode()+" ran request: GET:" + url);
        return res.getBody();
    }

    @Override
    public DrawResponse drawFullDeck(String deckId) {
        String url = "https://deckofcardsapi.com/api/deck/" + deckId + "/draw/?count=" + 52;
        val res = template.getForEntity(url, DrawResponse.class);
        logger.trace("status code: "+res.getStatusCode()+" ran request: GET:" + url);
        return res.getBody();
    }

    @Override
    public DrawResponse drawFromPile(String deckId, String pileName, int amount) {
        String url = "https://deckofcardsapi.com/api/deck/" + deckId + "/pile/" + pileName + "/draw/?count=" + amount;
        val res = template.getForEntity(url, DrawResponse.class);
        logger.trace("status code: "+res.getStatusCode()+" ran request: GET:" + url);
        return res.getBody();
    }

    @Override
    public void addToPile(String deckId, String pileName, Card[] cards) {
        String url = "https://deckofcardsapi.com/api/deck/" + deckId + "/pile/" + pileName + "/add/?cards=" + Arrays.stream(cards).map(Card::getCode).collect(Collectors.joining(","));
        val res = template.getForEntity(url, Void.class);
        logger.trace("status code: "+res.getStatusCode()+" ran request: GET:" + url);
    }

    @Async
    @Override
    public CompletableFuture<FullResponse> getPileInfoAsync(String deckId, String pileName) {
        return CompletableFuture.supplyAsync(() -> {
            String url = "https://deckofcardsapi.com/api/deck/" + deckId + "/pile/" + pileName + "/list";
            val res = template.getForEntity(url, FullResponse.class);
            logger.trace("status code: "+res.getStatusCode()+" ran request: GET:" + url);
            return res.getBody();
        });
    }
}
