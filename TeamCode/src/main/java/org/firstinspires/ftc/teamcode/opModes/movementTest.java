package org.firstinspires.ftc.teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.RepeatCommand;
import com.seattlesolvers.solverslib.command.WaitCommand;

import org.firstinspires.ftc.teamcode.hardware.Bot;

@Autonomous(name = "movement Test")
public class movementTest extends OpMode {
    Bot robot;

    @Override
    public void init() {
        robot = new Bot().init(hardwareMap,null);
        robot.getDrive().moveTo(0,30,Math.toRadians(90))
                .andThen(new WaitCommand(1000))
                .andThen(robot.getDrive().moveTo(0,0,0))
                .schedule();
    }

    @Override
    public void loop() {
        CommandScheduler.getInstance().run();
        robot.getDrive().getFollower().update();
    }
    public void stop(){
       CommandScheduler.getInstance().reset();
    }
}
