package com.example.myapplication.Class;

import android.content.Context;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;

import com.example.myapplication.R;

public class BlockM extends BlockH {
    public BlockM(Context context, int nUnits, int blockSize) {
        super(context, nUnits, blockSize);
        this.setImageDrawable(getResources().getDrawable(R.drawable.main_block));
    }

    @Override
    public void touchMove(MotionEvent motionEvent) {
        int x = (int) (this.x+motionEvent.getX() - this.offsetX);
        if(boundaryRight == 6 && x + (nUnits -1) *blockSize >= 5 * blockSize ){
            win();
            return;
        }
        super.touchMove(motionEvent);
    }

    private void win() {
        this.setClickable(false);
        this.setOnTouchListener(null);
        this.animate().x(8*blockSize).start();
    }
}
