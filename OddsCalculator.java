import java.util.*;

class OddsCalculator {
    Table table; //the table upon which odds are calculated, contains an arrayList of players called players
    int cardsOnBoard; //No. of cards on the board, equal to table.cardsOnBoard, it's here because I don't want to reference table every time 

    ArrayList<Deck> sevenCardHands = new ArrayList<Deck>(); //the players cards plus the board cards for each player
    ArrayList<Deck> fiveCardHands = new ArrayList<Deck>(); //the player's 5 card represented hand determined from that player's 7cardHand
    ArrayList<Integer> handClasses = new ArrayList<Integer>(); // classes can be 1-9, 1 = high card, 9 = straight flush
    ArrayList<Integer[]> handValues = new ArrayList<Integer[]>(); //the "values" of each hand, determined uniquely for each hand class

    ArrayList<Double> playerOdds = new ArrayList<Double>(); //stores the individual player odds as a percentage
    double splitOdds; //the odds of a split pot

    ArrayList<Deck> playerOuts = new ArrayList<Deck>(); //stored the individual players out cards
    Deck splitOuts = new Deck(false); //stores the cards which result in a split

    OddsCalculator(Table tempTable) {
        this.table = new Table(tempTable);
        this.cardsOnBoard = this.table.cardsOnBoard;

        //populates sevenCardHands
        for (int i = 0; i < table.players.size(); i++) {
            sevenCardHands.add(new Deck(false));
            for (Card c: table.board.inDeck) {
                sevenCardHands.get(i).add(c);
            }
            for (Card c: table.players.get(i).hand.inDeck) {
                sevenCardHands.get(i).add(c);
            }

            fiveCardHands.add(new Deck(false));
            playerOuts.add(new Deck(false));
        }
    }

    void repopulateSevenCardHands() { //it actually clears everything
        //populates sevenCardHands        
        for (int i = 0; i < table.players.size(); i++) {
            sevenCardHands.get(i).clear(); //clears them out entirely 
            fiveCardHands.get(i).clear(); //clears all the decks before appraisal, populated in appraiseHands()

            for (Card c: table.board.inDeck) { //adds in updated board cards
                sevenCardHands.get(i).add(c);
            }
            for (Card c: table.players.get(i).hand.inDeck) { //adds in that player's pocket cards
                sevenCardHands.get(i).add(c);
            }
        }
        
        handClasses.clear();
        handValues.clear();
    }

    void appraiseHands() { //takes a 7 card hand and evaluates its value as a 5 card hand 
        iLoop:
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
                boolean isTwoPair = false;

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
                                            //if the nth card past the current one has the same rank as the current one 
                                            if (n >= 3) { //if four cards in a row have the same rank a straight flush is impossible
                                                break jLoop;
                                            } else { //keep checking
                                                continue nLoop;
                                            }
                                        } else if (sevenCardHands.get(i).inDeck.get(j + k).rankValue - sevenCardHands.get(i).inDeck.get(j + k + n).rankValue == 1) {                                        
                                            //else if the nth card past the current one is the next rank down
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
                            } else { 
                                if (sevenCardHands.get(i).inDeck.get(j + k).suitValue == sevenCardHands.get(i).inDeck.get(j + k - 1).suitValue) {
                                    //if k isnt 0, and the current suit matches the previous card's, than you know that the current card's suit is the correct one, remove the dupe                                  
                                    duplicateCards.add(sevenCardHands.get(i).inDeck.get(j + k + 1)); //add the card to the dupe deck
                                    sevenCardHands.get(i).remove(sevenCardHands.get(i).inDeck.get(j + k + 1)); //remove it from this one
                                } else {
                                    //else the current suit is incorrect so remove this one
                                    duplicateCards.add(sevenCardHands.get(i).inDeck.get(j + k)); //add the card to the dupe deck
                                    sevenCardHands.get(i).remove(sevenCardHands.get(i).inDeck.get(j + k)); //remove it from this one
                                }                                
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
                    handClasses.add(i, 9);
                    for (Card c: flaggedCards.inDeck) { //all five cards constitute the player's hand
                        fiveCardHands.get(i).add(c);
                    }

                    fiveCardHands.get(i).absSort();
                    Integer[] handValue = {new Integer(fiveCardHands.get(i).inDeck.get(0).rankValue)}; //the value of the hand is the highest ranking card
                    handValues.add(i, handValue); 
                    continue iLoop;
                }

                if (!duplicateCards.inDeck.isEmpty()) { //if the dupe deck has cards in it
                    for (int n = 0; n < duplicateCards.inDeck.size(); n++) { //for every card in the dupe deck
                        sevenCardHands.get(i).add(duplicateCards.inDeck.get(n)); //add it back to its original hand 
                    }
                    duplicateCards.clear(); //clear the dupe deck                        
                }

                //next checks for flushes
                //rank then suit ensures the highest possible ranking flush
                Collections.sort(sevenCardHands.get(i).inDeck, new SortByRank());
                Collections.sort(sevenCardHands.get(i).inDeck, new SortBySuit());

                jLoop:
                for (int j = 0; j < 3; j++) { //for each grouping of 4 cards in the 7 card hand
                    kLoop:
                    for (int k = 0; k < 4; k++) { //for each card in that grouping  
                        if (sevenCardHands.get(i).inDeck.get(j + k).suitValue != sevenCardHands.get(i).inDeck.get(j + k + 1).suitValue) {
                            //if the next card is not of the same suit as the current one
                            break kLoop;
                        }
                        if (k == 3) { //if the card after the fourth card is the same suit, youve hit a flush
                            isFlush = true;
                            for (int t = 1; t >= -3; t--) { //add the cards to flaggedCards
                                flaggedCards.add(sevenCardHands.get(i).inDeck.get(j + k + t));
                            }
                            break jLoop;
                        }
                    }
                }

                if (isFlush) {
                    handClasses.add(i, 6);
                    for (Card c: flaggedCards.inDeck) { //all five cards constitute the player's hand
                        fiveCardHands.get(i).add(c);
                    }

                    fiveCardHands.get(i).absSort();
                    Integer[] handValue = {new Integer(fiveCardHands.get(i).inDeck.get(0).rankValue)}; //the value of the hand is the highest ranking card
                    handValues.add(i, handValue); 
                    continue iLoop;
                }

                sevenCardHands.get(i).absSort();

                //checks for straights
                jLoop:
                for (int j = 0; j < 3; j++) {
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
                    for (int k = 0; k < 4; k++) {
                        //wheel conditions [A2345 off suit]
                        if (k == 3 && 
                        sevenCardHands.get(i).inDeck.get(j + k).rankValue == Card.TWO && 
                        sevenCardHands.get(i).checkRank(Card.ACE) >= 0) {
                            //if youre on the 4th card, and that card is a two, and the hand has an ace 
                            isStraight = true;
                            //add the suited ace
                            flaggedCards.add(sevenCardHands.get(i).inDeck.get(sevenCardHands.get(i).checkRank(Card.ACE)));
                            //add the other cards
                            for (int t = 0; t >= -3; t--) { //add those 5 cards to flagged cards
                                flaggedCards.add(sevenCardHands.get(i).inDeck.get(j + k + t));
                            }
                            break jLoop; //leave the j loop
                        }

                        //break conditions                            
                        if (j + k + 1 >= sevenCardHands.get(i).inDeck.size()) { //if the index of the "next card" is out of bounds
                            break kLoop; 
                        } else if (sevenCardHands.get(i).inDeck.get(j + k).rankValue - sevenCardHands.get(i).inDeck.get(j + k + 1).rankValue > 1) {                            
                            //else if the rank of the next card in the grouping is greater than one less than the current card in the grouping
                            break kLoop; //move on to the next grouping
                        } else if (sevenCardHands.get(i).inDeck.get(j + k).rankValue - sevenCardHands.get(i).inDeck.get(j + k + 1).rankValue == 0) {
                            //else if the rank of the next card equals the rank of the current one
                            duplicateCards.add(sevenCardHands.get(i).inDeck.get(j + k + 1)); //add the card to the dupe deck
                            sevenCardHands.get(i).remove(sevenCardHands.get(i).inDeck.get(j + k + 1)); //remove it from this one
                            k--;
                            continue kLoop;
                        }

                        //straight conditions
                        if (k == 3) {
                            //you know that you have 5 cards in a row that are a straight 
                            isStraight = true;
                            for (int t = 1; t >= -3; t--) { //add those 5 cards to flagged cards
                                flaggedCards.add(sevenCardHands.get(i).inDeck.get(j + k + t));
                            }
                            break jLoop; //leave the j loop
                        }
                    }
                }

                if (isStraight) {
                    handClasses.add(i, 5);
                    for (Card c: flaggedCards.inDeck) { //all five cards constitute the player's hand
                        fiveCardHands.get(i).add(c);
                    }

                    fiveCardHands.get(i).absSort();
                    Integer[] handValue = {new Integer(fiveCardHands.get(i).inDeck.get(0).rankValue)}; //the value of the hand is the highest ranking card
                    handValues.add(i, handValue); 
                    continue iLoop;
                }

                if (!duplicateCards.inDeck.isEmpty()) { //if the dupe deck has cards in it
                    for (int n = 0; n < duplicateCards.inDeck.size(); n++) { //for every card in the dupe deck
                        sevenCardHands.get(i).add(duplicateCards.inDeck.get(n)); //add it back to its original hand 
                    }
                    duplicateCards.clear(); //clear the dupe deck                        
                }

                sevenCardHands.get(i).absSort();

                //checks for quads
                jLoop:
                for (int j = 0; j < 4; j++) {
                    kLoop:
                    for (int k = 0; k < 3; k++) {
                        if (sevenCardHands.get(i).inDeck.get(j + k).rankValue != sevenCardHands.get(i).inDeck.get(j + k + 1).rankValue) {
                            //if the next card is not of the same rank as the current one
                            break kLoop;
                        }
                        if (k == 2) { //if the card after the third card is the same suit, youve hit a flush
                            isQuads = true;
                            for (int t = 1; t >= -2; t--) { //add the cards to flaggedCards and remove them from sevenCardsHands.get(i)
                                flaggedCards.add(sevenCardHands.get(i).inDeck.get(j + k + t));
                                sevenCardHands.get(i).remove(sevenCardHands.get(i).inDeck.get(j + k + t)); //remove it from this one
                            }
                            break jLoop;
                        }
                    }
                }

                if (isQuads) {
                    handClasses.add(i, 8);
                    for (Card c: flaggedCards.inDeck) { //all flagged cards constitute the player's hand
                        fiveCardHands.get(i).add(c);
                    }
                    int currentFiveCardSize = fiveCardHands.get(i).inDeck.size();
                    for (int t = 0; t < 5 - currentFiveCardSize; t++) { //until the five card had is five cards full, add kickers there are no additional cards in flaggedCards
                        fiveCardHands.get(i).add(sevenCardHands.get(i).inDeck.get(t));//add the highest ranking card in the 7 card hand as a kicker
                    }
                    fiveCardHands.get(i).absSort();

                    Integer[] handValue = {new Integer(flaggedCards.inDeck.get(0).rankValue)}; //the value of the hand is the rank of the quad
                    handValues.add(i, handValue);
                    continue iLoop;
                }

                //checks for trips next

                jLoop:
                for (int j = 0; j < 5; j++) {
                    kLoop:
                    for (int k = 0; k < 2; k++) {
                        if (sevenCardHands.get(i).inDeck.get(j + k).rankValue != sevenCardHands.get(i).inDeck.get(j + k + 1).rankValue) {
                            //if the next card is not of the same rank as the current one
                            break kLoop;
                        }
                        if (k == 1) { //if the card after the second card is the same rank, youve hit a set
                            isTrips = true;
                            for (int t = 1; t >= -1; t--) { //add the cards to flaggedCards and remove them from sevenCardsHands.get(i)
                                flaggedCards.add(sevenCardHands.get(i).inDeck.get(j + k + t));
                                sevenCardHands.get(i).remove(sevenCardHands.get(i).inDeck.get(j + k + t)); //remove it from this one
                            }
                            break jLoop;
                        }
                    }
                }

                //finally checks for pairs

                jLoop:
                for (int j = 0; j < sevenCardHands.get(i).inDeck.size() - 1; j++) {
                    if (!isTwoPair) {
                        if (sevenCardHands.get(i).inDeck.get(j).rankValue == sevenCardHands.get(i).inDeck.get(j + 1).rankValue) {
                            if (isPair == true) {
                                isTwoPair = true;   
                            } else {
                                isPair = true;   
                            }
                            for (int t = 1; t >= 0; t--) { //add the cards to flaggedCards and remove them from sevenCardsHands.get(i)
                                flaggedCards.add(sevenCardHands.get(i).inDeck.get(j + t));
                                sevenCardHands.get(i).remove(sevenCardHands.get(i).inDeck.get(j + t)); //remove it from this one
                            }
                            j--; //fixes bug where the second pair was left undetected if its the next lowest rank in 7cardHands
                        }
                    }                    
                }

                //assigns combo hands
                //if the hand is a full house
                if (isTrips && isPair) { 
                    handClasses.add(i, 7);

                    for (int t = 0; t < 5; t++) { //first 5 flagged cards constitute the player's hand, trips are added into flagged cards before the highest pair
                        fiveCardHands.get(i).add(flaggedCards.inDeck.get(t));
                    }

                    int currentFiveCardSize = fiveCardHands.get(i).inDeck.size();
                    for (int t = 0; t < 5 - currentFiveCardSize; t++) { //until the five card had is five cards full, add kickers there are no additional cards in flaggedCards
                        fiveCardHands.get(i).add(sevenCardHands.get(i).inDeck.get(t));//add the highest ranking card in the 7 card hand as a kicker
                    }

                    Integer[] handValue = {new Integer(flaggedCards.inDeck.get(0).rankValue), new Integer(flaggedCards.inDeck.get(3).rankValue)}; //the value of the hand is the rank of the quad
                    handValues.add(i, handValue);
                    continue iLoop;  
                } else if (isTrips) {
                    //else if the hand is a set
                    handClasses.add(i, 4); 
                    for (int t = 0; t < 3; t++) { //first 3 flagged cards constitute the player's hand, trips are added into flagged cards before the highest pair
                        fiveCardHands.get(i).add(flaggedCards.inDeck.get(t));
                    }

                    int currentFiveCardSize = fiveCardHands.get(i).inDeck.size();
                    for (int t = 0; t < 5 - currentFiveCardSize; t++) { //until the five card had is five cards full, add kickers there are no additional cards in flaggedCards
                        fiveCardHands.get(i).add(sevenCardHands.get(i).inDeck.get(t));//add the highest ranking card in the 7 card hand as a kicker
                    }

                    Integer[] handValue = {new Integer(flaggedCards.inDeck.get(0).rankValue)}; //the value of the hand is the rank of the trip
                    handValues.add(i, handValue);
                    continue iLoop;
                } else if (isTwoPair) {
                    //else if the hand is a Two Pair
                    handClasses.add(i, 3);
                    for (int t = 0; t < 4; t++) { //first 4 flagged cards constitute the player's hand, high pair then low pair
                        fiveCardHands.get(i).add(flaggedCards.inDeck.get(t));
                    }

                    //im pretty sure i accounted for this possibility already in the pair checking algorithm, ignore it for now
                    /*for (int t = 4; t < flaggedCards.inDeck.size(); t ++) { //add back a third pair if it was flagged
                    sevenCardHands.get(i).add(flaggedCards.inDeck.get(t));
                    }
                    sevenCardHands.get(i).absSort(); */

                    int currentFiveCardSize = fiveCardHands.get(i).inDeck.size();
                    for (int t = 0; t < 5 - currentFiveCardSize; t++) { //until the five card had is five cards full, add kickers there are no additional cards in flaggedCards
                        fiveCardHands.get(i).add(sevenCardHands.get(i).inDeck.get(t));//add the highest ranking card in the 7 card hand as a kicker
                    }

                    Integer[] handValue = {new Integer(flaggedCards.inDeck.get(0).rankValue), new Integer(flaggedCards.inDeck.get(2).rankValue)}; //the value of the hand is the rank of the quad
                    handValues.add(i, handValue);
                    continue iLoop;
                } else if (isPair) {
                    //else if the hand is just a pair
                    handClasses.add(i, 2);
                    for (int t = 0; t < 2; t++) { //first 2 flagged cards constitute the player's hand
                        fiveCardHands.get(i).add(flaggedCards.inDeck.get(t));
                    }

                    int currentFiveCardSize = fiveCardHands.get(i).inDeck.size();
                    for (int t = 0; t < 5 - currentFiveCardSize; t++) { //until the five card had is five cards full, add kickers there are no additional cards in flaggedCards
                        fiveCardHands.get(i).add(sevenCardHands.get(i).inDeck.get(t));//add the highest ranking card in the 7 card hand as a kicker
                    }

                    Integer[] handValue = {new Integer(flaggedCards.inDeck.get(0).rankValue)}; //the value of the hand is the rank of the pair
                    handValues.add(i, handValue);
                    continue iLoop;
                } else {
                    //else if the hand is just a high card 
                    handClasses.add(i, 1);

                    int currentFiveCardSize = fiveCardHands.get(i).inDeck.size();
                    for (int t = 0; t < 5 - currentFiveCardSize; t++) { //until the five card had is five cards full, add kickers there are no additional cards in flaggedCards
                        fiveCardHands.get(i).add(sevenCardHands.get(i).inDeck.get(t));//add the highest ranking card in the 7 card hand as a kicker
                    }

                    Integer[] handValue = {new Integer(fiveCardHands.get(i).inDeck.get(0).rankValue)}; //the value of the hand is the rank of the pair
                    handValues.add(i, handValue);
                    continue iLoop;
                }
            }
        }
    }

    public void compareHands() {
        int winningIndex = 0;
        int splittingIndex = 0;
        boolean isSplit = false;

        iLoop:
        for (int i = 1; i < table.players.size(); i++) {
            if (handClasses.get(i).intValue() > handClasses.get(winningIndex).intValue()) {
                //if the current hand is of a higher class than the winning one
                winningIndex = i;
                continue iLoop;
            } else if (handClasses.get(i).intValue() == handClasses.get(winningIndex).intValue()) {
                //if the current hand and the winning one have the same class
                nLoop:
                for (int n = 0; n < handValues.get(i).length; n++) {
                    if (handValues.get(i)[n].intValue() > handValues.get(winningIndex)[n].intValue()) {
                        //if the value of the current hand is better than 
                        winningIndex = i;
                        continue iLoop;
                    } else if (handValues.get(i)[n].intValue() == handValues.get(winningIndex)[n].intValue()) {
                        continue nLoop;
                    } else {
                        continue iLoop; //else the winner has the better hand
                    }
                }

                //if you reach this loop the two hands have the same values, start comparing kickers
                mLoop:
                for (int m = 0; m < fiveCardHands.get(i).inDeck.size(); m++) {
                    if (fiveCardHands.get(i).inDeck.get(m).rankValue > fiveCardHands.get(winningIndex).inDeck.get(m).rankValue) {
                        //if the current hands mth kicker is greater in rank than the winning hands
                        winningIndex = i;
                        continue iLoop;
                    } else if (fiveCardHands.get(i).inDeck.get(m).rankValue == fiveCardHands.get(winningIndex).inDeck.get(m).rankValue) {
                        //if the two mth kickers are equal, keep checking
                        continue mLoop;
                    } else {
                        continue iLoop;
                    }
                }

                //if you reach here, the pot is split
                isSplit = true;
                splittingIndex = winningIndex;
                continue iLoop;
            } else {
                continue iLoop;
            }
        }

        if (isSplit && splittingIndex == winningIndex) {
            //if the pot is genuinely split
            splitOuts.add(table.board.get(4)); //add the river card to split outs   
        } else {
            //else there is a clear winner
            playerOuts.get(winningIndex).add(table.board.get(4));
        }
    }

    void calculateOdds() {
        double total = 0; //stores the total number of outs that have been calculated
        for (Deck d: playerOuts) {
            total += d.inDeck.size();   
        }
        total += splitOuts.inDeck.size();

        for (int i = 0; i < table.players.size(); i++) {
            playerOdds.add(i, new Double(((double) playerOuts.get(i).inDeck.size() / total) * 100));   
        }
        splitOdds = ((double) splitOuts.inDeck.size() / total) * 100;
    }

    void printOdds() {
        System.out.printf("%nFinal Odds: %n%n"); //upper buffer
        for (int i = 0; i < table.players.size(); i++) {
            System.out.printf("%s: %6.2f%% %n", table.players.get(i).username, playerOdds.get(i).doubleValue()); // "Brent: 56.44%" 
            if (playerOdds.get(i).doubleValue() < 50.0 && table.cardsOnBoard >= 4) { //if the player is losing/lost print their outs
                System.out.printf("%s Outs: ", table.players.get(i).username);
                if (playerOuts.get(i).inDeck.isEmpty()) { //if they have none
                    System.out.printf("Drawing Dead ");
                } else {
                    playerOuts.get(i).absSort();
                    for (Card c: playerOuts.get(i).inDeck) {
                        System.out.printf("%s ", c.shortName);
                    }
                }
                System.out.println("");
            }
        }

        System.out.printf("%nSplit: %6.2f%% %n", splitOdds);
        if (splitOdds < 50.0 && table.cardsOnBoard >= 4) { //if the player is losing/lost print their outs
            System.out.printf("Split Outs: ");
            if (splitOuts.inDeck.isEmpty()) { //if they have none
                System.out.printf("Drawing Dead ");
            } else {
                splitOuts.absSort();
                for (Card c: splitOuts.inDeck) {
                    System.out.printf("%s ", c.shortName);
                }
            }
            System.out.println("");
        }
    }

    public static void testMain() {
        Table table = new Table();
        OddsCalculator o = new OddsCalculator(table);
        o.appraiseHands();
        o.compareHands();
        o.calculateOdds();
        o.printOdds();
        for (int i = 0; i < table.players.size(); i++) {
            System.out.printf("%n Seven: %n");
            o.sevenCardHands.get(i).printComponents();
            System.out.printf("%n Five: %n");
            o.fiveCardHands.get(i).printComponents();
            System.out.printf("%nClass: %d%n", o.handClasses.get(i));
            System.out.printf("Value: ");
            for (Integer num: o.handValues.get(i)) {
                System.out.println(num.intValue() + "");
            }

        }
    }  

    public static void main() {
        Table externalTable = new Table();
        switch (externalTable.cardsOnBoard) {
            case 3: //if there are 3 board cards
            OddsCalculator o1 = new OddsCalculator(externalTable);
            o1.table.board.inDeck.add(3, new Card(Card.ACE, Card.SPADES)); //add in dummy cards for the following for loops
            o1.table.board.inDeck.add(4, new Card(Card.ACE, Card.SPADES));
            for (int i = 0; i < o1.table.deck.inDeck.size() - 1; i++) { //for every combo of two cards in the deck
                o1.table.board.inDeck.remove(3);
                o1.table.board.inDeck.add(3, o1.table.deck.inDeck.get(i));
                for (int j = i + 1; j < o1.table.deck.inDeck.size(); j++) {
                    o1.table.board.inDeck.remove(4);
                    o1.table.board.inDeck.add(4, o1.table.deck.inDeck.get(j));

                    o1.repopulateSevenCardHands();
                    o1.appraiseHands(); 
                    o1.compareHands();
                }
            }

            o1.calculateOdds();
            o1.printOdds();
            
            System.out.printf("%nThe Turn? %n");
            externalTable.addCard();

            case 4: //if the turn is down
            OddsCalculator o2 = new OddsCalculator(externalTable);             
            o2.table.board.inDeck.add(4, new Card(Card.ACE, Card.SPADES)); //add in dummy cards for the following for loops
            for (int j = 0; j < o2.table.deck.inDeck.size(); j++) {
                o2.table.board.inDeck.remove(4);
                o2.table.board.inDeck.add(4, o2.table.deck.inDeck.get(j));

                o2.repopulateSevenCardHands();
                o2.appraiseHands(); 
                o2.compareHands();
            }
            
            o2.calculateOdds();
            o2.printOdds();

            System.out.printf("%nThe River? %n");
            externalTable.addCard();
            
            case 5: //if the river is 
            OddsCalculator o3 = new OddsCalculator(externalTable); 
            o3.appraiseHands(); 
            o3.compareHands();
            o3.calculateOdds();
            o3.printOdds();
        }
    }
}   
