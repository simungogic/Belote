package com.game.belote.entity;

import com.sun.jdi.InternalException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Component
@Getter
@Setter
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Game {
    private UUID id;
    private GameStatus gameStatus;
    private Deck deck;
    private Player dealer;
    private Player turn;
    private Suit suit;
    private List<Player> players;

    public Game() {
        players = new ArrayList<>(4);
        gameStatus = GameStatus.NEW;
        id = UUID.randomUUID();
    }

    @Autowired
    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public void addPlayer(Player player) {
        if(players.size() >= 4)
            throw new InternalException("Game has already 4 players...");
        players.add(player);
    }

    private void setFirstSixCardsToVisible() {
        players.forEach(p -> p.getHand().subList(0, 6)
                .forEach(c -> c.setVisible(true)));
    }

    public void setLastTwoCardsToVisible() {
        players.forEach(p -> p.getHand().subList(6, 8)
                .forEach(c -> c.setVisible(true)));
    }

    public void changeTurn() {
        turn = players.indexOf(turn) < 3 ? players.get(players.indexOf(turn) + 1) : players.get(0);
    }

    public void resetTurn() {
        turn = players.indexOf(dealer) < 3 ? players.get(players.indexOf(dealer) + 1) : players.get(0);
    }

    public void deal() {
        dealer = players.get(players.size() - 1);
        turn = players.indexOf(dealer) < 3 ? players.get(players.indexOf(dealer) + 1) : players.get(0);
        Iterator playerIterator = players.listIterator(players.indexOf(turn));
        System.out.println(deck.getCards());
        while(playerIterator.hasNext()) {
            turn = (Player) playerIterator.next();

            for(int i = 0; i < 2; i++) {
                Card card = deck.getCards().pollFirst();
                if(card != null)
                    turn.addCardToHand(card);
                else {
                    setFirstSixCardsToVisible();
                    return;
                }

            }
            if(!playerIterator.hasNext())
                playerIterator = players.listIterator();
        }
    }

    public void throwCard(Player player, Card card) {
        player.getHand().remove(card);
        getPlayers().forEach(p -> System.out.println(p.getHand()));
    }
}
