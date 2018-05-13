package lab4_202_07.uwaterloo.ca.lab4_202_07;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Alex on 2017-07-06.
 */

public abstract class GameBlockTemplate extends ImageView {
    abstract boolean setDestination();
    abstract void move();
    GameBlockTemplate(Context context){
        super(context);
    }
}
