package org.firstinspires.ftc.teamcode.opModes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.command.button.GamepadButton;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.hardware.Bot;

@TeleOp(name = "test teleop")
public class testTeleop extends OpMode {
    Bot robot;
    GamepadEx controller1;
    GamepadEx controller2;
    boolean isBot = true;



    @Override
    public void init() {
        controller1 = new GamepadEx(gamepad1);
        robot = new Bot().init(hardwareMap,new Pose(0,0,0),"red",controller1);
        controller2 = new GamepadEx(gamepad2);
        robot.getDrive().getFollower().update();
        CommandScheduler.getInstance().reset();
        CommandScheduler.getInstance().registerSubsystem(robot.getDrive());
        CommandScheduler.getInstance().registerSubsystem(robot.getLauncher());
        robot.getCamera().getMotif().schedule();
        controller1.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(robot.getIntake().start());
        controller1.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(robot.getIntake().stop());
        controller1.getGamepadButton(GamepadKeys.Button.X)
                .whenPressed(robot.getLauncher().flywheelOn(false));
        controller1.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
                .whenPressed(robot.getLauncher().flywheelOff());
        controller1.getGamepadButton(GamepadKeys.Button.Y)
                .whenPressed(robot.getLauncher().stop());
//        controller1.getGamepadButton(GamepadKeys.Button.RIGHT_STICK_BUTTON)
//                .whenPressed(robot.getLauncher().shoot());
//        controller1.getGamepadButton(GamepadKeys.Button.DPAD_UP)
//                .whenPressed(robot.loading());
//        controller1.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
//                .whenPressed(robot.getLauncher().shoot());
        controller1.getGamepadButton(GamepadKeys.Button.DPAD_LEFT)
                .whenPressed(robot.getLauncher().toNext());
        controller1.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT)
                .whenPressed(robot.getLauncher().toShoot());
        controller1.getGamepadButton(GamepadKeys.Button.DPAD_DOWN)
                .whenPressed(robot.getLauncher().toZero());
        if(controller1.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > .1){
            isBot = false;
        }
        if(controller1.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > .1){
            isBot = true;
        }
    }
    @Override
    public void loop(){
        controller1.readButtons();
        controller2.readButtons();
        robot.getDrive().getFollower().setTeleOpDrive(controller1.getLeftY(),-controller1.getLeftX(),-controller1.getRightX(),isBot);

        if (controller1.wasJustPressed(GamepadKeys.Button.DPAD_UP)){
            robot.loading().schedule();
        }

        if (controller1.wasJustPressed(GamepadKeys.Button.LEFT_STICK_BUTTON)){
            robot.aim().schedule();
        }

        if(gamepad1.left_bumper){
            robot.getLauncher().shoot();
        }
        else if (controller1.wasJustReleased(GamepadKeys.Button.LEFT_BUMPER)){
            robot.getLauncher().retract();
        }

        CommandScheduler.getInstance().run();
        telemetry.addData("color1",robot.getLauncher().getColor(robot.getLauncher().cs1));
        telemetry.addData("color2",robot.getLauncher().getColor(robot.getLauncher().cs2));
        telemetry.addData("current", robot.getLauncher().spinner.getCurrentPosition());
        telemetry.addData("target", robot.getLauncher().current);
        telemetry.addData("order", robot.getCamera().getOrder());
        telemetry.addLine(robot.getLauncher().getTelemetry());
        FtcDashboard.getInstance().startCameraStream(robot.getCamera().getLimelight(),60);
        telemetry.update();
    }

    @Override
    public void stop(){
        CommandScheduler.getInstance().cancelAll();
        super.stop();
    }
}

