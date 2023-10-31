package com.game.belote.entity;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;

@Component
@Getter
public class Deck {
    private LinkedList<Card> cards;

    public Deck() {
        cards = new LinkedList<>();
        for(Suit suit : Suit.values()) {
            for(Face face : Face.values()) {
                cards.add(new Card(suit, face));
            }
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(cards);
    }

    @Override
    public String toString() {
        return "Deck{" +
                "cards=" + cards +
                '}';
    }
}
