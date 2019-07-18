import java.util.*;

class SortByRank implements Comparator<Card> {
    public int compare(Card a, Card b) {
        return a.rankValue - b.rankValue;
    }
}