package org.firstinspires.ftc.teamcode.hardware;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.Subsystem;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.command.WaitUntilCommand;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configurable
public class Launcher extends SubsystemBase {
    //public Motor spinner;
    public Servo spinner1;
    public Servo spinner2;
    public MotorEx flyWheel1;
    public MotorEx flyWheel2;
    public Motor throughBore;
    public RevColorSensorV3 cs1;
    public RevColorSensorV3 cs2;
    //public CRServo quickLaunch;
    Servo pusher;
    public Servo light;
    AnalogInput pusherInput;
    AnalogInput indexInput;
    Servo lift1;
    Servo lift2;
//    public static int halfDelta = -1365;

    public static int servoIndex = 0;
    public static double servoTarget = 0;
    public static double halfDelta = 0;
//    public static int fullDelta = -2731;
    public static double fullDelta = 0;
    public static int pidTarget = 0;
    private static final double FIRE1 = 0;
    private static final double FIRE2 =  0.185;
    private static final double FIRE3 = 0.381;
    private static final double FIRE4 = 0.555;
    private static final double FIRE5 = 0.751;
    private static final double LOAD1 = 0.0925;
    private static final double LOAD2 = 0.2775;
    private static final double LOAD3 = 0.4625;

    private static final double[] POSITIONS = {FIRE1,LOAD1,FIRE2,LOAD2,FIRE3,LOAD3,FIRE4};
    public static final double[][][] fireCalculator = {
            {{FIRE3,FIRE2,FIRE1},{FIRE4,FIRE3,FIRE2},{FIRE3,FIRE4,FIRE2}},
            {{FIRE4,FIRE3,FIRE2},{FIRE3,FIRE4,FIRE2},{FIRE3,FIRE2,FIRE1}},
            {{FIRE5,FIRE4,FIRE3},{FIRE3,FIRE2,FIRE1},{FIRE4,FIRE3,FIRE2}}
    };
    public static double[] fireQueue = {FIRE3,FIRE2,FIRE1};
    List<Color> chambers;
    public static int closeSpeed = -2000;
    //2 flywheel: -805
    //1 flywheel: -2000
    public static int farSpeed = -2675;
    //2 flywheel: -975
    //1 flywheel: -2650
    public static int autoSpeed = -1925;
    public static double power = .43;
    public static double servoPush = 0;
    public static double servoShootPos = 0.3;
    public static double liftPos = 1;
    public static Picked lastPicked = Picked.First;

    public static double[] veloCoeffecient = new double[] {11,0,0};
    public static double[] feedforward = new double[] {11,0,0};

    public static int safePose = 0;
    double speed1 = 0;
    double speed2 = 0;
    public Motif order = Motif.GPP;
    public boolean isMoving;
    private double lastHold = 0;
    public Launcher(HardwareMap hardwareMap){
        this.chambers = new ArrayList<>(3);

        flyWheel1 = new MotorEx(hardwareMap,"flywheel1",28,6000);
        flyWheel1.motor.setZeroPowerBehavior(com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT);
        flyWheel1.setRunMode(Motor.RunMode.VelocityControl);
        flyWheel1.setVeloCoefficients(veloCoeffecient[0],veloCoeffecient[1],veloCoeffecient[2]);
        flyWheel1.setFeedforwardCoefficients(feedforward[0], feedforward[1],feedforward[2]);

        flyWheel2 = new MotorEx(hardwareMap,"flywheel2",28,6000);
        flyWheel2.motor.setZeroPowerBehavior(com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT);
        flyWheel2.setRunMode(Motor.RunMode.VelocityControl);
        flyWheel2.setVeloCoefficients(veloCoeffecient[0],veloCoeffecient[1],veloCoeffecient[2]);
        flyWheel2.setFeedforwardCoefficients(feedforward[0], feedforward[1],feedforward[2]);

        throughBore = new Motor(hardwareMap,"rr",8192,0);
        throughBore.setInverted(true);

        spinner1 = hardwareMap.get(Servo.class, "rspinner");
        spinner2 = hardwareMap.get(Servo.class, "lspinner");
        pusher = hardwareMap.get(Servo.class,"pusher");
        pusher.setDirection(Servo.Direction.REVERSE);
        pusher.setPosition(0.05);
        pusherInput = hardwareMap.get(AnalogInput.class, "input1");
        indexInput = hardwareMap.get(AnalogInput.class, "input2");

        lift1 = hardwareMap.get(Servo.class,"lift1");
        lift2 = hardwareMap.get(Servo.class, "lift2");
        liftPos=0;

        light = hardwareMap.get(Servo.class, "light");

//        quickLaunch = new CRServo(hardwareMap,"quickLaunch");
        cs1 = hardwareMap.get(RevColorSensorV3.class,"cs1");
        cs2 = hardwareMap.get(RevColorSensorV3.class,"cs2");
        chambers.add(Color.Nothing);
        chambers.add(Color.Nothing);
        chambers.add(Color.Nothing);
    }
    public Color getColor(RevColorSensorV3 cs){
        if (cs.green() > (cs.red() + cs.blue()) * .9 && cs.getDistance(DistanceUnit.INCH) < .9) {
            return Color.Green;
        }
        else if (!(cs.green() > (cs.red() + cs.blue()) * .9) && cs.getDistance(DistanceUnit.INCH) < .9) {
            return Color.Purple;
        }
        return Color.Nothing;
    }
    public Command plusVelo(){
        return new InstantCommand(()-> speed1 = speed1 + 100);
    }
    public Command minusVelo(){
        return new InstantCommand(()-> speed1 = speed1 - 100);
    }
    public Command flywheelOn(boolean isClose){
        return new InstantCommand(()->{
            flyWheel1.setRunMode(Motor.RunMode.VelocityControl);
            flyWheel2.setRunMode(Motor.RunMode.VelocityControl);
            speed1 = isClose? closeSpeed:farSpeed;
            //speed2 = isClose? closeSpeed:farSpeed;
//            flyWheel1.setPower(-power);
//            flyWheel2.setPower(power);
        });
    }
    public Command flywheelAuto(boolean isClose){
        return new InstantCommand(()->{
            flyWheel1.setRunMode(Motor.RunMode.VelocityControl);
            flyWheel2.setRunMode(Motor.RunMode.VelocityControl);
            speed1 = isClose? autoSpeed:farSpeed;
            //speed2 = isClose? closeSpeed:farSpeed;
//            flyWheel1.setPower(-power);
//            flyWheel2.setPower(power);
        });
    }
    public void resetSpindexer(){
        servoTarget -= 10;
    }

    public void resetEncoder(){
//        spinner.stopAndResetEncoder();
        throughBore.stopAndResetEncoder();
        servoTarget = 0;
    }
    public Command flywheelOff(){
        return new InstantCommand(() -> {
            speed1 = 0;
            flyWheel1.setRunMode(Motor.RunMode.RawPower);
            flyWheel2.setRunMode(Motor.RunMode.RawPower);
            flyWheel1.set(0);
            flyWheel2.set(0);
        });
    }
    public void setLastPicked(Picked lastPick){
        lastPicked = lastPick;
    }

    public Command Park(){
        return new InstantCommand(() ->
            liftPos=1
        );
    }
    public Command UnPark(){
        return new InstantCommand(() ->
            liftPos=0
        );
    }
    public Command shoot() {
        return new Command() {
            double time;

            @Override
            public void initialize() {
                servoPush = servoShootPos;
                time = System.currentTimeMillis();
            }

            @Override
            public boolean isFinished() {
                return time + 200 < System.currentTimeMillis();
            }

            @Override
            public Set<Subsystem> getRequirements() {
                return Collections.emptySet();
            }

            @Override
            public void end(boolean interrupted) {
                servoPush = 0.05;
            }
        };
    }
    public Command fastshoot() {
        return new Command() {
            double time;

            @Override
            public void initialize() {
                servoPush = servoShootPos;
                time = System.currentTimeMillis();
            }

            @Override
            public boolean isFinished() {
                return time + 100 < System.currentTimeMillis();
            }

            @Override
            public Set<Subsystem> getRequirements() {
                return Collections.emptySet();
            }

            @Override
            public void end(boolean interrupted) {
                servoPush = 0.05;
            }
        };
    }
    public void setOrder(Motif tOrder){
        order = tOrder;
    }
    public Command setLaunch(Motif order) {
        return setLaunch(lastPicked,order);
    }
    public Command setLaunch(){
        return setLaunch(order);
    }
    public void setChambers(Color[] colors){
        chambers.set(0,colors[0]);
        chambers.set(1,colors[1]);
        chambers.set(2,colors[2]);
    }

    public InstantCommand setLaunch(Picked greenLoc, Motif order) {
        return new InstantCommand(()->{
            fireQueue = fireCalculator[greenLoc.ordinal()][order.ordinal()];
            servoTarget = fireQueue[0];
        });
    }
    public boolean atTarget(){
        return !isMoving;
    }
    public Command fire(){
        return fire(fireQueue);
    }
    public Command fire(double[] fires){
        return new SequentialCommandGroup(
                new InstantCommand(()-> servoTarget = fires[0]),
                //new WaitUntilCommand(this::atSpeed),
                new WaitCommand(200),
                shoot(),
                new WaitCommand(150),
                new InstantCommand(()-> servoTarget = fires[1]),
                new WaitCommand(300),
                shoot(),
                new WaitCommand(150),
                new InstantCommand(()-> servoTarget = fires[2]),
                new WaitCommand(300),
                shoot(),
                new WaitCommand(150),
                new InstantCommand(()->fireQueue = new double[]{FIRE3, FIRE2, FIRE1}),
                toZero()
        );
    }

    public void Zero(){
        servoTarget = 0;
    }
    public Command toNext(){
        return new Command() {
            long time;
            @Override
            public Set<Subsystem> getRequirements() {
                return new HashSet<>();
            }

            @Override
            public void initialize() {
                servoIndex += 2;
                if (servoIndex > POSITIONS.length - 1){
                    servoIndex = servoIndex % 2;
                }
                servoTarget = POSITIONS[servoIndex];
                time = System.currentTimeMillis();
            }

            @Override
            public boolean isFinished() {
                return time + 150 < System.currentTimeMillis();
            }
        };
    }
    public Command backNext(){
        return new Command() {
            long time;
            @Override
            public Set<Subsystem> getRequirements() {
                return new HashSet<>();
            }

            @Override
            public void initialize() {
                servoIndex -= 2;
                if (servoIndex < 0){
                    servoIndex = 0;
                }
                servoTarget = POSITIONS[servoIndex];
                time = System.currentTimeMillis();
            }

            @Override
            public boolean isFinished() {
                return time + 150 < System.currentTimeMillis();
            }
        };
    }
    public Command toShoot(){
        return new Command() {
            long time;
            @Override
            public Set<Subsystem> getRequirements() {
                Set<Subsystem> set = new HashSet<>();
                set.add(Launcher.this);
                return set;
            }

            @Override
            public void initialize() {
                servoIndex++;
                if (servoIndex > POSITIONS.length - 1){
                    servoIndex = 0;
                }
                servoTarget = POSITIONS[servoIndex];
                time = System.currentTimeMillis();
            }

            @Override
            public boolean isFinished() {
                return time + 150 < System.currentTimeMillis();
            }
        };
    }

    public Command backShoot(){
        return new Command() {
            long time;
            @Override
            public Set<Subsystem> getRequirements() {
                Set<Subsystem> set = new HashSet<>();
                set.add(Launcher.this);
                return set;
            }

            @Override
            public void initialize() {
                servoIndex--;
                if (servoIndex < 0){
                    servoIndex = 0;
                }
                servoTarget = POSITIONS[servoIndex];
                time = System.currentTimeMillis();
            }

            @Override
            public boolean isFinished() {
                return time + 150 < System.currentTimeMillis();
            }
        };
    }
    public Command toZero(){
        return new Command() {
            long time;
            @Override
            public Set<Subsystem> getRequirements() {
                Set<Subsystem> set = new HashSet<>();
                set.add(Launcher.this);
                return set;
            }

            @Override
            public void initialize() {
                servoTarget = LOAD1;
                servoIndex = 1;
                time = System.currentTimeMillis();
            }

            @Override
            public boolean isFinished() {
                return time + 150 < System.currentTimeMillis();
            }
        };
    }
    public Command toFull(){
        return new SequentialCommandGroup(
                toNext(),
                new WaitCommand(500),
                toNext(),
                new WaitCommand(500),
                toNext(),
                new WaitCommand(500)
        );
    }
    public int calculateVelo(MotorEx flywheel){
        return (int) (Math.abs(flywheel.getCorrectedVelocity())/.83);
    }
    public boolean atSpeed(){
        return Math.abs(speed1 + calculateVelo(flyWheel1)) < 30 && Math.abs(speed1 + calculateVelo(flyWheel2)) < 30 &&
                speed1 <= autoSpeed;
    }
    public double getSpeed1() {
        return speed1;
    }

    public String getTelemetry(){
        return "slot 0: " + chambers.get(0) + " slot 1: " + chambers.get(1) +" slot 2: " + chambers.get(2);
    }
    public void reverseSpin(){
        halfDelta *= -1;
        fullDelta *= -1;
    }

    @Override
    public void periodic(){
//        flyWheel2.setVeloCoefficients(veloCoeffecient[0],veloCoeffecient[1],veloCoeffecient[2]);
//        flyWheel2.setFeedforwardCoefficients(feedforward[0], feedforward[1],feedforward[2]);
        flyWheel2.setVelocity(speed1);
//        flyWheel1.setVeloCoefficients(veloCoeffecient[0],veloCoeffecient[1],veloCoeffecient[2]);
//        flyWheel1.setFeedforwardCoefficients(feedforward[0], feedforward[1],feedforward[2]);
        flyWheel1.setVelocity(-speed1);
        pusher.setPosition(servoPush);
        spinner1.setPosition(servoTarget);
        spinner2.setPosition(servoTarget);
        lift1.setPosition(liftPos);
        lift2.setPosition(liftPos);
        isMoving = !(lastHold + .01 > indexInput.getVoltage() && lastHold - .01 < indexInput.getVoltage());
        lastHold = indexInput.getVoltage();
    }

    public boolean canShoot() {
        return servoIndex % 2 == 0;
    }

    public static class Loading extends CommandBase{
        Launcher launcher;
        int currentChamber = 0;
        Color color1;
        Color color2;
        long time;
        public Loading(Launcher temp){
            launcher = temp;
        }

        @Override
        public void initialize() {
            currentChamber = 0;
            launcher.chambers.set(0,Color.Nothing);
            launcher.chambers.set(1,Color.Nothing);
            launcher.chambers.set(2,Color.Nothing);
//            launcher.Zero();
            time = -300;
        }

        @Override
        public void execute() {
            if (time + 300 < System.currentTimeMillis()){
                color1 = launcher.getColor(launcher.cs1);
                color2 = launcher.getColor(launcher.cs2);
                Color color = color1 != Color.Nothing ? color1 : color2;

                if (color != Color.Nothing) {
                    launcher.chambers.set(currentChamber, color);
                    currentChamber += 1;
                    time = System.currentTimeMillis();
                    if (launcher.chambers.contains(Color.Nothing)){
                        launcher.toNext().schedule();
                    }
                }
            }
        }

        @Override
        public boolean isFinished() {
            return !launcher.chambers.contains(Color.Nothing);
        }

        @Override
        public void end(boolean interrupted) {
            if (launcher.chambers.get(0) == Color.Green){
                lastPicked = Picked.First;
            }
            else if (launcher.chambers.get(1) == Color.Green){
                lastPicked = Picked.Second;
            }
            else if (launcher.chambers.get(2) == Color.Green){
                lastPicked = Picked.Third;
            }
//            launcher.backShoot().schedule();
        }
    }

    public enum Color {
        Purple,
        Green,
        Nothing
    }
    public enum Motif {
        GPP,
        PGP,
        PPG
    }
    public enum Picked{
        First,
        Second,
        Third
    }
}
