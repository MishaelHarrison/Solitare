package com.solitare.services;

import com.solitare.models.Card;
import com.solitare.models.Game;
import com.solitare.models.GameBoard;
import com.solitare.models.PileName;
import com.solitare.repositories.GameRepo;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public GameBoard getGame(int id) throws Exception {
        return null;
    }
}
