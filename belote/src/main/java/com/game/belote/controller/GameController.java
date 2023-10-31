package com.game.belote.controller;

import com.game.belote.entity.Card;
import com.game.belote.entity.Game;
import com.game.belote.service.GameService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class GameController {

    private GameService gameService;

    @PostMapping("/game/{player}")
    public ResponseEntity<Game> createGame(@PathVariable String player) {
        return new ResponseEntity<>(gameService.createGame(player), HttpStatus.OK);
    }

    @GetMapping("/game")
    public Map<UUID, Game> getGames() {
        return gameService.getGames();
    }

    @PostMapping("/game/{id}/{playerName}")
    public Game joinGame(@PathVariable UUID id,
                         @PathVariable String playerName) {
        return gameService.joinGame(playerName, id);
    }

    @PostMapping("/game/{id}/start")
    public Game startGame(@PathVariable UUID id) {
        return gameService.startGame(id);
    }

    @PostMapping("/game/{id}/{playerName}/suit")
    public Game passSuit(@PathVariable UUID id,
                         @PathVariable String playerName) {
        return gameService.passSuit(playerName, id);
    }

    @PostMapping("/game/{id}/{playerName}/suit/{suit}")
    public ResponseEntity<Game> pickSuit(@PathVariable UUID id,
                         @PathVariable String suit,
                         @PathVariable String playerName) {
        return new ResponseEntity<>(gameService.pickSuit(playerName, suit, id), HttpStatusCode.valueOf(200));
    }

    @PostMapping("/game/{id}/{playerName}/throw")
    public Game throwCard(@PathVariable UUID id,
                          @PathVariable String playerName,
                          @RequestBody Card card) {
        return gameService.throwCard(playerName, card, id);
    }
}
