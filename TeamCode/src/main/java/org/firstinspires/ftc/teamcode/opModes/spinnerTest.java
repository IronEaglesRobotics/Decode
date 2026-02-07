package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.hardware.Bot;
@Configurable
@TeleOp(name = "spinnerTest",group = "Tests")
public class spinnerTest extends LinearOpMode {
    DcMotorEx spinner;
    public static int target = 0;
    public static double kp = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        spinner = hardwareMap.get(DcMotorEx.class, "spinner");
        spinner.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        spinner.setPositionPIDFCoefficients(kp);
        spinner.setTargetPosition(0);
        spinner.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        waitForStart();
        spinner.setTargetPosition(target);
        spinner.setPower(.5);
        while (spinner.isBusy()){
            wait(1);
        }
        spinner.setPower(0);
    }
}
