package org.firstinspires.ftc.teamcode.opModes;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
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
        controller1 = new GamepadEx(gamepad1);
        robot = new Bot().init(hardwareMap,new Pose(0,0,0),"red",controller1);
        controller2 = new GamepadEx(gamepad2);
        robot.getDrive().getFollower().update();
        CommandScheduler.getInstance().enable();
        CommandScheduler.getInstance().reset();
    }
    @Override
    public void loop(){
        robot.getDrive().setVector();
        controller1.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(robot.getIntake().start());
        controller1.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(robot.getIntake().stop());
        controller1.getGamepadButton(GamepadKeys.Button.X)
                .whenPressed(robot.getLauncher().flywheelOn(false));
        controller1.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
                .whenPressed(robot.getLauncher().flywheelOn(false));
        controller1.getGamepadButton(GamepadKeys.Button.Y)
                .whenPressed(robot.getLauncher().flywheelOff());
        controller1.getGamepadButton(GamepadKeys.Button.RIGHT_STICK_BUTTON)
                .whenPressed(robot.getLauncher().shoot());
        controller1.getGamepadButton(GamepadKeys.Button.DPAD_UP)
                .whenPressed(robot.loading());
        controller1.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .whenPressed(robot.aim());
        controller1.getGamepadButton(GamepadKeys.Button.DPAD_LEFT)
                .whenPressed(new InstantCommand(()->robot.getLauncher().fan()));
        controller1.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT)
                .whenPressed(new InstantCommand(()->robot.getLauncher().toShoot()));
        controller1.getGamepadButton(GamepadKeys.Button.DPAD_DOWN)
                .whenPressed(new InstantCommand(()->robot.getLauncher().toZero()));
        robot.getLauncher().periodic();
        CommandScheduler.getInstance().run();
        telemetry.addData("color1: ",robot.getLauncher().getColor(robot.getLauncher().cs1));
        telemetry.addData("color2: ",robot.getLauncher().getColor(robot.getLauncher().cs2));
        telemetry.addData("current", robot.getLauncher().spinner.getCurrentPosition());
        telemetry.addData("target", robot.getLauncher().current);
        telemetry.addData("time", System.currentTimeMillis());
        telemetry.update();
    }

    @Override
    public void stop(){
        CommandScheduler.getInstance().cancelAll();
        super.stop();
    }
}

