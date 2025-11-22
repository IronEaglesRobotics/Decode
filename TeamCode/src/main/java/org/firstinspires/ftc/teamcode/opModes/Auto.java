package org.firstinspires.ftc.teamcode.opModes;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.command.WaitUntilCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import org.firstinspires.ftc.teamcode.hardware.Bot;

@Autonomous(name =  "Auto")
public class Auto extends OpMode{

    private String color = "blue";
    private boolean isFar = false;
    private int lines = 4;
    Bot robot;
    GamepadEx controller; // plz DO NOT feed into Bot
    int delay;
    int state = -3;
    int lastState = -3;
    int green = 0;
    boolean wantsShoot = false;
    public Paths paths;
    Command togate;
    Command pick3;
    Command pick2;
    Command pick1;
    int shot = 0;
    private void makeAuto(Paths paths) {
        togate = robot.getDrive().pathCommand(paths.Path4);
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
                        new WaitCommand(400)
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
    }

    public FollowPathCommand closeshoot(){
        return robot.getDrive().pathCommand(paths.shootPaths[shot++]);
    }

    @Override
    public void init() {
        robot = new Bot().init(hardwareMap,null,telemetry);
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
        robot.getDrive().getFollower().setStartingPose(new Pose(color.equalsIgnoreCase("blue")? 14:118 , 135,Math.toRadians(135)));
        paths = new Paths(robot.getDrive().getFollower(),color.equalsIgnoreCase("blue"));
        makeAuto(paths);
        robot.getLauncher().flywheelOn(!isFar).schedule();
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
        telemetry.addData("pose",robot.getDrive().getPose());
        telemetry.addData("order", robot.getCamera().getOrder());
        telemetry.addData("state", state);
        telemetry.addData("last state", lastState);
        telemetry.addData("f1 velocity", robot.getLauncher().flyWheel1.getVelocity());
        telemetry.addData("f2 velocity" , robot.getLauncher().flyWheel2.getVelocity());
        telemetry.addData("wantsShoot",wantsShoot);
        telemetry.addData("target",robot.getLauncher().current);
        telemetry.addData("current",robot.getLauncher().spinner.getCurrentPosition());
        telemetry.addData("can shoot",robot.getLauncher().canShoot());
        telemetry.addData("is busy",robot.getDrive().getFollower().isBusy());
        telemetry.addData("chambers", robot.getLauncher().getTelemetry());
        telemetry.update();
    }

    public void stop(){
        CommandScheduler.getInstance().cancelAll();
        CommandScheduler.getInstance().reset();
        super.stop();
    }

    public void run(){
        switch (state){
            case -3:
                robot.getCamera().getMotif()
                        .raceWith(robot.getDrive().cancelablePath(paths.Path1Ex)
                                .whenFinished(()->robot.getCamera().setOrder(1)))
                        .andThen(robot.getLauncher().setlaunch(0,robot.getCamera().getOrder())
                        .whenFinished(()->state = 0))
                        .schedule();
                state = -1;
                break;
            case -2:
                state = lines;
                lines-=1;
            case -1:
                break;
            case 0:
                lastState = 0;
                new SequentialCommandGroup(
                        closeshoot()
                                .whenFinished(()-> wantsShoot = true),
                        new WaitUntilCommand(()->!robot.getDrive().getFollower().isBusy()),
//                        new WaitCommand(700),
                        new WaitUntilCommand(() -> robot.getLauncher().canShoot()),
                        robot.getLauncher().fire(),
                        new WaitCommand(250),
                        new InstantCommand(()->state=-2)
                )
                        .schedule();
                state = -1;
                break;
            case 1:
                lastState = 1;
                state = -1;
                break;
            case 2:
                lastState = 2;
                robot.getLauncher().toZero().schedule();
                robot.getLauncher().flywheelOff().schedule();
                state = -1;
                break;
            case 3:
                lastState = 3;
                wantsShoot = false;
                robot.loading()
                        .raceWith(new WaitUntilCommand(()->wantsShoot))
                        .andThen(robot.getIntake().stop())
                        .schedule();
                new SequentialCommandGroup(
                        pick3
                                .whenFinished(()-> {
                                    green = 2;
                                    state = 0;
                                })
                )
                        .schedule();
                state = -1;
                break;
            case 4:
                lastState = 4;
                wantsShoot = false;
                robot.loading()
                        .raceWith(new WaitUntilCommand(()->wantsShoot))
                        .andThen(robot.getIntake().stop())
                        .schedule();
                new SequentialCommandGroup(
                        pick2,
                        new InstantCommand(()-> {
                            green = 2;
                            state = 0;
                        })
                )
                        .schedule();
                state = -1;
                break;
        }
    }
    public static class Paths {
        public PathChain Path1;
        public PathChain Path1Ex;
        public PathChain Path2;
        public PathChain Path3;
        public PathChain Path4;
        public PathChain Path5;
        public PathChain Path6;
        public PathChain Path7;
        public PathChain Path8;
        public PathChain Path9;
        public PathChain Path10;
        public PathChain Path11;
        public PathChain[] shootPaths;

        public Paths(Follower follower, boolean isBlue) {
            double startX = isBlue ? 15:118;
            double shootX = isBlue ? 40:88;
            double prePickX = isBlue ? 35:100;
            double postPickX1 = isBlue ? 0:130;
            double postPickX2 = isBlue ? 5:127;
            Path1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(startX, 135.000), new Pose(shootX, 95.500))
                    )
                    .setLinearHeadingInterpolation(flipAng(135,isBlue),flipAng(130,isBlue))
                    .build();
            Path1Ex = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(startX, 135.000), new Pose(shootX, 95.500))
                    )
                    .setLinearHeadingInterpolation(flipAng(135,isBlue),flipAng(90,isBlue))
                    .build();

            Path2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(shootX, 95.500), new Pose(prePickX, 71.000))
                    )
                    .setLinearHeadingInterpolation(flipAng(130,isBlue), flipAng(180,isBlue))
                    .build();

            Path3 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(prePickX, 71.000), new Pose(postPickX1, 71.000))
                    )
                    .setConstantHeadingInterpolation(flipAng(180,isBlue))
                    .build();

//            Path4 = follower
//                    .pathBuilder()
//                    .addPath(
//                            new BezierLine(new Pose(flip(19.000,isBlue), 69.000), new Pose(flip(5.000,isBlue), 75.000))
//                    )
//                    .setLinearHeadingInterpolation(flipAng(180,isBlue), flipAng(270,isBlue))
//                    .build();

            Path5 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierCurve(
                                    new Pose(postPickX1, 71.000),
                                    new Pose(isBlue ? 43 : 101, 52.500),
                                    new Pose(shootX, 95.500)
                            )
                    )
                    .setLinearHeadingInterpolation(flipAng(180,isBlue), flipAng(135,isBlue))
                    .build();

            Path6 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(shootX, 95.500), new Pose(prePickX, 92.000))
                    )
                    .setLinearHeadingInterpolation(flipAng(135,isBlue), flipAng(180,isBlue))
                    .build();

            Path7 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(prePickX, 92.000), new Pose(postPickX2, 92.000))
                    )
                    .setConstantHeadingInterpolation(flipAng(180,isBlue))
                    .build();

            Path8 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(postPickX2, 92.000), new Pose(shootX, 95.500))
                    )
                    .setLinearHeadingInterpolation(flipAng(180,isBlue), flipAng(135,isBlue))
                    .build();

            Path9 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(shootX, 95.000), new Pose(prePickX, 35.500))
                    )
                    .setLinearHeadingInterpolation(flipAng(135,isBlue),flipAng(180,isBlue))
                    .build();

            Path10 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(prePickX, 35.500), new Pose(postPickX2, 35.500))
                    )
                    .setConstantHeadingInterpolation(flipAng(180,isBlue))
                    .build();

            Path11 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(postPickX2, 35.500), new Pose(shootX, 95.000))
                    )
                    .setLinearHeadingInterpolation(flipAng(180,isBlue), flipAng(135,isBlue))
                    .build();
            shootPaths = new PathChain[]{Path1,Path5,Path8,Path11};
        }

        public double flipAng(double degrees, boolean ifFlip){
            return Math.toRadians(90 +((degrees - 90) * (ifFlip ? 1:-1)));
        }
    }
}
