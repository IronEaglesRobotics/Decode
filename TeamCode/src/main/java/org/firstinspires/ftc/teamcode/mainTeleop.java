package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="MainTeleOp")
public class mainTeleop extends LinearOpMode {
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;
    @Override
    public void runOpMode() throws InterruptedException {
        frontLeft = hardwareMap.get(DcMotor.class,"fl");
        frontRight = hardwareMap.get(DcMotor.class, "fr");
        backLeft = hardwareMap.get(DcMotor.class, "bl");
        backRight = hardwareMap.get(DcMotor.class, "br");

        //Variables

        //Movement
        float lStickY = gamepad1.left_stick_y;
        float lStickX = gamepad1.left_stick_x;
        //Turning
        float rStickY =  gamepad1.right_stick_y;
        float rStickX = gamepad1.right_stick_x;

        waitForStart();

        frontLeft.setPower(lStickY + lStickX - rStickX);
        frontRight.setPower(lStickY - lStickX + rStickX);
        backLeft.setPower(lStickY + lStickX - rStickX);
        backRight.setPower(lStickY - lStickX + rStickX);

    }
}
