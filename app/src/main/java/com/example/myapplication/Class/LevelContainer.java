package com.example.myapplication.Class;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LevelContainer extends RelativeLayout {
    private int _currentLevel;
    private int _minMoves;
    private int _record;
    private BlockFactory blockFactory;

    public LevelContainer(Context context) {
        super(context);
        blockFactory = new BlockFactory(context);
    }

    public LevelContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        blockFactory = new BlockFactory(context);
    }

    public void generateLevel(int id) throws IOException {
        InputStream is = getResources().openRawResource(id);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = br.readLine();
        _currentLevel = Integer.parseInt(line);
        line = br.readLine();
        _minMoves = Integer.parseInt(line);
        for(;;){
            line = br.readLine();
            if (line == null)
                break;
            String[] blockInfo = line.split(" ");
            int nUnits = blockInfo.length - 1;
            char type = blockInfo[0].charAt(0);
            int x = Character.getNumericValue(blockInfo[1].charAt(0));
            int y = Character.getNumericValue(blockInfo[1].charAt(2));
            placeBlock(type, x, y, nUnits);
        }
    }
    public void setBlockSize(int blockSize) {
        blockFactory.setBlockSize(blockSize);
    }

    public void placeBlock(char type, int x, int y, int nUnits){
        Block block = blockFactory.createBlock(type,nUnits);
        this.addView(block);
        block.setTranslationX(x*blockFactory.getBlockSize());
        block.setTranslationY(y*blockFactory.getBlockSize());
    }


}
