package com.game.belote.entity;

import com.game.belote.exception.GameFullException;
import com.game.belote.exception.NoDealerException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

@Component
@Getter
@Setter
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Game {
    private UUID id;
    private GameStatus gameStatus;
    private Deck deck;
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
        if(players.size() >= 4) throw new GameFullException("Game has already 4 players...");
        players.add(player);
    }

    private Player getDealer() {
        Optional<Player> dealer = players.stream().filter(p -> p.isDealer()).findFirst();
        if(dealer.isEmpty())
            throw new NoDealerException("There is no dealer in the game...");
        return dealer.get();
    }

    public void deal() {
        System.out.println("deal...");
        Iterator playerIterator = players.listIterator(players.indexOf(getDealer()) + 1);
        System.out.println(deck.getCards());
        AtomicInteger firstCardIndex = new AtomicInteger(1);
        AtomicInteger secondCardIndex = new AtomicInteger(2);
        while(playerIterator.hasNext()) {
            Player player = (Player) playerIterator.next();
            IntStream.range(0, deck.getCards().size())
                    .filter(i -> (i + 1) % 8 == firstCardIndex.get() || (i + 1) % 8 == secondCardIndex.get())
                    .forEach(i -> player.addCardToHand(deck.getCards().get(i)));
            firstCardIndex.addAndGet(2);
            secondCardIndex.addAndGet(2);
            System.out.println(player.getName() + " hand: " + player.getHand());
        }
        deck.getCards().clear();
    }
}
