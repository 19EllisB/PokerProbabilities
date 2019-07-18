
class Player {
    Deck hand;
    Deck outs = new Deck(false);
    double odds = 0.0;
    String username;
    
    Player() {
        username = "";
        hand = new Deck(false);        
    }
    
    Player(String username) {
        this.username = username;
        hand = new Deck(false); //sets the new deck to empty
    }
    
    void setUsername(String username) {
        this.username = username;
    }
    
    void setHand(Deck hand) {
        this.hand = hand;
    }
    //resets the player's hand to empty
    void reset() {
        hand.clear();
    }
}