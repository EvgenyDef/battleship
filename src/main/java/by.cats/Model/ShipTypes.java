package by.cats.Model;

public enum ShipTypes {
    ONE_DECK(1),
    TWO_DECK(2),
    THREE_DECK(3),
    FOUR_DECK(4);

    private final int count;

    ShipTypes(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public static ShipTypes fromInt(int i) {
        for (ShipTypes d : ShipTypes.values()) {
            if (d.getCount() == i) {
                return d;
            }
        }
        return null;
    }
}
