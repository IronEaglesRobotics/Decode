package org.firstinspires.ftc.teamcode.opModes;

import static org.firstinspires.ftc.teamcode.opModes.Auto.States.pickCorner;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
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
import org.firstinspires.ftc.teamcode.hardware.Storage;

@Autonomous(name = "SimpleAuto")
public class SimpleAuto extends OpMode {

    private Alliance color = Alliance.Blue;
    private boolean isFar = false;
    private int lines = 4;
    Bot robot;
    GamepadEx controller; // plz DO NOT feed into Bot
    int delay;
    States state = States.motifDetect;
    States lastState = States.idle;
    Auto.States nextState;
    boolean wantsShoot = false;
    public Paths paths;
    int shot = 0;
    boolean finished;

//    public FollowPathCommand closeshoot(){
//        return robot.getDrive().pathCommand(
//                !isFar ? paths.shootPaths[shot++] : paths.farShootPaths[shot++]);
//    }

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
        telemetry.addData("lines", lines - 1);
        telemetry.addData("if far", isFar);
        telemetry.addData("color", color);
        telemetry.addData("delay", ((double) delay) / 1000);
        telemetry.addLine("Lines: A:4 B:3 X:2 Y:1");
        telemetry.addLine("Color: Left Bumper: red Right Bumper: blue");
        telemetry.addLine("Start: Right Stick: far Left Stick: close");
        telemetry.addLine("Dpad: delay ^v");
        telemetry.update();
    }

    @Override
    public void start() {
        robot.getDrive().getFollower().setStartingPose(
                new Pose(!isFar ? color == Alliance.Blue ? 14 : 129 : color == Alliance.Blue ? 45 : 88,
                        isFar ? 23 : 135,
                        isFar ? Math.toRadians(90) : Math.toRadians(color == Alliance.Blue ? 135 : 45)));
        paths = new Paths(color == Alliance.Blue);
        nextState = !isFar ? States.pick1 : States.pickFar;
        robot.getLauncher().flywheelAuto(!isFar).schedule();
//        robot.getCamera().getMotif()
//                .andThen(robot.getLauncher().setlaunch(0,robot.getCamera().getOrder())
//                        .raceWith(new WaitUntilCommand(()->wantsShoot)))
//                .schedule();
    }

    @Override
    public void loop() {
        run();
        CommandScheduler.getInstance().run();
        robot.getDrive().getFollower().update();
        telemetry.addData("order", robot.getCamera().getOrder());
        telemetry.addData("last state", lastState);
        telemetry.addData("wantsShoot", wantsShoot);
        telemetry.addData("target", robot.getLauncher().pidTarget);
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
        Storage.getInstance().setPose(robot.getDrive().getPose());
        super.stop();
    }

    public void run() {
        switch (state) {
            case pickCorner:
                finished = false;
                lastState = Auto.States.pickFar;
                nextState = Auto.States.finish;
                wantsShoot = false;
                robot.loading()
                        .raceWith(new WaitUntilCommand(() -> wantsShoot))
                        .andThen(robot.getIntake().stop())
                        .whenFinished(() -> finished = true)
                        .schedule();
                new SequentialCommandGroup(
                        paths.PickCorner()
                                .whenFinished(() -> {
                                    state = Auto.States.shoot;
                                })
                )
                        .schedule();
                state = Auto.States.idle;
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
        public Follower follower;
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
            double postPickEx = isBlue ? 10 : 132;
            double postPickX2 = isBlue ? 5 : 137;
            double farShootX = isBlue ? 45 : 94;
            double closeAim = isBlue ? 130 : 50;
            double seeObelisk = isBlue ? 70 : 110;
            double pickUp = isBlue ? 180 : 0;
            double farAim = isBlue ? 108 : 80;
            double cornerZ = isBlue ? 245 : 335;
            Path1 = new Pose(shootX, 105.500, Math.toRadians(closeAim));
            Path1Ex = new Pose(shootX, 105.500, Math.toRadians(seeObelisk));
            Path2 = new Pose(prePickX, 71.000, Math.toRadians(pickUp));

            Path3 = new Pose(postPickX1, 71.000, Math.toRadians(pickUp));
            Path3Ex = new Pose(postPickEx, 71.000, Math.toRadians(pickUp));

//            Path4 = follower
//                    .pathBuilder()
//                    .addPath(
//                            new BezierLine(new Pose(flip(19.000,isBlue), 69.000), new Pose(flip(5.000,isBlue), 75.000))
//                    )
//                    .setLinearHeadingInterpolation(flipAng(180,isBlue), flipAng(270,isBlue))
//                    .build();

//            Path5 = follower
//                    .pathBuilder()
//                    .addPath(
//                            new BezierCurve(
//                                    new Pose(postPickX1, 71.000),
//                                    new Pose(isBlue ? 43 : 101, 52.500),
//                                    new Pose(shootX, 95.500)
//                            )
//                    )
//                    .setLinearHeadingInterpolation(flipAng(180,isBlue), flipAng(127,isBlue))
//                    .build();

            Path6 = new Pose(prePickX, 92.000, Math.toRadians(pickUp));

            Path7 = new Pose(postPickX2, 92.000, Math.toRadians(pickUp));

            Path9 = new Pose(prePickX, 50.000, Math.toRadians(pickUp));

            Path10 = new Pose(postPickX1, 50.000, Math.toRadians(pickUp));

            Path11 = new Pose(farShootX, 27, Math.toRadians(farAim));
            Path12 = new Pose(cornerPickX, 40, Math.toRadians(cornerZ));
            Path13 = new Pose(cornerPickX, 20, Math.toRadians(cornerZ));
//            Path13 = follower
//                    .pathBuilder()
//                    .addPath(
//                            new BezierLine(new Pose(postPickX2, 35.500), new Pose(shootX, 95.000))
//                    )
//                    .setLinearHeadingInterpolation(flipAng(180,isBlue), flipAng(135,isBlue))
//                    .build();
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

        public Command finish() {
            return robot.getDrive().moveTo(!isFar ?
                    new Pose(paths.Path1.getX(), 130, Math.toRadians(90)) :
                    new Pose(paths.Path11.getX(), 30, Math.toRadians(90)));
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
