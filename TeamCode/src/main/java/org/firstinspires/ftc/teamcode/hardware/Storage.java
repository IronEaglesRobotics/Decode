package org.firstinspires.ftc.teamcode.hardware;

import com.pedropathing.geometry.Pose;

public final class Storage {
    public Pose pose;
    public static Storage instance;
    public static synchronized Storage getInstance(){
        if (instance == null){
            instance = new Storage();
        }
        return instance;
    }

    public void setPose(Pose pose) {
        this.pose = pose;
    }
}
