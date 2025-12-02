package org.firstinspires.ftc.teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.hardware.Bot;
@TeleOp(name = "aimTest")
public class aimTest extends OpMode {
    Bot robot;
    GamepadEx controller1;
    int count;

    @Override
    public void init() {
        controller1 = new GamepadEx(gamepad1);
        robot = new Bot().init(hardwareMap,controller1);
        robot.getDrive().getFollower().startTeleOpDrive(true);
        robot.getDrive().getFollower().update();
        CommandScheduler.getInstance().reset();
        CommandScheduler.getInstance().registerSubsystem(robot.getDrive());
        CommandScheduler.getInstance().registerSubsystem(robot.getLauncher());
        controller1.getGamepadButton(GamepadKeys.Button.A)
                .toggleWhenPressed(robot.aim().whenFinished(()->count++));

    }

    @Override
    public void loop() {
        controller1.readButtons();
        robot.getDrive().getFollower().setTeleOpDrive(
                controller1.getLeftY(),
                -controller1.getLeftX(),
                -controller1.getRightX(),
                true);
        telemetry.addData("count",count);
        telemetry.addData("Tx: ", robot.getCamera().getFiducialAngle());
        CommandScheduler.getInstance().run();
        telemetry.update();
    }
}
