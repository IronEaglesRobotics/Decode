package org.firstinspires.ftc.teamcode.opModes;

import com.pedropathing.telemetry.SelectScope;
import com.pedropathing.telemetry.SelectableOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.CommandOpMode;

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
