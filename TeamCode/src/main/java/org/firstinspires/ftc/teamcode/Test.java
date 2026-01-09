package org.firstinspires.ftc.teamcode;
//import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.controller.PIDController;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import lombok.Getter;

@Configurable
@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Test")
//@Configurable
public class Test extends OpMode {
    public Pose resetPose = new Pose(135, 8, Math.toRadians(90));
    public Pose aimPose = new Pose(11, 138, 0);

    private Follower follower;

    public static double indicatorReady = 0.475;
    public static double indicatorNotReady = 0.28;

    private double blinkTimer = .2;
    private boolean ledOn = false;

    private ServoEx ramp, hood;
    private Servo indicator, shooterIndicator;

    public TelemetryManager telemetryM;
    private MotorEx motorR, motorL, intake;
    private MotorGroup shooter;
    private GamepadEx controller1;
    private int targetVelocity;

    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
    public static double P = 100;
    public static double I = 10;
    public static double D = 0;


    public int ticks = 0;
    public boolean far = true;

    private DcMotorEx turret;

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
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(0, 0, Math.toRadians(0)));
        follower.update();
        controller1 = new GamepadEx(gamepad1);

        motorR = new MotorEx(hardwareMap, "shooterR", MotorEx.GoBILDA.BARE);
//        motorR.setRunMode(Motor.RunMode.VelocityControl);
        motorR.setInverted(true);
        motorR.setCachingTolerance(0.0001);
        motorL = new MotorEx(hardwareMap, "shooterL", MotorEx.GoBILDA.BARE);
        motorL.setCachingTolerance(0.0001);

        intake = new MotorEx(hardwareMap, "intake", MotorEx.GoBILDA.BARE);
        intake.setCachingTolerance(0.0001);
        intake.setRunMode(Motor.RunMode.VelocityControl);
        intake.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
//        motorL.setRunMode(Motor.RunMode.VelocityControl);


        shooter = new MotorGroup(motorR, motorL);
        shooter.setRunMode(Motor.RunMode.VelocityControl);

        shooter.setVeloCoefficients(P, I, D);
        shooter.setPositionTolerance(1);
        shooter.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT);

        indicator = hardwareMap.get(Servo.class, "side");
        shooterIndicator = hardwareMap.get(Servo.class, "back");
//        indicator.setInverted(true);
        ramp = new ServoEx(hardwareMap, "ramp");

        hood = new ServoEx(hardwareMap, "hood");

//        ramp.set(0);
        updateTargetVelocity(0);
//        motor3 = new MotorEx(hardwareMap, "rightFront", MotorEx.GoBILDA.RPM_435);
//        motor3.setRunMode(Motor.RunMode.RawPower);
//        motor4 = new MotorEx(hardwareMap, "leftFront", MotorEx.GoBILDA.RPM_435);
//        motor4.setRunMode(Motor.RunMode.RawPower);
//        servoL = new CRServoEx(hardwareMap, "ServoL");

//        motor.setVeloCoefficients(0.05, 0.01, 0.31);
//        double[] coeffs = motor.getVeloCoefficients();
//        double kP = coeffs[0];
//        double kI = coeffs[1];
//        double kD = coeffs[2];

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

        if (controller1.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
            far = !far;
        }

        if (far) {
            setPower(.55);
            hood.set( 1 - (((double) (targetVelocity - calculatedVelocity()) /targetVelocity) * 1.35));
        } else {
            setPower(.375);
            hood.set( .6 - (((double) (targetVelocity - calculatedVelocity()) /targetVelocity) * 1.25));
        }

        if (controller1.isDown(GamepadKeys.Button.DPAD_DOWN)) {
            ramp.set(.59);
            intake.set(-.8);
        } else {
            ramp.set(.79);
            intake.set(-1);
        }
//        motorR.set(1);
//        motor3.set(1);
//        motor4.set(1);

        if (getRuntime() > blinkTimer) {
            ledOn = !ledOn;
            if (ledOn) {
                indicator.setPosition(.28);
            } else {
                indicator.setPosition(0);

            }

            blinkTimer = getRuntime() + .2;

        }

        follower.update();
        follower.setTeleOpDrive(
                controller1.getLeftY(),
                -controller1.getLeftX(),
                -controller1.getRightX(), true // Robot Centric
        );

        if (atTargetVelocity()) {
            readyColor();
        } else {
            notReadyColor();
        }


        if (controller1.isDown(GamepadKeys.Button.RIGHT_BUMPER)) {
            if (turret.getCurrent(CurrentUnit.AMPS) > 3) {
                turret.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
                follower.setPose(new Pose(resetPose.getX(), resetPose.getY(), Math.toRadians(90)));
                turret.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            }
            turret.setTargetPosition(turret.getCurrentPosition() - 50);
            turret.setPower(.8);
        } else {
            if (follower.getHeading() < Math.toRadians(63) && follower.getHeading() > Math.toRadians(-165)) {
                aim(calculateAngle(-0, 136, follower.getPose().getX(), follower.getPose().getY()));
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

        telemetryM.addData("shooter_velo ", calculatedVelocity());
        telemetryM.addData("target Velo", targetVelocity);
        telemetryM.addData("robot HEading", Math.toDegrees(follower.getHeading()));
        telemetryM.addData("x", follower.getPose().getX());
        telemetryM.addData("y", follower.getPose().getY());


        telemetryM.update(telemetry);


    }

    public boolean atTargetVelocity() {
        return (calculatedVelocity() >= targetVelocity - 80 && calculatedVelocity() <= targetVelocity + 80);
    }

    private void updateTargetVelocity(double power) {
        targetVelocity = (int) (power * 5333);
    }

    public int calculatedVelocity() {
        return (int) motorL.getCorrectedVelocity() / 28 * 60;
    }

    public void setPower(double power) {
        shooter.set(power);
        updateTargetVelocity(power);
    }

    public void readyColor() {
        shooterIndicator.setPosition(indicatorReady);
    }

    public void notReadyColor() {
        shooterIndicator.setPosition(indicatorNotReady);
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
