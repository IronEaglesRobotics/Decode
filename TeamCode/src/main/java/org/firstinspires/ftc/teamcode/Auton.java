package org.firstinspires.ftc.teamcode;


import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.BezierPoint;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

@Autonomous(name = "Blue auto")
public class Auton extends OpMode {
    private Robot robot;
    GamepadEx controller1;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;
    public TelemetryManager telemetryM;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
    private double timer;

    private shotTier tier = shotTier.REST;

    private enum shotTier {
        REST, NEAR, FAR
    }

    private final Pose startPose = new Pose(26, 130, Math.toRadians(145)); // Start Pose of our robot.
    private final Pose scorePose = new Pose(60, 96, Math.toRadians(145)); // Scoring Pose of our robot. It is facing the goal at a 135 degree angle.
    private final Pose pickup1Pose = new Pose(22, 87, Math.toRadians(180)); // Highest (First Set) of Artifacts from the Spike Mark.
    private final Pose pickup1Control = new Pose(48, 85, Math.toRadians(0)); // Highest (First Set) of Artifacts from the Spike Mark.
    private final Pose pickup2Transition = new Pose(48, 75, Math.toRadians(242)); // Middle (Second Set) of Artifacts from the Spike Mark.
    private final Pose pickup2Pose = new Pose(20, 62, Math.toRadians(180)); // Lowest (Third Set) of Artifacts from the Spike Mark.
    private final Pose pickup2Control = new Pose(37, 63, Math.toRadians(0)); // Lowest (Third Set) of Artifacts from the Spike Mark.

    private Follower follower() {
        return robot.getFollower();
    }

    private Pose offsetPose(Pose pose, double x, double y, double heading) {
        return new Pose(pose.getX() + x, pose.getY() + y, pose.getHeading() + heading);
    }

    private Pose offsetPose(Pose pose, double x, double y) {
        return new Pose(pose.getX() + x, pose.getY() + y, pose.getHeading());
    }

    private Path scorePreloads;
    private PathChain getPickup1, launchBatch1, getPickup2, launchBatch2;

    public void buildPaths() {
        scorePreloads = new Path(new BezierLine(startPose, scorePose));
        scorePreloads.setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading());

        getPickup1 = robot.getFollower().pathBuilder()
                .addPath(new BezierLine(scorePose, offsetPose(scorePose, 0, -5, Math.toRadians(60))))
                .setLinearHeadingInterpolation(scorePose.getHeading(), offsetPose(scorePose, 0, 0, Math.toRadians(60)).getHeading())
                .addPath(new BezierCurve(offsetPose(scorePose, 0, -5, Math.toRadians(60)), pickup1Control, pickup1Pose))
                .setTangentHeadingInterpolation()
                .setBrakingStart(.4)
//                .addParametricCallback(0.9,(()->tier = shotTier.NEAR))
                .build();

        launchBatch1 = follower().pathBuilder()
                .addPath(new BezierLine(pickup1Pose, scorePose))
                .setLinearHeadingInterpolation(pickup1Pose.getHeading(), scorePose.getHeading())
                .build();

        getPickup2 = robot.getFollower().pathBuilder()
                .addPath(new BezierLine(scorePose, pickup2Transition))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup2Transition.getHeading())
                .addPath(new BezierCurve(pickup2Transition, pickup2Control, pickup2Pose))
                .setTangentHeadingInterpolation()
                .setBrakingStart(.4)
                .build();

        launchBatch2 = follower().pathBuilder()
                .addPath(new BezierLine(pickup2Pose, scorePose))
                .setLinearHeadingInterpolation(pickup2Pose.getHeading(), scorePose.getHeading())
                .build();
        //add more pathchains as see fit
    }


    public void runAuto() {
        switch (pathState) {
            case 0: // Go to Launch 1 location
                robot.robotstate = Robot.robotStates.CALIBRATE;
                robot.getFollower().followPath(scorePreloads, true);
                setPathState(1);
                break;
            case 1: // immediately prep shooter -> if at location, launch
                tier = shotTier.NEAR;
                if (robot.robotstate == Robot.robotStates.IDLE) {
                    robot.robotstate = Robot.robotStates.HAS2;
                }
                if (!follower().isBusy()) {
                    robot.autoSwitch = true;
                    setPathState(2);
                }
                break;
            case 2: //is the robot done launching? if yes, go to pickup 1 and stop shooter
                robot.autoSwitch = false;
                if (robot.robotstate == Robot.robotStates.INTAKE && robot.balls == 0) {
                    follower().followPath(getPickup1, .6, true);
                    tier = shotTier.REST;
                    setPathState(3);
                }
                break;
            case 3://if Got all 3 of first batch || patch ended 1.5 secs passed, go to launch pose
                if (!follower().isBusy()) {
                    timer = getRuntime() + 1.5;
                    if (getRuntime() > timer || robot.robotstate == Robot.robotStates.HAS3) {
                        //follow next path
                        follower().followPath(launchBatch1, true);
                        setPathState(4);
                    }
                    tier = shotTier.NEAR;
                }

                break;
            case 4: //if at launch pos, then shoot
                if (!follower().isBusy()) {
                    robot.autoSwitch = true;
                    setPathState(5);
                }
                break;
            case 5: //if all balls are launched, reset the shooter and go to next position
                robot.autoSwitch = false;
                if (robot.robotstate == Robot.robotStates.INTAKE && robot.balls == 0) {
                    //follow get 2nd batch of balls
                    follower().followPath(getPickup2, .8, true);
//                    tier = shotTier.REST;
                    setPathState(6);
                }
                break;
            case 6://if Got all 3 of first batch || patch ended 1.5 secs passed, go to launch pose
                if (!follower().isBusy()) {
                    timer = getRuntime() + 1.5;
                    if (getRuntime() > timer || robot.robotstate == Robot.robotStates.HAS3) {
                        //follow next path
                        follower().followPath(launchBatch2, true);
                        setPathState(7);
                    }
                    tier = shotTier.NEAR;
                }

                break;
            case 7: //if at launch pos, then shoot
                if (!follower().isBusy()) {
                    robot.autoSwitch = true;
                    setPathState(8);
                }
                break;
            case 8:
                robot.autoSwitch = false;
                if (robot.robotstate == Robot.robotStates.INTAKE && robot.balls == 0) {
                    //follow get 2nd batch of balls
//                    follower().followPath(getPickup2,.8, true);
                    tier = shotTier.REST;
                    setPathState(-1);
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

        robot = new Robot().init(hardwareMap);
        robot.getFollower().setPose(startPose);
        controller1 = new GamepadEx(gamepad1);
        buildPaths();
        robot.getFollower().update();
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    @Override
    public void loop() {
        telemetryM.update();
        robot.getFollower().update();

        runAuto();
        robot.robotMacro(controller1, getRuntime());

        switch (tier) {
            case FAR:
                robot.getShooter().farShot();
                break;
            case NEAR:
                robot.getShooter().nearShot();
                break;
            case REST:
                robot.getShooter().rest();
                break;
        }

        telemetryM.debug("state", robot.robotstate);
        telemetryM.debug("step", pathState);
        telemetryM.debug("balls:", robot.balls);
        telemetryM.update(telemetry);
        panelsTelemetry.getFtcTelemetry().update();
    }


}