package com.game.belote.entity;

import java.util.Random;

public enum Suit {
    BUNDEVA, CRVENA, ZELENA, ŽIR;

    public static Suit randomSuit() {
        return values()[new Random().nextInt(values().length)];
    }
}
