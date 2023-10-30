package com.game.belote.entity;

import com.sun.jdi.InternalException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

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
    private List<LinkedHashMap<String, Card>> rounds;
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
                    return;
                }

            }
            if(!playerIterator.hasNext())
                playerIterator = players.listIterator();
        }
    }

    public void adjustFaceValueAndRank() {
        players.forEach(p -> {
                    p.getHand().forEach(c -> {
                        if(suit.equals(c.getSuit())) {
                            if (Face.DEVET.equals(c.getFace())) {
                                c.setFaceValue(14);
                                c.setFaceRank(7);
                            }
                            else if (Face.DEČKO.equals(c.getFace())) {
                                c.setFaceValue(8);
                                c.setFaceRank(20);
                            }
                        }
                    });
                });
    }

    private void printRounds() {
        rounds.forEach(r -> {
            System.out.printf("%d. round:%n", rounds.indexOf(r));
            r.forEach((k, v) -> System.out.printf("%s: %s%n", k, v.toString()));
        });
    }

    public boolean areAllCardsThrown() {
        List<String> playerNames = players.stream()
                .map(Player::getName)
                .toList();
        for(var round : rounds) {
            boolean result = round.keySet().containsAll(playerNames);
            if(!result)
                return false;
        }

        for(var player : players) {
            boolean result = player.getHand().size() == 0;
            if(!result)
                return false;
        }

        return true;
    }

    private void calculateResult() {
        System.out.println("Calculating result...");
    }

    private Card findMaxCardRankInRound(Collection<Card> cards) {
        return cards.stream()
                .max(Card::compareTo)
                .get();
    }

    private List<Card> getSameSuitThrows(Player player, LinkedHashMap<String, Card> round) {
        boolean isChosenSuitInCurrentRound = round.values().stream()
                .anyMatch(c -> suit.equals(c.getSuit()));

        Stream<Card> validCardsStream = player.getHand().stream()
                .filter(c -> c.getSuit().equals(
                        round.values().stream()
                                .findFirst()
                                .get().getSuit()));

        if(isChosenSuitInCurrentRound)
            return validCardsStream.toList();
        else {
            List<Card> cardsHigherThanCurrentMax = validCardsStream
                    .filter(c -> c.getFace().getRank() > findMaxCardRankInRound(round.values()).getFace().getRank())
                    .toList();
            if(!cardsHigherThanCurrentMax.isEmpty())
                return cardsHigherThanCurrentMax;

            return player.getHand().stream()
                    .filter(c -> c.getSuit().equals(
                            round.values().stream()
                                    .findFirst()
                                    .get().getSuit()))
                    .toList();
        }
    }

    private List<Card> getChosenSuitThrows(Player player, LinkedHashMap<String, Card> round) {
        boolean isChosenSuitInCurrentRound = round.values().stream()
                .anyMatch(c -> suit.equals(c.getSuit()));

        Stream<Card> chosenSuitCardsInHandStream = player.getHand().stream()
                .filter(c -> suit.equals(c.getSuit()));

        if(isChosenSuitInCurrentRound) {
            Optional<Card> maxChosenSuitCardInRound = round.values().stream()
                    .filter(c -> suit.equals(c.getSuit()))
                    .max(Card::compareTo);

            List<Card> cardsHigherThanCurrentMax = player.getHand().stream()
                    .filter(c -> suit.equals(c.getSuit()))
                    .filter(c -> c.getFace().getRank() > maxChosenSuitCardInRound.get().getFace().getRank())
                    .toList();

            if(!cardsHigherThanCurrentMax.isEmpty())
                return cardsHigherThanCurrentMax;
        }

        return chosenSuitCardsInHandStream.toList();
    }

    private List<Card> getOtherSuitThrows(Player player, LinkedHashMap<String, Card> round) {
        return player.getHand().stream()
                .filter(c -> !suit.equals(c.getSuit()))
                .filter(c -> !c.getSuit().equals(round.values().stream()
                        .findFirst()
                        .get()
                        .getSuit()))
                .toList();
    }

    public List<Card> getValidThrows(Player player, Card card) {
        Player currentPlayer = players.get(players.indexOf(player));
        LinkedHashMap<String, Card> currentRound = rounds.get(rounds.size() - 1);

        //player contains card in hand with same suit as first card in round
        boolean handContainsFirstCardSuit = currentPlayer.getHand().stream()
                .anyMatch(c -> currentRound.values().stream()
                        .findFirst()
                        .get()
                        .getSuit()
                        .equals(c.getSuit()));

        boolean handContainsChosenSuit = currentPlayer.getHand().stream()
                .anyMatch(c -> suit.equals(c.getSuit()));

        if(handContainsFirstCardSuit)
            return getSameSuitThrows(currentPlayer, currentRound);
        else if(handContainsChosenSuit)
            return getChosenSuitThrows(currentPlayer, currentRound);
        else
            return getOtherSuitThrows(currentPlayer, currentRound);
    }

    public void throwCard(Player player, Card card) {
        List<Card> hand = player.getHand();
        Card cardThrown = hand.get(hand.indexOf(card));
        if(rounds.size() == 0) {
            rounds.add(new LinkedHashMap<>(4));
            rounds.get(0).put(player.getName(), cardThrown);
        }
        else {
            int round = rounds.size() - 1;
            if(rounds.get(round).size() < 4) {
                List<Card> validThrows = getValidThrows(player, card);
                if(validThrows.contains(card)) {
                    rounds.get(round).put(player.getName(), cardThrown);
                    hand.remove(cardThrown);
                }
                else
                    throw new InternalException("Card %s is not valid throw...".formatted(card));
            }
            else {
                rounds.add(new LinkedHashMap<>(4));
                rounds.get(0).put(player.getName(), cardThrown);
            }
        }
        getPlayers().forEach(p -> System.out.println("%s's hand: %s".formatted( p.getName(), p.getHand())));
        printRounds();
        if(areAllCardsThrown())
            calculateResult();
    }
}
