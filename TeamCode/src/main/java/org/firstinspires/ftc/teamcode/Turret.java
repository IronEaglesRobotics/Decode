package org.firstinspires.ftc.teamcode;

//import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.seattlesolvers.solverslib.controller.PIDController;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import lombok.Getter;

//@Config
@Configurable
@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Turret")
public class Turret extends OpMode {
    @Getter
    private Follower follower;
    private GamepadEx controller1;
    private DcMotorEx turret;

    public int ticks = 0;

    public static double MOTOR_INTERNAL_GEARBOX = 1.0;
    public static double EXTERNAL_GEAR_RATIO = 10;
    public static double ENCODER_TICKS_PER_REV = 145.1;
    public static double MINPOWER = .15;

    public static double TURRETSTARTINGOFFSET = 68; // NEEDS TO CHANGE

    private PIDController pid;
    public static double p = .8, i = 0, d = 0.01;
    public static double TICKS_PER_DEGREE = (ENCODER_TICKS_PER_REV * MOTOR_INTERNAL_GEARBOX * EXTERNAL_GEAR_RATIO) / 360.0;


    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(135, 8, Math.toRadians(90)));
        follower.update();
        controller1 = new GamepadEx(gamepad1);

        this.turret = hardwareMap.get(DcMotorEx.class, "turret");
        turret.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        turret.setTargetPosition(0);
        turret.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        turret.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        this.pid = new PIDController(p, i, d);
    }

    @Override
    public void start() {
        follower.startTeleopDrive();

    }

    @Override
    public void loop() {
        controller1.readButtons();
        follower.update();
        follower.setTeleOpDrive(controller1.getLeftY(), -controller1.getLeftX(), -controller1.getRightX(), true // Robot Centric
        );

        if (controller1.isDown(GamepadKeys.Button.RIGHT_BUMPER)) {
            if (turret.getCurrent(CurrentUnit.AMPS) > 3) {
                turret.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
                follower.setPose(new Pose(135, 8, Math.toRadians(90)));
                turret.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            }
            turret.setTargetPosition(turret.getCurrentPosition() - 50);
            turret.setPower(.8);
        } else {
            if (follower.getHeading() < Math.toRadians(68) && follower.getHeading() > Math.toRadians(-170)) {
            aim(calculateAngle(11, 138, follower.getPose().getX(), follower.getPose().getY()));
            } else {
            turret.setPower(0);
            }
        }

        telemetry.addData("turret Pose Degrees", getCorrectedTurretPose());
        telemetry.addData("relative Degrees", getRelativeTurretPose());
        telemetry.addData("robot HEading", Math.toDegrees(follower.getHeading()));
        telemetry.addData("robot Correct HEading", correctedRobotHeading(Math.toDegrees(follower.getHeading())));
        telemetry.addData("amps", turret.getCurrent(CurrentUnit.AMPS));
        telemetry.addData("ticks", ticksToDegree(ticks));
        telemetry.addData("angle", (calculateAngle(11, 138, follower.getPose().getX(), follower.getPose().getY())));


        telemetry.update();

    }

    public void aim(double targetAngle) {
        double delta = pid.calculate(getCorrectedTurretPose(), targetAngle);

        setCorrectedTurretPose(targetAngle + delta);
        turret.setTargetPosition(ticks);
        turret.setPower(.8);
    }


    public int degreeToTicks(double degrees) {
        return (int) (degrees * TICKS_PER_DEGREE);
    }

    public double ticksToDegree(int ticks) {
        return (ticks / TICKS_PER_DEGREE);
    }

    private double getRelativeTurretPose() {
        return ticksToDegree(turret.getCurrentPosition()) + TURRETSTARTINGOFFSET;
    }

    public double getCorrectedTurretPose() {
        return getRelativeTurretPose() + Math.toDegrees(follower.getHeading());
//        return  getRelativeTurretPose();
    }

    private void setRelitiveTurretPose(double angle) {
        ticks = degreeToTicks(angle - TURRETSTARTINGOFFSET);
    }

    public void setCorrectedTurretPose(double angle) {
//        setRelitiveTurretPose(angle - correctedRobotHeading(Math.toDegrees(follower.getHeading())));
        setRelitiveTurretPose(angle - Math.toDegrees(follower.getHeading()));

    }

    public double correctedRobotHeading(double heading) {
        if (heading > 0) {
            return heading;
        } else {
            return 360 + heading;
        }
    }

    public double calculateAngle(double targetX, double targetY, double robotX, double robotY) {
        return Math.toDegrees(Math.atan2(targetY - robotY, targetX - robotX));
    }




}
