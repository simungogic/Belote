package com.game.belote.entity;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Component
@Getter
public class Deck {
    private List<Card> cards;

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
}
