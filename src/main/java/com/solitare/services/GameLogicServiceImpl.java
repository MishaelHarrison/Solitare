package com.solitare.services;

import com.solitare.exceptions.DataException;
import com.solitare.exceptions.GameLogicException;
import com.solitare.models.Card;
import com.solitare.models.FullResponse;
import com.solitare.models.PileName;
import com.solitare.repositories.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GameLogicServiceImpl implements GameLogicService {

    @Autowired
    private DeckOfCards api;
    @Autowired
    private GameRepo repo;

    @Override
    public void makeMove(PileName from, PileName to, int depth, int gameId) {
        String deckId = repo.findById(gameId).orElseThrow(() -> new DataException("Game not found")).getGameId();
        checkRequestLogic(from, to, depth);
        depth = checkGameLogic(from, to, depth, deckId);
        commitMove(from, to, depth, deckId);
    }

    private void commitMove(PileName from, PileName to, int depth, String deckId) {
        List<Card> cards = api.drawFromPile(deckId, from, depth).getCards();
        if (from.getCategory().equals("drawDown") || to.getCategory().equals("drawDown")) Collections.reverse(cards);
        api.addToPile(deckId, to, cards.toArray(new Card[0]));
    }

    private int checkGameLogic(PileName from, PileName to, int depth, String deckId) {
        Card fromCard, toCard;
        List<FullResponse> runners = Stream.of(api.getPileInfoAsync(deckId, from), api.getPileInfoAsync(deckId, to)).map(CompletableFuture::join).collect(Collectors.toList());
        List<Card> fromPile = runners.get(0).getPileFromPileName(from);
        if (to.getCategory().equals("drawUp")) return Math.min(fromPile.size(), 3);
        if (fromPile.size() < depth) throw new GameLogicException("cannot target more cards than exist");
        fromCard = fromPile.get(fromPile.size() - depth);
        List<Card> toPile = runners.get(1).getPileFromPileName(to);
        if (to.getCategory().equals("drawDown")) {
            if (toPile != null && toPile.size() != 0)
                throw new GameLogicException("can only flip draw deck when the face-down side is depleted");
            return fromPile.size();
        }
        toCard = toPile == null || toPile.size() == 0 ? null : toPile.get(toPile.size() - 1);
        switch (to.getCategory()) {
            case "playUp":
                if (from.getCategory().equals("playDown")) break;
                if (!playNextVal(fromCard, toCard)) throw new GameLogicException("invalid card value match");
                break;
            case "win":
                if (!winNextVal(fromCard, toCard)) throw new GameLogicException("invalid card value match");
                break;
        }
        return depth;
    }

    private boolean playNextVal(Card x, Card y) {
        if (y == null) return x.getValue().equals("KING");
        if (!(
                ((x.getSuit().equals("SPADES") || x.getSuit().equals("CLUBS")) &&
                        (y.getSuit().equals("HEARTS") || y.getSuit().equals("DIAMONDS"))) ||
                        ((y.getSuit().equals("SPADES") || y.getSuit().equals("CLUBS")) &&
                                (x.getSuit().equals("HEARTS") || x.getSuit().equals("DIAMONDS")))
        )) return false;
        try {
            return Integer.parseInt(x.getValue()) == Integer.parseInt(y.getValue()) - 1;
        } catch (NumberFormatException e) {
            switch (x.getValue()) {
                case "ACE":
                    return y.getValue().equals("2");
                case "10":
                    return y.getValue().equals("JACK");
                case "JACK":
                    return y.getValue().equals("QUEEN");
                case "QUEEN":
                    return y.getValue().equals("KING");
            }
        }
        return false;
    }

    private boolean winNextVal(Card x, Card y) {
        if (y == null) return x.getValue().equals("ACE");
        if (!(
                x.getSuit().equals(y.getSuit())
        )) return false;
        try {
            return Integer.parseInt(x.getValue()) - 1 == Integer.parseInt(y.getValue());
        } catch (NumberFormatException e) {
            switch (x.getValue()) {
                case "KING":
                    return y.getValue().equals("QUEEN");
                case "QUEEN":
                    return y.getValue().equals("JACK");
                case "JACK":
                    return y.getValue().equals("10");
                case "2":
                    return y.getValue().equals("ACE");
            }
        }
        return false;
    }

    private void checkRequestLogic(PileName from, PileName to, int depth) {
        if (from.getPile().equals(to.getPile())) throw new GameLogicException("cannot target the same pile twice");
        if (to.getCategory().equals("faceDown")) throw new GameLogicException("cannot target to a faceDown pile");
        if (depth < 1) throw new GameLogicException("cannot target less than 1 card");
        switch (from.getCategory()) {
            case "playUp":
                switch (to.getCategory()) {
                    case "win":
                        if (depth != 1)
                            throw new GameLogicException("only one card can be moved to the win pile at a time");
                        break;
                    case "playDown":
                    case "drawUp":
                    case "drawDown":
                        throw new GameLogicException("invalid target pair");
                }
                break;
            case "playDown":
                if (to.getCategory().equals("playUp")) {
                    if (depth != 1) throw new GameLogicException("cannot flip more than 1 card");
                } else {
                    throw new GameLogicException("invalid target pair");
                }
                break;
            case "win":
                if (to.getCategory().equals("playUp")) {
                    if (depth != 1)
                        throw new GameLogicException("cannot target more than 1 card from the win piles");
                } else {
                    throw new GameLogicException("invalid target pair");
                }
                break;
            case "drawUp":
                switch (to.getCategory()) {
                    case "playUp":
                    case "win":
                        if (depth != 1)
                            throw new GameLogicException("cannot target more than 1 card from the face-up draw pile");
                }
                break;
            case "drawDown":
                if (!to.getCategory().equals("drawUp")) throw new GameLogicException("invalid target pair");
        }
    }
}
