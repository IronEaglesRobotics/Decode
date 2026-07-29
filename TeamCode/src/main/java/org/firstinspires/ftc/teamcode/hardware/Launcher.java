package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.opencv.core.Mat;

public class Launcher extends SubsystemBase {

    private MotorEx launcher1;
    private MotorEx launcher2;
    private Servo turret1;
    private Servo turret2;
    private Servo hood;
    private Drive drive;

    public Launcher(HardwareMap hardwareMap, Drive temp){
        launcher1 = new MotorEx(hardwareMap, "launcher_l", 28, 6000);
        launcher2 = new MotorEx(hardwareMap, "launcher_r", 28, 6000);
        launcher1.motor.setDirection(DcMotorSimple.Direction.REVERSE);
        launcher2.motor.setDirection(DcMotorSimple.Direction.REVERSE);

        turret1 = hardwareMap.get(Servo.class, "turret_l");
        turret2 = hardwareMap.get(Servo.class, "turret_r");

        hood = hardwareMap.get(Servo.class,"hood");

        drive = temp;
    }

    public void periodic(){
        double deltaX = Math.abs(0 - drive.getX());
        double deltaY = Math.abs(144 - drive.getY());

        double angle = Math.toDegrees(Math.atan2(deltaX, deltaY));

        angle = angle - drive.getZ();

        angle = Math.min(Math.max(angle,0),180);

        turret1.setPosition(angle/180);
        turret2.setPosition(angle/180);

        double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

        double speed = 3200 + (1800/137.18) * (distance + 42.43);
        double hoodPos = (.45/137.18) * (distance + 42.43);

        launcher1.setVelocity(speed);
        launcher2.setVelocity(speed);

        hood.setPosition(hoodPos);
    }
}
