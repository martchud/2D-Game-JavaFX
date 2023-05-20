package classes;

import classes.barrier.Barrier;
import game.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.geometry.Rectangle2D;
import java.util.ArrayList;
import java.util.Objects;

public class Enemy extends GameComponent {
    
    int speedX = 0;
    int speedY = 0;
    int accel = 1;
    
    //constructor
    public Enemy(int positionX, int positionY) {
        super(positionX, positionY);
    }

    //@Override
    public void render(GraphicsContext load) {
        load.drawImage(getImage(), this.getPositionX(), this.getPositionY());
    }

    //Update movement of enemies
    public void update(int targetX, int targetY) {
        xPosMovement(targetX);
        yPosMovement(targetY);
        collisionCheck();
        positionX += speedX;
        positionY += speedY;
    }

    // Returns boundary
    @Override
    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX+speedX,positionY+speedY,40,40);
    }

    //Determines horizontal movement values
    private void xPosMovement(int targetX) {
        if (positionX < targetX) speedX = accel;
        else if (positionX > targetX) speedX = -accel;
        else speedX = 0;
    }

    //Determines vertical movement values
    private void yPosMovement(int targetY) {
        if (positionY < targetY) speedY = accel;
        else if (positionY > targetY) speedY = -accel;
        else speedY = 0;
    }




    private void collisionCheck() {
        ArrayList<Barrier> barriers = Main.barriers;
        for (Barrier barriersCheck : barriers) {
            if (getBoundary().intersects(barriersCheck.getBoundary())) {
                speedX = 0;
                speedY = 0;
            }
        }
    }

    private Image getImage() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Objects/police.png")));
    }
}
