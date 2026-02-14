package org.firstinspires.ftc.teamcode.hardware;

import com.pedropathing.geometry.Pose;

public final class Storage {
    public Pose pose = new Pose(0,0,0);
    public int order = 0;
    public Pose shootPose = new Pose(0,0,0);
    public boolean resetSpindexer = true;
    private static Storage instance;
    public static synchronized Storage getInstance(){
        if (instance == null){
            instance = new Storage();
        }
        return instance;
    }

    public void setPose(Pose pose,Pose shootPose) {
        this.pose = pose;
        this.shootPose = shootPose;
    }
}
