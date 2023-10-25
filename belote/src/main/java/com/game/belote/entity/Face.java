package com.game.belote.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@AllArgsConstructor
public enum Face {
    SEDAM(1, 0),
    OSAM(2, 0),
    DEVET(3, 0),
    DEČKO(4, 2),
    DAMA(5, 3),
    KRALJ(6, 4),
    DESET(7, 10),
    AS(8, 11);

    @Getter
    @Setter
    private int rank;

    @Getter
    @Setter
    private int points;

}
