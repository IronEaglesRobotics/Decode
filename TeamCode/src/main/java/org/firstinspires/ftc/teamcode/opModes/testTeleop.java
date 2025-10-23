package org.firstinspires.ftc.teamcode.opModes;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.hardware.Bot;

@TeleOp(name = "test teleop")
public class testTeleop extends OpMode {
    Bot robot;
    GamepadEx controller1;
    GamepadEx controller2;

    @Override
    public void init() {
        robot = new Bot().init(hardwareMap,new Pose(0,0,0),"red",controller1);
        controller1 = new GamepadEx(gamepad1);
        controller2 = new GamepadEx(gamepad2);
    }
    @Override
    public void loop(){
        robot.getDrive().getFollower().update();
        robot.getDrive().setVector();
        controller1.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(robot.getIntake().start());
        controller1.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(robot.getIntake().stop());
        controller1.getGamepadButton(GamepadKeys.Button.X)
                .whenPressed(robot.getLauncher().flywheelOn());
        controller1.getGamepadButton(GamepadKeys.Button.Y)
                .whenPressed(robot.getLauncher().flywheelOff());
        CommandScheduler.getInstance().run();



    }



}

