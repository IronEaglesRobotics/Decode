package org.firstinspires.ftc.teamcode.opModes;

import com.pedropathing.follower.Follower;
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

    Bot robot;
    Follower follower;
    final Command farshoot = robot.getDrive().moveTo(-50,12,20);
    final Command togate = robot.getDrive().moveTo(-21,43,90);
    final Command loadzonepick = robot.getDrive().moveTo(-55,55,90);
    final Command pick2 = new SequentialCommandGroup(
            robot.getDrive().moveTo(-21,34,90),
            new SequentialCommandGroup(
                    robot.getIntake().start(),
                    robot.getDrive().moveToWithSpeed(-21,43,90, .1)
            ),
            new WaitCommand(200),
            robot.getIntake().stop()

    );
    final Command pick1 = new SequentialCommandGroup(
            robot.getDrive().moveTo(-33,43,90),
            new SequentialCommandGroup(
                    robot.getIntake().start(),
                    robot.getDrive().moveToWithSpeed(-33,50,90,.1)
            ),
            new WaitCommand(200),
            robot.getIntake().stop()
    );
    final Command humanplayzone = robot.getDrive().moveToWithSpeed(-55,55,100,.7);

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
        robot.aim(),

    );

    Command farBlueline3 = new SequentialCommandGroup(
      farshoot,
      robot.aim(),
      pick2,
      
    );


    public hyperAuto(String color, boolean isFar, int lines){
        telemetry.addData("color: ",color);
        telemetry.addData("starting far: ",isFar);
        telemetry.addData("lines: ", lines);
        telemetry.update();
    }



    @Override
    public void initialize() {

    }

}
