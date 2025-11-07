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
    public static class Paths {

        public PathChain Path1;
        public PathChain Path2;
        public PathChain Path3;
        public PathChain Path4;
        public PathChain Path5;
        public PathChain Path6;
        public PathChain Path7;
        public PathChain Path8;
        public PathChain Path9;
        public PathChain Path10;
        public PathChain Path11;
        public PathChain[] shootPaths;

        public Paths(Follower follower,boolean isBlue) {
            Path1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(flip(56,isBlue), 8.000), new Pose(flip(40.000,isBlue), 95.500))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(90),flipAng(135,isBlue))
                    .build();

            Path2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(flip(48.000,isBlue), 95.500), new Pose(flip(28.000,isBlue), 69.500))
                    )
                    .setLinearHeadingInterpolation(flipAng(135,isBlue), flipAng(180,isBlue))
                    .build();

            Path3 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(flip(25.000,isBlue), 69.500), new Pose(flip(1.000,isBlue), 69.500))
                    )
                    .setConstantHeadingInterpolation(flipAng(180,isBlue))
                    .build();

//            Path4 = follower
//                    .pathBuilder()
//                    .addPath(
//                            new BezierLine(new Pose(flip(19.000,isBlue), 69.000), new Pose(flip(5.000,isBlue), 75.000))
//                    )
//                    .setLinearHeadingInterpolation(flipAng(180,isBlue), flipAng(270,isBlue))
//                    .build();

            Path5 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierCurve(
                                    new Pose(flip(9.000,isBlue), 69.500),
                                    new Pose(flip(43.000,isBlue), 52.500),
                                    new Pose(flip(40.000,isBlue), 95.500)
                            )
                    )
                    .setLinearHeadingInterpolation(flipAng(180,isBlue), Math.toRadians(135))
                    .build();

            Path6 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(flip(40.000,isBlue), 95.500), new Pose(flip(28.000,isBlue), 88.000))
                    )
                    .setLinearHeadingInterpolation(flipAng(135,isBlue), flipAng(180,isBlue))
                    .build();

            Path7 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(flip(40.000,isBlue), 104.000), new Pose(flip(19.000,isBlue), 104.000))
                    )
                    .setConstantHeadingInterpolation(flipAng(180,isBlue))
                    .build();

            Path8 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(flip(19.000,isBlue), 104.000), new Pose(flip(48.000,isBlue), 95.000))
                    )
                    .setLinearHeadingInterpolation(flipAng(180,isBlue), flipAng(135,isBlue))
                    .build();

            Path9 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(flip(48.000,isBlue), 95.000), new Pose(flip(40.000,isBlue), 35.500))
                    )
                    .setLinearHeadingInterpolation(flipAng(135,isBlue),flipAng(180,isBlue))
                    .build();

            Path10 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(flip(40.000,isBlue), 35.500), new Pose(flip(19.000,isBlue), 35.500))
                    )
                    .setConstantHeadingInterpolation(flipAng(180,isBlue))
                    .build();

            Path11 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(flip(19.000,isBlue), 35.500), new Pose(flip(48.000,isBlue), 95.000))
                    )
                    .setLinearHeadingInterpolation(flipAng(180,isBlue), flipAng(135,isBlue))
                    .build();
            shootPaths = new PathChain[]{Path1,Path5,Path8,Path11};
        }
        public double flip(double standard, boolean ifFlip){
            return Math.abs(standard - (!ifFlip ? 72:1))+(ifFlip ? 72:1);
        }
        public double flipAng(double degrees, boolean ifFlip){
            return Math.toRadians(90 +((degrees - 90) * (ifFlip ? 1:-1)));
        }
    }

}
