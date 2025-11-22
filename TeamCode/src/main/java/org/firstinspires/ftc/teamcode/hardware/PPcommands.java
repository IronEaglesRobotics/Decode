package org.firstinspires.ftc.teamcode.hardware;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.paths.PathPoint;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandBase;

import org.opencv.core.Point;

public class PPcommands extends CommandBase {
    Bot robot;
    Follower follower;


    private Path launchpreload;

    public void buildPaths(){
        final Pose startpose = new Pose(-60,-12, Math.toRadians(90));

        final Command farshoot = robot.getDrive().moveTo(-50,12,20);
        final Command prepick1 = robot.getDrive().moveTo(-21,34,90);
        final Command togate = robot.getDrive().moveTo(-21,43,90);
        final Command prepick2 = robot.getDrive().moveTo(-33,43,90);
        final Command loadzonepick = robot.getDrive().moveTo(-55,55,90);

    }


}
