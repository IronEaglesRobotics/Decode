package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.hardware.lynx.LynxController;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import org.firstinspires.ftc.robotcore.internal.files.DataLogger;
import org.firstinspires.ftc.teamcode.hardware.Bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configurable
@TeleOp(name = "spinnerTest",group = "Tests")
public class spinnerTest extends OpMode {
    public Motor spinner;
    public static int halfDelta = 238;
    public static int fullDelta = 475;
    public int target;
    public static int offset = 30;
    public static double speed = .5;
    GamepadEx controller;
    JoinedTelemetry joinedTelemetry;
    List<LynxModule> lynxController;
    List<Double> voltages = new ArrayList<>();
    List<Double> current = new ArrayList<>();
    DataLogger logger = new DataLogger("MyLogFile");

    public spinnerTest() throws IOException {
    }

    @Override
    public void init() {
        spinner = new Motor(hardwareMap,"spinner", Motor.GoBILDA.RPM_223);
        spinner.setRunMode(Motor.RunMode.VelocityControl);
        spinner.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        controller = new GamepadEx(gamepad1);
        joinedTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(),telemetry);
        lynxController = hardwareMap.getAll(LynxModule.class);
    }

    @Override
    public void loop() {
        controller.readButtons();
        if(controller.wasJustPressed(GamepadKeys.Button.A)) {
            target += fullDelta;
        }
        else if(controller.wasJustPressed(GamepadKeys.Button.B)){
            target += halfDelta;
        }
        else if(controller.wasJustPressed(GamepadKeys.Button.X)) {
            target = 0;
        }
        else if(controller.wasJustPressed(GamepadKeys.Button.Y)) {
            spinner.stopAndResetEncoder();
        }
        if (spinner.getCurrentPosition() < target - offset || spinner.getCurrentPosition() > target + offset){
            spinner.set(speed * Math.signum(target - spinner.getCurrentPosition()));
        }
        else {
            spinner.set(0);
        }
        for (int i = 0; i < lynxController.size(); i++){
            voltages.add(lynxController.get(i).getInputVoltage(VoltageUnit.VOLTS));
            current.add(lynxController.get(i).getCurrent(CurrentUnit.AMPS));
        }
        try {
            logger.addDataLine(System.currentTimeMillis(),voltages,current);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        joinedTelemetry.addData("target",target);
        joinedTelemetry.addData("current", spinner.getCurrentPosition());
    }
}
