package com.solitare.services;

import com.solitare.exceptions.DataException;
import com.solitare.models.*;
import com.solitare.repositories.GameRepo;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                case "faceDown":
                    amount = pile.getNum();
                    break;
                case "faceUp":
                    amount = 1;
                    break;
                case "drawDown":
                    amount = 24;
                    break;
            }
            api.addToPile(deckId, pile.getPile(), deck.subList(0,amount).toArray(new Card[0]));
            deck.subList(0,amount).clear();
        }

        logger.info("new deck created with id:" + deckId);

        return repo.save(new Game(id, deckId)).getId();
    }

    @Override
    public GameBoard getGame(int id){
        String gameId = repo.findById(id).orElseThrow(()->new DataException("Game not found")).getGameId();
        val runners = new CompletableFuture[12];
        GameBoard ret = new GameBoard();
        ret.setBoard(new HashMap<>());
        int index = 0;

        for(PileName pile: new PileName[]{PileName.FaceUp0, PileName.FaceUp1, PileName.FaceUp2,
                PileName.FaceUp3, PileName.FaceUp4, PileName.FaceUp5, PileName.FaceUp6,
                PileName.Win0, PileName.Win1, PileName.Win2, PileName.Win3, PileName.DrawUp}){
            CompletableFuture<FullResponse> response = api.getPileInfoAsync(gameId, pile.getPile());
            if (index == 0){
                response = response.thenApply(x->{
                    for (PileName innerPile: new PileName[]{PileName.FaceDown0, PileName.FaceDown1, PileName.FaceDown2,
                            PileName.FaceDown3, PileName.FaceDown4, PileName.FaceDown5, PileName.FaceDown6, PileName.DrawDown}){
                        ret.getBoard().put(innerPile, x.getRemainingFromPileName(innerPile));
                    }
                    return x;
                });
            }
            runners[index++] = response.thenAccept(x-> ret.getBoard().put(pile, x.getPileFromPileName(pile)));
        }

        CompletableFuture.allOf(runners).join();
        return ret;
    }
}
