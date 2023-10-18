package com.game.belote.service;

import com.game.belote.entity.Game;
import com.game.belote.entity.Player;
import com.game.belote.response.GenericResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

public interface GameService {
    Game createGame(Player player);
    Map<UUID, Game> getGames();
    Game joinGame(Player player, UUID uuid);
    Game startGame(UUID id);
}
