package org.firstinspires.ftc.teamcode.opModes;

import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.HeadingInterpolator;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.DeferredCommand;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.command.WaitUntilCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.hardware.Bot;
import org.firstinspires.ftc.teamcode.hardware.Launcher;
import org.firstinspires.ftc.teamcode.hardware.Paths;
import org.firstinspires.ftc.teamcode.hardware.Storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Autonomous(name = "AutoClose")
public class AutoClose extends OpMode {

    private Alliance color = Alliance.Blue;
    private int lines = 3;
    private boolean hitGate = false;
    Bot robot;
    GamepadEx controller; // plz DO NOT feed into Bot
    int delay;
    States state = States.motifDetect;
    States lastState = States.idle;
    States nextState;
    boolean wantsShoot = false;
    public Paths paths;
    boolean finished;
    int green = 0;

    @Override
    public void init() {
        robot = new Bot().init(hardwareMap, null);
        controller = new GamepadEx(gamepad1);
    }

    @Override
    public void init_loop() {
        controller.readButtons();
        if (controller.wasJustPressed(GamepadKeys.Button.A)) {
            lines = 4;
        }
        if (controller.wasJustPressed(GamepadKeys.Button.B)) {
            lines = 3;
        }
        if (controller.wasJustPressed(GamepadKeys.Button.X)) {
            lines = 2;
        }
        if (controller.wasJustPressed(GamepadKeys.Button.Y)) {
            lines = 1;
        }
        if (controller.wasJustPressed(GamepadKeys.Button.RIGHT_STICK_BUTTON)){
            lines = 0;
        }
        if (controller.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
            color = Alliance.Red;
        }
        if (controller.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
            color = Alliance.Blue;
        }
        if (controller.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
            delay += 500;
        }
        if (controller.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
            delay -= 500;
        }
        if (controller.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)){
            hitGate = true;
        }
        if (controller.wasJustPressed(GamepadKeys.Button.DPAD_LEFT)){
            hitGate = false;
        }
        telemetry.addData("shots", lines - 1);
        telemetry.addData("color", color);
        telemetry.addData("delay", ((double) delay) / 1000);
        telemetry.addData("hit gate",hitGate);
        telemetry.addLine("Lines: A:4 B:3 X:2 Y:1");
        telemetry.addLine("Color: Left Bumper: red Right Bumper: blue");
        telemetry.addLine("Start: Right Stick: far Left Stick: close");
        telemetry.addLine("delay: Dpad ^v");
        telemetry.addLine("Gate: Dpad <>");
        telemetry.update();
    }

    @Override
    public void start() {
        robot.getDrive().getFollower().setStartingPose(
                new Pose(color == Alliance.Blue ? 14 : 129,
                        135,
                        Math.toRadians(color == Alliance.Blue ? 45 : 135)));
        paths = new Paths(color == Alliance.Blue);
        state = lines == 1 ? States.finish : state;
        nextState = hitGate ? States.pick2 : nextState;
        robot.getLauncher().flywheelAuto(true).schedule();
    }

    @Override
    public void loop() {
        run();
        CommandScheduler.getInstance().run();
        robot.getDrive().getFollower().update();
        robot.getLauncher().updateSpindexer();
//        telemetry.addData("order", robot.getCamera().getOrder());
//        telemetry.addData("last state", lastState);
//        telemetry.addData("wantsShoot", wantsShoot);
//        telemetry.addData("target", Launcher.pidTarget);
//        telemetry.addData("current", robot.getLauncher().spinner.getCurrentPosition());
//        telemetry.addData("can shoot", robot.getLauncher().canShoot());
//        telemetry.addData("is busy", robot.getDrive().getFollower().isBusy());
//        telemetry.addData("loading finished", finished);
//        telemetry.addData("cs 1", robot.getLauncher().getColor(robot.getLauncher().cs1));
//        telemetry.addData("cs 2", robot.getLauncher().getColor(robot.getLauncher().cs2));
//        telemetry.update();
    }

    public void stop() {
        CommandScheduler.getInstance().cancelAll();
        CommandScheduler.getInstance().reset();
        Storage.getInstance().setPose(robot.getDrive().getPose(),paths.Path1);
        super.stop();
    }

    public void run() {
        switch (state) {
            case motifDetect:
                nextState = lines > 0 ? States.settingLaunch : States.finish;
                new SequentialCommandGroup(
                    robot.getLauncher().toShoot()
                            .alongWith(new WaitCommand(100)
                                    .andThen(PathShootEx())),
                    new WaitCommand(200),
                    robot.getCamera().getMotif(),
                    new WaitCommand(50),
                    new InstantCommand(()-> state = nextState))
                        .schedule();
                state = States.idle;
                break;
            case settingLaunch:
                nextState = lines > 1 ? States.pick1 : States.finish;
                new WaitCommand(delay)
                        .andThen(
                            new SequentialCommandGroup(
                                robot.getLauncher().setLaunch(0,robot.getCamera().getOrder())
                                        .alongWith(PathShoot()),
                                new WaitUntilCommand(robot.getLauncher()::canShoot),
                                robot.getLauncher().fire(),
                                new WaitCommand(150),
                                new InstantCommand(()->state = nextState)
                                        .alongWith(robot.getLauncher().toShoot())
                            )
                        )
                        .schedule();
                state = States.idle;
                break;
            case swap:
                state = nextState;
            case idle:
                break;
            case pick1:
                finished = false;
                lastState = States.pick1;
                nextState = lines > 2 ? States.pick2 : States.finish;
                wantsShoot = false;
                PathChain pathChain3 = robot.getDrive().getFollower().pathBuilder()
                    .addPath(new BezierCurve(paths.Path7,
                            paths.Path7Ex.plus(new Pose(color == Alliance.Blue ? 12:-12, 3)),
                            paths.Path7Ex))
                    .setLinearHeadingInterpolation(paths.Path7.getHeading(), paths.Path7Ex.getHeading())
                    .build();
                green = 2;
                new SequentialCommandGroup(
                    Pick1(),
                        new ParallelCommandGroup(
                            new SequentialCommandGroup(
                                robot.getLauncher().toFull(),
                                robot.getLauncher().setLaunch(green,robot.getCamera().getOrder())),
                            new SequentialCommandGroup(
                                hitGate ? robot.getDrive().pathCommand(pathChain3) : new WaitCommand(20),
                                new WaitCommand(1000),
                                new DeferredCommand(this::PathShoot, Collections.emptyList())
                            )
                        ),
                    new WaitUntilCommand(()->robot.getLauncher().canShoot()),
                    robot.getLauncher().fire(),
                    new WaitCommand(500),
                    new InstantCommand(()->state = nextState)
//                            .alongWith(robot.getLauncher().toShoot()
//                                    .andThen(new InstantCommand(robot.getLauncher()::resetEncoder)))
                ).schedule();
                state = States.idle;
                break;
            case pick2:
                finished = false;
                lastState = States.pick2;
                nextState = lines > 3 ? States.pickFar : States.finish;
                wantsShoot = false;
                green = 1;
                PathChain pathChain4 = robot.getDrive().getFollower().pathBuilder()
                    .addPath(new BezierCurve(paths.Path3,
                            paths.Path7Ex.plus(new Pose(color == Alliance.Blue ? 9 : -9, -3)),
                            paths.Path7Ex.plus(new Pose(0, -5))))
                    .setLinearHeadingInterpolation(paths.Path7.getHeading(), paths.Path7Ex.getHeading() + Math.PI)
                    .build();
                new SequentialCommandGroup(
                    Pick2(),
                    new ParallelCommandGroup(
                        new SequentialCommandGroup(
                            robot.getLauncher().toFull(),
                            robot.getLauncher().setLaunch(green,robot.getCamera().getOrder())),
                        new SequentialCommandGroup(
                            hitGate ? robot.getDrive().pathCommand(pathChain4) : new WaitCommand(20),
                            new WaitCommand(1000),
                            new DeferredCommand(this::PathShoot2, Collections.emptyList())
                        )
                    ),
                    new WaitUntilCommand(()->robot.getLauncher().canShoot()),
                    robot.getLauncher().fire(),
                    new WaitCommand(150),
                    new InstantCommand(()->state = nextState)
//                            .alongWith(robot.getLauncher().toShoot()
//                                    .andThen(new InstantCommand(robot.getLauncher()::resetEncoder)))
                ).schedule();
                state = States.idle;
                break;
            case pickFar:
                finished = false;
                lastState = States.pickFar;
                PathChain pathChain = robot.getDrive().getFollower().pathBuilder()
                        .addPath(new BezierLine(paths.Path10,paths.Path1.plus(new Pose(0,15))))
                        .setHeadingInterpolation(HeadingInterpolator.facingPoint(
                                new Pose(color == Alliance.Blue ? 0 : 144, 146)))
                        .build();
                wantsShoot = false;
                green = 0;
                new SequentialCommandGroup(
                    Pick3(),
                    new ParallelCommandGroup(
                            robot.getDrive().pathCommand(pathChain),
                            robot.getLauncher().toFull(),
                            robot.getLauncher().setLaunch(green,robot.getCamera().getOrder())
                    ),
                    new WaitUntilCommand(()->robot.getLauncher().canShoot()),
                    robot.getLauncher().fire()
//                    robot.getLauncher().toShoot(),
//                    new InstantCommand(robot.getLauncher()::resetEncoder)
                ).schedule();
                state = States.idle;
                break;
            case finish:
                lastState = States.finish;
                finish().schedule();
                robot.getLauncher().flywheelOff().schedule();
                robot.getLauncher().toShoot()
                        .andThen(new InstantCommand(robot.getLauncher()::resetEncoder))
                        .schedule();
                robot.getIntake().stop().schedule();
                state = States.idle;
                break;
        }
    }

    public enum States {
        motifDetect,
        settingLaunch,
        idle,
        swap,
        pick1,
        pick2,
        pickFar,
        finish
    }
    public Command PathShoot() {
        return robot.getDrive().pathCommand(robot.getDrive().getFollower().pathBuilder()
            .addPath(new BezierLine(robot.getDrive().getPose(), paths.Path1))
            .setHeadingInterpolation(HeadingInterpolator.facingPoint(new Pose(
                    color == Alliance.Blue ? 0 :144, 147)))
            .build());
    }
    public Command PathShoot2() {
        return robot.getDrive().pathCommand(robot.getDrive().getFollower().pathBuilder()
            .addPath(new BezierCurve(robot.getDrive().getPose(), paths.Path3Ex, paths.Path1))
            .setHeadingInterpolation(HeadingInterpolator.facingPoint(
                    new Pose(color == Alliance.Blue ? 0 : 144, 144)))
            .build());
    }
    public Command PathShootEx() {
        return robot.getDrive().moveTo(paths.Path1Ex);
    }

    public Command Pick1() {
        PathChain path = robot.getDrive().getFollower().pathBuilder()
                .addPath(new BezierLine(robot.getDrive().getPose(),
                        paths.Path7))
                .setConstantHeadingInterpolation(paths.Path7.getHeading())
                .build();
        return new ParallelCommandGroup(
                robot.getIntake().start(),
                robot.getDrive().pathCommand(path)
        );
    }

    public Command Pick2() {
        PathChain path = robot.getDrive().getFollower().pathBuilder()
                .addPath(new BezierCurve(robot.getDrive().getPose(),paths.Path2,paths.Path3))
                .setLinearHeadingInterpolation(robot.getDrive().getZ(),paths.Path3.getHeading())
                .build();
        return new ParallelCommandGroup(
                robot.getIntake().start(),
                robot.getDrive().pathCommand(path)
            );
    }

    public Command Pick3() {
        PathChain path = robot.getDrive().getFollower().pathBuilder()
                .addPath(new BezierCurve(robot.getDrive().getPose(),paths.Path9,paths.Path10))
                .setLinearHeadingInterpolation(robot.getDrive().getZ(), paths.Path10.getHeading())
                .build();
        return new ParallelCommandGroup(
            robot.getIntake().start(),
            robot.getDrive().pathCommand(path)
        );
    }

    public Command finish() {
        return robot.getDrive().moveTo(
                new Pose(paths.Path1.getX(), 130, Math.toRadians(90))
        );
    }

    public enum Alliance {
        Red,
        Blue
    }
}
