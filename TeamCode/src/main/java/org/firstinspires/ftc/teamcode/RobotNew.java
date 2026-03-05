package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.controller.PIDController;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.Map;
import java.util.TreeMap;

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
    @Getter
    private Lights lights;

    public static double distanceThreshold = 100;

    public RobotNew init(HardwareMap hardwareMap) {
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
        this.intake = new Intake().init(hardwareMap);
        this.shooter = new Shooter().init(hardwareMap);
        this.turret = new Turret().init(hardwareMap);
        this.follower = Constants.createFollower(hardwareMap);
        this.lights = new Lights().init(hardwareMap);
        return this;
    }

    @Configurable
    public static class Lights {
        private Servo indicator, shooterIndicator;

        private boolean ledOn = false;
        private double blinkTimer = .2;
        public static double indicatorReady = 0.475;
        public static double indicatorNotReady = 0.28;


        public Lights init(HardwareMap hardwareMap) {
            this.indicator = hardwareMap.get(Servo.class, "side");
            this.shooterIndicator = hardwareMap.get(Servo.class, "back");
            return this;
        }

        public void blinker(double runtime) {
            if (runtime > blinkTimer) {
                ledOn = !ledOn;
                if (ledOn) {
                    indicator.setPosition(.28);
                } else {
                    indicator.setPosition(0);

                }
                blinkTimer = runtime + .2;

            }
        }

        public void closeIndication() {
            indicator.setPosition(.28);
        }

        public void readyColor() {
            shooterIndicator.setPosition(indicatorReady);
        }

        public void notReadyColor() {
            shooterIndicator.setPosition(indicatorNotReady);
        }

        public void shooterCheck(boolean atTargetVelocity) {
            if (atTargetVelocity) {
                readyColor();
            } else {
                notReadyColor();
            }
        }

    }

    @Configurable
    public static class Intake {
        private MotorEx intake;
        private ServoEx ramp;

        public static double rampOpen = .59;
        public static double rampClose = .78;
        public static double intakeFast = -1;
        public static double intakeTransfer = -.3;
        public static int currentThreshold = 2250;


        public Intake init(HardwareMap hardwareMap) {
            this.intake = new MotorEx(hardwareMap, "intake", MotorEx.GoBILDA.BARE);
            this.intake.setCachingTolerance(0.0001);
            this.intake.setRunMode(Motor.RunMode.VelocityControl);
            this.intake.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
            this.ramp = new ServoEx(hardwareMap, "ramp");
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

        public double getCurrent(){
            return intake.getCurrent(CurrentUnit.MILLIAMPS);
        }

        public boolean currentSpiked(){
            return getCurrent() > currentThreshold;
        }

        public void transfer(double distance) {
            if (distance > distanceThreshold) {
                intake.set(intakeTransfer);
            } else {
                intake.set(intakeFast);
            }
        }

        public void transfer(boolean slow) {
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

        public static double powerFar = .85;
        public static double powerNear = .44;
        public static double hoodFar = .75;
        public static double hoodNear = .9;


        public Shooter init(HardwareMap hardwareMap) {
            this.motorR = new MotorEx(hardwareMap, "shooterR", MotorEx.GoBILDA.BARE);
            this.motorR.setInverted(true);
            this.motorR.setCachingTolerance(0.01);
            this.motorL = new MotorEx(hardwareMap, "shooterL", MotorEx.GoBILDA.BARE);
            this.motorL.setCachingTolerance(0.01);

            this.shooter = new MotorGroup(motorR, motorL);
            this.shooter.setRunMode(Motor.RunMode.VelocityControl);
//            this.shooter.setRunMode(Motor.RunMode.RawPower);

            this.shooter.setVeloCoefficients(P, I, D);
            this.shooter.setPositionTolerance(1);
            this.shooter.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT);
            updateTargetVelocity(0);

            this.hood = new ServoEx(hardwareMap, "hood");
            return this;
        }

        //INTERP --------------------------------------------------------------------------------------------------------------------------------
        //INTERP --------------------------------------------------------------------------------------------------------------------------------
        //INTERP --------------------------------------------------------------------------------------------------------------------------------

        public static class Metrics {
            public double y1;
            public double z;

            public Metrics(double y1, double z) {
                this.y1 = y1;
                this.z = z;
            }
        }

        private static final TreeMap<Double, Metrics> table = new TreeMap<>();

        static {
            // Data from your image
            table.put(0.0, new Metrics(0, 1));
            table.put(66.5, new Metrics(0.65, 0.1));
            table.put(74.0, new Metrics(0.7, 0.1));
            table.put(86.0, new Metrics(0.7, 0.14));
            table.put(97.0, new Metrics(0.76, 0.35));
            table.put(104.0, new Metrics(0.78, 0.58));
            table.put(118.0, new Metrics(0.83, 0.7));
            table.put(125.0, new Metrics(0.9, 0.78));
            table.put(139.0, new Metrics(0.94, 0.8));
            table.put(151.0, new Metrics(1.0, 0.95));
            table.put(250.0, new Metrics(1.0, .9));
        }

        public static Metrics getInterpolatedValue(double x) {
            // 1. Exact match
            if (table.containsKey(x)) return table.get(x);

            // 2. Out of bounds check
            if (x < table.firstKey() || x > table.lastKey()) return null;

            // 3. Find surrounding points
            Map.Entry<Double, Metrics> low = table.floorEntry(x);
            Map.Entry<Double, Metrics> high = table.ceilingEntry(x);

            double x0 = low.getKey();
            double x1 = high.getKey();

            // Calculate the "weight" (how far between x0 and x1 we are)
            double t = (x - x0) / (x1 - x0);

            // Linear interpolation formula: y = y0 + t * (y1 - y0)
            double interpolatedY1 = low.getValue().y1 + t * (high.getValue().y1 - low.getValue().y1);
            double interpolatedZ = low.getValue().z + t * (high.getValue().z - low.getValue().z);

            return new Metrics(interpolatedY1, interpolatedZ);
        }



        public static void main(String[] args) {
            double input = 80.0; // Not in the table
            Metrics result = getInterpolatedValue(input);

            if (result != null) {
                System.out.printf("Interpolated for x=%.1f: y1=%.3f, z=%.3f%n", input, result.y1, result.z);
            }
        }


        //INTERP --------------------------------------------------------------------------------------------------------------------------------
        //INTERP --------------------------------------------------------------------------------------------------------------------------------
        //INTERP --------------------------------------------------------------------------------------------------------------------------------

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

        public void farShot() {
            setPower(powerFar);
            hoodSet(hoodFar);
//            hood.set(1);
//            this.hood.set(1);
        }


        public void nearShot() {
            setPower(powerNear);
            hoodSet(hoodNear);
        }

        public void setShot(double power, double hood) {
            setPower(power);
            hoodSet(hood);
        }

        public void setShot(double power, double hood, boolean adjust) {
            setPower(power);
            this.hood.set(hood);
        }

        public void hoodSet(double pos) {
//            if (pos < .3) {
//                hood.set(pos);
//            } else {
            hood.set(pos - (((double) (targetVelocity - calculatedVelocity()) / targetVelocity) * 2.6));
//            }
        }

        public double calculateShooterPower(double distance) {
//            double x = distance;
            if (distance > 160) {
                return .605;
            } else if (distance > 30) {
//                return 0.00244811 * distance + 0.220092;
                return 0.00239396 * distance + 0.224175; //Desmos Regression
            } else {
                return .45;
            }
        }

        public double calculateHoodPose(double distance) {
            if (distance > distanceThreshold && distance < 135) {
//                return Math.min(0.00462719 * distance + 0.269066, 1); //desmos regression
                return hoodFar;
            } else {
                return .1;
            }
        }
    }

    @Configurable
    public static class Turret {
        @Getter
        private int ticks = 0;
        public int targetTicks;
        public DcMotorEx turret;
        public Limelight3A limelight;

        public static double P = 1;
        public static double I = 0;
        public static double D = 0;

        public static double MOTOR_INTERNAL_GEARBOX = 1.0;
        public static double EXTERNAL_GEAR_RATIO = 10;
        public static double ENCODER_TICKS_PER_REV = 145.1;
        public static double MINPOWER = .15;
        public static double TURRETSTARTINGOFFSET = 2; // NEEDS TO CHANGE

        private PIDController pid;
        public static double p = .8, i = 0, d = 0.01;
        public static double TICKS_PER_DEGREE = (ENCODER_TICKS_PER_REV * MOTOR_INTERNAL_GEARBOX * EXTERNAL_GEAR_RATIO) / 360.0;

        public Turret init(HardwareMap hardwareMap) {
            this.turret = hardwareMap.get(DcMotorEx.class, "turret");
            this.turret.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            this.turret.setTargetPosition(0);
            this.turret.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            this.turret.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

            this.limelight = hardwareMap.get(Limelight3A.class, "limelight");
            this.limelight.setPollRateHz(45);
            this.limelight.pipelineSwitch(0);
//            this.limelight.uploadFieldmap();
            this.limelight.start();
//            this.turret.setDirection(DcMotorSimple.Direction.REVERSE);
            this.pid = new PIDController(p, i, d);
            return this;
        }

        public void aim(double targetAngle, double heading) {
            double correctedTarget = targetAngle - TURRETSTARTINGOFFSET - heading;
            correctedTarget = correctedTarget % 360;
            if (correctedTarget < 0) correctedTarget += 360;
//            double delta = pid.calculate(ticksToDegree(turret.getCurrentPosition()), correctedTarget);
//
//            setCorrectedTurretPose(targetAngle + delta, heading);

            double finalTarget = Math.max(0, Math.min(correctedTarget, 330 - 5));
            targetTicks = degreeToTicks(finalTarget);
            turret.setTargetPosition(degreeToTicks(finalTarget));
            turret.setPower(.8);

        }

        public double getLimeError() {
            LLResult result = limelight.getLatestResult();
//            Pose3D pose = limelight.getLatestResult().getBotpose();
//            return new Pose(pose.getPosition().x,pose.getPosition().y,pose.getOrientation().getYaw());
//            if (limelight.)
            if (result.isValid()) {
                return limelight.getLatestResult().getTx() - 3;
            } else {
                return 0;
            }
        }

        public void printLimePose() {

        }

        public void off() {
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
            return getRelativeTurretPose() + (heading);
        }

        private void setRelitiveTurretPose(double angle) {
//            while (angle > 180) angle -= 360;
//            if (angle < 0 ) {angle = 180 + 180 + angle;}
            angle = angle % 360;
//            if (angle < 0) angle += 360;
            if (angle - TURRETSTARTINGOFFSET >= 180) angle -= 360;
            if (angle - TURRETSTARTINGOFFSET < -180) angle += 360;
            ticks = degreeToTicks(angle);
        }

        public void setCorrectedTurretPose(double angle, double heading) {

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

        public double getCurrent() {
            return turret.getCurrent(CurrentUnit.AMPS);
        }

        public void resetTurret() {
            turret.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            turret.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        }

        public void calibrate() {
            turret.setTargetPosition(turret.getCurrentPosition() - 50);
            turret.setPower(1);
        }

    }

    public double getGoalDistance(double x, double y, double gx, double gy) {
        return Math.sqrt(Math.pow(Math.abs(gx - x), 2) + Math.pow(Math.abs(gy - y), 2));
    }


}
