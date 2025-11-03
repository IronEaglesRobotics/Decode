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
    class Paths {

        public PathChain Path1;
        public PathChain Path2;
        public PathChain Path3;
        public PathChain Path4;
        public PathChain Path5;
        public PathChain Path6;
        public PathChain Path7;
        public PathChain Path8;
        public PathChain Path9;

        public Paths(Follower follower,boolean isBlue, boolean isFar) {
            Path1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(56.000, 8.000), new Pose(41.000, 59.500))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180))
                    .build();

            Path2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(41.000, 59.500), new Pose(19.000, 60.000))
                    )
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();

            Path3 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(19.000, 60.000), new Pose(15.000, 71.000))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(270))
                    .build();

            Path4 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierCurve(
                                    new Pose(15.000, 71.000),
                                    new Pose(67.000, 42.000),
                                    new Pose(53.000, 12.000)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(110))
                    .build();

            Path5 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(53.000, 12.000), new Pose(40.000, 36.000))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(110), Math.toRadians(180))
                    .build();

            Path6 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(40.000, 36.000), new Pose(19.000, 36.000))
                    )
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();

            Path7 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(19.000, 36.000), new Pose(53.000, 12.000))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(110))
                    .build();
            Path8 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(53.000, 12.000), new Pose(38.000, 84.000))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(110), Math.toRadians(180))
                    .build();

            Path9 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(38.000, 84.000), new Pose(19.000, 84.000))
                    )
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();
        }
    }

}
