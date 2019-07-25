import java.util.*;

class Table {
    ArrayList<Player> players;
    Deck deck; //the deck the cards are drawn from
    Deck board; //the cards on the tableau
    int cardsOnBoard; //the number of cards on the board, used mainly by the calculator
    
    public Table() { 
        //creates an empty list of players and an empty board
        players = new ArrayList<Player>();
        board = new Deck(false);
        deck = new Deck();
        cardsOnBoard = 0;
        //asks the user a series of inputs to build the board
        
        //first determine the number of players
        int numOfPlayers;//stores the user's input
        System.out.printf("How many players are in the hand? [Enter an int] %n");
        Scanner input = new Scanner(System.in);
        numOfPlayers = input.nextInt();
        input.nextLine();//used to throw out the rest of the line
        for (int i = 0; i < numOfPlayers; i++) {
            players.add(new Player());
            //asks for the player's info
            System.out.printf("What is Player %d's name?%n", i+1);
            players.get(i).setUsername(input.nextLine()); //set's that player's name to the next input
            System.out.println("Their hand? [Two consecutive inputs]");
            players.get(i).hand.add(new Card()); //prompts the user to input a card and adds it to that player's hand
            players.get(i).hand.add(new Card());            
        }  
        
        //next fills in the cards on the board
        System.out.println("How many cards are on the board?");
        cardsOnBoard = input.nextInt();
        input.nextLine();
        for (int i = 0; i < cardsOnBoard; i++) {//asks the user to input the board cards
            if (i == 0) {
                System.out.printf("The board cards? [%d consecutive inputs]%n", cardsOnBoard);
            }
            board.add(new Card());
        }
        
        //removes the named cards from deck
        for (Player p: players) { //for each player in the hand
            for (Card c: p.hand.inDeck) { //for each card in their pocket
                deck.remove(c); //remove that card from the table's deck
            }
        }
        for (Card c: board.inDeck) { //for each card on the board
            deck.remove(c);
        }
        //prints the finished result
        printComponents();
    }
    
    public Table(Table template) { //essentially used to hardcode a deep copy of a pre-built Table object
        players = new ArrayList<Player>(); //players are shallow copied 
        board = new Deck(false); //board and deck are deep copied
        deck = new Deck(false);
        cardsOnBoard = template.cardsOnBoard;
        
        for (Player p: template.players) {
            this.players.add(p);
        }
        for (Card c: template.board.inDeck) {
            this.board.add(new Card(c.rankValue, c.suitValue));
        }
        for (Card c: template.deck.inDeck) {
            this.deck.add(new Card(c.rankValue, c.suitValue));
        }
    }
    
    public void printComponents() { //Prints the player's cards along with the board
        System.out.printf("Done %n %n"); //Top buffer
        for (Player p: players) { //prints players' hands
            System.out.printf("%s: %s %s%n", p.username, p.hand.get(0).shortName, p.hand.get(1).shortName); 
        }
        System.out.printf("Board: ");
        for (int i = 0; i < cardsOnBoard; i++) { //[rints the board cards
            System.out.printf("%s ", board.get(i).shortName);
        }        
        System.out.printf("%n%n"); //bottom buffer
        //deck.printComponents();
        //System.out.printf("%nNo. of cards in deck: %d%n", deck.inDeck.size());
    }
    
    public void addCard() {
        if (cardsOnBoard < 5) {
            cardsOnBoard++;
            Card cardToAdd = new Card();
            board.add(cardToAdd);
            deck.remove(cardToAdd);
            printComponents();
        } else {
            System.out.println("The river card is already on the table.");
        }        
    }
    
    public static void main(String[] args) {
        Table t = new Table();
        Table tAlt = new Table(t);
        tAlt.addCard();
        System.out.print("");
    }
}   
