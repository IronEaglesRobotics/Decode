package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.hardware.motors.CRServoEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import lombok.Getter;


@Configurable
public class Robot {

    @Getter
    public Lever lever;
    @Getter
    public Conveyor conveyor;
    private Follower follower;
    @Getter
    public Intake intake;
    @Getter
    public Shooter shooter;


    public Robot init(HardwareMap hardwareMap) {
        this.lever = new Lever().init(hardwareMap);
        this.conveyor = new Conveyor().init(hardwareMap);
        this.intake = new Intake().init(hardwareMap);
        this.shooter = new Shooter().init(hardwareMap);
        this.follower = Constants.createFollower(hardwareMap);
        return this;
    }

    public Follower getFollower() {
        return follower;
    }


    @Configurable
    public static class Lever {
        private ServoEx lever;
        public static double leverUp = .35;
        public static double leverDown = 0.52;
        public static double leverHold = 0.45;


        public Lever init(HardwareMap hardwareMap) {
            this.lever = new ServoEx(hardwareMap, "lever"); // T
            return this;
        }

        public void up() {
            lever.set(leverUp);
        }

        public void hold() {
            lever.set(leverHold);
        }

        public void down() {
            lever.set(leverDown);
        }

    }

    @Configurable
    public static class Conveyor {
        private CRServoEx L1;
        private CRServoEx L2;
        private CRServoEx R1;
        private CRServoEx R2;

        public Conveyor init(HardwareMap hardwareMap) {
            this.L1 = new CRServoEx(hardwareMap, "L1"); // This is the correct constructor!
            this.L2 = new CRServoEx(hardwareMap, "L2");
            this.R1 = new CRServoEx(hardwareMap, "R1");
            this.R2 = new CRServoEx(hardwareMap, "R2");
            this.R1.setInverted(true);
            this.L2.setInverted(true);
            this.R2.setInverted(false);
            return this;
        }

        private void setT1(Double power) {
            L1.set(power);
            R1.set(power);
        }

        private void setT2(Double power) {
            L2.set(power);
            R2.set(power);
        }


        public void stop() {
            setT2(0.0);
            setT1(0.0);

        }

        public void spit() {
            setT1(-1.0);
            setT2(-1.0);
        }

        public void intake() {
            setT1(1.0);
            setT2(1.0);
        }

        public void seperate() {
            setT1(1.0);
            setT2(-1.0);
        }

    }


    @Configurable
    public static class Intake {

        private ColorRangeSensor iColor;
        private DistanceSensor iDistance;
        public static double distance = 3.5;
        private DcMotorEx intake;
        private static int in = -1;
        private static double slow = -.1;
        private static int out = 1;

        public Intake init(HardwareMap hardwareMap) {
            this.intake = hardwareMap.get(DcMotorEx.class, "intake");
            this.iColor = hardwareMap.get(ColorRangeSensor.class, "iColor");
            this.iDistance = hardwareMap.get(DistanceSensor.class, "iDistance");
            this.iColor.enableLed(true);
            return this;
        }

        public void setPower(double power) {
            intake.setPower(power);
        }

        public void intake() {
            setPower(in);
        }

        public void outtake() {
            setPower(out);
        }

        public void slow() {
            setPower(slow);
        }


        public double getVelocity() {
            return intake.getVelocity(AngleUnit.RADIANS) / (Math.PI * 2);
        }

        public double[] getDistance() {
            return new double[]{iColor.getDistance(DistanceUnit.CM)};
        }

        public boolean hasBall() {
            return getDistance()[0] < distance
//                    || getDistance()[1] < distance
                    ;
        }


    }

    @Configurable
    public static class Shooter {

        private ColorRangeSensor s1;
        private ColorRangeSensor s2;
        private MotorEx shooter;
        private ServoEx shooterHood;
        public static double powerFar = 1;
        public static double powerNear = .61;
        public static double hoodFar = .6;
        public static double hoodNear = .68;
        public static double rest = .77;
        public static double distance = 4;


        public Shooter init(HardwareMap hardwareMap) {
            this.shooter = new MotorEx(hardwareMap, "motor", MotorEx.GoBILDA.BARE);
            this.shooter.setRunMode(MotorEx.RunMode.VelocityControl);
            this.shooterHood = new ServoEx(hardwareMap, "shooterHood");
            this.s1 = hardwareMap.get(ColorRangeSensor.class, "s1");
            this.s2 = hardwareMap.get(ColorRangeSensor.class, "s2");
            this.s1.enableLed(true);
            this.s2.enableLed(true);
            return this;
        }

        public void setPower(double power) {
            shooter.set(power);
        }

        public void farShot() {
            setPower(powerFar);
            shooterHood.set(hoodFar);
        }

        public void nearShot() {
            setPower(powerNear);
            shooterHood.set(hoodNear);
        }


        public void rest() {
            setPower(-.25);
            shooterHood.set(rest);
        }

        public double getVelocity() {
            return shooter.getVelocity();
        }

        public double[] getDistance() {
            return new double[]{s1.getDistance(DistanceUnit.CM) / 2, s2.getDistance(DistanceUnit.CM)};
        }

        public boolean hasBall() {
            return getDistance()[0] < distance || getDistance()[1] < distance;
        }

        public void setNear(double power){
            powerNear = power;
        }

    }

    public robotStates robotstate = robotStates.CALIBRATE;

    public enum robotStates {
        CALIBRATE, IDLE, INTAKE, HAS1, HAS2, HAS3, SPIT, TRANSITION
    }

    public int transition = 0;

    public double delay;
    public boolean D1 = false;
    public int balls = 0;
    public boolean rapid = false;
    public static double foo = .2;
    public static double leverTime = 0.5;
    public boolean farShot = true;
    public boolean zit = false;
    public boolean autoSwitch = false;


    public void robotMacro(GamepadEx controller1, double runtime) {

        boolean rapidShoot = controller1.wasJustPressed(GamepadKeys.Button.A) || autoSwitch; //BUCKETH
        boolean shoot = controller1.wasJustPressed(GamepadKeys.Button.X); // BUCKETL
        boolean A = controller1.wasJustPressed(GamepadKeys.Button.A); // SPECIMENINTAKE
        boolean INTAKE = controller1.wasJustPressed(GamepadKeys.Button.DPAD_DOWN); //
        boolean D2 = controller1.wasJustPressed(GamepadKeys.Button.DPAD_LEFT); // SPECIMENL
        boolean L1 = controller1.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER); // RETRACT
        boolean L2 = controller1.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > .3;
        boolean delayed = runtime > delay;

        switch (robotstate) {
            case CALIBRATE:
                if (!zit) {
                    delay = runtime + 2;
                    zit = true;
                }
                conveyor.spit();
                if (delayed) {
                    robotstate = robotStates.IDLE;
                }
                break;
            case IDLE:
                //Idle Actions
                conveyor.stop();
                intake.setPower(0);
                lever.down();
                if (INTAKE) {
                    robotstate = robotStates.INTAKE;
                    balls = 0;
                }
                break;
            case INTAKE:
                conveyor.intake();
//                shooter.resthood();
                intake.intake();
                lever.down();

                //Intakes ball?
                if (shooter.hasBall()) {
                    robotstate = robotStates.HAS1;
                    balls = 1;
                }


                break;

            case HAS1:
                conveyor.seperate();
                intake.intake();
                lever.down();

                //Intakes 2nd ball?
                if (shooter.hasBall() && intake.hasBall()) {
                    robotstate = robotStates.HAS2;
                    balls = 2;
                    delay = runtime + .3;
                }

                //Shoot?
                if (shoot || rapid) {
                    robotstate = robotStates.TRANSITION;
                    lever.up();
                    delay = runtime + leverTime;
                } else if (rapidShoot) {
                    rapid = true;
                    robotstate = robotStates.TRANSITION;
                    lever.up();
                    delay = runtime + leverTime;
                }

                break;
            case HAS2:
                conveyor.seperate();
                intake.intake();
//                lever.down();

                //Intakes 3rd ball?
                if (ballCheck(runtime, 0, .5) && delayed) {
                    robotstate = robotStates.HAS3;
                    balls = 3;
                }

                //Shoot?
                if (shoot || rapid) {
                    robotstate = robotStates.TRANSITION;
                    lever.up();
                    delay = runtime + leverTime;
                } else if (rapidShoot) {
                    rapid = true;
                    robotstate = robotStates.TRANSITION;
                    lever.up();
                    delay = runtime + leverTime;
                }

                break;
            case HAS3:
                conveyor.seperate();
                intake.slow();
//                lever.down();


                //Shoot?
                if (shoot || rapid) {
                    robotstate = robotStates.TRANSITION;
                    lever.up();
                    delay = runtime + leverTime;
                } else if (rapidShoot) {
                    rapid = true;
                    robotstate = robotStates.TRANSITION;
                    lever.up();
                    delay = runtime + leverTime;
                }

                break;
            case TRANSITION:
                if (delayed) {
                    lever.down();

                    //Shot the ball?
                    switch (transition) {
                        //reload
                        case 0:
                            if (!ballCheck(runtime, 1, .1)) {
                                intake.intake();
                                conveyor.intake();
                                transition++;
                            }
                            break;
                        //done reloading?
                        case 1:
                            if (ballCheck(runtime, 1, foo) || balls == 1) {
                                switch (balls) {
                                    case 1:
                                        robotstate = robotStates.INTAKE;
                                        rapid = false;
                                        break;
                                    case 2:
                                        robotstate = robotStates.HAS1;
                                        lever.down();
                                        break;
                                    case 3:
                                        robotstate = robotStates.HAS2;
                                        lever.down();
                                        break;
                                }
                                balls--;
                                transition = 0;
                            }
                            break;
                    }
                }
                break;
            case SPIT:
                break;

        }

    }

    private boolean ballCheck(double runtime, int tier, double checktime) {
        if (tier == 1) {
            if (shooter.hasBall() && !D1) {
                D1 = true;
                delay = runtime + checktime;
            } else if (shooter.hasBall() && D1 && runtime > delay) {
                D1 = false;
                return true;
            }
        } else {
            if (intake.hasBall() && !D1) {
                D1 = true;
                delay = runtime + checktime;
            } else if (intake.hasBall() && D1 && runtime > delay) {
                D1 = false;
                return true;
            }
        }
        return false;
    }

}
