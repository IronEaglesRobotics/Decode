package org.firstinspires.ftc.teamcode.opModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "wheel test")
public class wheeltest extends OpMode {

    private DcMotor rf;
    private DcMotor lf;
    private DcMotor rr;
    private DcMotor lr;

    @Override
    public void init() {
        rf = hardwareMap.get(DcMotor.class, "rf");
        lf = hardwareMap.get(DcMotor.class, "lf");
        rr = hardwareMap.get(DcMotor.class, "rr");
        lr = hardwareMap.get(DcMotor.class, "lr");
    }

    @Override
    public void loop() {
        rf.setPower(gamepad1.a ? 1 : 0);
        lf.setPower(gamepad1.b ? 1 : 0);
        rr.setPower(gamepad1.x ? 1 : 0);
        lr.setPower(gamepad1.y ? 1 : 0);
    }
}
