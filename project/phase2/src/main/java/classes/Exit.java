package classes;

import game.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.Objects;

public class Exit extends GameComponent{

    //constructor
    public Exit(int positionX, int positionY) {
        super(positionX, positionY);
    }

    //@Override
    public void render(GraphicsContext load) {
        load.drawImage(getImage(), this.getPositionX(), this.getPositionY());
    }

    public void update() { winConditionCheck(); }

    private void winConditionCheck() {
         if (Main.thief.positionX == 19*40 && Main.thief.positionY == 3*40 && Main.rewards.size() == 0) {
             Main.setIsRunning(2);
         }
    }

    private Image getImage() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Objects/door.png")));
    }
}
