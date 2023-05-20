package classes;

public class ScoreUI {

    int cash;

    public ScoreUI(int cash) {
        this.cash = cash;
    }

    public void addBy(int integer) {
        cash = cash + integer;
    }

    public void subtractBy(int integer) {
        cash = cash - integer;
    }

    public int getCash() {
        return cash;
    }
}