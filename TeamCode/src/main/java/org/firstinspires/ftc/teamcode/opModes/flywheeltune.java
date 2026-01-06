package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

@Configurable
@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Flywheel Tune ")
public class flywheeltune extends OpMode {

    Servo pusher;

    public static double push;

    public static double starting;



    @Override
    public void init() {
        pusher = hardwareMap.servo.get("pusher");
        pusher.setDirection(Servo.Direction.REVERSE);
    }

    @Override
    public void loop() {
        pusher.setPosition(0);
    }
}
