package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.robotcore.internal.files.DataLogger;
import org.firstinspires.ftc.teamcode.hardware.Bot;
import org.firstinspires.ftc.teamcode.hardware.Launcher;
import org.firstinspires.ftc.teamcode.hardware.Storage;

import java.io.IOException;
import java.util.function.Supplier;

@Configurable
@TeleOp(name = "Test Teleop")
public class TeleOpTest extends OpMode {
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

    // 1. Create Logger
    DataLogger logger = new DataLogger("MyLogFile");
    JoinedTelemetry panelsTelemetry;
    public static double speedCap = .75;

    public TeleOpTest() throws IOException {
    }

    @Override
    public void init() {
        controller1 = new GamepadEx(gamepad1);
        robot = new Bot().init(hardwareMap,controller1);
        robot.getDrive().getFollower().setStartingPose(Storage.getInstance().pose);
        robot.getDrive().getFollower().update();
        CommandScheduler.getInstance().reset();
        CommandScheduler.getInstance().registerSubsystem(robot.getDrive());
        CommandScheduler.getInstance().registerSubsystem(robot.getLauncher());
        controller1.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT)
                .whenPressed(robot.getLauncher().toShoot());
        controller1.getGamepadButton(GamepadKeys.Button.DPAD_LEFT)
                .whenPressed(robot.getLauncher().toNext());
        controller1.getGamepadButton(GamepadKeys.Button.DPAD_UP)
                .whenPressed(robot.getLauncher().backNext());
        controller1.getGamepadButton(GamepadKeys.Button.DPAD_DOWN)
                .whenPressed(robot.getLauncher().backShoot());
        controller1.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(robot.getLauncher().toZero());
        controller1.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(robot.loading());
        panelsTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(),telemetry);
    }
    @Override
    public void loop() {
        robot.getAllHubs().get(0).clearBulkCache();
        robot.getAllHubs().get(1).clearBulkCache();
        controller1.readButtons();
        robot.getDrive().getFollower().update();
//        List<Double> voltages = new ArrayList<>();
//        List<Double> current = new ArrayList<>();
//
//        for (int i = 0; i < lynxController.size(); i++){
//            voltages.add(lynxController.get(i).getInputVoltage(VoltageUnit.VOLTS));
//            current.add(lynxController.get(i).getCurrent(CurrentUnit.AMPS));
//        }
        double driveY = controller1.getLeftY();
        double driveX = -controller1.getLeftX();
        double manualTurn = -controller1.getRightX() * speedCap;
        double turnOutput = manualTurn;// default

        // Send controls (translation from driver, rotation from PID or manual)
        robot.getDrive().getFollower().setTeleOpDrive(
            driveY,
            driveX,
            turnOutput,
            true
        );



        CommandScheduler.getInstance().run();
//        panelsTelemetry.addData("color1",robot.getLauncher().getColor(robot.getLauncher().cs1));
//        panelsTelemetry.addData("color2",robot.getLauncher().getColor(robot.getLauncher().cs2));
//        panelsTelemetry.addData("current", Launcher.spinnercurrent);
//        panelsTelemetry.addData("target", Launcher.pidTarget);
//        panelsTelemetry.addData("flywheel 1", robot.getLauncher().calculateVelo(robot.getLauncher().flyWheel1));
//        panelsTelemetry.addData("flywheel 2", robot.getLauncher().calculateVelo(robot.getLauncher().flyWheel2));
//        panelsTelemetry.addData("flywheel speed", robot.getLauncher().getSpeed1());
        panelsTelemetry.addData("index", Launcher.servoIndex);
        panelsTelemetry.update();
    }

    @Override
    public void stop() {
        CommandScheduler.getInstance().cancelAll();
        CommandScheduler.getInstance().reset();
        logger.close();
        super.stop();
    }
}