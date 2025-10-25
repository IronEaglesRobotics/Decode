package org.firstinspires.ftc.teamcode.opModes;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.telemetry.SelectScope;
import com.pedropathing.telemetry.SelectableOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;

import org.firstinspires.ftc.teamcode.hardware.Bot;
import org.firstinspires.ftc.teamcode.hardware.Intake;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Autonomous(name =  "Auto")
public class multiAuto extends SelectableOpMode {
    public multiAuto() {
        super("Select a color", s -> {
            s.folder("red", r -> {
                r.folder("close", p -> {
                    r.add("1+3", () -> new hyperAuto("red", false, 4));
                    r.add("1+2", () -> new hyperAuto("red", false, 3));
                    r.add("1+1", () -> new hyperAuto("red", false, 2));
                    r.add("1+0", () -> new hyperAuto("red", false, 1));
                });
                r.folder("far", p -> {
                    r.add("1+3", () -> new hyperAuto("red", true, 4));
                    r.add("1+2", () -> new hyperAuto("red", true, 3));
                    r.add("1+1", () -> new hyperAuto("red", true, 2));
                    r.add("1+0", () -> new hyperAuto("red", true, 1));
                });
            });
            s.folder("blue", r -> {
                r.folder("close", p -> {
                    r.add("1+3", () -> new hyperAuto("blue", false, 4));
                    r.add("1+2", () -> new hyperAuto("blue", false, 3));
                    r.add("1+1", () -> new hyperAuto("blue", false, 2));
                    r.add("1+0", () -> new hyperAuto("blue", false, 1));
                });
                r.folder("far", p -> {
                    r.add("1+3", () -> new hyperAuto("blue", true, 4));
                    r.add("1+2", () -> new hyperAuto("blue", true, 3));
                    r.add("1+1", () -> new hyperAuto("blue", true, 2));
                    r.add("1+0", () -> new hyperAuto("blue", true, 1));
                });
            });
        });
    }
}
class hyperAuto extends CommandOpMode{

    private final String color;
    private final boolean isFar;
    private final int lines;
    Bot robot;
    Follower follower;

    private Command makeAuto(int amount,int side, boolean far){
        final Command farshoot = robot.getDrive().moveTo(-50*side, 12, 20*side);
        final Command closeshoot = robot.getDrive().moveTo(0,0,20*side);
        final Command togate = robot.getDrive().moveTo(-21*side, 43, 90*side);
        final Command loadzonepick = robot.getDrive().moveTo(-55*side, 55, 90*side);
        final Command pick2 = new SequentialCommandGroup(
                robot.getDrive().moveTo(-21*side, 34, 90*side),
                new SequentialCommandGroup(
                        robot.getIntake().start(),
                        robot.getDrive().moveToWithSpeed(-21*side, 43, 90*side, .3)
                ),
                new WaitCommand(200),
                robot.getIntake().stop()

        );
        final Command pick1 = new SequentialCommandGroup(
                robot.getDrive().moveTo(-33*side, 43, 90*side),
                new SequentialCommandGroup(
                        robot.getIntake().start(),
                        robot.getDrive().moveToWithSpeed(-33*side, 50, 90*side, .3)
                ),
                new WaitCommand(200),
                robot.getIntake().stop()
        );
        final Command humanplayzone = robot.getDrive().moveToWithSpeed(-55*side, 55, 100*side, .7);

        Command farBlueline1 = new SequentialCommandGroup(
                farshoot,
                robot.aim(),
                pick1,
                farshoot,
                robot.aim()

        );

        Command farBlueline2 = new SequentialCommandGroup(
                farshoot,
                robot.aim(),
                pick1,
                farshoot,
                robot.aim(),
                pick2,
                farshoot,
                robot.aim()

        );

        Command farBlueline3 = new SequentialCommandGroup(
                farshoot,
                robot.aim(),
                pick2,
                togate,
                farshoot,
                robot.aim(),
                humanplayzone,
                farshoot,
                robot.aim(),
                pick1,
                robot.aim()
        );
        Command closeBlueline1 = new SequentialCommandGroup(
                closeshoot,
                robot.aim(),
                pick1,
                closeshoot,
                robot.aim()

        );

        Command closeBlueline2 = new SequentialCommandGroup(
                closeshoot,
                robot.aim(),
                pick1,
                closeshoot,
                robot.aim(),
                pick2,
                closeshoot,
                robot.aim()

        );

        Command closeBlueline3 = new SequentialCommandGroup(
                closeshoot,
                robot.aim(),
                pick2,
                togate,
                closeshoot,
                robot.aim(),
                humanplayzone,
                closeshoot,
                robot.aim(),
                pick1,
                robot.aim()
        );
        return null;
    }



    public hyperAuto(String color, boolean isFar, int lines){
        this.color = color;
        this.isFar = isFar;
        this.lines = lines;
    }



    @Override
    public void initialize() {
        robot = new Bot().init(hardwareMap,isFar ? new Pose(-60,12) : new Pose(12,12),color,null);
        makeAuto(lines,color.equalsIgnoreCase("blue")?1:-1,isFar);
    }

}
