package org.firstinspires.ftc.teamcode.hardware;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathPoint;
import com.seattlesolvers.solverslib.command.CommandBase;

import org.opencv.core.Point;

public class PPcommands extends CommandBase {
    Bot robot;
    Follower follower;

    private final Pose startpose = new Pose(-60,-12);
    private final Pose farshoot = new Pose(-50,12, Math.toRadians(20));
    private final Pose prepickup1 = new Pose(-21,34, Math.toRadians(90));

    private final Pose gate = new Pose(-21,43,Math.toRadians(90));

    private final Pose prepickup2 = new Pose(-33,43,Math.toRadians(90));
    private final Pose loadingzonepick = new Pose(-55,55,Math.toRadians(90));

    private Path launchpreload;

    public void buildPaths(){
        final Pose startpose = new Pose(-60,-12);
        final Pose farshoot = new Pose(-50,12, Math.toRadians(20));
        final Pose prepickup1 = new Pose(-21,34, Math.toRadians(90));

        final Pose gate = new Pose(-21,43,Math.toRadians(90));

        final Pose prepickup2 = new Pose(-33,43,Math.toRadians(90));
        final Pose loadingzonepick = new Pose(-55,55,Math.toRadians(90));


        launchpreload = new Path(new BezierLine(new PathPoint(), new PathPoint()));
    }

}
