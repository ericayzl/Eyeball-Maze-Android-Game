package nz.ac.ara.eyl.eyeballmaze;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import nz.ac.ara.eyl.gamemodel.BlankSquare;
import nz.ac.ara.eyl.gamemodel.Color;
import nz.ac.ara.eyl.gamemodel.Direction;
import nz.ac.ara.eyl.gamemodel.Game;
import nz.ac.ara.eyl.gamemodel.Message;
import nz.ac.ara.eyl.gamemodel.PlayableSquare;
import nz.ac.ara.eyl.gamemodel.Shape;
import nz.ac.ara.eyl.gamemodel.Square;

public class MainActivity extends AppCompatActivity {

    private final static int HEIGHT = 3;
    private final static int WIDTH = 4;
    private final static int MOVE_COUNT_OVER_LIMIT = 20;

    private int[][] imageViewIds = new int[HEIGHT][WIDTH];
    private int[][] bitMapIds = new int[HEIGHT][WIDTH];

    private Game game;

    private boolean gameStarted = false;
    private int moveCount = 0;
    private final static int MAXCOUNT = 7;
    private final static int INITIALGOALS = 1;
    private TextView mShowCount;
    private TextView mGoalCount;

    // Sound
    private SoundPool soundPool;
    private int winningSoundId;
    private int incorrectMoveSoundId;

    // Elapsed Time Features
    private boolean timeThreadStarted;
    private boolean timePaused;
    private int timeElapsed;
    private TextView mShowTime;

    private TextView pausedMsg;
    private Button buttonPause;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assigning ImageView Ids to imageViewIds array
        imageViewIds[0][0] = R.id.imageView_0_0;
        imageViewIds[0][1] = R.id.imageView_0_1;
        imageViewIds[0][2] = R.id.imageView_0_2;
        imageViewIds[0][3] = R.id.imageView_0_3;
        imageViewIds[1][0] = R.id.imageView_1_0;
        imageViewIds[1][1] = R.id.imageView_1_1;
        imageViewIds[1][2] = R.id.imageView_1_2;
        imageViewIds[1][3] = R.id.imageView_1_3;
        imageViewIds[2][0] = R.id.imageView_2_0;
        imageViewIds[2][1] = R.id.imageView_2_1;
        imageViewIds[2][2] = R.id.imageView_2_2;
        imageViewIds[2][3] = R.id.imageView_2_3;

        // Set Onclick listeners for the Image Views
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                ImageView view = findViewById(imageViewIds[row][col]);
                if (view != null) { view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        move(view);
                    }
                });

                }
            }
        }

        mShowCount = (TextView) findViewById(R.id.showCount);
        mShowCount.setVisibility(View.INVISIBLE);

        mGoalCount = (TextView) findViewById(R.id.showGoal);
        mGoalCount.setVisibility(View.INVISIBLE);

        // Sound
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build();
        winningSoundId = soundPool.load(this, R.raw.winning_sound, 1);
        incorrectMoveSoundId = soundPool.load(this, R.raw.incorrect_sound, 1);

        // Elapsed Time Features
        timeThreadStarted = true;
        timePaused = true;
        mShowTime = (TextView) findViewById(R.id.timeText);
        mShowTime.setVisibility(View.INVISIBLE);
        pausedMsg = (TextView) findViewById(R.id.pausedMessage);
        pausedMsg.setVisibility(View.INVISIBLE);
        buttonPause = (Button) findViewById(R.id.buttonPause);
        buttonPause.setVisibility(View.INVISIBLE);


        Thread timeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (timeThreadStarted) {
                    try {
                        Thread.sleep(1000);
                        if (!timePaused) {
                            timeElapsed++;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mShowTime.setText("Time taken: " + timeElapsed + "s");
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        //throw new RuntimeException(e);
                    }
                }
            }
        }
        );
        timeThread.start();
    }

    public void startGame(View view) {

        Button button = findViewById(R.id.buttonStart);
        if (gameStarted) {
            restartGame();
            /*removeCurrentEyeball();
            button.setText("Start");
            gameStarted = false;*/
        } else {
            button.setText("Restart");
            gameStarted = true;
        }

        game = new Game();
        game.addLevel(HEIGHT, WIDTH);

        addSquare(0, 0, Color.RED, Shape.FLOWER, R.drawable.red_flower);
        addSquare(0, 1, Color.YELLOW, Shape.FLOWER, R.drawable.yellow_flower);
        addSquare(0, 2, Color.BLUE, Shape.CROSS, R.drawable.blue_crosss);
        addSquare(0, 3, Color.GREEN, Shape.CROSS, R.drawable.green_crosss);
        addSquare(1, 0, Color.GREEN, Shape.STAR, R.drawable.green_star);
        addSquare(1, 1, Color.YELLOW, Shape.DIAMOND, R.drawable.yellow_diamond);
        addSquare(1, 2, Color.BLANK, Shape.BLANK, R.drawable.blank_blank);
        addSquare(1, 3, Color.BLUE, Shape.STAR, R.drawable.blue_star);
        addSquare(2, 0, Color.RED, Shape.DIAMOND, R.drawable.red_diamond);
        addSquare(2, 1, Color.YELLOW, Shape.FLOWER, R.drawable.yellow_flower);
        addSquare(2, 2, Color.BLUE, Shape.DIAMOND, R.drawable.blue_diamond);
        addSquare(2, 3, Color.GREEN, Shape.FLOWER, R.drawable.green_flower);

        addGoal(0,2);
        addEyeball(2,2, Direction.UP);

        timeElapsed = 0;

        if (gameStarted) {
            updateMoveCount();
            updateGoalCount();
            timePaused = false;
            mShowTime.setVisibility(View.VISIBLE);
            buttonPause.setVisibility(View.VISIBLE);
        }
        // timePaused = false;

    }

    private void updateMoveCount() {
        mShowCount.setText("Current Move Count: " + moveCount);
        mShowCount.setVisibility(View.VISIBLE);
    }

    private void updateGoalCount() {
        mGoalCount.setText("Current Goal Count: " + game.getGoalCount() + "/ " + INITIALGOALS);
        mGoalCount.setVisibility(View.VISIBLE);
    }

    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage) {
        Bitmap resultImage = Bitmap.createBitmap(secondImage.getWidth(),
                secondImage.getHeight(),
                secondImage.getConfig());
        Canvas canvas = new Canvas(resultImage);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, 10, 10, null);
        return resultImage;
    }

    private void addSquare(int row, int col, Color color, Shape shape, int indivBitmapId) {
        Square newSquare;
        if ((color == Color.BLANK) && (shape == Shape.BLANK)) {
            newSquare = new BlankSquare();
        } else {
            newSquare = new PlayableSquare(color, shape);
        }
        game.addSquare(newSquare, row, col);
        bitMapIds[row][col] = indivBitmapId;
    }

    private void addGoal(int row, int col) {
        game.addGoal(row,col);
        Bitmap goalSquareBitmap = BitmapFactory.decodeResource(getResources(), bitMapIds[row][col]);
        Bitmap goalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.goal);
        ImageView view = findViewById(imageViewIds[row][col]);
        view.setImageBitmap(createSingleImageFromMultipleImages(goalSquareBitmap, goalBitmap));
    }

    private void addEyeball(int row, int col, Direction direction) {
        game.addEyeball(row, col, direction);
        Bitmap currentSquareBitmap = BitmapFactory.decodeResource(getResources(), bitMapIds[row][col]);
        Bitmap eyeballBitmap = getEyeballBitmap(direction);
        ImageView view = findViewById(imageViewIds[row][col]);
        view.setImageBitmap(createSingleImageFromMultipleImages(currentSquareBitmap, eyeballBitmap));
    }

    private Bitmap getEyeballBitmap(Direction direction) {
        switch (direction) {
            default:
            case UP:
                return BitmapFactory.decodeResource(getResources(), R.drawable.eyeballs_up);
            case DOWN:
                return BitmapFactory.decodeResource(getResources(), R.drawable.eyeballs_down);
            case LEFT:
                return BitmapFactory.decodeResource(getResources(), R.drawable.eyeballs_left);
            case RIGHT:
                return BitmapFactory.decodeResource(getResources(), R.drawable.eyeballs_right);
        }
    }

    private int[] getViewCoordinates(ImageView imageView) {
        String viewName = getResources().getResourceName(imageView.getId());
        String[] nameArray = viewName.split("_");
        int row = Integer.parseInt(nameArray[1]);
        int col = Integer.parseInt(nameArray[2]);
        int[] coordinates = {row, col};
        return coordinates;
    }

    private void gameWonAction() {
        soundPool.play(winningSoundId, 1, 1, 0, 0, 1);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("You have reached the final goal!")
                .setTitle("Congratulations!")
                .setPositiveButton("Finish",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                .setNegativeButton("Restart",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                restartGame();
                            }
                        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void restartGame() {
        moveCount = 0;
        Button button = findViewById(R.id.buttonStart);
        removeCurrentEyeball();
        button.setText("Start");
        gameStarted = false;
        updateMoveCount();
        updateGoalCount();
        mShowCount.setVisibility(View.INVISIBLE);
        mGoalCount.setVisibility(View.INVISIBLE);
        mShowTime.setVisibility(View.INVISIBLE);
        timePaused = true;
        mShowTime.setText("Time taken: " + 0 + "s");
    }

    private void gameLostAction() {
        soundPool.play(incorrectMoveSoundId, 1, 1, 0, 0, 1);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("You have made too many moves..!")
                .setTitle("Oops, you have lost!")
                .setPositiveButton("Finish",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                .setNegativeButton("Try again",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                restartGame();
                            }
                        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void move(View view) {
        if (gameStarted) {
            ImageView targetImageView = (ImageView) view;
            int[] coordinates = getViewCoordinates(targetImageView);
            if (game.canMoveTo(coordinates[0], coordinates[1])) {
                removeCurrentEyeball();
                game.moveTo(coordinates[0], coordinates[1]);
                addEyeball(coordinates[0], coordinates[1], game.getEyeballDirection());
                moveCount++;
                updateMoveCount();
                updateGoalCount();

                if (moveCount > MAXCOUNT) {
                    gameLostAction();
                } else {
                    if (game.getGoalCount() == 0) {
                        gameWonAction();
                    }
                }

            } else {
                soundPool.play(incorrectMoveSoundId, 1, 1, 0, 0, 1);
                String logicViolationToast = "The tile is out of range";
                if (game.checkMessageForBlankOnPathTo(coordinates[0], coordinates[1]) == Message.MOVING_OVER_BLANK) {
                    logicViolationToast = "The eyeball cannot move \nover blank tiles.\n";
                }
                if (game.checkDirectionMessage(coordinates[0], coordinates[1]) == Message.MOVING_DIAGONALLY) {
                    logicViolationToast = "The eyeball cannot move \ndiagonally.\n";
                }
                if (game.checkDirectionMessage(coordinates[0], coordinates[1]) == Message.BACKWARDS_MOVE) {
                    logicViolationToast = "The eyeball cannot move \nbackwards.\n";
                }
                if (game.MessageIfMovingTo(coordinates[0], coordinates[1]) == Message.DIFFERENT_SHAPE_OR_COLOR) {
                    logicViolationToast = "The eyeball can only land \non same shape and/or colour.";
                }
                Toast toast = Toast.makeText(this, "" + logicViolationToast, Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        /*if () {
            Toast toast = Toast.makeText(this, "Yes", Toast.LENGTH_SHORT);
            toast.show();
        }*/
    }

    private void removeCurrentEyeball() {
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), bitMapIds[game.getEyeballRow()][game.getEyeballColumn()]);
        ImageView currentView = findViewById(imageViewIds[game.getEyeballRow()][game.getEyeballColumn()]);
        currentView.setImageBitmap(originalBitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // release resources
        soundPool.release();
        soundPool = null;
    }

    public void pauseGame(View view) {
        if (gameStarted) {
            if (!timePaused) {
                timePaused = true;
                buttonPause.setText("Resume");
                hideGame();
            } else {
                timePaused = false;
                buttonPause.setText("Pause Game");
                showGame();
            }
        }
    }

    private void hideGame() {
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                ImageView view = findViewById(imageViewIds[row][col]);
                view.setVisibility(View.INVISIBLE);
            }
        }
        Button button = findViewById(R.id.buttonStart);
        button.setEnabled(false);
        pausedMsg.setVisibility(View.VISIBLE);
    }

    private void showGame() {
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                ImageView view = findViewById(imageViewIds[row][col]);
                view.setVisibility(View.VISIBLE);
            }
        }
        Button button = findViewById(R.id.buttonStart);
        button.setEnabled(true);
        pausedMsg.setVisibility(View.INVISIBLE);
    }
}