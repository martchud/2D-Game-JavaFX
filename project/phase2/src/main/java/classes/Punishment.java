package classes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class Punishment extends GameComponent {

    //constructor
    public Punishment(int positionX, int positionY) {
        super(positionX, positionY);
    }

    //@Override
    public void render(GraphicsContext load) {
        load.drawImage(getImage(), this.getPositionX(), this.getPositionY());
    }
    
    private Image getImage() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Objects/bomb.png")));
    }
}
