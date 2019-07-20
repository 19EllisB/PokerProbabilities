import java.util.*;

class SortByRank implements Comparator<Card> {
    public int compare(Card a, Card b) { //sorts from highest to lowest [Ace high]
        return b.rankValue - a.rankValue;
    }
}