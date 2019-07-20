import java.util.*;

class TestDeck {
    public static void main(String[] args) {
        Deck d = new Deck(true);
        Collections.sort(d.inDeck, new SortBySuit());
        d.printComponents();
    }
}