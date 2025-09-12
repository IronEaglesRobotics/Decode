package org.firstinspires.ftc.teamcode.hardware;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;
import com.seattlesolvers.solverslib.pedroCommand.HoldPointCommand;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

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
        return new FollowPathCommand(follower,new Path(new BezierLine(follower.getPose(),new Pose(x,y,z))));
    }
    public HoldPointCommand hold(Pose pose){
        return new HoldPointCommand(follower,pose,true);
    }
    public HoldPointCommand hold(int x, int y, int z){
        return new HoldPointCommand(follower,new Pose(x,y,z),true);
    }

    @Override
    public void periodic() {
        follower.update();
    }
}
