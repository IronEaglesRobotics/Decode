package org.firstinspires.ftc.teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.hardware.Bot;
@TeleOp(name = "aimTest")
public class aimTest extends OpMode {
    Bot robot;
    GamepadEx controller1;
    int count;
    boolean aprilCentric = false;
    boolean manualDrive = true;

    public static double kP = 0.0199;
    public static double kI = 0;
    public static double kD = 0.01;

    double headingIntegral = 0;
    double lastHeadingError = 0;

    @Override
    public void init() {
        controller1 = new GamepadEx(gamepad1);
        robot = new Bot().init(hardwareMap,controller1);
        robot.getDrive().getFollower().startTeleOpDrive(true);
        robot.getDrive().getFollower().update();
        CommandScheduler.getInstance().reset();
        CommandScheduler.getInstance().registerSubsystem(robot.getDrive());
        CommandScheduler.getInstance().registerSubsystem(robot.getLauncher());
        controller1.getGamepadButton(GamepadKeys.Button.Y)
                .toggleWhenPressed(
                        new InstantCommand(() -> aprilCentric = true),
                        new InstantCommand(() -> aprilCentric = false)
                );

    }

    @Override
    public void loop() {
        controller1.readButtons();
        if (manualDrive) {
            double driveY = controller1.getLeftY();
            double driveX = -controller1.getLeftX();
            double manualTurn = -controller1.getRightX();
            double turnOutput = manualTurn;// default

            if (aprilCentric) {
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
                } else {
                    turnOutput = manualTurn;
                }
                robot.getDrive().getFollower().setTeleOpDrive(
                        driveY,
                        driveX,
                        turnOutput,
                        true
                );
            }
        }
            telemetry.addData("count", count);
            telemetry.addData("Tx: ", robot.getCamera().getFiducialAngle());
            telemetry.addData("Target Area", robot.getCamera().getTargetArea());
            CommandScheduler.getInstance().run();
            telemetry.update();

    }
}
