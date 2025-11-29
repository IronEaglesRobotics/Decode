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
import com.seattlesolvers.solverslib.command.ConditionalCommand;
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

    private Alliance color = Alliance.Blue;
    private boolean isFar = false;
    private int lines = 4;
    Bot robot;
    GamepadEx controller; // plz DO NOT feed into Bot
    int delay;
    States state = States.motifDetect;
    States lastState = States.idle;
    States nextState;
    boolean wantsShoot = false;
    public Paths paths;
    Command togate;
    Command pick3;
    Command pick2;
    Command finish;
    Command pickFar;
    int shot = 0;
    boolean finished;
    private void makeAuto(Paths paths) {
        togate = robot.getDrive().pathCommand(paths.Path4);
        pick3 = new SequentialCommandGroup(
                robot.getDrive().pathCommand(paths.Path6),
                new SequentialCommandGroup(
                        robot.getIntake().start()
                                .alongWith(robot.getLauncher().toZero()),
                        robot.getDrive().pathCommand(paths.Path7,.5),
                        new WaitCommand(200)
                )
        );
        pick2 = new SequentialCommandGroup(
                robot.getDrive().pathCommand(paths.Path2),
                new SequentialCommandGroup(
                        robot.getIntake().start()
                                .alongWith(robot.getLauncher().toZero()),
                        robot.getDrive().pathCommand(paths.Path3,.5),
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
        finish = robot.getDrive().moveTo(!isFar ?
                new Pose(paths.Path1.endPoint().getX(),130,90) :
                new Pose(paths.Path12.endPoint().getX(), 30, 90));
        pickFar = new SequentialCommandGroup(
                robot.getDrive().pathCommand(paths.Path9),
                new SequentialCommandGroup(
                        robot.getIntake().start()
                                .alongWith(robot.getLauncher().toZero()),
                        robot.getDrive().pathCommand(paths.Path10,.5),
                        new WaitCommand(400)
                )
        );
    }

    public FollowPathCommand closeshoot(){
        return robot.getDrive().pathCommand(
                !isFar ? paths.shootPaths[shot++] : paths.farShootPaths[shot++]);
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
            color = Alliance.Red;
        }
        if(controller.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)){
            color = Alliance.Blue;
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
        robot.getDrive().getFollower().setStartingPose(
            new Pose( !isFar ? color == Alliance.Blue? 14:129 : color == Alliance.Blue ? 56:88,
            isFar ? 9:135,
            Paths.flipAng(isFar ? 90:135,color == Alliance.Blue)));
        paths = new Paths(robot.getDrive().getFollower(),color == Alliance.Blue);
        makeAuto(paths);
        nextState = isFar ? States.pick2 : States.pickFar;
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
        telemetry.addData("order", robot.getCamera().getOrder());
        telemetry.addData("last state", lastState);
        telemetry.addData("wantsShoot",wantsShoot);
        telemetry.addData("target",robot.getLauncher().current);
        telemetry.addData("current",robot.getLauncher().spinner.getCurrentPosition());
        telemetry.addData("can shoot",robot.getLauncher().canShoot());
        telemetry.addData("is busy",robot.getDrive().getFollower().isBusy());
        telemetry.addData("loading finished", finished);
        telemetry.update();
    }

    public void stop(){
        CommandScheduler.getInstance().cancelAll();
        CommandScheduler.getInstance().reset();
        super.stop();
    }

    public void run(){
        switch (state){
            case motifDetect:
                robot.getCamera().getMotif()
                        .raceWith(new ConditionalCommand(
                                    robot.getDrive().cancelablePath(paths.Path1Ex),
                                    new WaitCommand(1000),
                                    ()->!isFar)
                                .whenFinished(()->robot.getCamera().setOrder(1)))
                        .andThen(robot.getLauncher().setlaunch(0,robot.getCamera().getOrder()))
                        .andThen(new WaitCommand(delay))
                        .whenFinished(()->state = States.shoot)
                        .schedule();
                state = States.idle;
                break;
            case swap:
                state = nextState;
                lines-=1;
            case idle:
                break;
            case shoot:
                lastState = States.shoot;
                new SequentialCommandGroup(
                        closeshoot()
                                .whenFinished(()-> wantsShoot = true),
                        new WaitUntilCommand(()->!robot.getDrive().getFollower().isBusy()),
//                        new WaitCommand(700),
                        new ConditionalCommand(robot.getLauncher().toShoot(),
                                new WaitUntilCommand(() -> robot.getLauncher().atTarget()),
                                ()->!robot.getLauncher().shootPos()),
                        new WaitUntilCommand(()->robot.getLauncher().canShoot()),
                        robot.getLauncher().fire(),
                        new WaitCommand(250),
                        new InstantCommand(()->state=States.swap)
                )
                        .schedule();
                state = States.idle;
                break;
            case pickFar:
                finished = false;
                lastState = States.pickFar;
                nextState = States.finish;
                wantsShoot = false;
                robot.loading()
                        .raceWith(new WaitUntilCommand(()->wantsShoot))
                        .andThen(robot.getIntake().stop())
                        .whenFinished(()->finished = true)
                        .schedule();
                new SequentialCommandGroup(
                        pickFar
                        .whenFinished(()-> {
                            state = States.shoot;
                        })
                )
                        .schedule();
                state = States.idle;
                break;
            case finish:
                lastState = States.finish;
                finish.schedule();
                robot.getLauncher().toZero().schedule();
                robot.getLauncher().flywheelOff().schedule();
                state = States.idle;
                break;
            case pick1:
                finished = false;
                lastState = States.pick1;
                nextState = States.finish;
                wantsShoot = false;
                robot.loading()
                        .raceWith(new WaitUntilCommand(()->wantsShoot))
                        .andThen(robot.getIntake().stop())
                        .whenFinished(()->finished = true)
                        .schedule();
                new SequentialCommandGroup(
                        pick3
                                .whenFinished(()-> {
                                    state = States.shoot;
                                })
                )
                        .schedule();
                state = States.idle;
                break;
            case pick2:
                finished = false;
                lastState = States.pick2;
                nextState = States.pick1;
                wantsShoot = false;
                robot.loading()
                        .raceWith(new WaitUntilCommand(()->wantsShoot))
                        .andThen(robot.getIntake().stop())
                        .whenFinished(()->finished = true)
                        .schedule();
                new SequentialCommandGroup(
                        pick2,
                        new InstantCommand(()-> {
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
        idle,
        swap,
        shoot,
        pick1,
        pick2,
        pickFar,
        finish
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
        public PathChain Path12;
        public PathChain[] shootPaths;
        public PathChain[] farShootPaths;

        public Paths(Follower follower, boolean isBlue) {
            double startX = isBlue ? 15:125;
            double shootX = isBlue ? 32:102;
            double prePickX = isBlue ? 35:109;
            double postPickX1 = isBlue ? 0:142;
            double postPickX2 = isBlue ? 5:137;
            double farStartX = isBlue ? 56:88;
            double farShootX = isBlue ? 50:94;
            Path1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(startX, 135.000), new Pose(shootX, 95.500))
                    )
                    .setLinearHeadingInterpolation(flipAng(135,isBlue),flipAng(127,isBlue))
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
                    .setLinearHeadingInterpolation(flipAng(127,isBlue), flipAng(180,isBlue))
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
                    .setLinearHeadingInterpolation(flipAng(180,isBlue), flipAng(127,isBlue))
                    .build();

            Path6 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(shootX, 95.500), new Pose(prePickX, 92.000))
                    )
                    .setLinearHeadingInterpolation(flipAng(127,isBlue), flipAng(180,isBlue))
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
                    .setLinearHeadingInterpolation(flipAng(180,isBlue), flipAng(127,isBlue))
                    .build();

            Path9 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(farShootX, 95.000), new Pose(prePickX, 35.500))
                    )
                    .setLinearHeadingInterpolation(flipAng(127,isBlue),flipAng(180,isBlue))
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
                            new BezierLine(new Pose(postPickX2, 35.500), new Pose(farShootX, 95.000))
                    )
                    .setLinearHeadingInterpolation(flipAng(180,isBlue), flipAng(120,isBlue))
                    .build();
            Path12 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(farStartX, 9), new Pose(farShootX, 14))
                    )
                    .setLinearHeadingInterpolation(flipAng(90,isBlue), flipAng(120,isBlue))
                    .build();
//            Path13 = follower
//                    .pathBuilder()
//                    .addPath(
//                            new BezierLine(new Pose(postPickX2, 35.500), new Pose(shootX, 95.000))
//                    )
//                    .setLinearHeadingInterpolation(flipAng(180,isBlue), flipAng(135,isBlue))
//                    .build();
            shootPaths = new PathChain[]{Path1,Path5,Path8,Path11};
            farShootPaths = new PathChain[]{Path12,Path11};
        }

        public static double flipAng(double degrees, boolean ifFlip){
            return Math.toRadians(90 +((degrees - 90) * (ifFlip ? 1:-1)));
        }
    }
    public enum Alliance{
        Red,
        Blue
    }
}
