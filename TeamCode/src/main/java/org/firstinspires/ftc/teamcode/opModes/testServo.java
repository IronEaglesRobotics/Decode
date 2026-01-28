package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

@Configurable
@TeleOp(name = "TestServo",group = "Tests")
public class testServo extends OpMode {

    CRServo pusher;

    public static double push;

    public static double starting;



    @Override
    public void init() {
        pusher = hardwareMap.get(CRServo.class, "quickLaunch");
    }

    @Override
    public void loop() {
        if(gamepad1.a){
            pusher.setPower(-1);
        }
        if(gamepad1.b){
            pusher.setPower(0);
        }
    }
}
