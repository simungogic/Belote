package com.game.belote.service;

import com.game.belote.entity.*;
import com.sun.jdi.InternalException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Getter
public class GameServiceImpl implements GameService {
    private Map<UUID, Game> games;

    @Lookup
    public Game getGame() {
        return null;
    }

    public Game createGame(String playerName) {
        Game game = getGame();
        game.addPlayer(new Player(playerName));
        games.put(game.getId(), game);
        return game;
    }

    public Game passSuit(String playerName, UUID uuid) {
        Game game = games.get(uuid);
        Player player = new Player(playerName);
        if(game == null)
            throw new InternalException("Game UUID doesn't exist...");
        if(!game.getPlayers().contains(player))
            throw new InternalException("Player %s is not joined in the game...".formatted(player.getName()));
        if(!player.equals(game.getTurn()))
            throw new InternalException("Player %s is not on turn...".formatted(player.getName()));
        if(!game.getDealer().equals(player))
            game.changeTurn();
        else
            throw new InternalException("Player %s must pick suit...".formatted(player.getName()));

        return game;
    }

    public Game pickSuit(String playerName, String suit, UUID uuid) {
        Game game = games.get(uuid);
        Player player = new Player(playerName);
        if(game == null)
            throw new InternalException("Game UUID doesn't exist...");
        if(!game.getPlayers().contains(player))
            throw new InternalException("Player %s is not joined in the game...".formatted(player.getName()));
        if(game.getSuit() != null)
            throw new InternalException("Suit %s is already chosen...".formatted(game.getSuit()));
        if(!player.equals(game.getTurn()))
            throw new InternalException("Player %s is not on turn...".formatted(player.getName()));
        game.setSuit(Suit.valueOf(suit.toUpperCase()));
        game.adjustFaceValueAndRank();
        game.setLastTwoCardsToVisible();
        game.resetTurn();

        return game;
    }

    private boolean areAllCardsThrown(Game game) {
        List<String> playerNames = game.getPlayers().stream()
                .map(p -> p.getName())
                .collect(Collectors.toList());
        for(var round : game.getRounds()) {
            boolean result = round.keySet().containsAll(playerNames);
            if(!result)
                return false;
        }

        for(var player : game.getPlayers()) {
            boolean result = player.getHand().size() == 0;
            if(!result)
                return false;
        }

        return true;
    }

    private void calculateResult() {
        System.out.println("Calculating result...");
    }

    @Override
    public Game throwCard(String playerName, Card card, UUID uuid) {
        Game game = games.get(uuid);
        Player player = new Player(playerName);

        if(game == null)
            throw new InternalException("Game UUID doesn't exist...");
        if(!game.getPlayers().contains(player))
            throw new InternalException("Player %s is not joined in the game...".formatted(player.getName()));
        if(!player.equals(game.getTurn()))
            throw new InternalException("Player %s is not on turn...".formatted(player.getName()));
        if(!game.getPlayers().get(game.getPlayers().indexOf(player)).getHand().contains(card))
            throw new InternalException("Player %s doesn't have %s card in hand[%s]".formatted(player.getName(), card,
                    game.getPlayers().get(game.getPlayers().indexOf(player)).getHand()));
        if(game.areAllCardsThrown())
            throw new InternalException("All cards are already thrown...Game ended.");

        player = game.getPlayers().get(game.getPlayers().indexOf(player));
        Card cardObj = player.getHand().get(player.getHand().indexOf(card));
        game.throwCard(player, cardObj);
        game.changeTurn();

        if(areAllCardsThrown(game))
            calculateResult();

        return game;
    }

    public Game joinGame(String playerName, UUID uuid) {
        Game game = games.get(uuid);
        Player player = new Player(playerName);

        if(game == null)
            throw new InternalException("Game UUID doesn't exist...");
        if(game.getPlayers().contains(player))
            throw new InternalException("Player with name %s already exists in game...".formatted(player.getName()));
        game.addPlayer(player);
        return game;
    }

    public Game startGame(UUID id) {
        Game game = games.get(id);
        if(game.getPlayers().size() == 4) {
            game.setGameStatus(GameStatus.IN_PROGRESS);
            game.getDeck().shuffleDeck();
            game.deal();
            game.getPlayers().forEach(p -> System.out.println("%s's hand: %s".formatted( p.getName(), p.getHand())));
            return game;
        }
        else
            throw new InternalException("Not enough players joined the game...");
    }
}
