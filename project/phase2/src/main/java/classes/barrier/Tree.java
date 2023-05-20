package classes.barrier;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Objects;

public class Tree extends Barrier {
    public Tree(int x, int y) {
        super(x, y);
    }

    //@Override
    public void render(GraphicsContext load) {
        load.drawImage(getTree(), this.getPositionX(), this.getPositionY());
    }

    private Image getTree() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Objects/tree.png")));
    }

}