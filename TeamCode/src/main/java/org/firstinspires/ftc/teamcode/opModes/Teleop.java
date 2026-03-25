package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.hardware.lynx.LynxController;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.ConditionalCommand;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.command.WaitUntilCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import org.firstinspires.ftc.robotcore.internal.files.DataLogger;
import org.firstinspires.ftc.teamcode.hardware.Bot;
import org.firstinspires.ftc.teamcode.hardware.Launcher;
import org.firstinspires.ftc.teamcode.hardware.Storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
@Configurable
@TeleOp(name = "Main Teleop")
public class Teleop extends OpMode {
    Bot robot;
    GamepadEx controller1;
    GamepadEx controller2;
    boolean isBot = true;
    boolean manualDrive = true;
    public Supplier<Command> toShoot;

    public static double kP = 0.0135;
    public static double kI = 0.002;
    public static double kD = 0.02;

    double headingIntegral = 0;
    double lastHeadingError = 0;

    boolean aprilCentric = false;
    // 1. Create Logger
    JoinedTelemetry panelsTelemetry;
    public static double speedCap = .75;

    @Override
    public void init() {
        controller1 = new GamepadEx(gamepad1);
        robot = new Bot().init(hardwareMap,controller1);
        controller2 = new GamepadEx(gamepad2);
        if (Storage.getInstance().resetSpindexer){
            robot.getLauncher().resetEncoder();
        }
        robot.getDrive().getFollower().setStartingPose(Storage.getInstance().pose);
        robot.getDrive().getFollower().update();
        CommandScheduler.getInstance().reset();
        CommandScheduler.getInstance().registerSubsystem(robot.getDrive());
        CommandScheduler.getInstance().registerSubsystem(robot.getLauncher());
        toShoot = ()-> robot.getDrive().moveTo(0,0,0).withTimeout(2000);
        controller1.getGamepadButton(GamepadKeys.Button.A)
                .toggleWhenPressed(
                        robot.getLauncher().toZero()
                                .andThen(robot.getIntake().start())
                                .andThen(robot.loading())
                                .andThen(robot.getIntake().stop())
                                .andThen(robot.getLauncher().backShoot())
                        ,robot.getIntake().stop().andThen(robot.getLauncher().backShoot())
                );
        controller1.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(robot.getIntake().stop());
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
        controller2.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(robot.getLauncher().fire());
        controller1.getGamepadButton(GamepadKeys.Button.DPAD_UP)
                .whenPressed(robot.getLauncher().plusVelo());
        controller1.getGamepadButton(GamepadKeys.Button.DPAD_DOWN)
                .whenPressed(robot.getLauncher().minusVelo());
        controller1.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .toggleWhenPressed(robot.getLauncher().Park(),robot.getLauncher().UnPark());
        controller2.getGamepadButton(GamepadKeys.Button.DPAD_UP)
                .whenPressed(robot.getLauncher().setLaunch());
        controller2.getGamepadButton(GamepadKeys.Button.LEFT_STICK_BUTTON)
                .whenPressed(robot.getLauncher().backShoot());
        controller1.getGamepadButton(GamepadKeys.Button.Y)
                .toggleWhenPressed(
                        new InstantCommand(() -> aprilCentric = true),
                        new InstantCommand(() -> aprilCentric = false)
                );
        panelsTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(),telemetry);
    }
    @Override
    public void loop(){
        controller1.readButtons();
        controller2.readButtons();
        robot.getDrive().getFollower().update();

        if(gamepad1.right_bumper){
            robot.getLauncher().resetEncoder();
        }
        if(gamepad1.dpad_left){
            robot.getLauncher().resetSpindexer();
        }
        if (manualDrive && !robot.getDrive().getFollower().getTeleopDrive()){
            robot.getDrive().getFollower().startTeleopDrive(true);
        }
        if (manualDrive){
            double driveY = controller1.getLeftY();
            double driveX = -controller1.getLeftX();
            double manualTurn = -controller1.getRightX() * speedCap;
            double turnOutput = manualTurn;// default

            if(aprilCentric) {
                double tx = robot.getCamera().getFiducialAngle() + robot.getCamera().getArea() < .6 ? -1.8 : 0; // Limelight horizontal offset

                headingIntegral += tx;
                double derivative = tx - lastHeadingError;
                lastHeadingError = tx;

                double pid = (kP * tx) + (kI * headingIntegral) + (kD * derivative);

                // limit
                pid = Math.max(-0.6, Math.min(0.6, pid));

                turnOutput = -pid;
            }

            // Send controls (translation from driver, rotation from PID or manual)
            robot.getDrive().getFollower().setTeleOpDrive(
                    driveY,
                    driveX,
                    turnOutput,
                    true
            );
        }
        if (controller1.wasJustPressed(GamepadKeys.Button.LEFT_STICK_BUTTON)){
            if (manualDrive) {
                manualDrive = false;
                // Schedule the command ONCE
                toShoot.get()
                        .whenFinished(() -> manualDrive = true)
                        .schedule();
            }
        }


        CommandScheduler.getInstance().run();
//        telemetry.addData("color1",robot.getLauncher().getColor(robot.getLauncher().cs1));
//        telemetry.addData("color2",robot.getLauncher().getColor(robot.getLauncher().cs2));
//        panelsTelemetry.addData("current", robot.getLauncher().throughBore.getCurrentPosition());
//        panelsTelemetry.addData("target", Launcher.pidTarget);
//        panelsTelemetry.addData("flywheel 1", robot.getLauncher().calculateVelo(robot.getLauncher().flyWheel1));
//        panelsTelemetry.addData("flywheel 2", robot.getLauncher().calculateVelo(robot.getLauncher().flyWheel2));
//        panelsTelemetry.addData("Drive", manualDrive);
//        panelsTelemetry.addData("follower Drive", robot.getDrive().getFollower().getTeleopDrive());
        telemetry.addData("Loop Time (ms)", getRuntime() * 1000);
        resetRuntime();
        telemetry.update();
    }

    @Override
    public void stop() {
        CommandScheduler.getInstance().cancelAll();
        CommandScheduler.getInstance().reset();
        super.stop();
    }
}