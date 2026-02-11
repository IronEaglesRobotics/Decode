package org.firstinspires.ftc.teamcode.opModes;

import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
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
        telemetry.addData("lines", lines - 1);
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
        super.stop();
    }

    public void run() {
        switch (state) {
            case motifDetect:
                new SequentialCommandGroup(
                    robot.getLauncher().toShoot(),
//                    new ConditionalCommand(
//                            new WaitCommand(20),
//                            paths.PathShootEx(),
//                            ()->isFar),
                    paths.PathShootEx(),
                    isFar ? new WaitCommand(20) : new WaitCommand(1000),
                    robot.getCamera().getMotif(),
                    new WaitCommand(400),
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
                        robot.getLauncher().setLaunch(green,robot.getCamera().getOrder()),
                        new InstantCommand(()->wantsShoot = true),
                        new WaitUntilCommand(() -> !robot.getDrive().getFollower().isBusy()),
//                        (isFar ? robot.aim():new WaitCommand(20)),
                        //new WaitUntilCommand(() -> robot.getLauncher().canShoot()),
                        robot.getLauncher().fire(),
                        new WaitCommand(100),
                        new InstantCommand(() -> state = States.swap)
                )
                        .schedule();
                state = States.idle;
                break;
            case pickFar:
                finished = false;
                lastState = States.pickFar;
                nextState = lines == 4 || !hitGate? States.pick2 : States.finish;
                wantsShoot = false;
                green = 0;
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
                finished = false;
                lastState = States.pickFar;
                nextState = States.finish;
                wantsShoot = false;
                green = 1;
                robot.loading()
                        .raceWith(new WaitUntilCommand(() -> wantsShoot))
                        .andThen(robot.getIntake().stop())
                        .whenFinished(() -> finished = true)
                        .schedule();
                new SequentialCommandGroup(
                        paths.PickCorner()
                                .whenFinished(() -> {
                                    state = States.shoot;
                                })
                )
                        .schedule();
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
                green = 2;
                robot.loading()
                        .raceWith(new WaitUntilCommand(() -> wantsShoot))
                        .andThen(robot.getIntake().stop())
                        .whenFinished(() -> finished = true)
                        .schedule();
                new SequentialCommandGroup(
                        paths.Pick1()
                                .andThen(new ConditionalCommand(
                                        robot.getDrive().moveTo(
                                                new Pose(color == Alliance.Blue ? 2 : 140,
                                                        88,
                                                        Math.toRadians(90)))
                                        .andThen(new WaitCommand(1000)),
                                                new WaitCommand(20),
                                        ()->hitGate))
                                .whenFinished(() -> {
                                    state = States.shoot;
                                })
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
                green = 1;
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
        public Pose Path12;
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
            double farAim = isBlue ? 115 : 72;
            double cornerZ = !isBlue ? 245 : 335;
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
            Path12 = new Pose(cornerPickX, 40, Math.toRadians(cornerZ));
            Path13 = new Pose(cornerPickX, 20, Math.toRadians(cornerZ));
        }

        public Command PathShoot() {
            return robot.getDrive().moveTo(Path1);
        }

        public Command PathShootEx() {
            return robot.getDrive().moveTo(Path1Ex);
        }

        public Command farPathShoot() {
            return robot.getDrive().moveTo(Path11);
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
            Pose gateHitPos = new Pose(
                    color == Alliance.Blue ? 1.5 : 141.5,
                    74,
                    Math.toRadians(270));
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
                    robot.getDrive().moveTo(Path12),
                    new SequentialCommandGroup(
                            robot.getIntake().start()
                                    .alongWith(robot.getLauncher().toZero()),
                            robot.getDrive().moveTo(Path12, Path13,
                                    Math.toRadians(270)),
                            new WaitCommand(200)
                    )
            );
        }

        public Command finish() {
            return robot.getDrive().moveTo(!isFar ?
                    new Pose(paths.Path1.getX(), 130, Math.toRadians(90)) :
                    new Pose(paths.Path11.getX(), 40, Math.toRadians(90)));
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
