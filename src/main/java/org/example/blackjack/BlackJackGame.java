package org.example.blackjack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class BlackJackGame {

    public static void main(String[] args) {

        Table table = new Table(new Dealer(1, "dealer", 10000, new Hand()));
        BlackJackPlayer player1 = new BlackJackPlayer(1, "A", 1000, new Hand());
        table.addPlayer(player1);

        BlackJackPlayer player2 = new BlackJackPlayer(2, "B", 1000, new Hand());
        table.addPlayer(player2);

        BlackJackPlayer player3 = new BlackJackPlayer(3, "C", 1000, new Hand());
        table.addPlayer(player3);

        List<BlackJackPlayer> players = List.of(player1, player2, player3);

        table.startGame();;
        System.out.println(table.getDescription());
        int currentTurn = table.getPlayerId();

        while (currentTurn < players.size()) {
            BlackJackPlayer player = players.get(currentTurn);
            System.out.println("current turn: " + player.getName());

            while (table.getPlayerId() == currentTurn) {
                table.hit();
                if (player.getHand().getValue() > 18) {
                    table.stand();;
                }
                System.out.println(player.getDescription());
            }

            System.out.println("-----------------");
            currentTurn = table.getPlayerId();
        }

        table.dealTurn();
        //System.out.println(table.getDealer().getDescription());
        System.out.println(table.getDescription());
    }
}


@Getter
enum CardSuit {
    SPADE, HEART, DIAMOND, CLUB
}

@Getter
enum CardRank {
    ACE(11), TWO(2), THREE(3), FOUR(4), FIVE(5),
    SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
    JACK(10), QUEUE(10), KING(10);

    private final int value;

    CardRank(int value) {
        this.value = value;
    }
}

@AllArgsConstructor
@Data
class Card {
    CardSuit suit;
    CardRank rank;

    public int getValue() {
        return rank.getValue();
    }
}

@Data
class Deck {

    List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        for (CardSuit cardSuit : CardSuit.values()) {
            for (CardRank cardRank : CardRank.values()) {
                cards.add(new Card(cardSuit, cardRank));
            }
        }
    }
}

@Data
class Shoe {

    int numberOfDecks;

    List<Deck> decks;

    Stack<Card> cards;

    public Shoe(int numberOfDecks) {
        this.numberOfDecks = numberOfDecks;
        decks = new ArrayList<>();
        cards = new Stack<>();

        for (int i = 0; i < numberOfDecks; i++) {
            decks.add(new Deck());
            cards.addAll(decks.get(i).getCards());
        }

    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card draw() {
        if (cards.isEmpty()) {
            throw new RuntimeException("no cards in the shoe.");
        }
        return cards.pop();
    }

}

@Data
class Hand {

    List<Card> cards;

    public Hand() {
        cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public boolean isBusted() {
        return getValue() > 21;
    }

    public int getValue() {
        int value = 0;
        int aces = 0;

        for (Card card : cards) {
            value += card.getValue();
            if (card.getRank() == CardRank.ACE) {
                aces++;
            }
        }

        while (value > 21 && aces > 0) {
            value -= 10;
            aces--;
        }

        return value;
    }

    public void resetHand() {
        cards.clear();
    }

    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("value = ").append(getValue()).append("\n");
        for (Card card : cards) {
            sb.append(card.getSuit().name()).append("_").append(card.getRank()).append(" ");
        }
        return sb.toString();
    }
}

@AllArgsConstructor
@Data
abstract class Player {
    int id;
    String name;
    int cash;
    Hand hand;

    public void addCard(Card card) {
        hand.addCard(card);
    }

    public boolean isBusted() {
        return hand.isBusted();
    }

    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        sb.append(hand.getDescription());
        return sb.toString();
    }
}

class BlackJackPlayer extends Player {
    int bet;

    public BlackJackPlayer(int id, String name, int cash, Hand hand) {
        super(id, name, cash, hand);
    }

    public void placeBets(int bet) {
        this.bet += bet;
    }

    public void winBets(int bet) {
        this.bet += bet;
    }
}

class Dealer extends Player {

    public Dealer(int id, String name, int cash, Hand hand) {
        super(id, name, cash, hand);
    }

    public boolean shouldHit() {
        int value = hand.getValue();
        return value < 17 || (value == 17 && hasSoft17());
    }


    private boolean hasSoft17() {
        return true;
//        return hand.getValue() == 17 && getHand().getCards().stream()
//                .anyMatch(card -> card.getRank() == Rank.ACE);
    }


}

@Data
class Table {

    Shoe shoe;
    List<BlackJackPlayer> players;
    Dealer dealer;
    int playerId;

    public Table(Dealer dealer) {
        this.shoe = new Shoe(2);
        this.players = new ArrayList<>();
        this.dealer = dealer;
        playerId = 0;
    }

    public void addPlayer(BlackJackPlayer player) {
        players.add(player);
    }

    public void startGame() {
        shoe.shuffle();

        for (BlackJackPlayer player : players) {
            player.addCard(shoe.draw());
            player.addCard(shoe.draw());
        }

        dealer.addCard(shoe.draw());
        dealer.addCard(shoe.draw());
    }

    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("shoe count = ").append(shoe.getCards().size()).append("\n");

        for (BlackJackPlayer player : players) {
            sb.append(player.getDescription()).append("\n");
        }
        sb.append(dealer.getDescription()).append("\n");
        return sb.toString();
    }

    public void hit() {
        Player player = players.get(playerId);
        if (player.isBusted()) {
            return;
        }
        player.addCard(shoe.draw());
        if (player.isBusted()) {
            System.out.println(player.getName() + " is busted.");
            stand();
        }
    }

    public void stand() {
        playerId++;
    }

    public void dealTurn() {
        while (dealer.shouldHit()) {
            dealer.addCard(shoe.draw());
        }
    }
}