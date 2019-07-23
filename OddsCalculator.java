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
                
                //sorts the deck in the same way everytime
                Collections.sort(sevenCardHands.get(i).inDeck, new SortBySuit());
                Collections.sort(sevenCardHands.get(i).inDeck, new SortByRank());                                              

                Deck duplicateCards = new Deck(false); //stores removed dupe cards so they dont get destroyed
                //checks for straight flushes
                jLoop:
                for (int j = 0; j <= 3; j++) { //for each possible group of 4 ordered cards, duplicate cards are removed in case ranks are repeated in the middle of a straight                     
                    if (!duplicateCards.inDeck.isEmpty()) { //if the dupe deck has cards in it
                        for (Card c: duplicateCards.inDeck) { //for every card in the dupe deck
                            sevenCardHands.get(i).add(c); //add it back to its original hand 
                        }
                        duplicateCards.clear(); //clear the dupe deck
                        //sorts the hand the same way every time
                        Collections.sort(sevenCardHands.get(i).inDeck, new SortBySuit());
                        Collections.sort(sevenCardHands.get(i).inDeck, new SortByRank());
                    }
                    kLoop:
                    for (int k = 0; k < 4; k++) { //for the 4 cards in that grouping                            
                        //steel wheel conditions [A2345 suited]
                        if (k == 3 && 
                        sevenCardHands.get(i).inDeck.get(j + k).rankValue == Card.TWO && 
                        sevenCardHands.get(i).check(Card.ACE, sevenCardHands.get(i).inDeck.get(j + k).suitValue) >= 0) {
                            //if youre on the 4th card, and that card is a two, and the hand has an ace of the same suit
                            isStraightFlush = true;
                            //add the suited ace
                            flaggedCards.add(sevenCardHands.get(i).inDeck.get(sevenCardHands.get(i).check(Card.ACE, sevenCardHands.get(i).inDeck.get(j + k).suitValue)));
                            //add the other cards
                            for (int t = 0; t >= -3; t--) { //add those 5 cards to flagged cards
                                flaggedCards.add(sevenCardHands.get(i).inDeck.get(j + k + t));
                            }
                            break jLoop; //leave the j loop
                        }
                        
                        //break conditions                            
                        if (j + k + 1 >= sevenCardHands.get(i).inDeck.size()) { //if the index of the "next card" is out of bounds
                            break; 
                        } else if (sevenCardHands.get(i).inDeck.get(j + k).rankValue - sevenCardHands.get(i).inDeck.get(j + k + 1).rankValue > 1) {                            
                            //else if the rank of the next card in the grouping is greater than one less than the current card in the grouping
                            break; //move on to the next grouping
                        } else if (sevenCardHands.get(i).inDeck.get(j + k).rankValue - sevenCardHands.get(i).inDeck.get(j + k + 1).rankValue == 1 &&
                        sevenCardHands.get(i).inDeck.get(j + k).suitValue != sevenCardHands.get(i).inDeck.get(j + k + 1).suitValue) {
                            //if the rank of the next card is 1 less but it's not the same suit
                            nLoop:
                            for (int n = 2; n <= 4; n++) { //for the nth card past the current one up to n = 4
                                //if the nth card past the current one exists and has the same rank as the card immediately previous
                                if ((j + k + n) < sevenCardHands.get(i).inDeck.size()) {
                                    if (sevenCardHands.get(i).inDeck.get(j + k + n - 1).rankValue == sevenCardHands.get(i).inDeck.get(j + k + n).rankValue) {
                                        //if the nth card past the current one has the same suit as the current one 
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
                        } else if (sevenCardHands.get(i).inDeck.get(j + k).rankValue - sevenCardHands.get(i).inDeck.get(j + k + 1).rankValue == 0) {
                            //if the rank of the next card is equal to the rank of the current card
                            //if the current card is the first card in your straight
                            if (k == 0) {
                                nLoop:
                                for (int n = 2; n < 4; n++) {
                                    if ((j + k + n) < sevenCardHands.get(i).inDeck.size()) {
                                        if (sevenCardHands.get(i).inDeck.get(j + k).rankValue == sevenCardHands.get(i).inDeck.get(j + k + n).rankValue) {
                                            //if the nth card past the current one has the same suit as the current one 
                                            if (n >= 3) { //if four cards in a row have the same rank a straight flush is impossible
                                                break jLoop;
                                            } else { //keep checking
                                                continue nLoop;
                                            }
                                        } else if (sevenCardHands.get(i).inDeck.get(j + k).rankValue - sevenCardHands.get(i).inDeck.get(j + k + n).rankValue == 1) {                                        
                                            //else if the nth card past the current one is the same suit and the next rank down
                                            //remove the dupe cards which dont match the suit so that you don't fuck up how k iterates
                                            for (int m = n - 1; m > 0; m--) {
                                                duplicateCards.add(sevenCardHands.get(i).inDeck.get(j + k + m));
                                                sevenCardHands.get(i).remove(sevenCardHands.get(i).inDeck.get(j + k + m));
                                            }
                                            break nLoop;
                                        } else { //else the suit doesnt match or the rank isnt good, either way you cant make a straight flush with this card
                                            break kLoop;
                                        }
                                    } else { // else youve missed the straight flush entirely
                                        break kLoop;
                                    }
                                }
                            } else { //if it isnt than you know that the current card's suit is the correct one, remove the dupe
                                duplicateCards.add(sevenCardHands.get(i).inDeck.get(j + k + 1)); //add the card to the dupe deck
                                sevenCardHands.get(i).remove(sevenCardHands.get(i).inDeck.get(j + k + 1)); //remove it from this one
                                k--; //deiterate k so that when you continue, k points to the current card
                                continue kLoop;
                            }
                        }

                        //straight flush conditions
                        if (k == 3) { //if youve made it to the 4th card and have overcome the soft break conditions
                            //you know that you have 5 cards in a row that are a straight and a flush
                            isStraightFlush = true;
                            for (int t = 1; t >= -3; t--) { //add those 5 cards to flagged cards
                                flaggedCards.add(sevenCardHands.get(i).inDeck.get(j + k + t));
                            }
                            break jLoop; //leave the j loop
                        }
                    }
                }
                
                if (isStraightFlush) {
                    handClasses.add(i, 8);
                    
                }
            }
        }
    }
}   