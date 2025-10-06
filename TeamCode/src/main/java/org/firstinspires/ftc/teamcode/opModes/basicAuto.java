package org.firstinspires.ftc.teamcode.opModes;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;

import org.firstinspires.ftc.teamcode.hardware.Bot;

@Autonomous(name = "basicAuto")
public class basicAuto extends CommandOpMode {
    Bot robot;
    @Override
    public void initialize() {
        robot = new Bot().init(hardwareMap,new Pose(-60,12),"blue");
        schedule(new main(robot));
    }

    public class main extends SequentialCommandGroup{
        Bot robot;
        public main (Bot bot){
            robot = bot;
            addCommands(
                    robot.getCamera().getMotif()
                            .whenFinished(()->telemetry.addData("motif",robot.getCamera().getOrder()))
                            .whenFinished(()->telemetry.update()),
                    robot.getDrive().moveTo(-50,12,20),
                    new WaitCommand(1000),
                    robot.getDrive().moveTo(-12,43,90),
                    new WaitCommand(1000),
                    robot.getDrive().turn(90),
                    new WaitCommand(2000),
                    robot.getDrive().moveTo(-50,12,20),
                    new WaitCommand(1000),
                    robot.getDrive().moveTo(-33,34,90),
                    new WaitCommand(1000),
                    robot.getDrive().moveToWithSpeed(-33,50,90,.1),
                    new WaitCommand(1000),
                    robot.getDrive().moveTo(-50,12,20),
                    new WaitCommand(1000),
                    robot.getDrive().moveTo(-42,55,100),
                    new WaitCommand(1000),
                    robot.getDrive().moveToWithSpeed(-55,55,100,.1),
                    new WaitCommand(1000),
//                    robot.getDrive().moveToWithSpeed(-33,50,90,.1),
//                    new WaitCommand(1000),
                    robot.getDrive().moveTo(-50,12,20)
            );
        }
    }
}
