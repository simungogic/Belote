package com.game.belote.service;

import com.game.belote.entity.Card;
import com.game.belote.entity.Game;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface GameService {
    Game createGame(String player);
    Map<UUID, Game> getGames();
    Game joinGame(String playerName, UUID uuid);
    Game startGame(UUID id);
    Game passSuit(String playerName, UUID uuid);
    Game pickSuit(String playerName, String suit, UUID uuid);
    Game throwCard(String playerName, Card card, UUID uuid);
}
