package com.game.belote.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.Comparator;

@Getter
@Setter
public class Card implements Comparator<Card> {
    private Suit suit;
    private Face face;
    private boolean visible;

    public Card(Suit suit, Face face) {
        this.suit = suit;
        this.face = face;
    }

    public void setFaceRank(int rank) {
        this.face.setRank(rank);
    }

    public void setFacePoints(int points) {
        this.face.setPoints(points);
    }

    @Override
    public String toString() {
        return "%s %s".formatted(suit.name(), face.name());
    }

    @Override
    public int compare(Card o1, Card o2) {
        if(o1.face.getRank() > o2.face.getRank())
            return 1;
        else if(o2.face.getRank() < o1.face.getRank())
            return -1;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        if (suit != card.suit) return false;
        return face == card.face;
    }
}
