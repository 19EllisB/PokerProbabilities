
class Player {
    Deck hand;
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
    //resets the player's hand to empty
    void reset() {
        hand.clear();
    }
}