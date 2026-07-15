package org.firstinspires.ftc.teamcode.opModes;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@Configurable
@TeleOp(name = "Servo Test")
public class ServoTest extends OpMode {
    Servo turret1;
    Servo turret2;

    public static double pos = 0;
    @Override
    public void init() {
        turret1 = hardwareMap.get(Servo.class, "turret_l");
        turret2 = hardwareMap.get(Servo.class, "turret_r");
    }

    @Override
    public void loop() {
        turret1.setPosition(pos);
        turret2.setPosition(pos);
    }
}
