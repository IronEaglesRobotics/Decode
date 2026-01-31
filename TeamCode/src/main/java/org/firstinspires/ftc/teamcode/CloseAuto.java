package org.firstinspires.ftc.teamcode;


import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.HeadingInterpolator;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

//@Disabled
@Autonomous(name = "Close Auto")
public class CloseAuto extends OpMode {


    private RobotNew robot;
    GamepadEx controller1;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;
    public TelemetryManager telemetryM;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
    private double timer;
    private boolean foo = true;
    private double shooterTimer = 0.6;
    private double power = .3;
    private double hood = .7;
    private double distance = 50;
    private int runs = 0;

    private Config config;

    private Follower follower() {
        return robot.getFollower();
    }

    private Pose offsetPose(Pose pose, double x, double y, double heading) {
        return new Pose(pose.getX() + x, pose.getY() + y, pose.getHeading() + heading);
    }

    private Pose offsetPose(Pose pose, double x, double y) {
        return new Pose(pose.getX() + x, pose.getY() + y, pose.getHeading());
    }

    private Path scorePreloads, openGate;
    private PathChain getPickup1, park, launchBatch1, getPickup2, launchBatch2, getPickup3, launchBatch3, getPickup4, launchBatch4;

    public void buildPaths() {
        scorePreloads = new Path(new BezierLine(this.config.getStartPose(), this.config.getScorePose()));
        scorePreloads.setLinearHeadingInterpolation(this.config.getStartPose().getHeading(), this.config.getScorePose().getHeading(), .8);

        getPickup1 = robot.getFollower().pathBuilder()
                .addPath(new BezierCurve(this.config.getScorePose(), this.config.getPickup1Control(), this.config.getPickup1Pose()))
                .setConstantHeadingInterpolation(this.config.getPickup1Pose().getHeading())
                .build();

        launchBatch1 = robot.getFollower().pathBuilder()
                .addPath(new BezierLine(this.config.getPickup1Pose(), this.config.getScorePose()))
                .setTangentHeadingInterpolation()
                .build();

        getPickup2 = robot.getFollower().pathBuilder()
                .addPath(new BezierCurve(this.config.getScorePose(), this.config.getPickup2Control(), this.config.getPickup2Pose()))
                .setTangentHeadingInterpolation()
                .setReversed()
                .build();

        launchBatch2 = follower().pathBuilder()
                .addPath(new BezierCurve(this.config.getPickup2Pose(), this.config.getPickup3Control2(), this.config.getScorePose()))
                .setTangentHeadingInterpolation()
                .build();

        getPickup3 = robot.getFollower().pathBuilder()
                .addPath(new BezierCurve(this.config.getScorePose(), this.config.getPickup3Control(), this.config.getPickup3Pose()))
                .setHeadingInterpolation(
                        HeadingInterpolator.piecewise(
                                new HeadingInterpolator.PiecewiseNode(0, .2, HeadingInterpolator.tangent),
                                new HeadingInterpolator.PiecewiseNode(.2, 1, HeadingInterpolator.constant(this.config.getPickup3Control().getHeading()))
                        )
                )
                .setReversed()
                .build();


        launchBatch3 = follower().pathBuilder()
                .addPath(new BezierCurve(this.config.getPickup3Pose(), config.getPickup3Control2(), this.config.getScorePose()))
                .setHeadingInterpolation(
                        HeadingInterpolator.piecewise(
                                new HeadingInterpolator.PiecewiseNode(0, .4, HeadingInterpolator.tangent),
                                new HeadingInterpolator.PiecewiseNode(.4, 1, HeadingInterpolator.constant(this.config.getScorePose().getHeading()))
                        )
                ).build();

        getPickup4 = robot.getFollower().pathBuilder()
                .addPath(new BezierCurve(this.config.getScorePose(), this.config.getPickup4Control(), this.config.getPickup4Pose()))
                .setTangentHeadingInterpolation()
                .setReversed()
                .build();


        launchBatch4 = follower().pathBuilder()
                .addPath(new BezierLine(this.config.getPickup4Pose(), this.config.getScorePose()))
                .setTangentHeadingInterpolation()
                .build();

        park = follower().pathBuilder()
                .addPath(new BezierLine(this.config.getScorePose(), this.config.getParkPose()))
                .setTangentHeadingInterpolation()
                .setReversed()
                .build();
        //add more pathchains as see fit
    }


    public void runAuto() {
        switch (pathState) {
            case 0: // Go to Launch 1 location
                robot.getIntake().close();
                robot.getFollower().followPath(scorePreloads, true);
                setPathState(1);
                break;
            case 1: // if at location, launch
                if (!follower().isBusy()) {
                    robot.getIntake().open();
                    setPathState(5);
                    timer = getRuntime() + shooterTimer;
                }
                break;
            case 2: //is the robot done launching? if yes, go to pickup 1 and stop shooter
                if (getRuntime() > timer) {
                    robot.intake.close();
                    follower().followPath(getPickup1, true);
                    setPathState(3);
                }
                break;
            case 3://if Got all 3 of first batch || patch ended 1.5 secs passed, go to launch pose
                if (!follower().isBusy()) {
                    follower().followPath(launchBatch1, true);
                    setPathState(4);
                    timer = getRuntime() + .9;
                }
                break;
            case 4: //if at launch pos and shooter fast enough, then shoot
                if (!follower().isBusy() && robot.getShooter().atTargetVelocity() && timer < getRuntime()) {
                    robot.intake.open();
                    setPathState(11);
                    timer = getRuntime() + shooterTimer;
                }
                break;
            case 5: //if all balls are launched, reset the shooter and go to next position
                if (getRuntime() > timer) {
                    robot.intake.close();
                    follower().followPath(getPickup2, true);
                    setPathState(6);
                }
                break;
            case 6://if Got all 3 of first batch || patch ended 1.5 secs passed, go to launch pose
                if (!follower().isBusy()) {
                    follower().followPath(launchBatch2, true);
                    setPathState(7);
                    timer = getRuntime() + .9;
                }
                break;
            case 7: //if at launch pos and shooter fast enough, then shoot
                if (!follower().isBusy() && robot.getShooter().atTargetVelocity() && timer < getRuntime()) {
                    robot.intake.open();
                    setPathState(8);
                    timer = getRuntime() + shooterTimer;
                }
                break;
            case 8: //if all balls are launched, reset the shooter and go to next position
                if (getRuntime() > timer) {
                    robot.intake.close();
                    runs++;
                    follower().followPath(getPickup3, .9, true);
                    setPathState(100);
//                    timer = getRuntime() + 4;

                }
                break;
            case 100: //if all balls are launched, reset the shooter and go to next position
                if (!follower().isBusy()) {
//                    follower().followPath(launchBatch3, true);
                    robot.intake.close();
                    timer = getRuntime() + 1;
                    setPathState(9);
                }
                break;
            case 9://if Got all 3 of first batch || patch ended 1.5 secs passed, go to launch pose
                if (!follower().isBusy() && timer < getRuntime()) {
                    follower().followPath(launchBatch3, true);
                    timer = getRuntime() + 8;
                    setPathState(10);
                }
                break;
            case 10: //if at launch pos and shooter fast enough, then shoot
                if (timer > getRuntime() && !follower().isBusy()) {
//                    if (!follower().isBusy() && follower().getPose().getX() < 100 && follower().getPose().getX() > 40) {
                    robot.intake.open();
                    if (runs < 2) {
                        setPathState(8);
                    } else {
                        setPathState(2);
                    }
                    timer = getRuntime() + shooterTimer;
//                    }
                }
                break;
            case 11: //is the robot done launching? if yes, go to pickup 1 and stop shooter
                if (getRuntime() > timer) {
                    robot.intake.close();
                    follower().followPath(getPickup4, true);
                    setPathState(12);
                }
                break;
            case 12://if Got all 3 of first batch || patch ended 1.5 secs passed, go to launch pose
                if (!follower().isBusy() && timer < getRuntime()) {
                    follower().followPath(launchBatch4, true);
                    setPathState(13);
                    timer = getRuntime() + .3;
                }
                break;
            case 13:
                if (!follower().isBusy() && robot.getShooter().atTargetVelocity() && timer < getRuntime()) {
                    robot.intake.open();
                    setPathState(14);
                    timer = getRuntime() + shooterTimer;
                }
                break;
            case 14://if Got all 3 of first batch || patch ended 1.5 secs passed, go to launch pose
                if (!follower().isBusy() && timer < getRuntime()) {
                    follower().followPath(park, true);
                    setPathState(15);
                    timer = getRuntime() + .3;
                }
                break;
            case 15:
                if (!follower().isBusy()) {
                    setPathState(-1);
                    timer = getRuntime() + shooterTimer;
                }
                break;
        }
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }


    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        robot = new RobotNew().init(hardwareMap);
        controller1 = new GamepadEx(gamepad1);
        robot.getFollower().update();
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
    }

    @Override
    public void init_loop() {
        if (this.controller1.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
            this.config = Config.blue;
        } else if (this.controller1.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
            this.config = Config.red;
        }
        telemetry.addData("Team:", this.config == null ? null : this.config.getTeam());
        controller1.readButtons();
        robot.getIntake().close();

    }

    @Override
    public void start() {
        robot.getFollower().setPose(this.config.getStartPose());
        buildPaths();
        robot.getFollower().update();
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    @Override
    public void loop() {
        telemetryM.update();
        robot.getFollower().update();

        robot.getIntake().intake();

        distance = 4 + robot.getGoalDistance(robot.getFollower().getPose().getX(), robot.getFollower().getPose().getY(), config.getGoalPose().getX(), config.getGoalPose().getY());
        power = robot.getShooter().calculateShooterPower(distance);
        hood = robot.getShooter().calculateHoodPose(distance);
        robot.getShooter().setShot(power, hood);
        robot.getTurret().aim(robot.getTurret().calculateAngle(config.getGoalPose().getX(), config.getGoalPose().getY(), robot.getFollower().getPose().getX(), robot.getFollower().getPose().getY()), Math.toDegrees(robot.getFollower().getHeading()));
        runAuto();

//        telemetryM.debug("state", robot.robotstate);
        telemetryM.debug("step", pathState);
        telemetryM.debug("x:" + robot.getFollower().getPose().getX());
        telemetryM.debug("y:" + robot.getFollower().getPose().getY());
        telemetryM.debug("heading:" + Math.toDegrees(robot.getFollower().getPose().getHeading()));
        telemetryM.update(telemetry);
        panelsTelemetry.getFtcTelemetry().update();

    }


}