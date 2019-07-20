import java.util.*;

class SortBySuit implements Comparator<Card> {
    public int compare(Card a, Card b) { 
        return a.suitValue - b.suitValue;
    }
}