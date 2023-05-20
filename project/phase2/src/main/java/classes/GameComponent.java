package classes;

import javafx.geometry.Rectangle2D;

public class GameComponent {
   protected int positionX;
   protected int positionY;
   
   //constructor
    public GameComponent(int x, int y) {
        this.positionX = x;
        this.positionY = y;
    }

    //getter
    public int getPositionX() {
        return this.positionX;
    }
    public int getPositionY() {
        return this.positionY;
    }

    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX, positionY, 40, 40);
    }

    public boolean intersects(GameComponent newGC) {
        return getBoundary().intersects(newGC.getBoundary());
    }
}
