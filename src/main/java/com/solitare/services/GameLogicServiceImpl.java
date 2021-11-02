package com.solitare.services;

import com.solitare.exceptions.DataException;
import com.solitare.exceptions.GameLogicException;
import com.solitare.models.Card;
import com.solitare.models.FullResponse;
import com.solitare.repositories.GameRepo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GameLogicServiceImpl implements GameLogicService {

    private Logger logger = LoggerFactory.getLogger(GameLogicServiceImpl.class);

    @Autowired
    private DeckOfCards api;
    @Autowired
    private GameRepo gameRepo;

    @Override
    public void makeMove(String from, String to, int depth, int gameId) {
        String deckId = gameRepo.findById(gameId).orElseThrow(() -> new DataException("game with id:" + gameId + " not found")).getGameId();
        checkValidity(from, to, depth);
        checkLogic(from, to, depth, deckId);
        commitMove(from, to, depth, deckId);
    }

    private void commitMove(String from, String to, int depth, String deckId) {
        CompletableFuture.supplyAsync(() -> {
            List<Card> cards = api.drawFromPile(deckId, from, depth).getCards();
            if (from.equals("drawDown") || to.equals("drawDown")) Collections.reverse(cards);
            api.addToPile(deckId, to, cards.toArray(new Card[0]));
            return null;
        });
    }

    private void checkLogic(String from, String to, int depth, String deckId) {
        if (to.equals("drawUp")) return;
        Card fromCard;
        Card toCard;
        {
            List<FullResponse> pileResponses = Stream.of(api.getPileInfoAsync(deckId, from), api.getPileInfoAsync(deckId, to))
                    .map(CompletableFuture::join).collect(Collectors.toList());
            List<Card> fromPile = extractPile(pileResponses.get(0), from);
            if (fromPile.size() < depth) throw new GameLogicException("cannot target more cards than exist");
            fromCard = fromPile.get(fromPile.size() - depth);
            List<Card> toPile = extractPile(pileResponses.get(1), to);
            toCard = toPile != null && toPile.size() != 0 ? toPile.get(toPile.size() - 1) : null;
            if (to.equals("drawDown")){
                if (toPile != null && toPile.size() != 0) throw new GameLogicException("cannot flip draw deck unless face-down draw deck is depleted");
                if (fromPile.size() != depth) throw new GameLogicException("depth of action must be equal to face-up draw deck's size when preforming a draw deck flip");
                return;
            }
        }
        logger.info("\n" + fromCard.toString() + "\n" + (toCard == null ? "null" : toCard.toString()) + "\n");
        if (from.startsWith("faceDown")){
            if (toCard != null) throw new GameLogicException("cannot flip top unknown card without an empty corresponding face-up pile");
            return;
        }
        switch (to) {
            case "faceUp0":
            case "faceUp1":
            case "faceUp2":
            case "faceUp3":
            case "faceUp4":
            case "faceUp5":
            case "faceUp6":
                if (!validNextCardRegPile(toCard, fromCard))
                    throw new GameLogicException("invalid card match");
                break;
            case "win0":
            case "win1":
            case "win2":
            case "win3":
                if (!validNextCardWinPile(toCard, fromCard))
                    throw new GameLogicException("invalid card match");
                break;
            default:
                throw new RuntimeException("if you are reading this it means that I am a bad coder");
        }
    }

    private boolean validNextCardRegPile(Card first, Card second){
        if (first == null){
            return second.getCode().startsWith("K");
        }else{
            return prevValue(first.getCode().charAt(0), second.getCode().charAt(0))
                    && nextSuit(first.getCode().charAt(1), second.getCode().charAt(1));
        }
    }

    private boolean validNextCardWinPile(Card first, Card second){
        if (first == null){
            return second.getCode().startsWith("A");
        }else{
            return nextValue(first.getCode().charAt(0), second.getCode().charAt(0))
                    && first.getCode().charAt(1) == second.getCode().charAt(1);
        }
    }

    private boolean nextValue(char val, char val2) {
        switch (val) {
            case 'A':
                return val2 == '2';
            case '2':
                return val2 == '3';
            case '3':
                return val2 == '4';
            case '4':
                return val2 == '5';
            case '5':
                return val2 == '6';
            case '6':
                return val2 == '7';
            case '7':
                return val2 == '8';
            case '8':
                return val2 == '9';
            case '9':
                return val2 == '0';
            case '0':
                return val2 == 'J';
            case 'J':
                return val2 == 'Q';
            case 'Q':
                return val2 == 'K';
            default:
                return false;
        }
    }

    private boolean prevValue(char val, char val2) {
        switch (val) {
            case '2':
                return val2 == 'A';
            case '3':
                return val2 == '2';
            case '4':
                return val2 == '3';
            case '5':
                return val2 == '4';
            case '6':
                return val2 == '5';
            case '7':
                return val2 == '6';
            case '8':
                return val2 == '7';
            case '9':
                return val2 == '8';
            case '0':
                return val2 == '9';
            case 'J':
                return val2 == '0';
            case 'Q':
                return val2 == 'J';
            case 'K':
                return val2 == 'Q';
            default:
                return false;
        }
    }

    private boolean nextSuit(char val, char val2) {
        switch (val) {
            case 'H':
            case 'D':
                return val2 == 'S' || val2 == 'C';
            case 'S':
            case 'C':
                return val2 == 'H' || val2 == 'D';
            default:
                throw new RuntimeException("if you are reading this it means that I am a bad coder");
        }
    }

    private List<Card> extractPile(FullResponse response, String pile) {
        switch (pile) {
            case "faceDown0":
                return response.getPiles().getFaceDown0().getCards();
            case "faceDown1":
                return response.getPiles().getFaceDown1().getCards();
            case "faceDown2":
                return response.getPiles().getFaceDown2().getCards();
            case "faceDown3":
                return response.getPiles().getFaceDown3().getCards();
            case "faceDown4":
                return response.getPiles().getFaceDown4().getCards();
            case "faceDown5":
                return response.getPiles().getFaceDown5().getCards();
            case "faceDown6":
                return response.getPiles().getFaceDown6().getCards();
            case "win0":
                return response.getPiles().getWin0().getCards();
            case "win1":
                return response.getPiles().getWin1().getCards();
            case "win2":
                return response.getPiles().getWin2().getCards();
            case "win3":
                return response.getPiles().getWin3().getCards();
            case "drawUp":
                return response.getPiles().getDrawUp().getCards();
            case "drawDown":
                return response.getPiles().getDrawDown().getCards();
            case "faceUp0":
                return response.getPiles().getFaceUp0().getCards();
            case "faceUp1":
                return response.getPiles().getFaceUp1().getCards();
            case "faceUp2":
                return response.getPiles().getFaceUp2().getCards();
            case "faceUp3":
                return response.getPiles().getFaceUp3().getCards();
            case "faceUp4":
                return response.getPiles().getFaceUp4().getCards();
            case "faceUp5":
                return response.getPiles().getFaceUp5().getCards();
            case "faceUp6":
                return response.getPiles().getFaceUp6().getCards();
            default:
                throw new RuntimeException("if you are reading this it means that I am a bad coder");
        }
    }

    private void checkValidity(String from, String to, int depth) {
        switch (from) {
            case "faceDown0":
            case "faceDown1":
            case "faceDown2":
            case "faceDown3":
            case "faceDown4":
            case "faceDown5":
            case "faceDown6":
                if (from.charAt(8) != to.charAt(6))
                    throw new GameLogicException("when drawing from a face-down pile the corresponding face-up pile must be targeted");
                if (depth != 1)
                    throw new GameLogicException("when drawing from a face-down pile only one card can be targeted");
                break;
            case "drawUp":
                if (to.equals("drawDown")) break;
            case "win0":
            case "win2":
            case "win1":
            case "win3":
                if (depth != 1) throw new GameLogicException("cannot target more than 1 card from pile " + from);
                break;
            case "drawDown":
                if (depth != 3)
                    throw new GameLogicException("cannot target more or less than 3 cards from the face-down draw pile");
                if (!to.equals("drawUp"))
                    throw new GameLogicException("cannot transfer cards from face-down draw pile to pile other than face-up draw pile");
                break;
            case "faceUp0":
            case "faceUp1":
            case "faceUp2":
            case "faceUp3":
            case "faceUp4":
            case "faceUp5":
            case "faceUp6":
                break;
            default:
                throw new GameLogicException("invalid from pile");
        }
        switch (to) {
            case "drawDown":
                if (from.equals("drawUp")) break;
            case "faceDown0":
            case "faceDown1":
            case "faceDown2":
            case "faceDown3":
            case "faceDown4":
            case "faceDown5":
            case "faceDown6":
                throw new GameLogicException("cannot target to " + to + " pile");
            case "drawUp":
                if (!from.equals("drawDown"))
                    throw new GameLogicException("cannot transfer cards to face-up draw pile from pile other than face-down draw pile");
                break;
            case "win0":
            case "win2":
            case "win1":
            case "win3":
                if (depth != 1) throw new GameLogicException("cannot target more than 1 card to pile " + from);
            case "faceUp0":
            case "faceUp1":
            case "faceUp2":
            case "faceUp3":
            case "faceUp4":
            case "faceUp5":
            case "faceUp6":
                break;
            default:
                throw new GameLogicException("invalid to pile");
        }
        if (from.equals(to)) throw new GameLogicException("cannot target one pile twice");
    }
}
