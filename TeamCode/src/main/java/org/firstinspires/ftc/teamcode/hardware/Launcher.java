package org.firstinspires.ftc.teamcode.hardware;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.Subsystem;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.command.WaitCommand;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Collections;
import java.util.Set;
import java.util.Timer;

@Configurable
public class Launcher extends SubsystemBase {
    public DcMotor spinner;
    DcMotorEx flyWheel1;
    DcMotorEx flyWheel2;
    RevColorSensorV3 cs1;
    RevColorSensorV3 cs2;
    Servo pusher;
    int halfDelta = -170;
    int fullDelta = -450;
    private static final int CHAMBER1 = 0;
    private static final int CHAMBER2 = 1000;
    private static final int CHAMBER3 = 2000;
    Color[] chambers;
    public static int closeSpeed = 105;
    public static int farSpeed = 125;
    public static double power = .43;

    public Launcher(HardwareMap hardwareMap){
        this.chambers = new Color[3];
        spinner = hardwareMap.get(DcMotor.class,"spinner");
        spinner.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        spinner.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        spinner.setTargetPosition(0);
        spinner.setPower(.3);
        spinner.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        flyWheel1 = hardwareMap.get(DcMotorEx.class,"flywheel1");
        flyWheel2 = hardwareMap.get(DcMotorEx.class,"flywheel2");
        pusher = hardwareMap.get(Servo.class,"pusher");
        // cs1 = hardwareMap.get(RevColorSensorV3.class,"cs1");
        // cs2 = hardwareMap.get(RevColorSensorV3.class,"cs2");
        chambers[0] = Color.Nothing;
        chambers[1] = Color.Nothing;
        chambers[2] = Color.Nothing;
    }
    public Color getColor(RevColorSensorV3 cs){
        if (cs.green() > (cs.red() + cs.blue()) * .9) {
            return Color.Green;
        }
        else if (!(cs.green() > (cs.red() + cs.blue()) * .9) && cs.getDistance(DistanceUnit.INCH) < .8) {
            return Color.Purple;
        }
        return Color.Nothing;
    }
    public Command flywheelOn(boolean isClose){
        return new InstantCommand(()->{
            flyWheel1.setVelocity(-(isClose? closeSpeed:farSpeed), AngleUnit.DEGREES);
            flyWheel2.setVelocity((isClose? closeSpeed:farSpeed),AngleUnit.DEGREES);
//            flyWheel1.setPower(-power);
//            flyWheel2.setPower(power);
        });
    }

    public void setSpeed(int s){
        speed = s;
    }
    public Command flywheelOff(){
        return new InstantCommand(()->{
            flyWheel1.setVelocity(0);
            flyWheel2.setVelocity(0);
//            flyWheel1.setPower(0);
//            flyWheel2.setPower(0);
        });
    }
    public Command shoot() {
        return new Command() {
            double time;

            @Override
            public void initialize() {
                pusher.setPosition(.5);
                time = System.currentTimeMillis();
            }

            @Override
            public boolean isFinished() {
                return time + 1000 < System.currentTimeMillis();
            }

            @Override
            public Set<Subsystem> getRequirements() {
                return Collections.emptySet();
            }

            @Override
            public void end(boolean interrupted) {
                pusher.setPosition(0);
            }
        };
    };
    public Command fire(){
        return new SequentialCommandGroup(
            shoot(),
            new InstantCommand(this::fan),
            shoot(),
            new InstantCommand(this::fan),
            shoot());
    }

    public void fan(){
        spinner.setTargetPosition(spinner.getCurrentPosition()+fullDelta);
    }
    public void toShoot(){
        spinner.setTargetPosition(spinner.getCurrentPosition()+halfDelta);
    }
    public void toZero(){
        spinner.setTargetPosition(0);
    }

    public Command start(){

        return new ParallelCommandGroup(
                new InstantCommand(() -> flyWheel1.setPower(1)),
                new InstantCommand(() -> flyWheel2.setPower(1))
        );

    }
    public Command stop(){

        return new ParallelCommandGroup(
                new InstantCommand(() -> flyWheel1.setPower(0)),
                new InstantCommand(() -> flyWheel2.setPower(0))
        );

    }

    public static class Loading extends CommandBase{
        Launcher launcher;
        int order;
        int currentChamber = 0;
        public Loading(Launcher temp,int Torder){
            launcher = temp;
            order = Torder;
        }

        @Override
        public void initialize() {
            launcher.spinner.setTargetPosition(CHAMBER1);
            launcher.spinner.setPower(.3);
            currentChamber = 0;
        }

        @Override
        public void execute() {
            if (!launcher.spinner.isBusy()){
                if (launcher.getColor(launcher.cs1) != Color.Nothing) {
                    launcher.chambers[currentChamber] = launcher.getColor(launcher.cs1);
                    currentChamber += 1;
                    launcher.spinner.setTargetPosition(currentChamber* launcher.fullDelta);//will be wrong
                } else if (launcher.getColor(launcher.cs2) != Color.Nothing) {
                    launcher.chambers[currentChamber] = launcher.getColor(launcher.cs1);
                    currentChamber += 1;
                    launcher.spinner.setTargetPosition(currentChamber* launcher.fullDelta);//will be wrong
                }
            }
        }

        @Override
        public boolean isFinished() {
            return currentChamber == 3;
        }

        @Override
        public void end(boolean interrupted) {
            if(!interrupted){
                int greenLoc = 2;
                if (launcher.chambers[0] == Color.Green){
                    greenLoc = 0;
                }
                else if (launcher.chambers[1] == Color.Green){
                    greenLoc = 1;
                }
                launcher.spinner.setTargetPosition(launcher.spinner.getCurrentPosition()+((order-greenLoc)*1000)-500);
            }
            else {
                launcher.spinner.setTargetPosition(2500);
            }
        }
    }

    public enum Color{
        Purple,
        Green,
        Nothing
    }
}
