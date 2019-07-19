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
    
    OddsCalculator() {
        table = new Table(); //allows the user to input the table
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
        for (int i = 0; i < sevenCardHands.size(); i++) { //for each hand in 7CardHands
            if (sevenCardHands.get(i).inDeck.size() != 7) { //if the size of the hand != 7
                System.out.println("I can't appraise a hand of not 7 cards :( ");
            } else {
                //resume work here
            }
        }
    }
}   