import java.util.*;

class Table {
    ArrayList<Player> players;
    Deck board; //the cards on the tableau
    int cardsOnBoard; //the number of cards on the board, used mainly by the calculator
    
    public Table() { 
        //creates an empty list of players and an empty board
        players = new ArrayList<Player>();
        board = new Deck(false);
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
        
        //prints the finished result
        printComponents();
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
    }
    
    public void addCard() {
        if (cardsOnBoard < 5) {
            cardsOnBoard++;
            board.add(new Card());
        }
    }
    
    public static void main(String[] args) {
        Table t = new Table();
    }
}   
