package org.firstinspires.ftc.teamcode.hardware;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.Subsystem;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.controller.PIDController;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Collections;
import java.util.Set;

@Configurable
public class Launcher extends SubsystemBase {
    public MotorEx spinner;
    MotorEx flyWheel1;
    MotorEx flyWheel2;
    public RevColorSensorV3 cs1;
    public RevColorSensorV3 cs2;
    public static double kp = 10;
    public static double ki = 10;
    public static double kd = 0;
    PIDController controller = new PIDController(kp, ki, kd);
    public Servo pusher;
    public static int halfDelta = -238;
    public static int fullDelta = -470;
    public int current = 0;

    private static final int CHAMBER1 = halfDelta + fullDelta;
    private static final int CHAMBER2 = halfDelta + (fullDelta * 2);
    private static final int CHAMBER3 = halfDelta;
    public static double starting = 0.25;
    public static double push = 0.4;
    Color[] chambers;
    public static int closeSpeed = 1000;
    public static int farSpeed = 1150;
    public static double power = .43;
    double speed1 = 0;
    double speed2 = 0;

    public Launcher(HardwareMap hardwareMap) {
        this.chambers = new Color[3];
        spinner = new MotorEx(hardwareMap, "spinner", Motor.GoBILDA.RPM_223);
        spinner.resetEncoder();
        spinner.setRunMode(Motor.RunMode.VelocityControl);
        flyWheel1 = new MotorEx(hardwareMap, "flywheel1", 28, 6000);
        flyWheel2 = new MotorEx(hardwareMap, "flywheel2", 28, 6000);
        flyWheel1.setRunMode(Motor.RunMode.VelocityControl);
        flyWheel2.setRunMode(Motor.RunMode.VelocityControl);
        pusher = hardwareMap.servo.get("pusher");
        pusher.setDirection(Servo.Direction.REVERSE);
        cs1 = hardwareMap.get(RevColorSensorV3.class, "cs1");
        cs2 = hardwareMap.get(RevColorSensorV3.class, "cs2");
        chambers[0] = Color.Nothing;
        chambers[1] = Color.Nothing;
        chambers[2] = Color.Nothing;
        controller.setTolerance(20);
    }

    public Color getColor(RevColorSensorV3 cs) {
        if (cs.green() > (cs.red() + cs.blue()) * .9) {
            return Color.Green;
        } else if (!(cs.green() > (cs.red() + cs.blue()) * .9) && cs.getDistance(DistanceUnit.INCH) < .8) {
            return Color.Purple;
        }
        return Color.Nothing;
    }

    public Command flywheelOn(boolean isClose) {
        return new InstantCommand(() -> {
            flyWheel1.setRunMode(Motor.RunMode.VelocityControl);
            flyWheel1.setRunMode(Motor.RunMode.VelocityControl);
            speed1 = -(isClose ? closeSpeed : farSpeed);
            speed2 = isClose ? closeSpeed : farSpeed;
//            flyWheel1.setPower(-power);
//            flyWheel2.setPower(power);
        });
    }

    public Command flywheelOff() {
        return new InstantCommand(() -> {
            speed1 = 0;
            speed2 = 0;
//            flyWheel1.setPower(0);
//            flyWheel2.setPower(0);
        });
    }

    public void shoot() {
        pusher.setPosition(push);
    }

    ;

    public void retract() {
        pusher.setPosition(starting);
    }

//    public Command fire(){
//        return new SequentialCommandGroup(
//            shoot(),
//            new InstantCommand(this::fan),
//            shoot(),
//            new InstantCommand(this::fan),
//            shoot());
//    }

    public void fan() {
        current = current + fullDelta;
        current = Math.max(Math.min(current, 2000), -5000);
    }

    //    public void Shoot(){
//        current= current+halfDelta;
//        current = Math.max(Math.min(current,2000),-5000);
//    }
    public void Zero() {
        current = 0;
    }

    public Command toNext() {
        return new InstantCommand(() -> {
            current = current + fullDelta;
//            current = Math.max(Math.min(current, 2000), -5000);
        });
    }

    public Command toShoot() {
        return new InstantCommand(() -> {
            current = current + halfDelta;
//            current = Math.max(Math.min(current, 2000), -5000);
        });
    }

    public Command toZero() {
        return new InstantCommand(() -> current = 0);
    }

    public Command start() {

        return new SequentialCommandGroup(
                new InstantCommand(() -> {
                    flyWheel1.setRunMode(Motor.RunMode.RawPower);
                    flyWheel1.setRunMode(Motor.RunMode.RawPower);
                }),
                new InstantCommand(() -> flyWheel1.set(-0.43)),
                new InstantCommand(() -> flyWheel2.set(0.43))
        );

    }

    public Command stop() {

        return new ParallelCommandGroup(
                new InstantCommand(() -> flyWheel1.stopMotor()),
                new InstantCommand(() -> flyWheel2.stopMotor())
        );

    }

    public String getTelemetry() {
        return "slot 0: " + chambers[0] + " slot 1: " + chambers[1] + " slot 2: " + chambers[2];
    }

    @Override
    public void periodic() {
        controller.setPID(kp, ki, kd);
        controller.setSetPoint(current);
        spinner.setVelocity(controller.calculate(spinner.getCurrentPosition(), current));
        flyWheel1.setVelocity(speed1);
        flyWheel2.setVelocity(speed2);
    }

    public static class Loading extends CommandBase {
        Launcher launcher;
        int order;
        int currentChamber = 0;

        public Loading(Launcher temp, int Torder) {
            launcher = temp;
            order = Torder;
        }

        @Override
        public void initialize() {
            currentChamber = 0;
        }

        @Override
        public void execute() {
            if (launcher.controller.atSetPoint()) {
                if (launcher.getColor(launcher.cs1) != Color.Nothing) {
                    launcher.chambers[currentChamber] = launcher.getColor(launcher.cs1);
                    currentChamber += 1;
                    launcher.fan();
                } else if (launcher.getColor(launcher.cs2) != Color.Nothing) {
                    launcher.chambers[currentChamber] = launcher.getColor(launcher.cs2);
                    currentChamber += 1;
                    launcher.fan();
                }
            }
        }

        @Override
        public boolean isFinished() {
            return currentChamber == 3;
        }

        @Override
        public void end(boolean interrupted) {
            if (!interrupted) {
                int greenLoc = 0;
                if (launcher.chambers[0] == Color.Green) {
                    greenLoc = 0;
                    launcher.current = CHAMBER1;
                } else if (launcher.chambers[1] == Color.Green) {
                    greenLoc = 1;
                    launcher.current = CHAMBER2;
                } else if (launcher.chambers[2] == Color.Green) {
                    greenLoc = 2;
                    launcher.current = CHAMBER3;
                }
                int exception = 0;
                for (Color color : launcher.chambers) {
                    if (color == Color.Purple) {
                        exception++;
                    }
                }
                if (exception == 3) {
                    launcher.current = halfDelta;
                } else {
                    launcher.current -= fullDelta * (order - 1);
                }

            } else {
                launcher.current = 2500;
            }
        }
    }

    public enum Color {
        Purple,
        Green,
        Nothing
    }
}
