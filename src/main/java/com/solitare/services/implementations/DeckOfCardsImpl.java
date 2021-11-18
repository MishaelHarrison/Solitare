package com.solitare.services.implementations;

import com.solitare.exceptions.ApiException;
import com.solitare.models.*;
import com.solitare.models.enums.PileName;
import com.solitare.models.values.DrawResponse;
import com.solitare.models.values.FullResponse;
import com.solitare.models.values.InitialResponse;
import com.solitare.services.DeckOfCards;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class DeckOfCardsImpl implements DeckOfCards {

    private final Logger logger = LoggerFactory.getLogger(DeckOfCardsImpl.class);
    private final RestTemplate template = new RestTemplate();

    private static final int DECK_COUNT = 1;
    private static final int FULL_DECK_SIZE = DECK_COUNT * 52;

    @Override
    public String createDeck() {
        String url = "https://deckofcardsapi.com/api/deck/new/shuffle/?deck_count=" + DECK_COUNT;
        val res = template.getForEntity(url, InitialResponse.class);
        logger.trace("status code: "+res.getStatusCode()+" ran request: GET:" + url);
        if (res.getBody() == null || res.getStatusCode().isError()) throw new ApiException("Error within the DeckOfCards api");
        return res.getBody().getDeck_id();
    }

    @Override
    public List<Card> drawFullDeck(String deckId) {
        String url = "https://deckofcardsapi.com/api/deck/" + deckId + "/draw/?count=" + FULL_DECK_SIZE;
        val res = template.getForEntity(url, DrawResponse.class);
        logger.trace("status code: "+res.getStatusCode()+" ran request: GET:" + url);
        if (res.getBody() == null || res.getStatusCode().isError()) throw new ApiException("Error within the DeckOfCards api");
        return res.getBody().getCards();
    }

    @Override
    public List<Card> drawFromPile(String deckId, PileName pile, int amount) {
        String url = "https://deckofcardsapi.com/api/deck/" + deckId + "/pile/" + pile.getPile() + "/draw/?count=" + amount;
        val res = template.getForEntity(url, DrawResponse.class);
        logger.trace("status code: "+res.getStatusCode()+" ran request: GET:" + url);
        if (res.getBody() == null || res.getStatusCode().isError()) throw new ApiException("Error within the DeckOfCards api");
        return res.getBody().getCards();
    }

    @Override
    public void addToPile(String deckId, PileName pile, Card[] cards) {
        String url = "https://deckofcardsapi.com/api/deck/" + deckId + "/pile/" + pile.getPile() + "/add/?cards=" + Arrays.stream(cards).map(Card::getCode).collect(Collectors.joining(","));
        val res = template.getForEntity(url, Void.class);
        logger.trace("status code: "+res.getStatusCode()+" ran request: GET:" + url);
        if (res.getStatusCode().isError()) throw new ApiException("Error within the DeckOfCards api");
    }

    @Async
    @Override
    public CompletableFuture<Map<PileName, ?>> getPileInfoAsync(String deckId, PileName pile) {
        return CompletableFuture.supplyAsync(() -> {
            String url = "https://deckofcardsapi.com/api/deck/" + deckId + "/pile/" + pile.getPile() + "/list";
            val res = template.getForEntity(url, FullResponse.class);
            logger.trace("status code: "+res.getStatusCode()+" ran request: GET:" + url);
            if (res.getBody() == null || res.getStatusCode().isError()) throw new ApiException("Error within the DeckOfCards api");
            return res.getBody().getPiles().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> {
                                if (e.getValue().getCards() != null) return e.getValue().getCards();
                                return e.getValue().getRemaining();
                            }
                    ));
        });
    }
}
