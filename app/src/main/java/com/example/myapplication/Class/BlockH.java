package com.example.myapplication.Class;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class BlockH extends Block {
    float offsetX = 0; //block position
    float x = 0;

    public BlockH(Context context, int nUnits, int blockSize) {
        super(context, blockSize);
        this.nUnits = nUnits;
        this.setLayoutParams(new RelativeLayout.LayoutParams(nUnits * blockSize , blockSize));
    }

    @Override
    protected void touchDown(MotionEvent motionEvent) {
        this.offsetX = motionEvent.getX();
        this.x = this.getTranslationX();
    }

    @Override
    public void touchMove(MotionEvent motionEvent) {
        x += motionEvent.getX() - this.offsetX;
        if(x < blockSize){
            x = blockSize;
        }else if(x + (nUnits -1) *blockSize > 6 * blockSize){
            x = 6 * blockSize - (nUnits - 1) * blockSize;
        }
        this.setTranslationX(x);

    }

    @Override
    protected void touchUp(MotionEvent motionEvent) {
        float adjustmentX = Math.round(x /blockSize) *blockSize;
        this.setTranslationX(adjustmentX);
    }
}

