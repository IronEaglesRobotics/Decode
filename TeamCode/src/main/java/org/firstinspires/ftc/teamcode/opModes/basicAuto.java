package org.firstinspires.ftc.teamcode.opModes;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

import org.firstinspires.ftc.teamcode.hardware.Bot;

@Autonomous(name = "basicAuto")
public class basicAuto extends CommandOpMode {
    Bot robot;
    private final Pose farshoot = new Pose(-50,12, Math.toRadians(20));
    private final Pose prepickup1 = new Pose(-21,34, Math.toRadians(90));

    private final Pose gate = new Pose(-21,43,Math.toRadians(90));

    private final Pose prepickup2 = new Pose(-33,43,Math.toRadians(90));
    private final Pose loadingzonepick = new Pose(-55,55,Math.toRadians(90));
    @Override
    public void initialize() {
        robot = new Bot().init(hardwareMap,null);
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
                    robot.aim(),//launch
                    new WaitCommand(1000),
                    robot.getDrive().moveTo(-12,34,90),//pre pickup 1
                    new WaitCommand(1000),
                    robot.getDrive().moveToWithSpeed(-21,43,90, .1),//pick up
                    new WaitCommand(1000),
//                    robot.getDrive().turn(90),// hit gate
//                    new WaitCommand(2000),
                    robot.getDrive().moveTo(-50,12,20),// return to launch
                    new WaitCommand(1000),
                    robot.getDrive().moveTo(-33,34,90), //pre pickup 2
                    new WaitCommand(1000),
                    robot.getDrive().moveToWithSpeed(-33,50,90,.1), // picks up
                    new WaitCommand(1000),
                    robot.getDrive().moveTo(-50,12,20), // return to launch
                    new WaitCommand(1000),
                    robot.getDrive().moveTo(-42,55,100), // pre pick up in load zone
                    new WaitCommand(1000),
                    robot.getDrive().moveToWithSpeed(-55,55,100,.1), // pick up in load
                    new WaitCommand(1000),
//                    robot.getDrive().moveToWithSpeed(-33,50,90,.1),
//                    new WaitCommand(1000),
                    robot.getDrive().moveTo(-50,12,20) // return to shoot
            );
        }
    }
}
