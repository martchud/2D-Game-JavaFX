package classes;

import classes.barrier.Barrier;
import game.Main;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.Objects;

public class Player extends GameComponent {

    int speedX = 0;
    int speedY = 0;
    int accel = 5;
    int currentCash = 0;

    ScoreUI display = new ScoreUI(currentCash);

    //Constructor
    public Player(int positionX, int positionY) {
        super(positionX, positionY);
    }

    //@Override
    public void render(GraphicsContext load) {
        load.drawImage(getImage(), this.getPositionX(), this.getPositionY());
    }

    //Update movement of player
    public void update() {
        statusUpdateCheck();
        collisionCheck();
        positionX += speedX;
        positionY += speedY;
    }

    public int getCurrentCash() {
        return currentCash;
    }

    //Control movement of player using arrow keys
    public void control(Scene scene) {
        scene.setOnKeyPressed(event -> {
                    if (event.getCode().equals(KeyCode.UP)) {
                        speedY -= accel;
                    } else if (event.getCode().equals(KeyCode.DOWN)) {
                        speedY += accel;
                    } else if (event.getCode().equals(KeyCode.LEFT)) {
                        speedX -= accel;
                    } else if (event.getCode().equals(KeyCode.RIGHT)) {
                        speedX += accel;
                    }
                }
        );

        scene.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.UP)) {
                speedY = 0;
            }
            if (event.getCode().equals(KeyCode.DOWN)) {
                speedY = 0;
            }
            if (event.getCode().equals(KeyCode.LEFT)) {
                speedX = 0;
            }
            if (event.getCode().equals(KeyCode.RIGHT)) {
                speedX = 0;
            }
        });
    }

    // Returns boundary
    @Override
    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX+speedX,positionY+speedY,35,35);
    }

    // Handles collision checks of all components in the maze
    private void collisionCheck() {
        ArrayList<Enemy> enemies = Main.enemies;
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemyCheck = enemies.get(i);
            if (intersects(enemyCheck)) {
                enemies.clear();
                Main.setIsRunning(1);
            }
        }

        ArrayList<Punishment> bombs = Main.bombs;
        for (int i = 0; i < bombs.size(); i++) {
            Punishment bombsCheck = bombs.get(i);
            if (intersects(bombsCheck)) {
                Main.bombs.remove(bombsCheck);

                if (currentCash >= 0) {
                    display.subtractBy(20);
                    currentCash = display.getCash();
                    if (currentCash < 0) {
                        Main.setIsRunning(1);
                    }
                }
            }
        }

        ArrayList<Reward> rewards = Main.rewards;
        for (int i = 0; i < rewards.size(); i++) {
            Reward rewardsCheck = rewards.get(i);
            if (intersects(rewardsCheck)) {
                Main.rewards.remove(rewardsCheck);

                if (currentCash >= 0) {
                    display.addBy(10);
                    currentCash = display.getCash();
                }
            }
        }

        ArrayList<Bonus> bonuses = Main.bonuses;
        if (Main.checkBonus) {
            for (int i = 0; i < bonuses.size(); i++) {
                Bonus bonusesCheck = bonuses.get(i);
                if (intersects(bonusesCheck)) {
                    Main.bonuses.remove(bonusesCheck);
//                    diamondCount--;
                    if (currentCash >= 0) {
                        display.addBy(20);
                        currentCash = display.getCash();
                    }
                }
            }
        }

        ArrayList<Barrier> barriers = Main.barriers;
        for (int i = 0; i < barriers.size(); i++) {
            Barrier barriersCheck = barriers.get(i);
            if (getBoundary().intersects(barriersCheck.getBoundary())) {
                speedX = 0;
                speedY = 0;
            }
        }
    }


    private void statusUpdateCheck(){
        if(currentCash<0)
            Main.setIsRunning(0);
    }

    private Image getImage() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Objects/Thief.jpg")));
    }
}