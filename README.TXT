------------------------------------------------------------------------
This is the project README file. Here, you should describe your project.
Tell the reader (someone who does not know anything about this project)
all he/she needs to know. The comments should usually include at least:
------------------------------------------------------------------------

PROJECT TITLE:
PURPOSE OF PROJECT:
VERSION or DATE:
HOW TO START THIS PROJECT:
AUTHORS:
USER INSTRUCTIONS:

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