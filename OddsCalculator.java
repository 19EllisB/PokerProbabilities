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
                boolean isStraightFlush = false;
                boolean isFlush = false;
                boolean isStraight = false;
                boolean isQuads = false;
                boolean isTrips = false;
                boolean isPair = false;

                Collections.sort(sevenCardHands.get(i).inDeck, new SortByRank());                                              

                Deck duplicateCards = new Deck(false); //stores removed dupe cards so they dont get destroyed
                //checks for straights
                for (int j = 0; j <= sevenCardHands.get(i).inDeck.size() - 4; j++) { //for each possible group of 4 ordered cards, duplicate cards are removed in case ranks are repeated in the middle of a straight                     
                    if (sevenCardHands.get(i).inDeck.size() >= 5) { //in order for a straight to be possible there must still be at least 5 non-dupe cards remaining
                        kLoop:
                        for (int k = 0; k < 4; k++) { //for the 4 cards in that grouping                            

                            //break conditions                            
                            if (j + k + 1 >= sevenCardHands.get(i).inDeck.size()) { //if the index of the "next card" is out of bounds
                                break; //else if the rank of the next card in the grouping is greater than one less than the current card in the grouping
                            } else if (sevenCardHands.get(i).inDeck.get(j + k).rankValue - sevenCardHands.get(i).inDeck.get(j + k + 1).rankValue > 1) {                            
                                break; //move on to the next grouping
                            } else if (sevenCardHands.get(i).inDeck.get(j + k).rankValue - sevenCardHands.get(i).inDeck.get(j + k + 1).rankValue == 1 &&
                            sevenCardHands.get(i).inDeck.get(j + k).suitValue != sevenCardHands.get(i).inDeck.get(j + k + 1).suitValue) {
                                //if the rank of the next card is 1 less but it's not the same suit
                                nLoop:
                                for (int n = 2; n <= 4; n++) { //for the nth card past the current one up to n = 4
                                    //if the nth card past the current one exists and has the same rank as the card immediately previous
                                    if ((j + k + n) < sevenCardHands.get(i).inDeck.size()) {
                                        if (sevenCardHands.get(i).inDeck.get(j + k + n - 1).rankValue == sevenCardHands.get(i).inDeck.get(j + k + n).rankValue) {
                                            //if the nth card past the current one has the same rank as the current one 
                                            if (sevenCardHands.get(i).inDeck.get(j + k).suitValue == sevenCardHands.get(i).inDeck.get(j + k + n).suitValue) {
                                                //remove the dupe cards which dont match the suit so that you don't fuck up how k iterates
                                                for (int m = n - 1; m > 0; m--) {
                                                    duplicateCards.add(sevenCardHands.get(i).inDeck.get(j + k + m));
                                                    sevenCardHands.get(i).remove(sevenCardHands.get(i).inDeck.get(j + k + m));
                                                }
                                                //k += n - 1; //redundant with the above for loop 
                                                break nLoop;
                                            } else { //else check to see if the next card can be used
                                                continue;
                                            }
                                        } else {
                                            break kLoop;
                                        }
                                    } else { // else youve missed the straight flush entirely
                                        break kLoop;
                                    }
                                }                                
                            }
                            
                            //straight conditions

                        }
                    }
                }
            }
        }
    }
}   