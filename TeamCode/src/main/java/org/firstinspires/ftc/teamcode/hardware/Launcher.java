package org.firstinspires.ftc.teamcode.hardware;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.rev.RevColorSensorV3;
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
import com.seattlesolvers.solverslib.controller.PIDController;
import com.seattlesolvers.solverslib.hardware.motors.CRServo;
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
    public MotorEx spinner;
    public MotorEx flyWheel1;
    public MotorEx flyWheel2;
    public RevColorSensorV3 cs1;
    public RevColorSensorV3 cs2;
    //public CRServo quickLaunch;
    public static double kp = 6.1;
    public static double ki = 0.06;
    public static double kd = 0.005;
    public PIDController controller = new PIDController(kp,ki,kd);
    Servo pusher;
    Servo lift1;
    Servo lift2;
    public static int halfDelta = -238;
    public static int fullDelta = -475;
    public static int pidTarget = 0;
    private static final int CHAMBER1 = halfDelta+fullDelta;
    private static final int CHAMBER2 = halfDelta+(fullDelta*2);
    private static final int CHAMBER3 = halfDelta+(fullDelta*3);
    List<Color> chambers;
    public static int closeSpeed = -775;
    public static int farSpeed = -990;
    public static int autoSpeed = -750;
    public static double power = .43;
    public static double servoPos = 0;
    public static double liftpos = 1;

    public static double[] veloCoeffecient = new double[] {11,0,0};
    public static double[] feedforward = new double[] {11,0,0};

    public static int safePose = 0;
    double speed1 = 0;
    double speed2 = 0;
    private int order;

    public Launcher(HardwareMap hardwareMap){
        this.chambers = new ArrayList<>(3);
        spinner = new MotorEx(hardwareMap,"spinner", Motor.GoBILDA.RPM_223);
        spinner.resetEncoder();
        spinner.setRunMode(Motor.RunMode.VelocityControl);
        flyWheel1 = new MotorEx(hardwareMap,"flywheel1",28,6000);
        flyWheel2 = new MotorEx(hardwareMap,"flywheel2",28,6000);
        flyWheel1.setRunMode(Motor.RunMode.VelocityControl);
        flyWheel2.setRunMode(Motor.RunMode.VelocityControl);

        pusher = hardwareMap.get(Servo.class,"pusher");
        pusher.setDirection(Servo.Direction.FORWARD);
        pusher.setPosition(0.0000001);

        lift1 = hardwareMap.get(Servo.class,"lift1");
        lift1.setPosition(0);
        lift2 = hardwareMap.get(Servo.class, "lift2");
        lift2.setPosition(0);

        quickLaunch = new CRServo(hardwareMap,"quickLaunch");
        cs1 = hardwareMap.get(RevColorSensorV3.class,"cs1");
        cs2 = hardwareMap.get(RevColorSensorV3.class,"cs2");
        chambers.add(Color.Nothing);
        chambers.add(Color.Nothing);
        chambers.add(Color.Nothing);
        controller.setTolerance(40);
    }
    public Color getColor(RevColorSensorV3 cs){
        if (cs.green() > (cs.red() + cs.blue()) * .9 && cs.getDistance(DistanceUnit.INCH) < 1.1) {
            return Color.Green;
        }
        else if (!(cs.green() > (cs.red() + cs.blue()) * .9) && cs.getDistance(DistanceUnit.INCH) < 1.1) {
            return Color.Purple;
        }
        return Color.Nothing;
    }
    public void startQuickLaunch(){
        quickLaunch.set(1);
    }
    public void stopQuickLaunch(){
        quickLaunch.set(0);
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
            flyWheel1.setRunMode(Motor.RunMode.VelocityControl);
            speed1 = isClose? closeSpeed:farSpeed;
            //speed2 = isClose? closeSpeed:farSpeed;
//            flyWheel1.setPower(-power);
//            flyWheel2.setPower(power);
        });
    }
    public Command flywheelAuto(boolean isClose){
        return new InstantCommand(()->{
            flyWheel1.setRunMode(Motor.RunMode.VelocityControl);
            flyWheel1.setRunMode(Motor.RunMode.VelocityControl);
            speed1 = isClose? autoSpeed:farSpeed;
            //speed2 = isClose? closeSpeed:farSpeed;
//            flyWheel1.setPower(-power);
//            flyWheel2.setPower(power);
        });
    }
    public Command flywheelOff(){
        return new InstantCommand(()->{
            speed1 = 0;
            speed2 = 0;
//            flyWheel1.setPower(0);
//            flyWheel2.setPower(0);
        });
    }

    public Command Park(){
        return new InstantCommand(() ->{
            lift1.setPosition(liftpos);
            lift2.setPosition(liftpos);
        });
    }
    public Command UnPark(){
        return new InstantCommand(() ->{
            lift1.setPosition(0);
            lift2.setPosition(0);
        });
    }
    public Command shoot() {
        return new Command() {
            double time;

            @Override
            public void initialize() {
                servoPos = 0.15;
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
                servoPos = 0;
            }
        };
    }
    public void setOrder(int tOrder){
        order = tOrder;
    }
    public Command setLaunch(int order) {
        int i = chambers.indexOf(Color.Green);
        return setLaunch(i,order);
    }
    public Command setLaunch(){
        return setLaunch(order);
    }
    public void setChambers(Color[] colors){
        chambers.set(0,colors[0]);
        chambers.set(1,colors[1]);
        chambers.set(2,colors[2]);
    }

    public Command setLaunch(int greenLoc, int order) {
        return new Command() {
            @Override
            public Set<Subsystem> getRequirements() {
                return Collections.emptySet();
            }

            @Override
            public void initialize() {
                if (greenLoc == 0) {
                    pidTarget = CHAMBER1;
                } else if (greenLoc == 1) {
                    pidTarget = CHAMBER2;
                } else if (greenLoc == 2) {
                    pidTarget = CHAMBER3;
                }
                if (greenLoc == -1 || order == 0){
                    pidTarget = CHAMBER1;
                }
                else {
                    pidTarget -= fullDelta * (order - 1);
                }
            }

            @Override
            public boolean isFinished() {
                return controller.atSetPoint();
            }
        };
    }
    public boolean canShoot(){
        return atShootPos() && atTarget();
    }
    public boolean atShootPos(){
        return ((pidTarget - halfDelta)%fullDelta) == 0;
    }
    public boolean atTarget(){
        return controller.atSetPoint();
    }
    public Command fire(){
        return new SequentialCommandGroup(
            new WaitUntilCommand(this::atSpeed),
            shoot(),
            new WaitCommand(150),
            toNext(),
            shoot(),
            new WaitCommand(150),
            toNext(),
            shoot());
    }

    public void fan(){
        pidTarget = pidTarget +fullDelta;
        pidTarget = Math.max(Math.min(pidTarget,2000),-5000);
    }
    public void Shoot(){
        pidTarget = pidTarget +halfDelta;
        pidTarget = Math.max(Math.min(pidTarget,2000),-5000);
    }

    public void fixPose(){
        int fixer = spinner.getCurrentPosition()/fullDelta;
        pidTarget = fullDelta * fixer;
    }
    public void Zero(){
        pidTarget = 0;
    }
    public Command toNext(){
        return new Command() {
            @Override
            public Set<Subsystem> getRequirements() {
                //Set<Subsystem> set = new HashSet<>();
                //set.add(Launcher.this);
                return new HashSet<>();
            }

            @Override
            public void initialize() {
                pidTarget += fullDelta;
            }

            @Override
            public boolean isFinished() {
                return controller.atSetPoint();
            }
        };
    }
    public Command toShoot(){
        return new Command() {
            @Override
            public Set<Subsystem> getRequirements() {
                Set<Subsystem> set = new HashSet<>();
                set.add(Launcher.this);
                return set;
            }

            @Override
            public void initialize() {
                pidTarget += halfDelta;
            }

            @Override
            public boolean isFinished() {
                return controller.atSetPoint();
            }
        };
    }

    public Command backShoot(){
        return new Command() {
            @Override
            public Set<Subsystem> getRequirements() {
                Set<Subsystem> set = new HashSet<>();
                set.add(Launcher.this);
                return set;
            }

            @Override
            public void initialize() {
                pidTarget -= halfDelta;
            }

            @Override
            public boolean isFinished() {
                return controller.atSetPoint();
            }
        };
    }
    public Command toZero(){
        return new Command() {
            @Override
            public Set<Subsystem> getRequirements() {
                Set<Subsystem> set = new HashSet<>();
                set.add(Launcher.this);
                return set;
            }

            @Override
            public void initialize() {
                pidTarget = 0;
            }

            @Override
            public boolean isFinished() {
                return controller.atSetPoint();
            }
        };
    }
    public Command toFull(){
        return new SequentialCommandGroup(
                toNext(),
                toNext(),
                toNext()
        );
    }
    public int calculateVelo(MotorEx flywheel){
        return (int) (Math.abs(flywheel.getCorrectedVelocity())/.83);
    }
    public boolean atSpeed(){
        return Math.abs(speed1 + calculateVelo(flyWheel1)) < 30 &&
                Math.abs(speed1 + calculateVelo(flyWheel2)) < 30 &&
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
        controller.setPID(kp,ki,kd);
        controller.setSetPoint(pidTarget);
        flyWheel1.setVeloCoefficients(veloCoeffecient[0],veloCoeffecient[1],veloCoeffecient[2]);
        flyWheel1.setFeedforwardCoefficients(feedforward[0], feedforward[1],feedforward[2]);
        flyWheel2.setVeloCoefficients(veloCoeffecient[0],veloCoeffecient[1],veloCoeffecient[2]);
        flyWheel2.setFeedforwardCoefficients(feedforward[0], feedforward[1],feedforward[2]);
        spinner.setVelocity(controller.calculate(spinner.getCurrentPosition(), pidTarget));
        flyWheel1.setVelocity(speed1);
        flyWheel2.setVelocity(-speed1);
        pusher.setPosition(servoPos);
        if(controller.atSetPoint()){
            safePose = pidTarget;
        }
    }

    public static class Loading extends CommandBase{
        Launcher launcher;
        int order;
        int currentChamber = 0;
        Color color1;
        Color color2;
        public Loading(Launcher temp,int Torder){
            launcher = temp;
            order = Torder;
        }

        @Override
        public void initialize() {
            currentChamber = 0;
            pidTarget = 0;
            launcher.chambers.set(0,Color.Nothing);
            launcher.chambers.set(1,Color.Nothing);
            launcher.chambers.set(2,Color.Nothing);
        }

        @Override
        public void execute() {
            if (launcher.controller.atSetPoint()){
                color1 = launcher.getColor(launcher.cs1);
                color2 = launcher.getColor(launcher.cs2);
                Color color = color1 != Color.Nothing ? color1 : color2;

                if (color != Color.Nothing ) {
                    launcher.chambers.set(currentChamber,color);
                    currentChamber += 1;
                    pidTarget += fullDelta;
                }
            }
        }

        @Override
        public boolean isFinished() {
            return !launcher.chambers.contains(Color.Nothing);
        }

        @Override
        public void end(boolean interrupted) {
            if (!interrupted) {
                launcher.pidTarget = halfDelta;
                if (launcher.chambers.get(0) == Color.Green) {
                    launcher.pidTarget += fullDelta;
                } else if (launcher.chambers.get(1) == Color.Green) {
                    launcher.pidTarget += fullDelta * 2;
                } else if (launcher.chambers.get(2) == Color.Green) {
                    launcher.pidTarget += fullDelta * 3;
                } else {
                    launcher.pidTarget += fullDelta;
                }
//                launcher.current -= fullDelta * (order - 1);

            } else {
                launcher.pidTarget = CHAMBER1;
            }
        }
    }

    public enum Color {
        Purple,
        Green,
        Nothing
    }
    public enum speed{
        Close,
        Far,
        Auto
    }
}
