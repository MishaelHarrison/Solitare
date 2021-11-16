package com.solitare.controllers;

import com.solitare.exceptions.DataException;
import com.solitare.exceptions.GameLogicException;
import com.solitare.models.GameBoard;
import com.solitare.models.PileName;
import com.solitare.services.BoardManagerService;
import com.solitare.services.GameLogicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
public class Game {

    @Autowired
    private BoardManagerService gameBoard;
    @Autowired
    private GameLogicService logic;

    Logger logger = LoggerFactory.getLogger(Game.class);

    @ExceptionHandler(GameLogicException.class)
    public ResponseEntity<String> handleLogicException(GameLogicException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(DataException.class)
    public ResponseEntity<?> handleDataException(DataException ex){
        return ResponseEntity.notFound().build();
    }

    @PostMapping({"/game", "/game/{id}"})
    public ResponseEntity<Integer> generate(@PathVariable(name = "id", required = false) Integer id){
        return ResponseEntity.ok(gameBoard.newGame(id));
    }

    @GetMapping({"/game/{id}", "/game/{id}/{piles}"})
    public ResponseEntity<GameBoard> getBoard(@PathVariable("id") Integer id, @PathVariable(name = "piles", required = false) String piles){
        if (piles == null) return ResponseEntity.ok(gameBoard.getGame(id));
        return ResponseEntity.ok(gameBoard.getGame(id, (PileName[]) Arrays.stream(piles.split(",")).map(PileName::fromString).toArray()));
    }

    @PostMapping("/game/{id}/move/{from}/{to}/{depth}")
    public ResponseEntity<?> makeMove(@PathVariable("from") String from, @PathVariable("depth") int depth, @PathVariable("to") String to, @PathVariable("id") int id){
        logic.makeMove(PileName.fromString(from), PileName.fromString(to), depth, id);
        return ResponseEntity.ok().build();
    }
}
