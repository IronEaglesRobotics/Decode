package org.firstinspires.ftc.teamcode.hardware;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.Subsystem;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;
import com.seattlesolvers.solverslib.pedroCommand.HoldPointCommand;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.Collections;
import java.util.Set;

public class Drive extends SubsystemBase {
    Follower follower;
    public Drive(HardwareMap hardwareMap, Pose start){
        follower = Constants.createFollower(hardwareMap);
        follower.setPose(start);
    }
    public FollowPathCommand pathCommand(Path paths){
        return new FollowPathCommand(follower,paths);
    }
    public FollowPathCommand moveTo(int x, int y, int z){
        Path path = new Path(new BezierLine(follower.getPose(),new Pose(x,y,Math.toRadians(z))));
        path.setConstantHeadingInterpolation(Math.toRadians(z));
        return new FollowPathCommand(follower,path);
    }
    public FollowPathCommand moveToWithSpeed(int x, int y, int z,double speed) {
        Path path = new Path(new BezierLine(follower.getPose(), new Pose(x, y, Math.toRadians(z))));
        path.setConstantHeadingInterpolation(Math.toRadians(z));
        return new FollowPathCommand(follower, path, speed);
    }
        public HoldPointCommand hold(Pose pose){
        return new HoldPointCommand(follower,pose,true);
    }
    public HoldPointCommand hold(int x, int y, int z){
        return new HoldPointCommand(follower,new Pose(x,y,z),true);
    }
    public Command turn(double degrees){
        return new Command() {
            @Override
            public Set<Subsystem> getRequirements() {
                return Collections.emptySet();
            }

            @Override
            public void initialize() {
                follower.turnDegrees(degrees,false);
            }

            @Override
            public void execute() {
                follower.update();
            }

            @Override
            public boolean isFinished() {
                return follower.isTurning();
            }
        };
    }

    public Follower getFollower() {
        return follower;
    }
    public Pose getPose(){
        return follower.getPose();
    }
    public double getX(){
        return follower.getPose().getX();
    }
    public double getY(){
        return follower.getPose().getY();
    }
    public double getZ(){
        return follower.getPose().getHeading();
    }

    @Override
    public void periodic() {
        follower.update();
    }
}
