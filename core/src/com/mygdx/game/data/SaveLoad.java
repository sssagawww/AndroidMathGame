package com.mygdx.game.data;

import com.mygdx.game.states.Play;

import java.io.*;

public class SaveLoad {
    private Play play;
    private DataStorage this_ds;

    public SaveLoad(Play play) {
        this.play = play;
    }

    public void save(){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("save.dat")));

            DataStorage ds = new DataStorage();
            ds.playerPos = play.getPlayer().getPosition();
            //ds.save = play.savePlay;

            oos.writeObject(ds);
        } catch (Exception e) {
            System.out.println("can't save");
        }
    }

    public void load(){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("save.dat")));

            DataStorage ds = (DataStorage) ois.readObject();
            //play.bdef.position.set(ds.playerPosX); <--- уже есть в play.createPlayer()
            //play.savePlay = ds.save;
            this_ds = ds;
            System.out.println(ds.playerPos + " save = "  + play.savePlay);

        } catch (Exception e) {
            System.out.println("can't load");
        }
    }

    public DataStorage getDs() {
        return this_ds;
    }
}
