import java.util.*;

class Deck {
    ArrayList<Card> inDeck = new ArrayList();
    //Creates full deck
    Deck() {
        for (byte i = 1; i < 5; i += 1) {
            for (byte j = 1; j < 14; j += 1) {
                inDeck.add(new Card(j, i));
            }
        }
    }
    //Allow to decide to make full or empty deck; if full it auto-shuffles it
    Deck(boolean buildDeck){
        if (buildDeck == true){
            for (byte i = 1; i < 5; i += 1) {
                for (byte j = 1; j < 14; j += 1) {
                    inDeck.add(new Card(j, i));
                }
            }
            shuffle();
        } else {
            //Creates empty deck.
        }
    }
    //Reverts it to a normal 52 card deck
    void resetDeck() {
        inDeck.clear(); //empties the deck
        for (byte i = 1; i < 5; i += 1) {
            for (byte j = 1; j < 14; j += 1) {
                inDeck.add(new Card(j, i));
            }
        }
    }
    //Gets the "top" card and removes it from the deck
    Card draw() {
        try {
            //Assigns it to a variable so it can delete the card from the deck
            Card topCard = inDeck.get(0);
            inDeck.remove(0);
            return topCard;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Sorry chief, no can do. The deck is empty.");
            return null;
        }
    }
    //Same as last one but allows to select the position of the card
    Card draw(int drawPos) {
        if (drawPos < inDeck.size() && drawPos >= 0) {
            Card topCard = inDeck.get(drawPos);
            inDeck.remove(drawPos);
            return topCard;
        } else {
            System.out.println("Error: drawPos is outside deck size. You get nothing in return.");
            return null;
        }
    }
    //Returns and removes a card in a random position
    Card drawRand(){
        Random rand = new Random();
        int cardPos = rand.nextInt(inDeck.size())-1; //Returns a number 0-51
        Card transferVar = inDeck.get(cardPos);
        inDeck.remove(cardPos);
        return transferVar;
    }

    int count() {
        return inDeck.size();
    }   
    //Checks for a specific card in the deck, returns the index of the card, -1 if not in
    int check(Card templateCard){
        int templateRank = templateCard.rankValue;
        int templateSuit = templateCard.suitValue;
        for (int i = 0; i < inDeck.size(); i++){
            //Looks through each item in the deck, if one matches returns that cards index
            if (templateRank == inDeck.get(i).rankValue && templateSuit == inDeck.get(i).suitValue){                
                return i;
            }
        }
        return -1; //if nothing matches
    }
    //Same as previous but uses ints as opposed to a card object
    int check(int rank, int suit){
        for (int i = 0; i < inDeck.size(); i++){
            if (rank == inDeck.get(i).rankValue && suit == inDeck.get(i).suitValue){
                return i;
            }
        }
        return -1;
    }
    //Checks for a suit in a deck
    boolean checkSuit(int suitCheck){
        boolean cardIn = false;
        for (Card currentCard: inDeck){
            if (suitCheck == currentCard.suitValue){
                cardIn = true;
            }
        }
        return cardIn;
    }
    //Checks for a rank in a deck
    boolean checkRank(int rankCheck){
        boolean cardIn = false;
        for (Card currentCard: inDeck){
            if (rankCheck == currentCard.rankValue){
                cardIn = true;
            }
        }
        return cardIn;
    }
    //Removes the "top" card without returning it like draw() does
    void discard() {
        try {
            inDeck.remove(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Sorry chief, no can do. The deck is empty.");
        }
    }
    //Same but allowes to pick where the card is discarded from
    void discard(int discardPos){
        if (discardPos < inDeck.size() && discardPos >= 0) {
            inDeck.remove(discardPos);
        } else {
           System.out.println("Error: discardPos is outside deck size. You can't discard what ain't there."); 
        }
    }
    //removes a specified card from the deck, returns true if the list was changed as a result of the call
    void remove(Card card) {
         int indexToRemove = check(card);
         if (indexToRemove < 0) {
             System.out.println("The card you wanted to remove is not in this deck.");
         } else {
             inDeck.remove(indexToRemove);
         }
    }
    //Shuffles the deck
    void shuffle() {
        Collections.shuffle(inDeck);
    }
    //Adds a card without taking it from somewhere else
    void add(Card newCard){
        inDeck.add(newCard);
    }
    
    Card get(int index) throws IndexOutOfBoundsException{
        if (index < 0 || index >= inDeck.size()) {
            throw new IndexOutOfBoundsException();
        } else {
            return inDeck.get(index);
        }
    }
    //Empties the deck
    void clear(){
        inDeck.clear();
    }
    //splits the deck in half and returns the second deck
    Deck cut() {
        Deck splitDeck = new Deck(false); //Creates a new empty deck
        //Takes every other card out of this deck and adds it to the new one
        for (int i = 0; i < inDeck.size(); i++) {
            splitDeck.add(draw(i));
        }
        return splitDeck;
    }
    //Prints out every card in the deck; more for testing than using
    void printComponents() {
        for (int k = 0; k < inDeck.size(); k++){
            System.out.println(inDeck.get(k).cardName + ", " + inDeck.get(k).shortName);
        }
    }
    //sorts the deck absolutely, any two deck with the same cards will be sorted the same no matter the order
    void absSort() {
        Collections.sort(inDeck, new SortBySuit());
        Collections.sort(inDeck, new SortByRank());        
    }
    //Combines two decks, deleting the old one
    void merge(Deck newDeck){
        for (Card currentCard: newDeck.inDeck){
            inDeck.add(currentCard);
        }
        newDeck.inDeck.clear();
    }
}