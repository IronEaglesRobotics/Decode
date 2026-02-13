package org.firstinspires.ftc.teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "wheel test",group = "Tests")
public class wheelTest extends OpMode {
    DcMotor lf;
    DcMotor rf;
    DcMotor lb;
    DcMotor rb;
    @Override
    public void init() {
        lf = hardwareMap.get(DcMotor.class,"lf");
        lb = hardwareMap.get(DcMotor.class,"lr");
        rf = hardwareMap.get(DcMotor.class,"rf");
        rb = hardwareMap.get(DcMotor.class,"rr");
        lb.setDirection(DcMotorSimple.Direction.REVERSE);
        lf.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void loop() {
        if (gamepad1.a){
            lf.setPower(1);
        }
        else {
            lf.setPower(0);
        }
        if (gamepad1.b){
            lb.setPower(1);
        }
        else {
            lb.setPower(0);
        }
        if (gamepad1.x){
            rf.setPower(1);
        }
        else {
            rf.setPower(0);
        }
        if (gamepad1.y){
            rb.setPower(1);
        }
        else {
            rb.setPower(0);
        }
        float drive = -gamepad1.left_stick_y;
        float strafe = -gamepad1.left_stick_x;
        float turn = gamepad1.right_stick_x;

        rb.setPower(drive + strafe + turn);
        rf.setPower(drive - strafe + turn);
        lb.setPower(drive - strafe - turn);
        lf.setPower(drive + strafe - turn);
    }
}
