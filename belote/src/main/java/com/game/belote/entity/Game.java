package com.game.belote.entity;

import com.sun.jdi.InternalException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

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
    private List<HashMap<String, Card>> rounds;
    private List<Player> players;

    public Game() {
        players = new ArrayList<>(4);
        gameStatus = GameStatus.NEW;
        id = UUID.randomUUID();
        rounds = new ArrayList<>(8);
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
                    adjustFaceRank();
                    return;
                }

            }
            if(!playerIterator.hasNext())
                playerIterator = players.listIterator();
        }
    }

    private void adjustFaceRank() {
        players.forEach(p -> {
                    p.getHand().forEach(c -> {
                        if(suit.equals(c.getSuit())) {
                            if (Face.DEVET.equals(c.getFace())) {
                                c.setFacePoints(14);
                                c.setFaceRank(7);
                            }

                            else if (Face.DEČKO.equals(c.getFace())) {
                                c.setFacePoints(20);
                                c.setFaceRank(8);
                            }
                        }
                    });
                });
    }

    private void printRounds() {
        rounds.forEach(r -> {
            System.out.println("%d. round:".formatted(rounds.indexOf(r)));
            r.forEach((k, v) -> System.out.println("%s: %s".formatted(k, v.toString())));
        });
    }

    public void throwCard(Player player, Card card) {
        Card cardThrown = player.getHand().get(player.getHand().indexOf(card));
        if(rounds.size() == 0) {
            rounds.add(new HashMap<>(4));
            rounds.get(0).put(player.getName(), cardThrown);
        } else {
            if(rounds.get(rounds.size() - 1).size() < 4) {
                rounds.get(rounds.size() - 1).put(player.getName(), cardThrown);
                player.getHand().remove(cardThrown);
            }
            else {
                rounds.add(new HashMap<>(4));
                rounds.get(rounds.size() - 1).put(player.getName(), cardThrown);
            }
        }
        getPlayers().forEach(p -> System.out.println("%s's hand: %s".formatted( p.getName(), p.getHand())));
        printRounds();
    }
}
