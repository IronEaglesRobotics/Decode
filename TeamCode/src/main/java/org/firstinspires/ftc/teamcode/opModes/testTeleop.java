package org.firstinspires.ftc.teamcode.opModes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.hardware.Bot;
import org.firstinspires.ftc.teamcode.hardware.Storage;

import java.util.function.Supplier;
@Configurable
@TeleOp(name = "test teleop")
public class testTeleop extends OpMode {
    Bot robot;
    GamepadEx controller1;
    GamepadEx controller2;
    boolean isBot = true;
    boolean manualDrive = true;
    public Supplier<Command> toShoot;

    public static double kP = 0.0199;
    public static double kI = 0;
    public static double kD = 0.01;

    double headingIntegral = 0;
    double lastHeadingError = 0;

    boolean aprilCentric = false;


    @Override
    public void init() {
        controller1 = new GamepadEx(gamepad1);
        robot = new Bot().init(hardwareMap,controller1);
        controller2 = new GamepadEx(gamepad2);
        robot.getDrive().getFollower().setStartingPose(Storage.instance.pose);
        robot.getDrive().getFollower().update();
        CommandScheduler.getInstance().reset();
        CommandScheduler.getInstance().registerSubsystem(robot.getDrive());
        CommandScheduler.getInstance().registerSubsystem(robot.getLauncher());
        toShoot = ()-> robot.getDrive().moveTo(56,95.5,135);

        controller1.getGamepadButton(GamepadKeys.Button.A)
                .toggleWhenPressed(
                        robot.getLauncher().toZero()
                                .andThen(robot.getIntake().start()
                                .alongWith(robot.loading()))
                                .andThen(robot.getIntake().stop())
                        ,robot.getIntake().stop()
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
        controller2.getGamepadButton(GamepadKeys.Button.DPAD_UP)
                .whenPressed(robot.getLauncher().flywheelAuto(true));
        controller2.getGamepadButton(GamepadKeys.Button.LEFT_STICK_BUTTON)
                .whenPressed(robot.getLauncher().backShoot());
        controller1.getGamepadButton(GamepadKeys.Button.Y)
                .toggleWhenPressed(
                        new InstantCommand(() -> aprilCentric = true),
                        new InstantCommand(() -> aprilCentric = false)
                );
        robot.getCamera().getMotif().schedule();
    }
    @Override
    public void loop(){
        controller1.readButtons();
        controller2.readButtons();
        if (manualDrive){
            double driveY = controller1.getLeftY();
            double driveX = -controller1.getLeftX();
            double manualTurn = -controller1.getRightX();
            double turnOutput = manualTurn;// default

            if(aprilCentric) {
                double tx = robot.getCamera().getFiducialAngle(); // Limelight horizontal offset

                // If Limelight sees a tag:
                if (!Double.isNaN(tx)) {

                    // PID compute
                    double error = tx;
                    headingIntegral += error;
                    double derivative = error - lastHeadingError;
                    lastHeadingError = error;

                    double pid = (kP * error) + (kI * headingIntegral) + (kD * derivative);

                    // limit
                    pid = Math.max(-0.3, Math.min(0.3, pid));

                    turnOutput = -pid;
                }
                else{
                    turnOutput = manualTurn;
                }
            }

// Send controls (translation from driver, rotation from PID or manual)
            robot.getDrive().getFollower().setTeleOpDrive(
                    driveY,
                    driveX,
                    turnOutput,
                    true
            );
        }
        if (controller2.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)){
            manualDrive = false;
            robot.getDrive().moveTo(56,95,135)
                    .whenFinished(()->manualDrive = true)
                    .schedule();
        }

        if(controller1.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)){
            robot.getLauncher().fixPose();
        }



        CommandScheduler.getInstance().run();
        telemetry.addData("color1",robot.getLauncher().getColor(robot.getLauncher().cs1));
        telemetry.addData("color2",robot.getLauncher().getColor(robot.getLauncher().cs2));
        telemetry.addData("current", robot.getLauncher().spinner.getCurrentPosition());
        telemetry.addData("target", robot.getLauncher().pidTarget);
        telemetry.addData("order", robot.getCamera().getOrder());
        telemetry.addData("flywheel 1", robot.getLauncher().calculateVelo(robot.getLauncher().flyWheel1));
        telemetry.addData("flywheel 2", robot.getLauncher().calculateVelo(robot.getLauncher().flyWheel2));
        telemetry.addData("can Shoot", robot.getLauncher().canShoot());
        telemetry.addData("is field centric",!isBot);
        telemetry.addData("Tx: ", robot.getCamera().getFiducialAngle());
        telemetry.addLine(robot.getLauncher().getTelemetry());
        FtcDashboard.getInstance().startCameraStream(robot.getCamera().getLimelight(),20);
        telemetry.update();
    }

    @Override
    public void stop(){
        CommandScheduler.getInstance().cancelAll();
        CommandScheduler.getInstance().reset();
        super.stop();
    }
}

