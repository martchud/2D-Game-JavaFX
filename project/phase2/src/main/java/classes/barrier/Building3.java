package classes.barrier;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class Building3 extends Barrier {
    public Building3(int x, int y) {
        super(x, y);
    }

    //@Override
    public void render(GraphicsContext load) {
        load.drawImage(getBuilding(), this.getPositionX(), this.getPositionY());
    }

    private Image getBuilding() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Objects/building4.png")));
    }

}