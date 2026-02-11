package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.hardware.Bot;

@Configurable
@TeleOp(name = "spinnerTest",group = "Tests")
public class spinnerTest extends OpMode {
    public DcMotor spinner;
    Gamepad gamepad1;

    public static int chamber1;
    public static int chamber2;
    public static int chamber3;

    @Override
    public void init() {
      spinner = hardwareMap.get(DcMotorEx.class, "spinner");
      spinner.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
      spinner.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      spinner.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void loop() {

        final int[] index = {0};
        if(gamepad1.a) {
            spinner.setTargetPosition(chamber1);
            spinner.setPower(1);

        }
        else if(gamepad1.b){
            spinner.setTargetPosition(chamber2);
            spinner.setPower(1);
        }
        else if(gamepad1.b){
            spinner.setTargetPosition(chamber3);
            spinner.setPower(1);
        }
        else {
            spinner.setPower(0);
        }
    }
}
