package com.game.belote.controller;

import com.game.belote.entity.Card;
import com.game.belote.entity.Game;
import com.game.belote.entity.Player;
import com.game.belote.entity.Suit;
import com.game.belote.service.GameService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class GameController {

    private GameService gameService;

    @PostMapping("/game/{player}")
    public void createGame(@PathVariable String player) {
        gameService.createGame(player);
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
    public Game pickSuit(@PathVariable UUID id,
                         @PathVariable String suit,
                         @PathVariable String playerName) {
        return gameService.pickSuit(playerName, suit, id);
    }

    @PostMapping("/game/{id}/{playerName}/throw")
    public Card throwCard(@PathVariable UUID id,
                          @PathVariable String playerName,
                          @RequestBody Card card) {
        return gameService.throwCard(playerName, card, id);
    }
}
