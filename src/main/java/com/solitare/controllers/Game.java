package com.solitare.controllers;

import com.solitare.models.GameBoardFull;
import com.solitare.services.BoardManagerService;
import com.solitare.services.DeckOfCards;
import com.solitare.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Game {

    @Autowired
    private GameService service;
    @Autowired
    private DeckOfCards api;
    @Autowired
    private BoardManagerService boardManagerService;

    @GetMapping("/new")
    public ResponseEntity<Integer> generate(){
        return ResponseEntity.ok(service.generate());
    }

    @GetMapping("/newGame")
    public Integer getDeck(){
        return boardManagerService.newGame(null);
    }

    @GetMapping("/getGame")
    public GameBoardFull getGame(@RequestParam("id")Integer id) throws Exception {
        return boardManagerService.getGameFull(id);
    }
}
