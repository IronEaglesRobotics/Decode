package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.Subsystem;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.command.WaitCommand;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Collections;
import java.util.Set;
import java.util.Timer;

public class Launcher extends SubsystemBase {
    DcMotor spinner;
    DcMotor flyWheel1;
    DcMotor flyWheel2;
    RevColorSensorV3 cs1;
    RevColorSensorV3 cs2;
    Servo pusher;
    private static final int CHAMBER1 = 0;
    private static final int CHAMBER2 = 1000;
    private static final int CHAMBER3 = 2000;
    Color[] chambers;

    public Launcher(HardwareMap hardwareMap){
        spinner = hardwareMap.get(DcMotor.class,"spinner");
        spinner.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        spinner.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        flyWheel1 = hardwareMap.get(DcMotor.class,"flywheel1");
        flyWheel2 = hardwareMap.get(DcMotor.class,"flywheel2");
        pusher = hardwareMap.get(Servo.class,"pusher");
        cs1 = hardwareMap.get(RevColorSensorV3.class,"cs1");
        cs2 = hardwareMap.get(RevColorSensorV3.class,"cs2");
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
    public Command shoot() {
        return new Command() {
            double time;

            @Override
            public void initialize() {
                pusher.setPosition(1);
                time = System.currentTimeMillis();
            }

            @Override
            public boolean isFinished() {
                return time + 100 > System.currentTimeMillis();
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
            new InstantCommand(()->spinner.setTargetPosition(spinner.getCurrentPosition()+1000)),
            shoot(),
            new InstantCommand(()->spinner.setTargetPosition(spinner.getCurrentPosition()+1000)),
            shoot());
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
            launcher.spinner.setPower(1);
            currentChamber = 0;
        }

        @Override
        public void execute() {
            if (!launcher.spinner.isBusy()){
                if (launcher.getColor(launcher.cs1) != Color.Nothing) {
                    launcher.chambers[currentChamber] = launcher.getColor(launcher.cs1);
                    currentChamber += 1;
                    launcher.spinner.setTargetPosition(currentChamber*1000);//will be wrong
                } else if (launcher.getColor(launcher.cs2) != Color.Nothing) {
                    launcher.chambers[currentChamber] = launcher.getColor(launcher.cs1);
                    currentChamber += 1;
                    launcher.spinner.setTargetPosition(currentChamber*1000);//will be wrong
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
