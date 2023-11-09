package com.game.belote.entity;

import com.sun.jdi.InternalException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import wniemiec.util.data.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;

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
    private List<Map<String, Card>> rounds;
    private Map<String, Integer> teamSums;
    private Map<String, List<Player>> teams;
    private List<Player> players;

    public Game() {
        deck = new Deck();
        players = new ArrayList<>(4);
        gameStatus = GameStatus.NEW;
        id = UUID.randomUUID();
        rounds = new ArrayList<>(8);
        teams = new HashMap<>(2);
        teamSums = new HashMap<>(2);
    }

    public void setUpTeams() {
        //set teams
        List<Player> team1 = new ArrayList<>(2);
        team1.add(players.get(0));
        team1.add(players.get(2));

        List<Player> team2 = new ArrayList<>(2);
        team2.add(players.get(1));
        team2.add(players.get(3));

        teams.put("team1", team1);
        teams.put("team2", team2);

        //set team results
        teamSums.put("team1", 0);
        teamSums.put("team2", 0);
    }

    public void addPlayer(Player player) {
        if(players.size() >= 4)
            throw new InternalException("Game has already 4 players...");
        players.add(player);
    }

    public List<Player> getClonedPlayers() {
        return new ArrayList<>(players);
    }

    private void setFirstSixCardsToVisible() {
        players.forEach(p -> p.getHand().subList(0, 6)
                .forEach(c -> c.setVisible(true)));
    }

    public void setLastTwoCardsToVisible() {
        players.forEach(p -> p.getHand().subList(6, 8)
                .forEach(c -> c.setVisible(true)));
    }

    public void changeTurn(Player player) {
        turn = players.get(players.indexOf(player));
        System.out.println("%nChanging turn to: %s".formatted(turn.getName()));
    }

    public void changeTurn() {
        turn = players.indexOf(turn) < 3 ? players.get(players.indexOf(turn) + 1) : players.get(0);
        System.out.println("%nChanging turn to: %s".formatted(turn.getName()));
    }

    public void resetTurn() {
        turn = players.indexOf(dealer) < 3 ? players.get(players.indexOf(dealer) + 1) : players.get(0);
    }

    public void sortHandsByFaceAndSequence() {
        getPlayers().forEach(p -> p.getHand().sort(Comparator.comparing(Card::getSuit).thenComparing(Card::getSequence)));
    }

    public void deal() {
        dealer = players.get(players.size() - 1);
        turn = players.indexOf(dealer) < 3 ? players.get(players.indexOf(dealer) + 1) : players.get(0);
        Iterator playerIterator = players.listIterator(players.indexOf(turn));
        //System.out.println(deck.getCards());
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

    public void adjustValueAndRank() {
        players.forEach(p -> {
            p.getHand().forEach(c -> {
                if(suit.equals(c.getSuit())) {
                    switch (c.getFace()) {
                        case DAMA -> c.setRank(3);
                        case KRALJ -> c.setRank(4);
                        case DESET -> c.setRank(5);
                        case AS -> c.setRank(6);
                        case DEVET -> {
                            c.setRank(7);
                            c.setValue(14);
                        }
                        case DEČKO -> {
                            c.setRank(8);
                            c.setValue(20);
                        }
                    }
                }
            });
        });
    }

    public void printRound(int i) {
        System.out.printf("%n%d. round:%n", (i+1));
        for(Map.Entry<String, Card> throwing : rounds.get(i).entrySet()) {
            System.out.printf("%s: %s%n", throwing.getKey(), throwing.getValue());
        }
    }

    public void printRounds() {
        System.out.println();
        rounds.forEach(r -> {
            System.out.printf("%d. round:%n", rounds.indexOf(r) + 1);
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

    private Card findMaxCardRankInRound(Collection<Card> cards) {
        Card max = cards.stream()
                .max(Card::compareTo)
                .get();
        //System.out.println("Max rank card in round: " + max);
        return max;
    }

    private List<Card> getSameSuitThrows(Player player, Map<String, Card> round) {
        boolean isChosenSuitInCurrentRound = round.values().stream()
                .anyMatch(c -> suit.equals(c.getSuit()));

        List<Card> validCards = player.getHand().stream()
                .filter(c -> c.getSuit().equals(
                        round.values().stream()
                                .findFirst()
                                .get().getSuit()))
                .toList();

        if(isChosenSuitInCurrentRound && !round.values().stream().findFirst().get().getSuit().equals(suit))
            return validCards;
        else {
            List<Card> cardsHigherThanCurrentMax = validCards.stream()
                    .filter(c -> c.getRank() > findMaxCardRankInRound(round.values()).getRank())
                    .toList();
            if(!cardsHigherThanCurrentMax.isEmpty())
                return cardsHigherThanCurrentMax;

            return validCards;
        }
    }

    private List<Card> getChosenSuitThrows(Player player, Map<String, Card> round) {
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
                    .filter(c -> c.getRank() > maxChosenSuitCardInRound.get().getRank())
                    .toList();

            if(!cardsHigherThanCurrentMax.isEmpty())
                return cardsHigherThanCurrentMax;
        }

        return chosenSuitCardsInHandStream.toList();
    }

    private List<Card> getOtherSuitThrows(Player player, Map<String, Card> round) {
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
        Map<String, Card> currentRound = rounds.get(rounds.size() - 1);

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

    public Optional<Player> throwCard(Player player, Card card) {
        List<Card> hand = player.getHand();
        Card cardThrown = hand.get(hand.indexOf(card));
        if(rounds.size() == 0) {
            System.out.println("-".repeat(300));
            System.out.println("Starting new round...(chosen suit: %s)".formatted(suit));
            System.out.println("Player %s tried to throw %s".formatted(player.getName(), card));
            rounds.add(new LinkedHashMap<>(4));
            rounds.get(0).put(player.getName(), cardThrown);
            hand.remove(cardThrown);
        }
        else {
            if(rounds.get(rounds.size() - 1).size() < 4) {
                List<Card> validThrows = getValidThrows(player, card);
                System.out.println("Player %s tried to throw %s".formatted(player.getName(), card));
                if(validThrows.contains(card)) {
                    rounds.get(rounds.size() - 1).put(player.getName(), cardThrown);
                    hand.remove(cardThrown);
                }
                else
                    throw new InternalException("Card %s is not valid throw...".formatted(card));
                if(rounds.get(rounds.size() - 1).size() == 4) {
                    printRound(rounds.size() - 1);
                    if(!areAllCardsThrown()) {
                        System.out.println("-".repeat(300));
                        System.out.println("Starting new round...(chosen suit: %s)".formatted(suit));
                    }
                    Pair<Player, Integer> roundWinner = getRoundWinner();
                    Player roundWinnerPlayer = roundWinner.getFirst();
                    Integer roundWinnerPoints = roundWinner.getSecond();
                    addPointsToTeamSum(roundWinnerPlayer, roundWinnerPoints);
                    return Optional.ofNullable(roundWinnerPlayer);
                }
            }
            else {
                System.out.println("Player %s tried to throw %s".formatted(player.getName(), card));
                rounds.add(new LinkedHashMap<>(4));
                rounds.get(rounds.size() - 1).put(player.getName(), cardThrown);
                hand.remove(cardThrown);
            }
        }

        return Optional.empty();
    }

    private void addPointsToTeamSum(Player roundWinnerPlayer, Integer roundWinnerPoints) {
        for(Map.Entry<String, List<Player>> team : teams.entrySet()) {
            boolean isRoundWinnerInCurrentTeam = team.getValue().contains(roundWinnerPlayer);
            if(isRoundWinnerInCurrentTeam) {
                teamSums.merge(team.getKey(), roundWinnerPoints, Integer::sum);
                //check if last round
                if(rounds.size() == 8)
                    teamSums.merge(team.getKey(), 10, Integer::sum);
            }
        }
    }

    private Player findPlayerByCardInRound(Card card, Map<String, Card> round) {
        for(Map.Entry<String, Card> throwing : round.entrySet()) {
            if(throwing.getValue().equals(card)){
                return players.get(players.indexOf(new Player(throwing.getKey())));
            }
        }

        return null;
    }

    private Pair<Player, Integer> getRoundWinner() {
        Map<String, Card> lastRound = rounds.get(rounds.size() - 1);
        Player roundWinnerPlayer;

        //sum card values in round
        int roundWinnerPoints = lastRound.values()
                .stream()
                .map(Card::getValue)
                .reduce(0, Integer::sum);

        //get max player-value pair if there are chosen suits in round
        List<Card> chosenSuitCards = lastRound.values()
                .stream()
                .filter(c -> suit.equals(c.getSuit()))
                .toList();

        if(!chosenSuitCards.isEmpty()) {
            Card chosenSuitCardWithMaxRank = chosenSuitCards.stream()
                    .max(Card::compareTo)
                    .get();
            roundWinnerPlayer = findPlayerByCardInRound(chosenSuitCardWithMaxRank, lastRound);
            return new Pair<>(roundWinnerPlayer, roundWinnerPoints);
        }

        //get max player-value pair if there aren't chosen suits in round
        Suit firstCardSuit = lastRound.values()
                .stream()
                .findFirst()
                .get()
                .getSuit();

        Card sameSuitCardWithMaxRank = lastRound.values()
                .stream()
                .filter(c -> c.getSuit().equals(firstCardSuit))
                .max(Card::compareTo)
                .get();

        roundWinnerPlayer = findPlayerByCardInRound(sameSuitCardWithMaxRank, lastRound);
        return new Pair<>(roundWinnerPlayer, roundWinnerPoints);
    }

    private Map<String, List<Card>> getFourSameFaceBonusCards(String playerName, List<Card> cards) {
        //get four of kind cards(DEČKO, DEVET, AS, DESET, DAMA)
        Map<Face, Long> fourOfKindBonusCards = cards.stream()
                .collect(collectingAndThen(groupingBy(Card::getFace, Collectors.counting()),
                        m -> {
                            m.values().removeIf(v -> v != 4);
                            m.keySet().removeIf(k -> k.equals(Face.SEDAM)
                                    || k.equals(Face.OSAM));
                            return m;
                }));

        Map<String, List<Card>> descListFourSameFaceCards = new HashMap<>(2);
        for(Face face : fourOfKindBonusCards.keySet()) {
            String desc = switch (face) {
                case DEVET -> "fourOfKindDevet";
                case DEČKO -> "fourOfKindDecko";
                case DAMA -> "fourOfKindDama";
                case KRALJ -> "fourOfKindKralj";
                case DESET -> "fourOfKindDeset";
                case AS -> "fourOfKindAs";
                default -> null;
            };
            List<Card> listFourOfKindCards = cards.stream()
                    .filter(c -> c.getFace().equals(face))
                    .toList();
            descListFourSameFaceCards.put(desc, listFourOfKindCards);
        }
        System.out.println("-".repeat(300));
        System.out.println("four of a kind...(%s)".formatted(playerName));
        System.out.println(descListFourSameFaceCards.isEmpty() ? "empty" : descListFourSameFaceCards);
        return descListFourSameFaceCards;
    }

    private Map<String, List<Card>> getSameSuitSequenceBonusCards(List<Card> cards) {
        //nedovršeno
        return null;
    }

    public Map<String, List<Card>> calculateBonus(String playerName, List<Card> cards) {
        Map<String, List<Card>> fourSameFaceBonusCards = getFourSameFaceBonusCards(playerName, cards);
        Map<String, List<Card>> sameSuitSequenceBonusCards = getSameSuitSequenceBonusCards(cards);
        return fourSameFaceBonusCards;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", gameStatus=" + gameStatus +
                ", deck=" + deck +
                ", dealer=" + dealer +
                ", turn=" + turn +
                ", suit=" + suit +
                ", rounds=" + rounds +
                ", players=" + players +
                '}';
    }
}
