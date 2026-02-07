package org.firstinspires.ftc.teamcode.opModes;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.DeferredCommand;
import com.seattlesolvers.solverslib.command.RepeatCommand;
import com.seattlesolvers.solverslib.command.WaitCommand;

import org.firstinspires.ftc.teamcode.hardware.Bot;

import java.util.List;

@Autonomous(name = "movement Test")
public class movementTest extends OpMode {
    Bot robot;

    @Override
    public void init() {
        robot = new Bot().init(hardwareMap,null);
        robot.getDrive().getFollower().setStartingPose(new Pose(0,0,0));
        robot.getDrive().moveTo(0,-48,0)
                .andThen(robot.getDrive().hold(new Pose(0,-48,0)))
                .schedule();
    }

    @Override
    public void loop() {
        CommandScheduler.getInstance().run();
        telemetry.addData("pose", robot.getDrive().getPose());
        robot.getDrive().getFollower().update();
    }
    public void stop(){
       CommandScheduler.getInstance().reset();
    }
}
