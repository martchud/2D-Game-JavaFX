package classes.barrier;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class Wall extends Barrier {
    public Wall(int x, int y) {
        super(x, y);
    }

    //@Override
    public void render(GraphicsContext load) {
        load.drawImage(getWall(), this.getPositionX(), this.getPositionY());
    }

    private Image getWall() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Objects/wall.jpg")));
    }
}