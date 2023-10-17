package com.game.belote.service;

import com.game.belote.entity.Game;
import com.game.belote.entity.Player;

import java.util.Map;
import java.util.UUID;

public interface GameService {
    void createGame(Player player);
    Map<UUID, Game> getGames();
    void joinGame(Player player, UUID uuid);
    void startGame(UUID id);
}
