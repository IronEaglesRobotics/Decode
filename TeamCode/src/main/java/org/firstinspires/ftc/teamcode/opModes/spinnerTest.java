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

    public static int halfDelta = 238;
    public static int fullDelta = 475;

    @Override
    public void init() {
      spinner = hardwareMap.get(DcMotorEx.class, "spinner");
      spinner.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
      spinner.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void loop() {
        if(gamepad1.a) {
            while(spinner.getCurrentPosition() < fullDelta){
                spinner.setPower(1);
            }


        }
        else if(gamepad1.b){
            while(spinner.getCurrentPosition() < (fullDelta*2)){
                spinner.setPower(1);
            }
        }
        else if(gamepad1.b) {
            while (spinner.getCurrentPosition() < (fullDelta * 3)) {
                spinner.setPower(1);
            }
        }
        else if(gamepad1.dpad_down){
                while(spinner.getCurrentPosition() > 0) {
                    spinner.setPower(-1);
                }
        }
        else {
            spinner.setPower(0);
        }
    }
}
