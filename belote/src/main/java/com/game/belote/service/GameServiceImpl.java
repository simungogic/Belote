package com.game.belote.service;

import com.game.belote.entity.Game;
import com.game.belote.entity.GameStatus;
import com.game.belote.entity.Player;
import com.game.belote.exception.GameNotFoundException;
import com.game.belote.exception.NotEnoughPlayersException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@Getter
public class GameServiceImpl implements GameService {
    private Map<UUID, Game> games;

    @Lookup
    public Game getGame() {
        return null;
    }

    public void createGame(Player player) {
        Game game = getGame();
        game.addPlayer(player);
        games.put(game.getId(), game);
    }

    public void joinGame(Player player, UUID id) {
        Game game = games.get(id);
        if(game == null) throw new GameNotFoundException("Game UUID doesn't exist...");
        game.addPlayer(player);
    }

    public void startGame(UUID id) {
        Game game = games.get(id);
        if(game.getPlayers().size() == 4)
            game.setGameStatus(GameStatus.IN_PROGRESS);
        else
            throw new NotEnoughPlayersException("Not enough players joined the game...");
    }
}
