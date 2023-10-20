package com.game.belote.service;

import com.game.belote.entity.Card;
import com.game.belote.entity.Game;
import com.game.belote.entity.Player;
import com.game.belote.entity.Suit;
import com.game.belote.response.GenericResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

public interface GameService {
    Game createGame(String player);
    Map<UUID, Game> getGames();
    Game joinGame(String playerName, UUID uuid);
    Game startGame(UUID id);
    Game passSuit(String playerName, UUID uuid);
    Game pickSuit(String playerName, String suit, UUID uuid);
    Card throwCard(String playerName, Card card, UUID uuid);
}
