import java.util.*;

class TestDeck {
    public static void main(String[] args) {
        Deck d = new Deck(true);
        Collections.sort(d.inDeck, new SortBySuit());
        d.printComponents();
    }
    
    public static void testSort() {
        Deck d1 = new Deck(true);
        Deck d2 = new Deck(true);
        
        Collections.sort(d1.inDeck, new SortBySuit());
        Collections.sort(d2.inDeck, new SortBySuit());
        
        Collections.sort(d1.inDeck, new SortByRank());
        Collections.sort(d2.inDeck, new SortByRank());
        
        d1.printComponents();
        System.out.println("");
        d2.printComponents();
    }
}