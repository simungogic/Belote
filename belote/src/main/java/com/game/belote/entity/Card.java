package com.game.belote.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class Card {
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

    @Override
    public String toString() {
        return "%s %s".formatted(suit.name(), face.name());
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
