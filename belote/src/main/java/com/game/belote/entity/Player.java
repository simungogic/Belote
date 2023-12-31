package com.game.belote.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
public class Player {
    private String name;
    private List<Card> hand;

    public Player() {
    }

    public Player(String name) {
        this(name, new LinkedList<>());
    }

    public Player(String name, List<Card> hand) {
        this.name = name;
        this.hand = hand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        return Objects.equals(name, player.name);
    }

    public void addCardToHand(Card card) {
        hand.add(card);
    }
}
