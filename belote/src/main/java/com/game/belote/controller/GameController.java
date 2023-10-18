package com.game.belote.controller;

import com.game.belote.entity.Deck;
import com.game.belote.entity.Game;
import com.game.belote.entity.Player;
import com.game.belote.response.GenericResponse;
import com.game.belote.service.GameService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        Game game = gameService.joinGame(player, id);

    }

    @PostMapping("/game/{id}/start")
    public ResponseEntity<Game> startGame(@PathVariable UUID id) {
        Game game = gameService.startGame(id);
        return new ResponseEntity<>(game, HttpStatus.OK);
    }
}
