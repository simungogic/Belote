package com.game.belote.controller;

import com.game.belote.entity.Game;
import com.game.belote.entity.Player;
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
    public void joinGame(@RequestBody Player player,
                         @PathVariable UUID id) {
        gameService.joinGame(player, id);
    }

    @PostMapping("/game/{id}/start")
    public void startGame(@PathVariable UUID id) {
        gameService.startGame(id);
    }
}
