package org.firstinspires.ftc.teamcode.opModes;

import static org.firstinspires.ftc.teamcode.hardware.Launcher.Motif.*;

import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.ConditionalCommand;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.command.WaitUntilCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.hardware.Bot;
import org.firstinspires.ftc.teamcode.hardware.Launcher;
import org.firstinspires.ftc.teamcode.hardware.Storage;

import java.util.ArrayList;
import java.util.List;

@Autonomous(name = "Auto")
public class Auto extends OpMode {

    private Alliance color = Alliance.Blue;
    private boolean isFar = false;
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

    @Override
    public void init() {
        robot = new Bot().init(hardwareMap, null);
        controller = new GamepadEx(gamepad1);
        robot.getLauncher().resetEncoder();
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
        if (controller.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
            color = Alliance.Red;
        }
        if (controller.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
            color = Alliance.Blue;
        }
        if (controller.wasJustPressed(GamepadKeys.Button.RIGHT_STICK_BUTTON)) {
            isFar = true;
        }
        if (controller.wasJustPressed(GamepadKeys.Button.LEFT_STICK_BUTTON)) {
            isFar = false;
        }
        if (controller.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
            delay += 100;
        }
        if (controller.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
            delay -= 100;
        }
        if (controller.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)){
            hitGate = true;
        }
        if (controller.wasJustPressed(GamepadKeys.Button.DPAD_LEFT)){
            hitGate = false;
        }
        telemetry.addData("lines", lines);
        telemetry.addData("if far", isFar);
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
                new Pose(!isFar ? color == Alliance.Blue ? 14 : 129 : color == Alliance.Blue ? 45 : 99,
                        isFar ? 22 : 135,
                        isFar ? Math.toRadians(90) : Math.toRadians(color == Alliance.Blue ? 45 : 135)));
        paths = new Paths(color == Alliance.Blue);
        nextState = !isFar ? States.pick1 : lines == 2 ? States.finish : States.pickFar;
        nextState = isFar && hitGate && lines < 4 ? States.pickCorner : nextState;
        state = lines == 1 ? States.finish : state;
        if (color == Alliance.Blue && isFar){
            robot.getLauncher().reverseSpin();
        }
        robot.getLauncher().flywheelAuto(!isFar).schedule();
    }

    @Override
    public void loop() {
        run();
        CommandScheduler.getInstance().run();
        robot.getDrive().getFollower().update();
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
        Storage.getInstance().resetSpindexer = false;
        super.stop();
    }

    public void run() {
        switch (state) {
            case motifDetect:
                new SequentialCommandGroup(
                    new ParallelCommandGroup(
                            robot.getLauncher().toShoot(),
                            paths.PathShootEx()),
                    isFar ? new WaitCommand(20) : new WaitCommand(1000),
                    //robot.getCamera().getMotif(),
                    new WaitCommand(200),
                    new InstantCommand(()-> state = States.settingLaunch)
                ).schedule();
                state = States.idle;
                break;
            case settingLaunch:
                new WaitCommand(delay)
                        .whenFinished(() -> {
                            robot.getLauncher().setOrder(robot.getCamera().getOrder());
                            robot.getLauncher().setChambers(new Launcher.Color[]
                                    {Launcher.Color.Green, Launcher.Color.Purple, Launcher.Color.Green}
                            );
                            state = States.shoot;
                        })
                        .schedule();
                state = States.idle;
                break;
            case swap:
                state = nextState;
            case idle:
                break;
            case shoot:
                lastState = States.shoot;
                new SequentialCommandGroup(
                        (!isFar ? paths.PathShoot() : paths.farPathShoot()),
                        new InstantCommand(()->wantsShoot = true),
                        robot.getLauncher().setLaunch(Launcher.Picked.First,robot.getCamera().getOrder()),
                        new WaitUntilCommand(() -> !robot.getDrive().getFollower().isBusy()),
//                        (isFar ? robot.aim():new WaitCommand(20)),
                        robot.getLauncher().fire(),
                        new WaitCommand(400),
                        new InstantCommand(() -> state = States.swap)
                )
                        .schedule();
                state = States.idle;
                break;
            case pickFar:
                finished = false;
                lastState = States.pickFar;
                nextState = lines < 4 ? States.finish : States.pick2;
                nextState = hitGate ? States.pickCorner : nextState;
                wantsShoot = false;
                robot.loading()
                        .raceWith(new WaitUntilCommand(() -> wantsShoot))
                        .andThen(robot.getIntake().stop())
                        .whenFinished(() -> finished = true)
                        .schedule();
                new SequentialCommandGroup(
                        paths.Pick3Start()
                                .whenFinished(() -> {
                                    state = States.shoot;
                                })
                )
                        .schedule();
                state = States.idle;
                break;
            case pickCorner:
                lastState = States.pickCorner;
                nextState = States.finish;
                PathChain path2 = robot.getDrive().getFollower().pathBuilder()
                    .addPath(new BezierLine(robot.getDrive().getPose(),
                        new Pose(
                        color == Alliance.Blue ? 0 : 144,
                        31)))
                    .setLinearHeadingInterpolation(robot.getDrive().getZ(),
                            Math.toRadians(color == Alliance.Blue ? 135 : 45))
                    .build();
                PathChain path3 = robot.getDrive().getFollower().pathBuilder()
                        .addPath(new BezierLine(new Pose(
                                        color == Alliance.Blue ? 0 : 144,
                                        31),
                                new Pose(color == Alliance.Blue ? 0 : 144,
                                        33)))
                        .setConstantHeadingInterpolation(Math.toRadians(color == Alliance.Blue ? 135 : 45))
                        .build();
                PathChain path4 = robot.getDrive().getFollower().pathBuilder()
                        .addPath(new BezierLine(new Pose(color == Alliance.Blue ? 0 : 144,
                                33),
                                new Pose(
                                color == Alliance.Blue ? 0 : 144,
                                31)
                                ))
                        .setConstantHeadingInterpolation(Math.toRadians(color == Alliance.Blue ? 135 : 45))
                        .build();
                robot.getDrive().pathCommand(path2)
                        .andThen(new WaitCommand(lines == 4 ? 2500 : 5000))
                        .andThen(robot.getDrive().pathCommand(path3))
                        .andThen(robot.getDrive().pathCommand(path4))
                        .whenFinished(()->state = States.shoot)
                        .schedule();
                new SequentialCommandGroup (
                        robot.getLauncher().toZero(),
                        robot.getIntake().start(),
                        new WaitCommand(6500),
                        //robot.getLauncher().toFull(),
                        robot.getIntake().reverse(),
                        robot.getLauncher().setLaunch(robot.getCamera().getOrder()),
                        robot.getIntake().stop()
                ).schedule();
                state = States.idle;
                break;
            case finish:
                lastState = States.finish;
                paths.finish().schedule();
                robot.getLauncher().toZero().schedule();
                robot.getLauncher().flywheelOff().schedule();
                state = States.idle;
                break;
            case pick1:
                finished = false;
                lastState = States.pick1;
                nextState = States.pick2;
                wantsShoot = false;
                PathChain path = robot.getDrive().getFollower().pathBuilder()
                        .addPath(new BezierCurve(robot.getDrive().getPose(),
                                new Pose(color == Alliance.Blue ? 8 : 135,
                                        92 + (color == Alliance.Blue ? 0:4),
                                        Math.toRadians(90)),
                                new Pose(color == Alliance.Blue ? 2 : 140,
                                        88 + (color == Alliance.Blue ? 0:-2),
                                        Math.toRadians(90))))
                        .setLinearHeadingInterpolation(robot.getDrive().getZ(), Math.toRadians(90))
                        .build();
                robot.loading()
                        .raceWith(new WaitUntilCommand(() -> wantsShoot))
                        .andThen(robot.getIntake().stop())
                        .whenFinished(() -> finished = true)
                        .schedule();
                new SequentialCommandGroup(
                        paths.Pick1()
                                .andThen(new ConditionalCommand(
                                    robot.getDrive().pathCommand(path)
                                        .andThen(new WaitCommand(500)),
                                                new WaitCommand(20),
                                        ()->hitGate))
                                .whenFinished(() -> state = States.shoot)
                )
                        .schedule();
                state = States.idle;
                break;
            case pick2:
                finished = false;
                lastState = States.pick2;
                nextState = States.finish;
                nextState = isFar ? (hitGate ? States.pickFar : States.finish) : nextState;
                wantsShoot = false;
                robot.loading()
                        .raceWith(new WaitUntilCommand(() -> wantsShoot))
                        .andThen(robot.getIntake().stop())
                        .whenFinished(() -> finished = true)
                        .schedule();
                new SequentialCommandGroup(
                        paths.Pick2(),
                        new InstantCommand(() -> {
                            state = States.shoot;
                        })
                )
                        .schedule();
                state = States.idle;
                break;
        }
    }

    public enum States {
        motifDetect,
        settingLaunch,
        idle,
        swap,
        shoot,
        pick1,
        pick2,
        pickFar,
        pickCorner,
        cornerWait,
        finish
    }

    public class Paths {
        public Pose Path1;
        public Pose Path1Ex;
        public Pose Path2;
        public Pose Path3;
        public Pose Path3Ex;
        public Pose Path6;
        public Pose Path7;
        public Pose Path9;
        public Pose Path10;
        public Pose Path11;
        public Pose Path13;

        public Paths(boolean isBlue) {
            double shootX = isBlue ? 32 : 106;
            double prePickX = isBlue ? 35 : 109;
            double postPickX1 = isBlue ? 0 : 142;
            double postPickEx = isBlue ? 25 : 117;
            double postPickX2 = isBlue ? 5 : 137;
            double farShootX = isBlue ? 42 : 99;
            double cornerPickX = isBlue ? -3 : 137;
            double closeAim = isBlue ? 131 : 52;
            double seeObelisk = isBlue ? 70 : 110;
            double pickUp = isBlue ? 180 : 0;
            double farAim = isBlue ? 113 : 70;
            double cornerZ = isBlue ? 180 : 0;
            Path1 = new Pose(shootX, 105.500, Math.toRadians(closeAim));
            Path1Ex = !isFar ? new Pose(shootX, 105.500, Math.toRadians(seeObelisk)) :
                    new Pose(farShootX, 50, Math.toRadians(90));
            Path2 = new Pose(prePickX, 70.000, Math.toRadians(pickUp));

            Path3 = new Pose(postPickX1, 70.000, Math.toRadians(pickUp));
            Path3Ex = new Pose(postPickEx, 70.000, Math.toRadians(pickUp));

            Path6 = new Pose(prePickX, 93.000, Math.toRadians(pickUp));

            Path7 = new Pose(postPickX2, 93.000, Math.toRadians(pickUp));

            Path9 = new Pose(prePickX, 49.000, Math.toRadians(pickUp));

            Path10 = new Pose(postPickX1, 49.000, Math.toRadians(pickUp));

            Path11 = new Pose(farShootX, 32, Math.toRadians(farAim));
            Path13 = new Pose(cornerPickX, 16, Math.toRadians(cornerZ));
        }

        public Command PathShoot() {
            return robot.getDrive().moveTo(Path1);
        }

        public Command PathShootEx() {
            return robot.getDrive().moveTo(Path1Ex);
        }

        public Command farPathShoot() {
            PathConstraints test = PathConstraints.defaultConstraints;
            test.setTValueConstraint(.999);
            test.setVelocityConstraint(.001);
            test.setBrakingStrength(1.5);
            test.setBrakingStart(1);
            PathChain pathChain = robot.getDrive().getFollower().pathBuilder(test)
                    .addPath(new BezierLine(robot.getDrive().getPose(),Path11))
                    .setLinearHeadingInterpolation(robot.getDrive().getZ(), Path11.getHeading())
                    .build();
            return robot.getDrive().pathCommand(pathChain);
        }

        public Command Pick1() {
            return new SequentialCommandGroup(
                    robot.getDrive().moveTo(Path6),
                    new SequentialCommandGroup(
                            robot.getIntake().start()
                                    .alongWith(robot.getLauncher().toZero()),
                            robot.getDrive().moveTo(Path6, Path7,
                                    flipAng(180, color == Alliance.Blue),
                                    .5),
                            new WaitCommand(200)
                    )
            );
        }

        public Command Pick2() {
            return new SequentialCommandGroup(
                    robot.getDrive().moveTo(Path2),
                    new SequentialCommandGroup(
                            robot.getIntake().start()
                                    .alongWith(robot.getLauncher().toZero()),
                            robot.getDrive().moveTo(Path2, Path3,
                                    flipAng(180, color == Alliance.Blue)
                                    , .5),
                            new WaitCommand(200),
                            robot.getDrive().moveTo(Path3, Path3Ex)
                    )
            );
        }

        public Command Pick3Start() {
            return new SequentialCommandGroup(
                    robot.getDrive().moveTo(Path9),
                    new SequentialCommandGroup(
                            robot.getIntake().start()
                                    .alongWith(robot.getLauncher().toZero()),
                            robot.getDrive().moveTo(Path9, Path10,
                                    flipAng(180, color == Alliance.Blue)
                                    , .5),
                            new WaitCommand(200)
                    )
            );
        }
        public Command PickCorner() {
            return new SequentialCommandGroup(
                robot.getIntake().start()
                        .alongWith(robot.getLauncher().toZero()),
                robot.getDrive().moveTo(Path13)
                        .withTimeout(10000),
                new WaitCommand(200)
            );
        }

        public Command finish() {
            return robot.getDrive().moveTo(!isFar ?
                    new Pose(paths.Path1.getX(), 130, Math.toRadians(90)) :
                    new Pose(paths.Path11.getX() + (color == Alliance.Blue ? -30 : 30),
                            paths.Path11.getY() - 10,
                            Math.toRadians(90)));
        }

        public double flipAng(double degrees, boolean ifFlip) {
            return Math.toRadians(90 + ((degrees - 90) * (ifFlip ? 1 : -1)));
        }
    }

    public enum Alliance {
        Red,
        Blue
    }
}
