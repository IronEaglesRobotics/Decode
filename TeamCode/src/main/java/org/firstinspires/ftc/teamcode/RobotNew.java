package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.controller.PIDController;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.List;

import lombok.Getter;

@Configurable
public class RobotNew {
    @Getter
    public Intake intake;
    @Getter
    public Shooter shooter;
    @Getter
    public Turret turret;
    @Getter
    private Follower follower;

    public RobotNew init(HardwareMap hardwareMap) {
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
        this.intake = new Intake().init(hardwareMap);
        this.shooter = new Shooter().init(hardwareMap);
        this.turret = new Turret().init(hardwareMap);
        this.follower = Constants.createFollower(hardwareMap);
        return this;
    }

    @Configurable
    public static class Intake {
        private MotorEx intake;
        private ServoEx ramp;

        public static double rampOpen = .59;
        public static double rampClose = .79;
        public static double intakeFast = -1;
        public static double intakeTransfer = -0.5;


        public Intake init(HardwareMap hardwareMap) {
            this.intake = new MotorEx(hardwareMap, "intake", MotorEx.GoBILDA.BARE);
            this.intake.setCachingTolerance(0.0001);
            this.intake.setRunMode(Motor.RunMode.VelocityControl);
            this.intake.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
            this.ramp =  new ServoEx(hardwareMap, "ramp");
            return this;
        }

        public void open() {
            ramp.set(rampOpen);
        }

        public void close() {
            ramp.set(rampClose);
        }

        public void intake() {
            intake.set(intakeFast);
        }

        public void transfer() {
            intake.set(intakeTransfer);
        }

    }

    @Configurable
    public static class Shooter {
        private MotorEx motorR, motorL;
        private MotorGroup shooter;
        private ServoEx hood;


        private int targetVelocity;
        public static double P = 67;
        public static double I = 5;
        public static double D = 0;

        public static double powerFar = .55;
        public static double powerNear = .44;
        public static double hoodFar = 1;
        public static double hoodNear = .9;


        public Shooter init(HardwareMap hardwareMap) {
            this.motorR = new MotorEx(hardwareMap, "shooterR", MotorEx.GoBILDA.BARE);
            this.motorR.setInverted(true);
            this.motorR.setCachingTolerance(0.01);
            this.motorL = new MotorEx(hardwareMap, "shooterL", MotorEx.GoBILDA.BARE);
            this.motorL.setCachingTolerance(0.01);

            this.shooter = new MotorGroup(motorR, motorL);
            this.shooter.setRunMode(Motor.RunMode.VelocityControl);
            this.shooter.setVeloCoefficients(P, I, D);
            this.shooter.setPositionTolerance(1);
            this.shooter.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT);
            updateTargetVelocity(0);

            this.hood =  new ServoEx(hardwareMap, "hood");
            return this;
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

        public void farShot(){
            setPower(powerFar);
            hoodSet(hoodFar);
        }

        public void nearShot(){
            setPower(powerNear);
            hoodSet(hoodNear);
        }

        public void hoodSet(double pos){
            hood.set(pos - (((double) (targetVelocity - calculatedVelocity()) /targetVelocity) * 1.35));
        }
    }

    @Configurable
    public static class Turret {
        public DcMotorEx turret;
        @Getter
        private int ticks = 0;

        public static double P = 1;
        public static double I = 0;
        public static double D = 0;

        public static double MOTOR_INTERNAL_GEARBOX = 1.0;
        public static double EXTERNAL_GEAR_RATIO = 10;
        public static double ENCODER_TICKS_PER_REV = 145.1;
        public static double MINPOWER = .15;
        public static double TURRETSTARTINGOFFSET = 68; // NEEDS TO CHANGE

        private PIDController pid;
        public static double p = .8, i = 0, d = 0.01;
        public static double TICKS_PER_DEGREE = (ENCODER_TICKS_PER_REV * MOTOR_INTERNAL_GEARBOX * EXTERNAL_GEAR_RATIO) / 360.0;

        public Turret init(HardwareMap hardwareMap) {
            this.turret = hardwareMap.get(DcMotorEx.class, "turret");
            this.turret.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            this.turret.setTargetPosition(0);
            this.turret.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            this.turret.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
            this.pid = new PIDController(p, i, d);
            return this;
        }

        public void aim(double targetAngle, double heading) {
            double delta = pid.calculate(getCorrectedTurretPose(heading), targetAngle);

            setCorrectedTurretPose(targetAngle + delta, heading);
            turret.setTargetPosition(ticks);
            turret.setPower(.8);
        }

        public void off(){
            turret.setPower(0);
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

        public double getCorrectedTurretPose(double heading) {
            return getRelativeTurretPose() + Math.toDegrees(heading);
        }

        private void setRelitiveTurretPose(double angle) {
//            while (angle > 180) angle -= 360;
//            while (angle < -180) angle += 360;
            ticks = degreeToTicks(angle - TURRETSTARTINGOFFSET);
        }

        public void setCorrectedTurretPose(double angle,double heading) {

            setRelitiveTurretPose(angle - Math.toDegrees(heading));

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

        public double getCurrent(){
            return turret.getCurrent(CurrentUnit.AMPS);
        }

        public void resetTurret(){
            turret.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            turret.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        }

        public void calibrate(){
            turret.setTargetPosition(turret.getCurrentPosition() - 50);
            turret.setPower(.8);
        }

    }
}
