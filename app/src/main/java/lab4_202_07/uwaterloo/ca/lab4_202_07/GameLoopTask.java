package lab4_202_07.uwaterloo.ca.lab4_202_07;

/**
 * Created by Alex on 2017-06-30.
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

public class GameLoopTask extends TimerTask {
    public enum GameDirection{UP, DOWN, LEFT, RIGHT, UNKNOWN}
    private GameDirection currentDirection = GameDirection.UNKNOWN;

    public static LinkedList<GameBlock> gameBlockLinkedList = new LinkedList<>();
    private Activity activity;
    private Context context;
    private RelativeLayout relativeLayout;
    private boolean willCreateBlock;
    private boolean gameOver = false;
    final static int WIN_CONDITION = 512;

    public Random rng = new Random();

    //constructor
    GameLoopTask(Activity activity, Context context, RelativeLayout relativeLayout){
        this.activity = activity;
        this.context = context;
        this.relativeLayout = relativeLayout;
        createBlock();
    }
    public void run(){
        activity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        boolean finishedMoving = true;
                        for(GameBlock gb: gameBlockLinkedList) {
                            gb.move();
                            if(gb.getVelocity() != 0) {
                                finishedMoving = false;
                            }
                        }
                        if (finishedMoving) {

                            if (willCreateBlock) {
                                List<GameBlock> toRemove = new LinkedList<>();
                                for(GameBlock gb: gameBlockLinkedList){
                                    if (gb.cleanUp()){
                                        toRemove.add(gb);
                                    }
                                }

                                gameBlockLinkedList.removeAll(toRemove);
                                checkForWin();
                                createBlock();
                                willCreateBlock = false;
                            }
                            if (currentDirection != GameDirection.UNKNOWN && !gameOver){
                                ((lab4_202_07) activity).resumeSensor();
                            }
                            setDirection(GameDirection.UNKNOWN);

                        }
                    }
                }
        );
    }

    private void checkForWin() {
        for (GameBlock gb : gameBlockLinkedList){
            Log.wtf("Blocknum and win Condition", Boolean.toString(gb.getBlockNum()==WIN_CONDITION));
            if (gb.getBlockNum() == WIN_CONDITION){
                ((lab4_202_07) activity).lblDirection.setText("You WON!!");     //If one of the game block reaches 256, it displays the message "You WON!!"
                Log.wtf("Game status:", "You won!");
                gameOver=true;                                                  //And set the flag to true, indicating the game is over
                ((lab4_202_07) activity).stopSensor();
            }
        }
    }

    private void checkForLose(){
        int[][] numArray = new int[4][4];
        for (GameBlock gb : gameBlockLinkedList ){
            int x = (gb.getCoord()[0]-GameBlock.LEFT_TOP_LIM)/GameBlock.SLOT_ISO;
            int y = (gb.getCoord()[1]-GameBlock.LEFT_TOP_LIM)/GameBlock.SLOT_ISO;
            numArray [x][y] = gb.getBlockNum();
        }   //get the blocks in to the new array
        boolean lose = true;
        for (int i = 0; i < numArray.length; i++){
            for (int j = 0; j < numArray[i].length; j++){
                if (i < 3 && j < 3){
                    if (numArray[i][j]==numArray[i+1][j] || numArray[i][j] == numArray[i][j+1] ){
                        lose = false;
                    }
                }else if (i == 3 && j <3){
                    if (numArray[i][j] == numArray[i][j+1]){
                        lose = false;
                    }
                }else if (j == 3 && i <3){
                    if (numArray [i][j] == numArray[i+1][j]){
                        lose = false;
                    }
                }
            }
        }   //check if there exists adjacent blocks that have the same value, if not, then lose
        if (lose){
            ((lab4_202_07) activity).lblDirection.setText("You Lost!!");
            Log.wtf("Game status:", "You won!");
            gameOver=true;
            ((lab4_202_07) activity).stopSensor();
        }
    }

    private void createBlock(){

        GameBlock gameBlock;
        int emptySlot = 16;
        boolean[][] blockMap = new boolean[4][4];   // 4 by 4 array to hold the current location of the block on the board
        for (GameBlock gb : gameBlockLinkedList) {
            int x = (gb.getCoord()[0]-GameBlock.LEFT_TOP_LIM)/GameBlock.SLOT_ISO;
            int y = (gb.getCoord()[1]-GameBlock.LEFT_TOP_LIM)/GameBlock.SLOT_ISO;

            blockMap[x][y] = true;
            emptySlot--;
        }   //every time we create a new block, we indicate its location by setting the according boolean value true
            // count the empty slots
        Log.d ("EmptySlot",emptySlot+"");
        int rnd = rng.nextInt(emptySlot);
        int x =0;
        int y =0;
        for (int k = 0; k < blockMap.length; k++) {
            for (int j = 0; j < blockMap[k].length; j++) {
                if (!blockMap[k][j]) {
                    if (rnd == 0) {
                        x = k;
                        y = j;
                        k = blockMap.length;
                        break;
                    }
                    rnd--;
                }
            }
        }   //create the new block on the empty slot
        gameBlock = new GameBlock(context, GameBlock.LEFT_TOP_LIM + x * GameBlock.SLOT_ISO, GameBlock.LEFT_TOP_LIM + y * GameBlock.SLOT_ISO, relativeLayout);
        gameBlockLinkedList.add(gameBlock);
        if (emptySlot <= 1){
            checkForLose();
        }
        //check for lose after addition of the block

    }

    public void setDirection(GameDirection gameDirection){
        currentDirection = gameDirection;
        if (gameDirection != GameDirection.UNKNOWN){    //check that the direction is not unknown
            ((lab4_202_07) activity).stopSensor();
            willCreateBlock = false;
            for (GameBlock gb: gameBlockLinkedList) {
                gb.setBlockDirection(currentDirection); //set the direction of gameblock to the current direction
                if(gb.setDestination()){
                    willCreateBlock = true;             //set the final destination of each block and check all blocks have different final destination
                }
            }
            Log.d("Debug: ","Called Create Block: "+ gameDirection);
        }
        //Log.d("Current Direction: ", gameDirection.toString());
    }

    public static boolean isOccupied(int x, int y){
        for (GameBlock gb : gameBlockLinkedList){
            if (gb.getCoord()[0] == x  && gb.getCoord()[1] == y ){
                return true;        // returns true if there is a block in the specified x and y coordinates
            }
        }
        return false;
    }

    public static GameBlock getGameblock (int x, int y){
        for (GameBlock gb : gameBlockLinkedList){
            if (gb.getCoord()[0] == x  && gb.getCoord()[1] == y ){
                return gb;          //returns the reference of the game block at the specified x and y
            }
        }
        return null;
    }
}
