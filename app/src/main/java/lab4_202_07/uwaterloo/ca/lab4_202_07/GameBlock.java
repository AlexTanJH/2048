package lab4_202_07.uwaterloo.ca.lab4_202_07;

/**
 * Created by Alex on 2017-06-30.
 */

import android.content.Context;
import android.graphics.Color;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Random;

public class GameBlock extends GameBlockTemplate{
    private final float IMAGE_SCALE = 0.5f;
    private RelativeLayout relativeLayout;

    private GameLoopTask.GameDirection currentDirection = GameLoopTask.GameDirection.UNKNOWN;

    //sets the coordinates of the gameblock
    private int coordX;
    private int coordY;
    private int finalX;
    private int finalY;

    private TextView textView;

    private boolean destroyMe = false;
    GameBlock blockToDouble;


    private float velocity = 0;

    private final int BLOCK_ACCELERATION = 4;

    final static int LEFT_TOP_LIM = -110; // left and top limit
    final static int RIGHT_BOT_LIM = 790; // right and bottom limit
    final static int SLOT_ISO = 300;
    final static float TEXT_OFFSET = 220;


    GameBlock(Context context, int coordX, int coordY, RelativeLayout relativeLayout){
        super(context);

        //spawns a a gameblock with a random number on it
        Random rng = new Random();
        //makes sure it is a power of 2
        int rndNum = (rng.nextInt(2)+1)*2;

        //draws the game block
        textView = new TextView(context);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(20);
        textView.setText(Integer.toString(rndNum));
        relativeLayout.addView(textView);

        //makes sure the number is in the middle of the block
        textView.setY(coordY+ TEXT_OFFSET);
        textView.setX(coordX+ TEXT_OFFSET);

        //sets the location of the initialized
        setX(coordX);
        setY(coordY);
        this.coordY = coordY;
        this.coordX = coordX;

        //drwas/scales the gameblock
        this.setImageResource(R.drawable.gameblock);
        this.setScaleX(IMAGE_SCALE);
        this.setScaleY(IMAGE_SCALE);
        this.relativeLayout = relativeLayout;
        relativeLayout.addView(this);

        //draws image on top of the grid
        textView.bringToFront();
    }

    //sets the direction of the block
    public void setBlockDirection (GameLoopTask.GameDirection gameDirection){
        currentDirection = gameDirection;
    }

    //moves the block
    public void move(){
//        Log.d("Current Direction: ", currentDirection.toString());
//        Log.d("X", ""+coordX);
//        Log.d("Y", ""+coordY);
        if (currentDirection == GameLoopTask.GameDirection.RIGHT){
            //right boundary
            if (coordX >= finalX ){
                velocity = 0;
                coordX = finalX;
            }else {    //moves the block
                velocity += BLOCK_ACCELERATION;
                coordX += velocity;
            }
        }
        if (currentDirection == GameLoopTask.GameDirection.LEFT){
            //left boundary
            if (coordX <= finalX){
                velocity = 0;
                coordX = finalX;
            }else {     //moves the block
                velocity -= BLOCK_ACCELERATION;
                coordX += velocity;
            }
        }
        if (currentDirection == GameLoopTask.GameDirection.DOWN){
            //upper boundary
            if (coordY >= finalY){
                coordY = finalY;
                velocity = 0;
            }else {    //moves the block
                velocity += BLOCK_ACCELERATION;
                coordY += velocity;
            }
        }
        if (currentDirection == GameLoopTask.GameDirection.UP){
            //lower boundary
            if (coordY <= finalY){
                coordY = finalY;
                velocity = 0;
            }else {    //moves the block
                velocity -= BLOCK_ACCELERATION;
                coordY += velocity;
            }
        }
        //in the event that the movement was unkown
        if (currentDirection == GameLoopTask.GameDirection.UNKNOWN) {
            velocity = 0;
            return;
        }

        //resets the block's location
        setY(coordY);
        setX(coordX);
        textView.setY(coordY+TEXT_OFFSET);
        textView.setX(coordX+TEXT_OFFSET);
    }

    //get how fast block goes
    public float getVelocity (){
        return  this.velocity;
    }

    //marks the block for deletion and doubles the number of the other block
    public boolean cleanUp (){
        if (destroyMe){
            //doubles the number
            blockToDouble.doubleMyNumber();
            blockToDouble = null;
            //removes the block
            relativeLayout.removeView(this.textView);
            relativeLayout.removeView(this);
            // insert object destructor and double the adjacent block
        }
        return destroyMe;
    }
    @Override
    /**
     * returns true if destination is changed from current destination
     */

    //merging / collision algorithm
    public boolean setDestination() {


        LinkedList<Integer> numberInRow = new LinkedList<>();
        int gameSlots = 0;
        int blockNums = 0;
        int currentCoord;

        int mergeOffset = 0;

        //tells current direction of the block
        switch (currentDirection) {


            case RIGHT:   //sets direction right
                currentCoord = RIGHT_BOT_LIM;
                while (currentCoord > coordX) {    //checks block in the way of the path of the direction of the block
                    if (!GameLoopTask.isOccupied(currentCoord, coordY)) {
                        blockNums++;               //increments the number of blocks in the way
                    }else {
                        numberInRow.add(GameLoopTask.getGameblock(currentCoord,coordY).getBlockNum());
                    }
                    gameSlots++;                   //counts how many slots there are in the way
                    currentCoord -= SLOT_ISO;
                }
                numberInRow.add(this.getBlockNum());   //adds the current number of the block to the end of the list

                mergeOffset = determineMerge(numberInRow);   //gets the merged offset number from merging algorithm
                if (mergeOffset >= 1){                       //if there is a merge
                    int x = coordX+SLOT_ISO;
                    //iterate from current block to find first non null reference of a block
                    while (x <= RIGHT_BOT_LIM){
                        GameBlock gb = GameLoopTask.getGameblock(x, coordY);
                        if (gb != null){
                            blockToDouble = gb;
                            break;
                        }
                        x+= SLOT_ISO;
                    }
                }

                //sets final destination according to the formula
                finalX = RIGHT_BOT_LIM - (gameSlots - blockNums -  mergeOffset) * SLOT_ISO;
                finalY = coordY;
                break;
            case LEFT:                           //see above for format but eith LEFT direction
                currentCoord = LEFT_TOP_LIM;
                while (currentCoord < coordX) {
                    if (!GameLoopTask.isOccupied(currentCoord, coordY)) {
                        blockNums++;
                    }else {
                        numberInRow.add(GameLoopTask.getGameblock(currentCoord,coordY).getBlockNum());
                    }
                    gameSlots++;
                    currentCoord += SLOT_ISO;
                }
                numberInRow.add(this.getBlockNum());

                mergeOffset = determineMerge(numberInRow);
                if (mergeOffset >= 1){
                    int x = coordX-SLOT_ISO;
                    while (x >= LEFT_TOP_LIM){
                        GameBlock gb = GameLoopTask.getGameblock(x, coordY);
                        if (gb != null){
                            blockToDouble = gb;
                            break;
                        }
                        x-= SLOT_ISO;
                    }
                }

                finalX = LEFT_TOP_LIM + (gameSlots - blockNums - mergeOffset) * SLOT_ISO;
                finalY = coordY;
                break;
            case UP:                           //see above for format but eith UP direction
                currentCoord = LEFT_TOP_LIM;
                while (currentCoord <coordY) {
                    if (!GameLoopTask.isOccupied(coordX, currentCoord)) {
                        blockNums++;
                    }
                    else {
                        numberInRow.add(GameLoopTask.getGameblock(coordX,currentCoord).getBlockNum());
                    }
                    gameSlots++;
                    currentCoord += SLOT_ISO;
                }
                numberInRow.add(this.getBlockNum());

                mergeOffset = determineMerge(numberInRow);
                if (mergeOffset >= 1){
                    int y = coordY-SLOT_ISO;
                    while (y >= LEFT_TOP_LIM){
                        GameBlock gb = GameLoopTask.getGameblock(coordX,y);
                        if (gb != null){
                            blockToDouble = gb;
                            break;
                        }
                        y-= SLOT_ISO;
                    }
                }

                finalY = LEFT_TOP_LIM + (gameSlots - blockNums - mergeOffset) * SLOT_ISO;
                finalX = coordX;
                break;
            case DOWN:                           //see above for format but eith DOWN direction
                currentCoord = RIGHT_BOT_LIM;
                while (currentCoord > coordY) {
                    if (!GameLoopTask.isOccupied(coordX, currentCoord)) {
                        blockNums++;
                    }
                    else {
                        numberInRow.add(GameLoopTask.getGameblock(coordX,currentCoord).getBlockNum());
                    }
                    gameSlots++;
                    currentCoord -= SLOT_ISO;
                }
                numberInRow.add(this.getBlockNum());

                mergeOffset = determineMerge(numberInRow);
                if (mergeOffset >= 1){
                    int y = coordY+SLOT_ISO;
                    while (y <= RIGHT_BOT_LIM){
                        GameBlock gb = GameLoopTask.getGameblock(coordX,y);
                        if (gb != null){
                            blockToDouble = gb;
                            break;
                        }
                        y+= SLOT_ISO;
                    }
                }

                finalY = RIGHT_BOT_LIM - (gameSlots - blockNums - mergeOffset) * SLOT_ISO;
                finalX = coordX;
                break;
            case UNKNOWN:                         //when direction is unkown
            default:
                break;
        }

        //returns true if final destination is different, else returns false
        if (finalX == coordX && finalY == coordY)
            return false;
        else
            return true;
    }

    //gets block number
    public int getBlockNum() {
        int n = Integer.parseInt(textView.getText().toString());
        return n;
    }

    /**
     *
     * @return 1 if block is to be merged else don't merge
     */

    //merging algoritm
    private int determineMerge(LinkedList<Integer> list){
        int size = list.size();
        switch (size){
            case 2:   //checks cases where there are 2 numbers in a row in a direction of movement
                if (list.get(0).equals(list.get(1))) {
                    destroyMe = true; //check that we merged them
                    return 1;         //returns the merge offset
                }
                break;
            case 3:   //checks if there are 3 numbers in a row
                if (!list.get(0).equals(list.get(1))) {
                    if (list.get(1).equals(list.get(2))) {
                        destroyMe = true;  //destroy the block
                        return 1;
                    }
                }else
                    return 1; //returns 1 merge offset, but doesnot destroy the block
                break;
            case 4:    //different cases that return different values
                if (list.get(0).equals(list.get(1)) && list.get(2).equals(list.get(3))) {
                    destroyMe = true;
                    return 2;
                }else if (list.get(0).equals(list.get(1)) && !list.get(2).equals(list.get(3))){
                    return 1;
                }else if (list.get(1).equals(list.get(2))){
                    return 1;
                }else if (!list.get(0).equals(list.get(1)) && list.get(2).equals(list.get(3))){
                    destroyMe = true;
                    return 1;
                }
                break;
            default:
                return 0;
        }
        return 0;
    }

    //doubles the game block number
    public void doubleMyNumber() {
        int n = getBlockNum();
        textView.setText(n*2 + "");
    }

    //returns the current coordinates
    public int[] getCoord (){
        int[] x ={coordX, coordY};
        return  x;
    }
}
