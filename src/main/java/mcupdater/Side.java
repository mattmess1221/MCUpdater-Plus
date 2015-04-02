package mcupdater;

public class Side {

    public static enum Sides {
        CLIENT,
        SERVER;
    }

    private static Sides side;

    static void setSide(Sides sides) {
        side = sides;
    }

    public static Sides getSide() {
        return side;
    }
}
