import java.util.Scanner;


public class Card {

    // Kinds of ranks
    public final static int ACE   = 14; //used to make ace high [1+13]
    public final static int TWO   = 2;
    public final static int THREE = 3;
    public final static int FOUR  = 4;
    public final static int FIVE  = 5;
    public final static int SIX   = 6;
    public final static int SEVEN = 7;
    public final static int EIGHT = 8;
    public final static int NINE  = 9;
    public final static int TEN   = 10;
    public final static int JACK  = 11;
    public final static int QUEEN = 12;
    public final static int KING  = 13;

    // Here you need to define the inds of suits. They are
    // DIAMONDS, CLUBS, HEARTS, SPADES (in order of value, starting at 1)

    public final static int DIAMONDS = 1;
    public final static int CLUBS = 2;
    public final static int HEARTS = 3;
    public final static int SPADES = 4;

    public String cardName;
    public String rankString;
    public String suitString;
    public String shortName;

    public int rankValue;
    public int suitValue;

    // Here is the if-then-else approach for returning the string
    // as a rank
    public static String rankToString(int rank) {
        if (rank == ACE) {
            return "Ace";
        } else if (rank == TWO) {
            return "Two";
        } else if (rank == THREE) {
            return "Three";
        } else if (rank == FOUR) {
            return "Four";
        } else if (rank == FIVE) {
            return "Five";
        } else if (rank == SIX) {
            return "Six";
        } else if (rank == SEVEN) {
            return "Seven";
        } else if (rank == EIGHT) {
            return "Eight";
        } else if (rank == NINE) {
            return "Nine";
        } else if (rank == TEN) {
            return "Ten";
        } else if (rank == JACK) {
            return "Jack";
        } else if (rank == QUEEN) {
            return "Queen";
        } else if (rank == KING) {
            return "King";
        } else {
            //Handle an illegal argument.  There are generally two
            //ways to handle invalid arguments, throwing an exception
            //(see the section on Handling Exceptions) or return null
            return null;
        }    
    }
    
    public static String rankToShortString(int rank) {
        switch (rank) {
            case ACE: 
            return "A";
            case 2: 
            return "2";
            case 3: 
            return "3";
            case 4: 
            return "4";
            case 5: 
            return "5";
            case 6: 
            return "6";
            case 7: 
            return "7";
            case 8: 
            return "8";
            case 9: 
            return "9";
            case 10: 
            return "T";
            case JACK: 
            return "J";
            case QUEEN: 
            return "Q";
            case KING: 
            return "K";
            default: 
            return "";
        }
    }

    public static String suitToString(int newSuit) {
        switch (newSuit) {
            case DIAMONDS:
            return "Diamonds";
            case CLUBS:
            return "Clubs";
            case HEARTS:
            return "Hearts";
            case SPADES:
            return "Spades";
            default:
            return null;
        }
    }

    public static String suitToShortString(int newSuit) {
        switch (newSuit) {
            case DIAMONDS:
            return "D";
            case CLUBS:
            return "C";
            case HEARTS:
            return "H";
            case SPADES:
            return "S";
            default:
            return null;
        }
    }
    
    Card() throws RuntimeException { //uses user input to create a card, imperative that the user gets it right, no safeguards against failure
        String choice; //stores the user's input
        Scanner input = new Scanner(System.in);
        /*
         * When inputing a card's info, use a string in the following format: RS 
         *      where R is the card's rank (T is used in place of 10) and S is the cards suit
         *      C for clubs
         *      D for diamonds
         *      H for hearts
         *      S for spades
         */        
        System.out.println("[Instructions for inputing values in Card class]"); 
        choice = input.nextLine();
        for (char c: choice.toCharArray()) {
           switch (c + "") {//converts the char to a string which can be used as a case
               case "A":
               case "a":
                    rankValue = ACE;
                    break;
               case "2":
                    rankValue = 2;
                    break;
               case "3":
                    rankValue = 3;
                    break;
               case "4":
                    rankValue = 4;
                    break;
               case "5":
                    rankValue = 5;
                    break;
               case "6":
                    rankValue = 6;
                    break;
               case "7":
                    rankValue = 7;
                    break;
               case "8":
                    rankValue = 8;
                    break;
               case "9":
                    rankValue = 9;
                    break;
               case "T":
               case "t":
                    rankValue = 10;
                    break;
               case "J":
               case "j":
                    rankValue = 11;
                    break;
               case "Q":
               case "q":
                    rankValue = 12;
                    break;
               case "K":
               case "k":
                    rankValue = 13;
                    break;
               case "D":
               case "d":
                    suitValue = DIAMONDS;
                    break;
               case "H":
               case "h":
                    suitValue = HEARTS;
                    break;
               case "C":
               case "c":
                    suitValue = CLUBS;
                    break;
               case "S":
               case "s":
                    suitValue = SPADES;
                    break;
               default: 
                    throw new RuntimeException();
           }
        }
        
        rankString = rankToString(rankValue);
        suitString = suitToString(suitValue);
        cardName = "The " + rankString + " of " + suitString;
        shortName = "[" +rankToShortString(rankValue) + "" + suitToShortString(suitValue) + "]";
    }
    
    Card (int rank, int suit) throws RuntimeException{
        if (rank > 14 || rank < 1 || suit > 4 || suit < 1) {
            throw new RuntimeException();
        }
        else {
            if (rank == 1) { //bad spaget, but I need this to not break Deck
                rank = 14;
            }
            rankValue = rank;
            suitValue  = suit;
            rankString = rankToString(rank);
            suitString = suitToString(suit);
            cardName = "The " + rankString + " of " + suitString;
            shortName = "[" +rankToShortString(rank) + "" + suitToShortString(suit) + "]";
        }
    }
}
