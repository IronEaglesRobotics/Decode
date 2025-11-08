package org.firstinspires.ftc.teamcode.hardware;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.Subsystem;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;
import com.seattlesolvers.solverslib.pedroCommand.HoldPointCommand;
import com.seattlesolvers.solverslib.pedroCommand.*;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.Collections;
import java.util.Set;

public class Drive extends SubsystemBase {
    Follower follower;
    GamepadEx controller;
    public Drive(HardwareMap hardwareMap){
        follower = Constants.createFollower(hardwareMap);
        follower.update();
    }
    public Drive(HardwareMap hardwareMap, GamepadEx gamepadEx){
        follower = Constants.createFollower(hardwareMap);
        follower.update();
        controller = gamepadEx;
        follower.startTeleopDrive(true);
    }
    public FollowPathCommand pathCommand(PathChain paths){
        return new FollowPathCommand(follower, paths);
    }
    public FollowPathCommand pathCommand(PathChain paths,double speed){
        return new FollowPathCommand(follower, paths,speed);
    }
    public FollowPathCommand moveTo(Pose pose){
        return pathCommand(new PathChain(new Path(new BezierLine(follower.getPose(),pose))));
    }
    public FollowPathCommand moveTo(double x, double y, double z){
        Path path = new Path(new BezierLine(follower.getPose(),new Pose(x,y,Math.toRadians(z))));
        path.setConstantHeadingInterpolation(Math.toRadians(z));
        return new FollowPathCommand(follower, path);
    }
    public FollowPathCommand moveToWithSpeed(double x, double y, double z,double speed) {
        Path path = new Path(new BezierLine(follower.getPose(), new Pose(x, y, Math.toRadians(z))));
        path.setConstantHeadingInterpolation(Math.toRadians(z));
        return new FollowPathCommand(follower, path, speed);
    }
    public HoldPointCommand hold(Pose pose){
        return new HoldPointCommand(follower,pose,true);
    }
    public HoldPointCommand hold(double x, double y, double z){
        return new HoldPointCommand(follower,new Pose(x,y,z),true);
    }
    public Command turn(double degrees){
        return new TurnCommand(follower,Math.toRadians(degrees),false);
    }
    public TurnToCommand turnTo(double degrees){
        return new TurnToCommand(follower,Math.toRadians(degrees));
    }

    public Follower getFollower() {
        return follower;
    }
    public void setVector(){
        follower.setTeleOpDrive(-controller.getLeftY(),controller.getLeftX(),controller.getRightX(),true);
    }
    public void setVector(GamepadEx controller1){
        follower.setTeleOpDrive(controller1.getLeftY(),controller1.getLeftX(),controller1.getRightX(),true);
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

