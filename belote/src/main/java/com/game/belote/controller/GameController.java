package com.game.belote.controller;

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

    @PostMapping("/game")
    public void createGame(@RequestBody Player player) {
        gameService.createGame(player);
    }

    @GetMapping("/game")
    public Map<UUID, Game> getGames() {
        return gameService.getGames();
    }

    @PostMapping("/game/{id}")
    public Game joinGame(@PathVariable UUID id,
                         @RequestBody Player player) {
        Game game = gameService.joinGame(player, id);
        return game;

    }

    @PostMapping("/game/{id}/start")
    public Game startGame(@PathVariable UUID id) {
        Game game = gameService.startGame(id);
        return game;
    }

    @PostMapping("/game/{id}/{suit}")
    public Game pickSuit(@PathVariable UUID id,
                         @PathVariable Suit suit,
                         @RequestBody Player player) {
        Game game = gameService.getGames().get(id);
        game.setSuit(suit);
        return game;
    }
}
