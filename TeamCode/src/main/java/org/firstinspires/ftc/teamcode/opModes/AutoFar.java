package org.firstinspires.ftc.teamcode.opModes;

import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.ConditionalCommand;
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
import java.util.Set;
import java.util.function.Supplier;

@Autonomous(name = "AutoFar")
public class AutoFar extends OpMode {

    private Alliance color = Alliance.Blue;
    private int lines = 4;
    private boolean hitGate = false;
    Bot robot;
    GamepadEx controller; // plz DO NOT feed into Bot
    int delay;
    States state = States.motifDetect;
    States lastState = States.idle;
    States nextState;
    List<States> toDo = new ArrayList<>();
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
        telemetry.addData("shots", lines);
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
                new Pose(color == Alliance.Blue ? 45 : 99,
                        22,
                        Math.toRadians(90)));
        paths = new Paths(color == Alliance.Blue);
        nextState = hitGate ? States.pick2 : nextState;
        if (color == Alliance.Blue){
            robot.getLauncher().reverseSpin();
        }
        robot.getLauncher().flywheelAuto(false).schedule();
    }

    @Override
    public void loop() {
        run();
        CommandScheduler.getInstance().run();
        robot.getDrive().getFollower().update();
        telemetry.addData("order", robot.getCamera().getOrder());
        telemetry.addData("last state", lastState);
        telemetry.addData("wantsShoot", wantsShoot);
        telemetry.addData("target", Launcher.pidTarget);
        telemetry.addData("current", robot.getLauncher().spinner.getCurrentPosition());
        telemetry.addData("can shoot", robot.getLauncher().canShoot());
        telemetry.addData("is busy", robot.getDrive().getFollower().isBusy());
        telemetry.addData("loading finished", finished);
        telemetry.addData("cs 1", robot.getLauncher().getColor(robot.getLauncher().cs1));
        telemetry.addData("cs 2", robot.getLauncher().getColor(robot.getLauncher().cs2));
        telemetry.update();
    }

    public void stop() {
        CommandScheduler.getInstance().cancelAll();
        CommandScheduler.getInstance().reset();
        Storage.getInstance().setPose(robot.getDrive().getPose(),paths.Path1);
        Storage.getInstance().spindexerPos = Launcher.pidTarget;
        super.stop();
    }

    public void run() {
        switch (state) {
            case motifDetect:
                nextState = lines > 0 ? States.settingLaunch : States.finish;
                new SequentialCommandGroup(
                    robot.getLauncher().toShoot(),
                    robot.getCamera().getMotif(),
                    new WaitCommand(50),
                    new InstantCommand(()-> state = nextState))
                        .schedule();
                state = States.idle;
                break;
            case settingLaunch:
                nextState = lines > 1 ? States.pickFar : States.finish;
                new WaitCommand(delay)
                        .whenFinished(() -> {
                            robot.getLauncher().setOrder(robot.getCamera().getOrder());
                            robot.getLauncher().setChambers(new Launcher.Color[]
                                    {Launcher.Color.Green, Launcher.Color.Purple, Launcher.Color.Green}
                            );
                            state = nextState;
                        })
                        .schedule();
                state = States.idle;
                break;
            case pickFar:
                finished = false;
                lastState = States.pickFar;
                nextState = lines > 2 ? States.pick2 : States.finish;
                wantsShoot = false;
                green = 0;
                new ParallelCommandGroup(
                        new SequentialCommandGroup(
                            Pick3(),
                            new DeferredCommand(farPathShoot(), Collections.emptyList()),
                            new WaitUntilCommand(()->robot.getLauncher().canShoot()),
                            robot.getLauncher().fire(),
                            new InstantCommand(() -> state = nextState)
                        ),
                        new SequentialCommandGroup (
                            new WaitUntilCommand(
                                    ()->robot.getLauncher().getColor(robot.getLauncher().cs1)
                                            != Launcher.Color.Nothing || Bot.hasBeen(3000)),
                            robot.getLauncher().toFull(),
                            robot.getLauncher().setLaunch(green,robot.getCamera().getOrder())
                        )
                ).schedule();
                state = States.idle;
                break;
            case pick2:
                finished = false;
                lastState = States.pick2;
                nextState = lines > 3 ? States.pick1 : States.finish;
                wantsShoot = false;
                green = 1;
                new ParallelCommandGroup(
                    new SequentialCommandGroup(
                        Pick2(),
                        new DeferredCommand(farPathShoot(), Collections.emptyList()),
                        new WaitUntilCommand(()->robot.getLauncher().canShoot()),
                        robot.getLauncher().fire(),
                        new InstantCommand(() -> state = nextState)
                    ),
                    new SequentialCommandGroup (
                        new WaitUntilCommand(
                                ()->robot.getLauncher().getColor(robot.getLauncher().cs1)
                                        != Launcher.Color.Nothing || Bot.hasBeen(3000)),
                        robot.getLauncher().toFull(),
                        robot.getLauncher().setLaunch(green,robot.getCamera().getOrder())
                    )
                ).schedule();
                state = States.idle;
                break;
            case pick1:
                finished = false;
                lastState = States.pick1;
                nextState = States.finish;
                wantsShoot = false;
                green = 2;
                new ParallelCommandGroup(
                    new SequentialCommandGroup(
                        Pick1(),
                        new DeferredCommand(farPathShoot(), Collections.emptyList()),
                        new WaitUntilCommand(()->robot.getLauncher().canShoot()),
                        robot.getLauncher().fire(),
                        new InstantCommand(() -> state = nextState)
                    ),
                    new SequentialCommandGroup (
                        new WaitUntilCommand(
                                ()->robot.getLauncher().getColor(robot.getLauncher().cs1)
                                        != Launcher.Color.Nothing || Bot.hasBeen(3000)),
                        robot.getLauncher().toFull(),
                        robot.getLauncher().setLaunch(green,robot.getCamera().getOrder())
                    )
                ).schedule();
                state = States.idle;
                break;
            case pickCorner:
                finished = false;
                lastState = States.pickCorner;
                nextState = States.finish;
                wantsShoot = false;
                green = 1;
                new ParallelCommandGroup(
                        new SequentialCommandGroup(
                                PickCorner(),
                                new DeferredCommand(farPathShoot(), Collections.emptyList()),
                                new WaitUntilCommand(()->robot.getLauncher().canShoot()),
                                robot.getLauncher().fire(),
                                new InstantCommand(() -> state = nextState)
                        ),
                        new SequentialCommandGroup (
                                new WaitUntilCommand(
                                        ()->robot.getLauncher().getColor(robot.getLauncher().cs1)
                                                != Launcher.Color.Nothing || Bot.hasBeen(3000)),
                                robot.getLauncher().toFull(),
                                robot.getLauncher().setLaunch(green,robot.getCamera().getOrder())
                        )
                ).schedule();
                state = States.idle;
                break;
            case finish:
                lastState = States.finish;
                finish().schedule();
                robot.getLauncher().toZero().schedule();
                robot.getLauncher().flywheelOff().schedule();
                state = States.idle;
                break;
            default:
                break;
        }
    }

    public enum States {
        motifDetect,
        settingLaunch,
        idle,
        pick1,
        pick2,
        pickFar,
        pickCorner,
        finish
    }

    public Supplier<Command> farPathShoot() {
        return ()->robot.getDrive().moveTo(paths.Path11);
    }

    public Command Pick1() {
        PathChain path = robot.getDrive().getFollower().pathBuilder()
                .addPath(new BezierCurve(robot.getDrive().getPose(),paths.Path6,paths.Path7))
                .setLinearHeadingInterpolation(robot.getDrive().getZ(),paths.Path7.getHeading())
                .build();
        return new ParallelCommandGroup(
                robot.getIntake().start(),
                robot.getLauncher().toZero(),
                robot.getDrive().pathCommand(path)
        );
    }

    public Command Pick2() {
        Pose gateHitPos = new Pose(
                color == Alliance.Blue ? 1.5 : 141.5,
                74,
                Math.toRadians(270));
        PathChain path = robot.getDrive().getFollower().pathBuilder()
                .addPath(new BezierCurve(robot.getDrive().getPose(),paths.Path2,paths.Path3))
                .setLinearHeadingInterpolation(robot.getDrive().getZ(),paths.Path3.getHeading())
                .build();
        return new SequentialCommandGroup(
            new ParallelCommandGroup(
                robot.getIntake().start(),
                robot.getLauncher().toZero(),
                robot.getDrive().pathCommand(path),
                new WaitCommand(200),
                hitGate ? robot.getDrive().moveTo(paths.Path3,gateHitPos)
                        : new WaitCommand(20)
            )
        );
    }

    public Command Pick3() {
        PathChain path = robot.getDrive().getFollower().pathBuilder()
                .addPath(new BezierCurve(robot.getDrive().getPose(),paths.Path9,paths.Path10))
                .setLinearHeadingInterpolation(robot.getDrive().getZ(), paths.Path10.getHeading())
                .build();
        return new ParallelCommandGroup(
            robot.getIntake().start(),
            robot.getLauncher().toZero(),
            robot.getDrive().pathCommand(path)
        );
    }
    public Command PickCorner() {
        PathChain path = robot.getDrive().getFollower().pathBuilder()
                .addPath(new BezierCurve(robot.getDrive().getPose(),paths.Path12,paths.Path13))
                .setTangentHeadingInterpolation()
                .build();
        return new SequentialCommandGroup(
            robot.getIntake().start(),
            robot.getLauncher().toZero(),
            robot.getDrive().pathCommand(path)
        );
    }

    public Command finish() {
        return robot.getDrive().moveTo(
                new Pose(paths.Path11.getX() + 40 * (color == Alliance.Blue ? -1 : 1),
                        32, Math.toRadians(90)));
    }


    public enum Alliance {
        Red,
        Blue
    }
}
