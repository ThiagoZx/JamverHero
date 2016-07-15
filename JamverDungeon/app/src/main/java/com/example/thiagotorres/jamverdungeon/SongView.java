package com.example.thiagotorres.jamverdungeon;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Thiago.Torres on 08/07/2016
 */
public class SongView extends View implements Runnable {


    //Context
    Context context;

    //Manager
    private Handler handler;

    //Time measure
    long lastTime;

    //Note test
    int currNote;

    //Listas
    List<Controller> controllers;
    List<Grid> bar;
    List<Command> commands;

    public SongView(Context context) {

        super(context);
        handler = new Handler();
        handler.post(this);

        this.context = context;

        Start();
    }

    void sendNote() {
        long currTime = System.currentTimeMillis();
        if (currTime - lastTime > 1000){
            commands.add(commands.size(), new Command(BitmapFactory.decodeResource(getResources(), R.drawable.arrow), currNote));
            lastTime = currTime;
            currNote++;
        } else if ( currNote > 3){
            currNote = 0;
        }
    }

    void destroyNote(int note) {
        for (int i = 0; i < commands.size(); i++) {
            if (Rect.intersects(commands.get(i).body, bar.get(note).body) && commands.get(i).getDirection() == note) {
                commands.remove(i);
            }
        }
    }

    void Start() {
        controllers = new ArrayList<>();
        bar = new ArrayList<>();
        commands = new ArrayList<>();


        for (int i = 0; i < 4; i++) {
            controllers.add(controllers.size(), new Controller(BitmapFactory.decodeResource(getResources(), R.drawable.arrow), i));
            bar.add(bar.size(), new Grid(BitmapFactory.decodeResource(getResources(), R.drawable.arrow), i));
        }

    }

    void Update(){
        sendNote();

        for (int i = 0; i < bar.size(); i++) {
            bar.get(i).updateGrid();
        }


        for (int i = 0; i < commands.size(); i++) {
            commands.get(i).updateCommand();
            if (commands.get(i).deleteCommand()){
                commands.remove(i);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas){

        for (int i = 0; i < 4; i++){
            controllers.get(i).drawController(canvas);
            bar.get(i).drawGrid(canvas);
        }

        for (int i = 0; i < commands.size(); i++) {
            commands.get(i).drawCommand(canvas);
        }

        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch(action){
            case MotionEvent.ACTION_DOWN:

                for (int i = 0; i < controllers.size(); i++){
                    if (x >= controllers.get(i).getAxis("x") && x < (controllers.get(i).getAxis("x") + controllers.get(i).getSize("width"))
                            && y >= controllers.get(i).getAxis("y") && y < (controllers.get(i).getAxis("y") + controllers.get(i).getSize("height"))) {
                        destroyNote(i);
                    }
                }

                break;
        }

        return true;
    }

    @Override
    public void run() {
        handler.postDelayed(this, 30);
        Update();
        invalidate();
    }
}
