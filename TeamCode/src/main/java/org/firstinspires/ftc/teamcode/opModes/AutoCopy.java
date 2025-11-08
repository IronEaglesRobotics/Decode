package org.firstinspires.ftc.teamcode.opModes;

import static org.opencv.core.Core.flip;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.ParallelRaceGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.hardware.Bot;
import org.firstinspires.ftc.teamcode.hardware.PPcommands;

@Autonomous(name =  "CAuto")
public class AutoCopy extends OpMode{

    private String color;
    private boolean isFar;
    private int lines;
    Bot robot;
    GamepadEx controller; // plz DO NOT feed into Bot
    int delay;
    int state = 0;
    public PPcommands.Paths paths;
    Command farshoot;
    Command closeshoot;
    Command togate;
    Command loadzonepick;
    Command pick3;
    Command pick2;
    Command pick1;
    Command humanplayzone;
    int shot = -1;
    private void makeAuto(PPcommands.Paths paths) {
        //farshoot = robot.getDrive().pathCommand(paths.Path1);
        togate = robot.getDrive().pathCommand(paths.Path4);
        //loadzonepick = robot.getDrive().moveTo(-55 * side, 55, 90 * side);
        pick3 = new SequentialCommandGroup(
                robot.getDrive().pathCommand(paths.Path6),
                new SequentialCommandGroup(
                        robot.getIntake().start()
                                .alongWith(robot.getLauncher().toZero()),
                        robot.getDrive().pathCommand(paths.Path7,.05),
                        new WaitCommand(200)
                )
        );
        pick2 = new SequentialCommandGroup(
                robot.getDrive().pathCommand(paths.Path2),
                new SequentialCommandGroup(
                        robot.getIntake().start()
                                .alongWith(robot.getLauncher().toZero()),
                    robot.getDrive().pathCommand(paths.Path3,.05),
                        new WaitCommand(200)
                )

        );
//        pick1 = new SequentialCommandGroup(
//                robot.getDrive().pathCommand(paths.Path9),
//                new SequentialCommandGroup(
//                        robot.getIntake().start(),
//                        new ParallelCommandGroup(
//                                robot.loading(),
//                                robot.getDrive().pathCommand(paths.Path10)
//                        )
//                ),
//                new WaitCommand(200),
//                robot.getIntake().stop()
        pick1 = new ParallelCommandGroup(
                robot.getDrive().moveTo(paths.Path1.endPose().getX(),120,135),
                robot.getLauncher().toZero()
        );
        //humanplayzone = robot.getDrive().moveToWithSpeed(-55 * side, 55, 100 * side, .7);
    }

    public Command closeshoot(){
        shot++;
        return robot.getDrive().pathCommand(paths.shootPaths[shot]);
    }
    public void run(){
        switch (state){
            case -3:
//                robot.getCamera().getMotif()
//                        .raceWith(new WaitCommand(1000)
//                                .andThen(new InstantCommand(()->robot.getCamera().setOrder(1))))
//                        .andThen(robot.loading()
//                                .alongWith(new InstantCommand(()->state = 0)));
//                state = -1;
                break;
            case -2:
                state = lines;
                lines-=1;
            case -1:
                break;
            case 0:
                new SequentialCommandGroup(
                        closeshoot(),
                        robot.getLauncher().flywheelOn(!isFar),
                        new WaitCommand(700),
                        robot.getLauncher().fire(),
                        new WaitCommand(500),
                        new InstantCommand(()->state=-2)
                )
                        .schedule();
                state = -1;
                break;
            case 1:
                state = -1;
                break;
            case 2:
                pick1
                        .whenFinished(()->state = 0)
                        .schedule();
                state = -1;
                break;
            case 3:
                robot.loading()
                        .andThen(robot.getIntake().stop())
                        .schedule();
                new SequentialCommandGroup(
                        pick3
                        .whenFinished(()->state = 0)
                )
                        .schedule();
                state = -1;
                break;
            case 4:
                robot.loading()
                        .andThen(robot.getIntake().stop())
                        .schedule();
                new SequentialCommandGroup(
                        pick2,
                        new InstantCommand(()->state = 0)
                )
                        .schedule();
                state = -1;
                break;
        }
    }


    @Override
    public void init() {
        robot = new Bot().init(hardwareMap,null);
        controller = new GamepadEx(gamepad1);
    }
    @Override
    public void init_loop(){
        controller.readButtons();
        if(controller.wasJustPressed(GamepadKeys.Button.A)){
            lines = 4;
        }
        if(controller.wasJustPressed(GamepadKeys.Button.B)){
            lines = 3;
        }
        if(controller.wasJustPressed(GamepadKeys.Button.X)){
            lines = 2;
        }
        if(controller.wasJustPressed(GamepadKeys.Button.Y)){
            lines = 1;
        }
        if(controller.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)){
            color = "red";
        }
        if(controller.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)){
            color = "blue";
        }
        if(controller.wasJustPressed(GamepadKeys.Button.RIGHT_STICK_BUTTON)){
            isFar = true;
        }
        if(controller.wasJustPressed(GamepadKeys.Button.LEFT_STICK_BUTTON)){
            isFar = false;
        }
        if(controller.wasJustPressed(GamepadKeys.Button.DPAD_UP)){
            delay += 100;
        }
        if(controller.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)){
            delay -= 100;
        }
        telemetry.addData("lines", lines-1);
        telemetry.addData("if far", isFar);
        telemetry.addData("color", color);
        telemetry.addData("delay",((double)delay)/1000);
        telemetry.addLine("Lines: A:4 B:3 X:2 Y:1");
        telemetry.addLine("Color: Left Bumper: red Right Bumper: blue");
        telemetry.addLine("Start: Right Stick: far Left Stick: close");
        telemetry.addLine("Dpad: delay ^v");
        telemetry.update();
    }
    @Override
    public void start(){
        robot.getDrive().getFollower().setStartingPose(new Pose(color.equalsIgnoreCase("blue")? 56:88 , 20,Math.toRadians(90)));
        paths = new PPcommands.Paths(robot.getDrive().getFollower(),color.equalsIgnoreCase("blue"));
        makeAuto(paths);
        robot.getCamera().setOrder(2);
        robot.loading().schedule();
    }

    @Override
    public void loop() {
        run();
        CommandScheduler.getInstance().run();
        robot.getDrive().getFollower().update();
        telemetry.addData("pose",robot.getDrive().getPose());
        telemetry.addData("order", robot.getCamera().getOrder());
        telemetry.addData("state", state);
        telemetry.update();
    }
    public double flip(double standard, boolean ifFlip){
        return Math.abs(standard - (!ifFlip ? 72:1))+(ifFlip ? 72:1);
    }

    public void stop(){
        CommandScheduler.getInstance().cancelAll();
        CommandScheduler.getInstance().reset();
        super.stop();
    }

}
