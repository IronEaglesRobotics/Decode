package org.firstinspires.ftc.teamcode;


import static org.firstinspires.ftc.teamcode.RobotNew.Shooter.getInterpolatedValue;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

//@Disabled
@Autonomous(name = "Far Park")
public class FarPark extends OpMode {


    private RobotNew robot;
    GamepadEx controller1;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;
    public TelemetryManager telemetryM;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
    private double timer;
    private boolean foo = true;
    private double shooterTimer = 1.25;
    private double power = .3;
    private double hood = .7;
    private double distance = 50;
    private boolean runs = false;

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

    private Path scorePreloads, park, openGate;
    private PathChain getPickup1, launchBatch1, getPickup2, launchBatch2, getPickup1Straight, launchBatch3, getPickup4, launchBatch4;

    public void buildPaths() {
        getPickup1 = robot.getFollower().pathBuilder()
                .addPath(new BezierLine(this.config.getStartPose(), this.config.getPickup1Pose()))
                .setConstantHeadingInterpolation(this.config.getPickup1Pose().getHeading())
//                .setReversed()
                .build();

        getPickup1Straight = robot.getFollower().pathBuilder()
                .addPath(new BezierLine(this.config.getScorePose(), this.config.getPickup1Pose()))
//                .setLinearHeadingInterpolation(this.config.getShootPose().getHeading(), this.config.getPickup1Pose().getHeading())
                .setTangentHeadingInterpolation()
                .setReversed()
                .build();

        launchBatch1 = robot.getFollower().pathBuilder()
                .addPath(new BezierLine(this.config.getPickup1Pose(), this.config.getScorePose()))
                .setLinearHeadingInterpolation(this.config.getPickup1Pose().getHeading(), this.config.getScorePose().getHeading())
                .addParametricCallback(.99, (() -> timer = getRuntime() + 1))
                .build();

        getPickup2 = robot.getFollower().pathBuilder()
//                .addPath(new BezierCurve(this.config.getScorePose(), this.config.getPickup2Control(), this.config.getPickup2Pose()))
                .addPath(new BezierLine(this.config.getScorePose(), this.config.getPickup2Pose()))

                .setLinearHeadingInterpolation(this.config.getScorePose().getHeading(), (this.config.getPickup2Pose().getHeading()))

                .build();
//
        launchBatch2 = follower().pathBuilder()
//                .addPath(new BezierLine(this.config.getPickup2Pose(), this.config.getScorePose()))
                .addPath(new BezierLine(this.config.getPickup2Pose(), this.config.getScorePose()))
                .setTangentHeadingInterpolation()
                .addParametricCallback(.99, (() -> timer = getRuntime() + 1))
                .build();
    }


    public void runAuto() {
        switch (pathState) {
            case 0: // Go to Launch 1 location
                robot.getIntake().close();
                setPathState(2);
                break;
            case 1: // if at location, launch
                if (robot.getShooter().atTargetVelocity()) {
                    robot.getIntake().transfer(true);
                    robot.getIntake().open();
                    setPathState(2);
                    timer = getRuntime() + shooterTimer;
                }
                break;
            case 2: //is the robot done launching? if yes, go to pickup 1 and stop shooter
//                if (getRuntime() > timer) {
//                    robot.intake.intake();
//                    robot.intake.close();
                    follower().followPath(getPickup1, .85, true);
//                    timer = getRuntime() + 3.5;
                    setPathState(3);
//                }
                break;
            case 3://if Got all 3 of first batch || patch ended 1.5 secs passed, go to launch pose
                if (!follower().isBusy() || timer < getRuntime()) {
//                    follower().followPath(launchBatch1, true);
                    setPathState(-1);
//                    timer = getRuntime() + .9;
                }
                break;
            case 4: //if at launch pos and shooter fast enough, then shoot
                if (!follower().isBusy() && robot.getShooter().atTargetVelocity() && timer < getRuntime()) {
                    robot.getIntake().transfer(true);
                    robot.intake.open();
                    setPathState(5);
                    timer = getRuntime() + shooterTimer;
                }
                break;
            case 5: //is the robot done launching? if yes, go to pickup 1 and stop shooter
                if (getRuntime() > timer) {
                    robot.intake.intake();
                    robot.intake.close();
//                    if (!runs) {
                    follower().followPath(getPickup2, 1, true);
//                    } else {
//                        follower().followPath(getPickup1Straight, .75, true);
//                    }
                    setPathState(6);
                    timer = getRuntime() + 7.5;
                }
                break;
            case 6://if Got all 3 of first batch || patch ended 1.5 secs passed, go to launch pose
                if (!follower().isBusy() && robot.getIntake().currentSpiked()) {
//                    if (!runs) {
                    follower().followPath(launchBatch2, true);
//                    } else {
//                        follower().followPath(launchBatch1, .75, true);
//                    }
                    runs = !runs;
                    setPathState(7);
//                    timer = getRuntime() + .9;
                }
                break;
            case 7: //if at launch pos and shooter fast enough, then shoot
                if (!follower().isBusy() && robot.getShooter().atTargetVelocity() && timer < getRuntime()) {
                    robot.getIntake().transfer(true);

                    robot.intake.open();
                    setPathState(8);
                    timer = getRuntime() + shooterTimer;
                }
                break;
            case 8: //if all balls are launched, reset the shooter and go to next position
                if (getRuntime() > timer) {
                    robot.intake.intake();
                    robot.intake.close();
//                    follower().followPath(getPickup2, true);
                    setPathState(5);
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
            this.config = Config.blueFarPark;
        } else if (this.controller1.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
            this.config = Config.redFar;
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

//        robot.getIntake().intake();
        distance = robot.getGoalDistance(robot.getFollower().getPose().getX(), robot.getFollower().getPose().getY(), config.getGoalPose().getX(), config.getGoalPose().getY());

        RobotNew.Shooter.Metrics result = getInterpolatedValue(distance);
        power = result.y1;
        hood = result.z;
        robot.getShooter().setShot(power, hood);
        robot.getTurret().aim(robot.getTurret().calculateAngle(config.getGoalPose().getX(), config.getGoalPose().getY(), robot.getFollower().getPose().getX(), robot.getFollower().getPose().getY()), Math.toDegrees(robot.getFollower().getHeading()));
        runAuto();


        telemetryM.debug("step", pathState);
        telemetryM.debug("x:" + robot.getFollower().getPose().getX());
        telemetryM.debug("y:" + robot.getFollower().getPose().getY());
        telemetryM.debug("heading:" + Math.toDegrees(robot.getFollower().getPose().getHeading()));
        telemetryM.debug("turret" + robot.getTurret().targetTicks);
        telemetryM.update(telemetry);
        panelsTelemetry.getFtcTelemetry().update();

    }


}