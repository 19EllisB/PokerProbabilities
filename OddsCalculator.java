import java.util.*;

class OddsCalculator {
    Table table; //the table upon which odds are calculated, contains an arrayList of players called players
    int cardsOnBoard; //No. of cards on the board, equal to table.cardsOnBoard, it's here because I don't want to reference table every time 

    ArrayList<Deck> sevenCardHands = new ArrayList<Deck>(); //the players cards plus the board cards for each player
    ArrayList<Deck> fiveCardHands = new ArrayList<Deck>(); //the player's 5 card represented hand determined from that player's 7cardHand
    ArrayList<Integer> handClasses = new ArrayList<Integer>(); // classes can be 0-8, 0 = high card, 8 = straight flush
    ArrayList<Integer[]> handValues = new ArrayList<Integer[]>(); //the "values" of each hand, determined uniquely for each hand class

    ArrayList<Double> playerOdds = new ArrayList<Double>(); //stores the individual player odds
    double splitOdds; //the odds of a split pot

    OddsCalculator(Table table) {
        this.table = table;
        this.cardsOnBoard = table.cardsOnBoard;

        //populates sevenCardHands
        for (int i = 0; i < table.players.size(); i++) {
            sevenCardHands.add(new Deck(false));
            for (Card c: table.board.inDeck) {
                sevenCardHands.get(i).add(c);
            }
            for (Card c: table.players.get(i).hand.inDeck) {
                sevenCardHands.get(i).add(c);
            }
        }
    }

    void appraiseHands() { //takes a 7 card hand and evaluates its value as a 5 card hand 
        for (int i = 0; i < sevenCardHands.size(); i++) { //for each hand in 7CardHands, ie for each player
            if (sevenCardHands.get(i).inDeck.size() != 7) { //if the size of the hand != 7
                System.out.println("I can't appraise a hand of not 7 cards :( ");
            } else { 
                Deck flaggedCards = new Deck(false);//creates an empty deck to place cards flagged by the following methods
                //flags for determining hands, self-explanatory
                boolean isFlush = false;
                boolean isStraight = false;
                boolean isQuads = false;
                boolean isTrips = false;
                boolean isPair = false;

                //checks for flushes first
                Deck flushFlaggedCards = new Deck(false);
                int diamonds = 0; //counts the number of cards in each suit
                int clubs = 0;
                int hearts = 0;
                int spades = 0;
                for (Card c: sevenCardHands.get(i).inDeck) {
                    switch (c.suitValue) {
                        case Card.DIAMONDS:
                        diamonds++;
                        break;
                        case Card.CLUBS:
                        clubs++;
                        break;
                        case Card.HEARTS:
                        hearts++;
                        break;
                        case Card.SPADES:
                        spades++;
                        break;
                        default:
                        System.out.println("The for loop which checks for flushes in OddsCalculator broke");
                    }
                }
                if (diamonds >= 5 ||
                clubs >= 5 ||
                hearts >= 5 ||
                spades >= 5) {
                    isFlush = true;
                }
                //sorts the cards by rank, the cards are not unordered later in this method so it's fine to do it here [ace = 14, not 1]
                Collections.sort(sevenCardHands.get(i).inDeck, new SortByRank());
                //places the relevant cards in the flushFlaggedCards Deck
                if (isFlush) {
                    int cardsSelected = 0; //keeps track of how many cards have been added to the flagged deck
                    if (diamonds >= 5) { //if you have a diamonds flush                    
                        for (int j = 0; j < sevenCardHands.get(i).inDeck.size(); j++) { //for every card in the 7Card hand
                            if (sevenCardHands.get(i).inDeck.get(j).suitValue == Card.DIAMONDS && cardsSelected < 5) { //if its the appropriate suit and fewer than 5 cards have been flagged
                                flushFlaggedCards.add(sevenCardHands.get(i).inDeck.get(j)); //flag the card
                                cardsSelected++; //increment the counter
                            }
                        }
                    } else if (clubs >= 5) {
                        for (int j = 0; j < sevenCardHands.get(i).inDeck.size(); j++) { //for every card in the 7Card hand
                            if (sevenCardHands.get(i).inDeck.get(j).suitValue == Card.CLUBS && cardsSelected < 5) { //if its the appropriate suit and fewer than 5 cards have been flagged
                                flushFlaggedCards.add(sevenCardHands.get(i).inDeck.get(j)); //flag the card
                                cardsSelected++; //increment the counter
                            }
                        }
                    }  else if (hearts >= 5) {
                        for (int j = 0; j < sevenCardHands.get(i).inDeck.size(); j++) { //for every card in the 7Card hand
                            if (sevenCardHands.get(i).inDeck.get(j).suitValue == Card.HEARTS && cardsSelected < 5) { //if its the appropriate suit and fewer than 5 cards have been flagged
                                flushFlaggedCards.add(sevenCardHands.get(i).inDeck.get(j)); //flag the card
                                cardsSelected++; //increment the counter
                            }
                        }
                    }  else if (spades >= 5) {
                        for (int j = 0; j < sevenCardHands.get(i).inDeck.size(); j++) { //for every card in the 7Card hand
                            if (sevenCardHands.get(i).inDeck.get(j).suitValue == Card.SPADES && cardsSelected < 5) { //if its the appropriate suit and fewer than 5 cards have been flagged
                                flushFlaggedCards.add(sevenCardHands.get(i).inDeck.get(j)); //flag the card
                                cardsSelected++; //increment the counter
                            }
                        }
                    }
                }
                //Sorts the 7 Card hand in order of rankValue, highest to lowest [Ace = 14 not 1]                
                
                Deck duplicateCards = new Deck(false); //stores removed dupe cards so they dont get destroyed
                //checks for straights
                for (int j = 0; j <= sevenCardHands.get(i).inDeck.size() - 4; j++) { //for each possible group of 4 ordered cards, duplicate cards are removed in case ranks are repeated in the middle of a straight                     
                    if (sevenCardHands.get(i).inDeck.size() >= 5) { //in order for a straight to be possible there must still be at least 5 non-dupe cards remaining
                        for (int k = 0; k < 4; k++) { //for the 4 cards in that grouping
                            //if the player gets a wheel [A2345], ie if k == 3 and if the current card is a 2 and if the hand has an ace
                            if (k == 3 && sevenCardHands.get(i).inDeck.get(j + k).rankValue == 2 && sevenCardHands.get(i).checkRank(Card.ACE)) {
                                isStraight = true;
                            }
                            
                            //break conditions                            
                            if (j + k + 1 >= sevenCardHands.get(i).inDeck.size()) { //if the index of the "next card" is out of bounds
                                break; //else if the rank of the next card in the grouping is greater than one less than the current card in the grouping
                            } else if (sevenCardHands.get(i).inDeck.get(j + k).rankValue - sevenCardHands.get(i).inDeck.get(j + k + 1).rankValue > 1) {                            
                                break; //move on to the next grouping, else if the rank of the next card == the rank of the current card
                            } else if (sevenCardHands.get(i).inDeck.get(j + k).rankValue - sevenCardHands.get(i).inDeck.get(j + k + 1).rankValue == 0) {
                                duplicateCards.add(sevenCardHands.get(i).inDeck.get(j + k + 1)); //add the dupe card to the duplicateCards deck so that we don't lose it
                                sevenCardHands.get(i).inDeck.remove(j + k + 1); //remove the dupe card
                                k--; //deiterate k so that when we continue k is what it was on the previous loop
                                continue;
                            }
                            //straight conditions
                            
                        }
                    }
                }
            }
        }
    }
}   