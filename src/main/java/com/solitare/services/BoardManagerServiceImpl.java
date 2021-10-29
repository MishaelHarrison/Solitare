package com.solitare.services;

import com.solitare.models.Card;
import com.solitare.models.Game;
import com.solitare.models.GameBoard;
import com.solitare.models.GameBoardFull;
import com.solitare.repositories.GameRepo;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class BoardManagerServiceImpl implements BoardManagerService{

    @Autowired
    private DeckOfCards api;
    @Autowired
    private GameRepo gameRepo;

    Logger logger = LoggerFactory.getLogger(BoardManagerServiceImpl.class);

    @Override
    public Integer newGame(Integer id) {
        String deckId = api.createDeck().getDeck_id();
        id = gameRepo.save(new Game(id, deckId)).getId();

        logger.info("new deck created deckid="+deckId);

        List<Card> cards = api.drawFullDeck(deckId).getCards();
        for(int i:new int[]{0,1,2,3,4,5,6}){
            api.addToPile(deckId, "faceUp"+i, cards.subList(0,1).toArray(new Card[0]));
            cards.remove(0);
        }
        for(int i:new int[]{0,1,2,3,4,5,6}){
            api.addToPile(deckId, "faceDown"+i, cards.subList(0,i).toArray(new Card[0]));
            cards.subList(0, i).clear();
        }
        api.addToPile(deckId, "drawDown", cards.toArray(new Card[0]));
        api.addToPile(deckId, "drawUp", new Card[0]);
        api.addToPile(deckId, "winC", new Card[0]);
        api.addToPile(deckId, "winS", new Card[0]);
        api.addToPile(deckId, "winH", new Card[0]);
        api.addToPile(deckId, "winD", new Card[0]);

        return id;
    }

    @Override
    public GameBoard getGame(int id) throws Exception {
        GameBoard ret = new GameBoard();
        String deckId = gameRepo.findById(id).orElseThrow(Exception::new).getGameId();
        val runners = new CompletableFuture[12];
        int index = -1;

        logger.info("querying deck deckid="+deckId);

        runners[++index] = api.getPileInfoAsync(deckId, "drawUp").thenAccept(x->ret.setDrawUp(x.getPiles().getDrawUp().getCards()));

        runners[++index] = api.getPileInfoAsync(deckId, "winC").thenAccept(x->ret.setWinC(x.getPiles().getWinC().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "winH").thenAccept(x->ret.setWinC(x.getPiles().getWinH().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "winS").thenAccept(x->ret.setWinC(x.getPiles().getWinS().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "winD").thenAccept(x->ret.setWinC(x.getPiles().getWinD().getCards()));

        runners[++index] = api.getPileInfoAsync(deckId, "faceUp0").thenAccept(x->ret.setFaceUp0(x.getPiles().getFaceUp0().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "faceUp1").thenAccept(x->ret.setFaceUp1(x.getPiles().getFaceUp1().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "faceUp2").thenAccept(x->ret.setFaceUp2(x.getPiles().getFaceUp2().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "faceUp3").thenAccept(x->ret.setFaceUp3(x.getPiles().getFaceUp3().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "faceUp4").thenAccept(x->ret.setFaceUp4(x.getPiles().getFaceUp4().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "faceUp5").thenAccept(x->ret.setFaceUp5(x.getPiles().getFaceUp5().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "faceUp6").thenAccept(x->{
            ret.setFaceUp6(x.getPiles().getFaceUp6().getCards());

            ret.setFaceDown0(x.getPiles().getFaceDown0().getRemaining());
            ret.setFaceDown1(x.getPiles().getFaceDown1().getRemaining());
            ret.setFaceDown2(x.getPiles().getFaceDown2().getRemaining());
            ret.setFaceDown3(x.getPiles().getFaceDown3().getRemaining());
            ret.setFaceDown4(x.getPiles().getFaceDown4().getRemaining());
            ret.setFaceDown5(x.getPiles().getFaceDown5().getRemaining());
            ret.setFaceDown6(x.getPiles().getFaceDown6().getRemaining());
            ret.setDrawDown(x.getPiles().getDrawDown().getRemaining());
        });

        CompletableFuture.allOf(runners).join();

        return ret;
    }

    @Override
    public GameBoardFull getGameFull(int id) throws Exception {
        GameBoardFull ret = new GameBoardFull();
        String deckId = gameRepo.findById(id).orElseThrow(Exception::new).getGameId();
        val runners = new CompletableFuture[20];
        int index = -1;

        logger.info("querying deck deckid="+deckId);

        runners[++index] = api.getPileInfoAsync(deckId, "drawUp").thenAccept(x->ret.setDrawUp(x.getPiles().getDrawUp().getCards()));

        runners[++index] = api.getPileInfoAsync(deckId, "winC").thenAccept(x->ret.setWinC(x.getPiles().getWinC().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "winH").thenAccept(x->ret.setWinC(x.getPiles().getWinH().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "winS").thenAccept(x->ret.setWinC(x.getPiles().getWinS().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "winD").thenAccept(x->ret.setWinC(x.getPiles().getWinD().getCards()));

        runners[++index] = api.getPileInfoAsync(deckId, "faceUp0").thenAccept(x->ret.setFaceUp0(x.getPiles().getFaceUp0().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "faceUp1").thenAccept(x->ret.setFaceUp1(x.getPiles().getFaceUp1().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "faceUp2").thenAccept(x->ret.setFaceUp2(x.getPiles().getFaceUp2().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "faceUp3").thenAccept(x->ret.setFaceUp3(x.getPiles().getFaceUp3().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "faceUp4").thenAccept(x->ret.setFaceUp4(x.getPiles().getFaceUp4().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "faceUp5").thenAccept(x->ret.setFaceUp5(x.getPiles().getFaceUp5().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "faceUp6").thenAccept(x->ret.setFaceUp6(x.getPiles().getFaceUp6().getCards()));

        runners[++index] = api.getPileInfoAsync(deckId, "FaceDown0").thenAccept(x->ret.setFaceDown0(x.getPiles().getFaceDown0().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "FaceDown1").thenAccept(x->ret.setFaceDown1(x.getPiles().getFaceDown1().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "FaceDown2").thenAccept(x->ret.setFaceDown2(x.getPiles().getFaceDown2().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "FaceDown3").thenAccept(x->ret.setFaceDown3(x.getPiles().getFaceDown3().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "FaceDown4").thenAccept(x->ret.setFaceDown4(x.getPiles().getFaceDown4().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "FaceDown5").thenAccept(x->ret.setFaceDown5(x.getPiles().getFaceDown5().getCards()));
        runners[++index] = api.getPileInfoAsync(deckId, "FaceDown6").thenAccept(x->ret.setFaceDown6(x.getPiles().getFaceDown6().getCards()));

        runners[++index] = api.getPileInfoAsync(deckId, "DrawDown").thenAccept(x->ret.setDrawDown(x.getPiles().getDrawDown().getCards()));

        CompletableFuture.allOf(runners).join();

        return ret;
    }
}
