package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

@Configurable
@TeleOp(name = "jack test")
public class JackPractice extends OpMode {

    private boolean intakeOn = false;
    private boolean lastIntakeButtonPressed = false;
    private boolean flywheelOn = false;
    private boolean lastFlywheelButtonPressed = false;

    private DcMotor rf;
    private DcMotor lf;
    private DcMotor rr;
    private DcMotor lr;
    private DcMotor intake1;
    private DcMotor intake2;
    private MotorEx launcher1;
    private MotorEx launcher2;
    private Servo turret1;
    private Servo turret2;
    private Servo hood;
    private Servo stopper;
    public Limelight3A limelight;

    public static double turretPos = 0.5;
    public static double intakeSpeed = 1800;
    public static double launcherSpeed = 6000;
    public static double hoodPos = 0;

    @Override
    public void init() {
        rf = hardwareMap.get(DcMotor.class, "rf");
        lf = hardwareMap.get(DcMotor.class, "lf");
        rr = hardwareMap.get(DcMotor.class, "rr");
        lr = hardwareMap.get(DcMotor.class, "lr");

        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        lr.setDirection(DcMotorSimple.Direction.REVERSE);
        rf.setDirection(DcMotorSimple.Direction.REVERSE);
        rr.setDirection(DcMotorSimple.Direction.REVERSE);

        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intake1 = hardwareMap.get(DcMotor.class, "intake_l");
        intake2 = hardwareMap.get(DcMotor.class, "intake_r");

        launcher1 = new MotorEx(hardwareMap, "launcher_l", 28, 6000);
        launcher2 = new MotorEx(hardwareMap, "launcher_r", 28, 6000);
        launcher1.motor.setDirection(DcMotorSimple.Direction.REVERSE);
        launcher2.motor.setDirection(DcMotorSimple.Direction.REVERSE);

        turret1 = hardwareMap.get(Servo.class, "turret_l");
        turret2 = hardwareMap.get(Servo.class, "turret_r");

        hood = hardwareMap.get(Servo.class,"hood");

        limelight = hardwareMap.get(Limelight3A.class,"limelight");
    }

    @Override
    public void loop() {
        toggleDriveTrain();
        toggleIntake();
        turret();

    }
    private void toggleDriveTrain(){
        double y = -gamepad1.left_stick_y;
        double x = -gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;

        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1.0);

        double leftFrontPower = (y + x + rx) / denominator;
        double leftRearPower = (y - x + rx) / denominator;
        double rightFrontPower = (y - x - rx) / denominator;
        double rightRearPower = (y + x - rx) / denominator;

        lf.setPower(leftFrontPower);
        lr.setPower(leftRearPower);
        rf.setPower(rightFrontPower);
        rr.setPower(rightRearPower);
    }
    private void toggleIntake(){

        boolean currentIntakeButtonPressed = gamepad1.a;
        if (currentIntakeButtonPressed && !lastIntakeButtonPressed) {
            intakeOn = !intakeOn;
        }
        lastIntakeButtonPressed = currentIntakeButtonPressed;
        if (intakeOn) {
            intake1.setPower(intakeSpeed/(60*3.33)*28);
            intake2.setPower(intakeSpeed/(60*3.33)*28);
        } else {
            intake1.setPower(0);
            intake2.setPower(0);
        }
    }
    private void turret(){

        turret1.setPosition(turretPos);
        turret2.setPosition(turretPos);
    }
    private void launcher() {

        boolean currentFlywheelButtonPressed = gamepad1.b;
        if (currentFlywheelButtonPressed && !lastFlywheelButtonPressed) {
            flywheelOn = !flywheelOn;
        }
        lastFlywheelButtonPressed = currentFlywheelButtonPressed;
        if (flywheelOn) {
            launcher1.setVelocity((launcherSpeed / 60) * 28);
            launcher2.setVelocity((launcherSpeed / 60) * 28);
        } else {
            launcher1.stopMotor();
            launcher2.stopMotor();
        }
    }
    private void hood(){

    }

}
