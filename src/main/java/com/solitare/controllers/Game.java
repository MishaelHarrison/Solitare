package com.solitare.controllers;

import com.solitare.exceptions.DataException;
import com.solitare.exceptions.GameLogicException;
import com.solitare.models.GameBoard;
import com.solitare.services.BoardManagerService;
import com.solitare.services.GameLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class Game {

    @Autowired
    private BoardManagerService gameBoard;
    @Autowired
    private GameLogicService logic;

    @ExceptionHandler(GameLogicException.class)
    public ResponseEntity<String> handleLogicException(GameLogicException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(DataException.class)
    public ResponseEntity<?> handleDataException(DataException ex){
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/game")
    public ResponseEntity<Integer> generate(@PathVariable(name = "id", required = false) Integer id){
        return ResponseEntity.ok(gameBoard.newGame(id));
    }

    @GetMapping("/game")
    public ResponseEntity<GameBoard> getBoard(@PathVariable("id") Integer id) throws Exception {
        return ResponseEntity.ok(gameBoard.getGame(id));
    }

    @PostMapping("/game/move")
    public ResponseEntity<?> makeMove(@PathVariable("from")String from, @PathVariable("depth") int depth, @PathVariable("to") String to, @PathVariable("id") int id){
        logic.makeMove(from, to, depth, id);
        return ResponseEntity.ok().build();
    }
}
