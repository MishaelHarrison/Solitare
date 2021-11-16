package com.solitare.services;

import com.solitare.exceptions.DataException;
import com.solitare.models.*;
import com.solitare.repositories.GameRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class BoardManagerServiceImpl implements BoardManagerService{

    @Autowired
    private DeckOfCards api;
    @Autowired
    private GameRepo repo;

    Logger logger = LoggerFactory.getLogger(BoardManagerServiceImpl.class);

    @Override
    public Integer newGame(Integer id) {
        String deckId = api.createDeck().getDeck_id();
        List<Card> deck = api.drawFullDeck(deckId).getCards();

        for (PileName pile: PileName.values()){
            int amount = 0;
            switch (pile.getCategory()){
                case "playDown":
                    amount = pile.getNum();
                    break;
                case "playUp":
                    amount = 1;
                    break;
                case "drawDown":
                    amount = 24;
                    break;
            }
            api.addToPile(deckId, pile, deck.subList(0,amount).toArray(new Card[0]));
            deck.subList(0,amount).clear();
        }

        logger.info("new deck created with id:" + deckId);

        return repo.save(new Game(id, deckId)).getId();
    }

    @Override
    public GameBoard getGame(int id){
        return getGame(id, PileName.values());
    }

    @Override
    public GameBoard getGame(int id, PileName[] piles) {
        String gameId = repo.findById(id).orElseThrow(()->new DataException("Game not found")).getGameId();
        List<CompletableFuture<?>> runners = new ArrayList<>();
        GameBoard ret = new GameBoard();
        ret.setBoard(new HashMap<>());

        Arrays.stream(piles).filter(PileName::isVisible).forEach(pile->{
            CompletableFuture<FullResponse> response = api.getPileInfoAsync(gameId, pile);
            if (runners.size() == 0){
                response = response.thenApply(x->{
                    Arrays.stream(piles).filter(y->!y.isVisible()).forEach(innerPile->
                            ret.getBoard().put(innerPile, x.getRemainingFromPileName(innerPile))
                    );
                    return x;
                });
            }
            runners.add(response.thenAccept(x-> ret.getBoard().put(pile, x.getPileFromPileName(pile))));
        });

        CompletableFuture.allOf(runners.toArray(new CompletableFuture[0])).join();
        return ret;
    }
}
