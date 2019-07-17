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
        }        
    }
    
    public static void main(String[] args) {
        Table t = new Table();
    }
}   
