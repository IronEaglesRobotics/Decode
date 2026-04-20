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
@Autonomous(name = "Far Stack Auto")
public class FarAutoStack extends OpMode {


    private RobotNew robot;
    GamepadEx controller1;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;
    public TelemetryManager telemetryM;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
    private double timer;
    private boolean foo = true;
    private double shooterTimer = .75;
    private double power = .3;
    private double hood = .7;
    private double distance = 50;
    private boolean preloads = false;

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
    private PathChain launchBatch3Secondary, getPickup1, launchBatch1, getPickup2, launchBatch2, getPickup3, launchBatch3, getPickup4, launchBatch4;

    public void buildPaths() {
        getPickup1 = robot.getFollower().pathBuilder()
                .addPath(new BezierCurve(this.config.getStartPose(), this.config.getPickup1Control(), this.config.getPickup1Pose()))
                .setLinearHeadingInterpolation(this.config.getStartPose().getHeading(), this.config.getPickup1Pose().getHeading(),.75)
//                .setReversed()
                .build();

        launchBatch1 = robot.getFollower().pathBuilder()
                .addPath(new BezierLine(this.config.getPickup1Pose(), this.config.getScorePose()))
                .setLinearHeadingInterpolation(this.config.getPickup1Pose().getHeading(), this.config.getScorePose().getHeading())
                .addParametricCallback(.99, (() -> timer = getRuntime() + .5))
                .build();

        getPickup2 = robot.getFollower().pathBuilder()
                .addPath(new BezierCurve(this.config.getScorePose(), this.config.getPickup2Control(), this.config.getPickup2Pose()))
//                .setLinearHeadingInterpolation(this.config.getScorePose().getHeading(), (this.config.getPickup2Pose().getHeading()))
                .setConstantHeadingInterpolation(this.config.getPickup2Pose().getHeading())
                .build();
//
        launchBatch2 = follower().pathBuilder()
//                .addPath(new BezierLine(this.config.getPickup2Pose(), this.config.getScorePose()))
                .addPath(new BezierLine(this.config.getPickup2Pose(), this.config.getScorePose()))
                .setConstantHeadingInterpolation(this.config.getScorePose().getHeading())
                .addParametricCallback(.99, (() -> timer = getRuntime() + .5))
                .build();

        getPickup3 = robot.getFollower().pathBuilder()
                .addPath(new BezierCurve(this.config.getScorePose(), this.config.getPickup3Control(), this.config.getPickup3Pose()))
                .setConstantHeadingInterpolation(this.config.getPickup3Pose().getHeading())
                .build();

        getPickup4 = robot.getFollower().pathBuilder()
                .addPath(new BezierCurve(this.config.getPickup3Pose(), this.config.getPickup4Control(), this.config.getPickup4Pose()))
                .setConstantHeadingInterpolation(this.config.getPickup4Pose().getHeading())
                .build();
//
        launchBatch3 = follower().pathBuilder()
                .addPath(new BezierLine(this.config.getPickup3Pose(), this.config.getScorePose()))
                .setConstantHeadingInterpolation(this.config.getScorePose().getHeading())
                .addParametricCallback(.99, (() -> timer = getRuntime() + .5))
                .build();
        //
        launchBatch3Secondary = follower().pathBuilder()
                .addPath(new BezierLine(this.config.getPickup4Pose(), this.config.getScorePose()))
                .setConstantHeadingInterpolation(this.config.getScorePose().getHeading())
                .addParametricCallback(.99, (() -> timer = getRuntime() + .5))
                .build();
    }


    public void runAuto() {
        switch (pathState) {
            case 0: // Go to Launch 1 location
                robot.getIntake().close();
                setPathState(1);
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
                if (getRuntime() > timer) {
                    robot.intake.intake();
                    robot.intake.close();
                    follower().followPath(getPickup1, .85, true);
                    timer = getRuntime() + 3.5;
                    preloads = true;
                    setPathState(3);
                }
                break;
            case 3://if Got all 3 of first batch || patch ended 1.5 secs passed, go to launch pose
                if (!follower().isBusy() || timer < getRuntime()) {
                    follower().followPath(launchBatch1, true);
                    setPathState(4);
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
                    follower().followPath(getPickup2, 1, true);
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
                    follower().followPath(getPickup3);
                    setPathState(9);
                }
                break;
            case 9: //if all balls are launched, reset the shooter and go to next position
                if (!follower().isBusy() && getRuntime() < 27) {
                    if(robot.getIntake().currentSpiked()) {
                        follower().followPath(launchBatch3, true);
                        setPathState(10);
                    } else {
                        follower().followPath(getPickup4,true);
                        setPathState(100);
                    }
                }
                break;
            case 10:
                if (!follower().isBusy() && robot.getShooter().atTargetVelocity() && timer < getRuntime()) {
                    robot.getIntake().transfer(true);
                    robot.intake.open();
                    setPathState(8);
                    timer = getRuntime() + shooterTimer;
                }
                break;
            case 100:
                if((!follower().isBusy() || robot.getIntake().currentSpiked()) && getRuntime() < 27){
                    follower().breakFollowing();
                    follower().followPath(launchBatch3Secondary, true);
                    setPathState(10);
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
            this.config = Config.blueFarStack;
        } else if (this.controller1.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
            this.config = Config.redFarStack;
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

        RobotNew.Shooter.Metrics result = getInterpolatedValue(distance+1);
        power = result.y1;
        hood = result.z;
        robot.getShooter().setShot(power, hood);
//        robot.getTurret().aim(robot.getTurret().calculateAngle(config.getGoalPose().getX(), config.getGoalPose().getY(), robot.getFollower().getPose().getX(), robot.getFollower().getPose().getY()), Math.toDegrees(robot.getFollower().getHeading()));

        if (!preloads) {
            robot.getTurret().aim(robot.getTurret().calculateAngle(config.getGoalPose().getX(), config.getGoalPose().getY(), config.getStartPose().getX(), config.getStartPose().getY()), Math.toDegrees(config.getStartPose().getHeading()));
        } else {
            robot.getTurret().aim(3+robot.getTurret().calculateAngle(config.getGoalPose().getX(), config.getGoalPose().getY(), config.getScorePose().getX(), config.getScorePose().getY()), Math.toDegrees(config.getScorePose().getHeading()));
        }
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