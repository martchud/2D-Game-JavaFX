package game;

import classes.*;
import classes.barrier.*;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Main extends Application
{
    private Canvas canvas;
    private GraphicsContext gc;
    public static Exit exit;
    public static int isRunning = 0;
    public static boolean checkBonus = true;
    public static final double FPS = 24;
    public static final int WIDTH = 40;
    public static final int HEIGHT = 40;
    public static final int ROWS = 15;
    public static final int COLS = 20;

    public static Player thief;
    public static final ArrayList<Enemy> enemies = new ArrayList<>();
    public static final ArrayList<Punishment> bombs = new ArrayList<>();
    public static final ArrayList<Reward> rewards = new ArrayList<>();
    public static final ArrayList<Barrier> barriers = new ArrayList<>();
    public static final ArrayList<Bonus> bonuses = new ArrayList<>();

    long startTime = System.currentTimeMillis();
    public static long elapsedTime;
    long elapsedSeconds;
    long secondsDisplay;
    long elapsedMinutes;
    int respawnBonus = ThreadLocalRandom.current().nextInt(10, 20);
    int respawnBonus1 = ThreadLocalRandom.current().nextInt(40, 50);

    public static void setIsRunning(int isRunning) {
        Main.isRunning = isRunning;
    }

    public static int[][] theMap = new int[][]{
            //  0 = wall  1 = building1 2 = building2   3 = building3   4 = tree
            //  5 = path  6 = reward    7 = bomb    8 = enemy  9 = thief  10 = exit 11 = bonus
            //  0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //0
            {0, 9, 5, 5, 5, 5, 5, 1, 5, 5, 5, 5, 5, 3, 5, 5, 5, 1, 5, 0}, //1
            {0, 5, 5, 5, 5, 5, 5, 4, 4, 5, 2, 2, 5, 5, 7, 5, 5, 2, 5, 0}, //2
            {0, 2, 5, 2, 2, 2, 5, 1, 5, 5, 5, 5, 5, 3, 11, 5, 5, 5, 5, 10},//3
            {0, 11, 5, 5, 5, 5, 5, 4, 5, 5, 1, 4, 1, 4, 5, 1, 5, 5, 5, 0}, //4
            {0, 5, 8, 5, 5, 2, 5, 1, 8, 5, 5, 5, 5, 5, 5, 4, 5, 5, 5, 0}, //5
            {0, 5, 4, 4, 5, 2, 5, 5, 6, 5, 5, 5, 5, 5, 7, 5, 5, 5, 5, 0}, //6
            {0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 4, 2, 4, 2, 4, 3, 5, 5, 4, 0}, //7
            {0, 5, 3, 3, 1, 5, 3, 3, 5, 7, 3, 5, 5, 2, 5, 1, 5, 5, 5, 0}, //8
            {0, 5, 5, 7, 5, 11, 5, 5, 5, 5, 4, 5, 5, 5, 5, 6, 5, 5, 5, 0}, //9
            {0, 5, 3, 5, 5, 5, 5, 6, 5, 5, 3, 5, 5, 3, 5, 5, 5, 5, 5, 0}, //10
            {0, 5, 1, 1, 5, 1, 1, 5, 1, 5, 2, 5, 2, 2, 2, 2, 5, 5, 1, 0}, //11
            {0, 5, 5, 5, 5, 4, 5, 5, 5, 5, 5, 5, 5, 5, 11, 5, 5, 8, 5, 0}, //12
            {0, 5, 5, 5, 5, 3, 5, 5, 5, 5, 3, 5, 5, 7, 5, 5, 5, 5, 5, 0}, //13
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}  //14
    };

    private void addPunishment(Punishment bomb) {
        bombs.add(bomb);
    }
    private void addReward(Reward reward) {
        rewards.add(reward);
    }
    private void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }
    private void addBarrier(Barrier barrier) {
        barriers.add(barrier);
    }
    private void addBonus(Bonus bonus) {
        bonuses.add(bonus);
    }

    public static void main ( String[] args )
    {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Thief Escape");

        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();
        StackPane root = new StackPane();

        root.getChildren().add(canvas);

        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/background.jpeg")));

        BackgroundImage bgImg = new BackgroundImage(img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        Background bGround = new Background(bgImg);
        root.setBackground(bGround);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Map construction
        for(int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (theMap[i][j] == 0) {
                    addBarrier(new Wall(j * WIDTH, i * HEIGHT));
                }
                if (theMap[i][j] == 1) {
                    addBarrier(new Building1(j * WIDTH, i * HEIGHT));
                }
                if (theMap[i][j] == 2) {
                    addBarrier(new Building2(j * WIDTH, i * HEIGHT));
                }
                if (theMap[i][j] == 3) {
                    addBarrier(new Building3(j * WIDTH, i * HEIGHT));
                }
                if (theMap[i][j] == 4) {
                    addBarrier(new Tree(j * WIDTH, i * HEIGHT));
                }
                if (theMap[i][j] == 6) {
                    addReward(new Reward(j * WIDTH, i * HEIGHT));
                }
                if (theMap[i][j] == 7) {
                    addPunishment(new Punishment(j * WIDTH, i * HEIGHT));
                }
                if (theMap[i][j] == 8) {
                    addEnemy(new Enemy(j * WIDTH, i * HEIGHT));
                }
                if (theMap[i][j] == 9){
                    thief = new Player(j * WIDTH, i * HEIGHT);
                }
                if (theMap[i][j] == 10) {
                    exit = new Exit(j * WIDTH, i * HEIGHT);
                    addBarrier(new Wall((j + 1) * WIDTH, i * HEIGHT));
                }
                if (theMap[i][j] == 11) {
                    addBonus(new Bonus(j * WIDTH, i * HEIGHT));
                }
            }
        }

        AnimationTimer timer = new AnimationTimer() {
            final double ns = 1000000000.0 / FPS;
            long lastTime = System.nanoTime();
            double delta = 0;
            int updates = 0;

            @Override
            public void handle(long current) {
                delta += (current - lastTime) / ns;
                lastTime = current;
                while (delta >= 1) {
                    update();
                    try {
                        render(primaryStage);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updates++;
                    delta--;

                    Font theFont = Font.font( "Helvetica", FontWeight.NORMAL, 18 );

                    String cashScoreText = "Score: " + thief.getCurrentCash();

                    gc.fillText(cashScoreText, 700, 27);
                    gc.strokeText(cashScoreText, 700, 27);
                    gc.setFont( theFont );

                    elapsedTime = System.currentTimeMillis() - startTime;
                    elapsedSeconds = elapsedTime / 1000;
                    secondsDisplay = elapsedSeconds % 60;
                    elapsedMinutes = elapsedSeconds / 60;

                    String timer;
                    if(secondsDisplay < 10){
                         timer = "Time: " + elapsedMinutes+ ":0" + secondsDisplay;
                    }
                    else{
                        timer = "Time: " + elapsedMinutes+ ":" + secondsDisplay;
                    }


                    gc.fillText(timer, 40, 28);
                    gc.strokeText(timer, 40, 28);
                    gc.setFont( theFont );

                    if(isRunning != 0) {
                        stop();
                    }
                }
            }
        };
            thief.control(scene);
            timer.start();
    }
    public void update() {
        thief.update();
        for (Enemy temp : enemies) {
            temp.update(thief.getPositionX(), thief.getPositionY());
        }
        exit.update();
    }


    public void render(Stage stage) throws InterruptedException {
        if (isRunning == 0) {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            for (Punishment tempPunishment : bombs) {
                tempPunishment.render(gc);
            }
            for (Reward tempReward : rewards) {
                tempReward.render(gc);
            }
            if(secondsDisplay < respawnBonus){
                checkBonus = true;
                for (Bonus tempBonus : bonuses) {
                    tempBonus.render(gc);
                }
            }
            else if (respawnBonus + 10 < secondsDisplay && secondsDisplay < respawnBonus1) {
                checkBonus = true;
                for (Bonus tempBonus : bonuses) {
                    tempBonus.render(gc);
                }
            } else {
                checkBonus = false;
            }
            for (Enemy tempEnemy : enemies) {
                tempEnemy.render(gc);
            }
            for (Barrier barrier : barriers) {
                if (barrier instanceof Building1) {
                    Building1 tempWall = (Building1) barrier;
                    tempWall.render(gc);
                } else if (barrier instanceof Building2) {
                    Building2 tempWall = (Building2) barrier;
                    tempWall.render(gc);
                } else if (barrier instanceof Building3) {
                    Building3 tempWall = (Building3) barrier;
                    tempWall.render(gc);
                } else if (barrier instanceof Tree) {
                    Tree tempWall = (Tree) barrier;
                    tempWall.render(gc);
                } else {
                    Wall tempWall = (Wall) barrier;
                    tempWall.render(gc);
                }
                exit.render(gc);
                thief.render(gc);
            }
        } else if (isRunning == 1) {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            stage.close();
            Stage loseStage = new Stage();
            StackPane root = new StackPane();
            Scene scene = new Scene(root, 800, 600);
            Image lose = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/lose.png")));
            BackgroundImage bImg = new BackgroundImage(lose,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT);
            Background bGround = new Background(bImg);
            root.setBackground(bGround);
            loseStage.setScene(scene);
            loseStage.show();
            loseStage.centerOnScreen();
        } else {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            stage.close();
            Stage winStage = new Stage();
            StackPane root = new StackPane();
            Scene scene = new Scene(root, 800, 600);
            Image win = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/game/win.png")));
            BackgroundImage bImg = new BackgroundImage(win,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT);
            Background bGround = new Background(bImg);
            root.setBackground(bGround);
            winStage.setScene(scene);
            winStage.show();
            winStage.centerOnScreen();
        }
    }
}
