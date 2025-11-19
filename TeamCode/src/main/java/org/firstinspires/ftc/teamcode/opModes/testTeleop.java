package org.firstinspires.ftc.teamcode.opModes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.command.button.GamepadButton;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.hardware.Bot;

import java.util.function.Supplier;

@TeleOp(name = "test teleop")
public class testTeleop extends OpMode {
    Bot robot;
    GamepadEx controller1;
    GamepadEx controller2;
    boolean isBot = true;
    boolean manualDrive = true;
    public Supplier<Command> toShoot;


    @Override
    public void init() {
        controller1 = new GamepadEx(gamepad1);
        robot = new Bot().init(hardwareMap,controller1);
        controller2 = new GamepadEx(gamepad2);
        robot.getDrive().getFollower().update();
        CommandScheduler.getInstance().reset();
        CommandScheduler.getInstance().registerSubsystem(robot.getDrive());
        CommandScheduler.getInstance().registerSubsystem(robot.getLauncher());
        toShoot = ()-> robot.getDrive().moveTo(56,95.5,135);
        robot.getCamera().getMotif()
                .whenFinished(()->
                    controller1.getGamepadButton(GamepadKeys.Button.A)
                    .toggleWhenPressed(
                            robot.getIntake().start()
                                    .alongWith(robot.loading())
                                    .andThen(robot.getIntake().stop())
                            ,robot.getIntake().stop()
                    ))
                .schedule();
//        controller1.getGamepadButton(GamepadKeys.Button.A)
//                .whenPressed(robot.getIntake().start());
//        controller1.getGamepadButton(GamepadKeys.Button.Y)
//                        .whenPressed(robot.loading());
//        controller1.getGamepadButton(GamepadKeys.Button.A)
//                .toggleWhenPressed(
//                        robot.getIntake().start()
//                                .alongWith(robot.loading())
//                                .andThen(robot.getIntake().stop())
//                        ,robot.getIntake().stop()
//                );
//        controller1.getGamepadButton(GamepadKeys.Button.B)
//                .whenPressed(robot.getIntake().stop());
        controller1.getGamepadButton(GamepadKeys.Button.X)
                .whenPressed(robot.getIntake().reverse());
        controller2.getGamepadButton(GamepadKeys.Button.X)
                .whenPressed(robot.getLauncher().flywheelOn(false));
        controller2.getGamepadButton(GamepadKeys.Button.Y)
                .whenPressed(robot.getLauncher().flywheelOff());
        controller2.getGamepadButton(GamepadKeys.Button.RIGHT_STICK_BUTTON)
                .whenPressed(robot.getLauncher().shoot());
        controller2.getGamepadButton(GamepadKeys.Button.DPAD_LEFT)
                .whenPressed(robot.getLauncher().toNext());
        controller2.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(robot.getLauncher().flywheelOn(true));
        controller2.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT)
                .whenPressed(robot.getLauncher().toShoot());
        controller2.getGamepadButton(GamepadKeys.Button.DPAD_DOWN)
                .whenPressed(robot.getLauncher().toZero());
        controller1.getGamepadButton(GamepadKeys.Button.LEFT_STICK_BUTTON)
                .whenPressed(robot.getDrive().turnTo(Math.toRadians(90)));
        controller2.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(robot.getLauncher().fire());
        controller1.getGamepadButton(GamepadKeys.Button.DPAD_UP)
                .whenPressed(robot.getLauncher().plusVelo());
        controller1.getGamepadButton(GamepadKeys.Button.DPAD_DOWN)
                .whenPressed(robot.getLauncher().minusVelo());
        controller2.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
                .whenPressed(robot.aim());
    }
    @Override
    public void loop(){
        controller1.readButtons();
        controller2.readButtons();
        if (manualDrive){
            robot.getDrive().getFollower().setTeleOpDrive(
                    controller1.getLeftY(),
                    -controller1.getLeftX(),
                    -controller1.getRightX(),
                    true);
        }
        if (controller2.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)){
            manualDrive = false;
            robot.getDrive().moveTo(56,95,135)
                    .whenFinished(()->manualDrive = true)
                    .schedule();
        }

        CommandScheduler.getInstance().run();
        telemetry.addData("color1",robot.getLauncher().getColor(robot.getLauncher().cs1));
        telemetry.addData("color2",robot.getLauncher().getColor(robot.getLauncher().cs2));
        telemetry.addData("current", robot.getLauncher().spinner.getCurrentPosition());
        telemetry.addData("target", robot.getLauncher().current);
        telemetry.addData("order", robot.getCamera().getOrder());
        telemetry.addData("flywheel 1", robot.getLauncher().flyWheel1.getVelocity());
        telemetry.addData("flywheel 2", robot.getLauncher().flyWheel2.getVelocity());
        telemetry.addData("is field centric",!isBot);
        telemetry.addData("Tx: ", robot.getCamera().getFiducialAngle());
        telemetry.addLine(robot.getLauncher().getTelemetry());
        FtcDashboard.getInstance().startCameraStream(robot.getCamera().getLimelight(),60);
        telemetry.update();
    }

    @Override
    public void stop(){
        CommandScheduler.getInstance().cancelAll();
        CommandScheduler.getInstance().reset();
        super.stop();
    }
}

