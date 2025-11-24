package org.firstinspires.ftc.teamcode;


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
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

@Disabled
@Autonomous(name = "Red auto")
public class AutonRed extends OpMode {
    private Robot robot;
    GamepadEx controller1;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;
    public TelemetryManager telemetryM;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
    private double timer;
    private boolean foo = true;

    private shotTier tier = shotTier.REST;

    private enum shotTier {
        REST, NEAR, FAR
    }

    private final Pose startPose = new Pose(118, 130, Math.toRadians(35)); // Mirrored Start Pose: x'=144-26=118, angle'=180-145=35
    private final Pose scorePose = new Pose(84, 96, Math.toRadians(35)); // Mirrored Scoring Pose: x'=144-60=84, angle'=180-145=35
    private final Pose pickup1Pose = new Pose(122, 89, Math.toRadians(0)); // Mirrored Highest (First Set) of Artifacts: x'=144-22=122, angle'=180-180=0
    private final Pose pickup1Control = new Pose(96, 87, Math.toRadians(180)); // Mirrored Highest (First Set) Control: x'=144-48=96, angle'=180-0=180
    private final Pose pickup2Transition = new Pose(96, 77, Math.toRadians(298)); // Mirrored Middle (Second Set) Transition: x'=144-48=96, angle'=180-242=-62 (or 298)
    private final Pose pickup2Pose = new Pose(124, 64, Math.toRadians(0)); // Mirrored Lowest (Third Set) of Artifacts: x'=144-20=124, angle'=180-180=0
    private final Pose pickup2Control = new Pose(107, 65, Math.toRadians(180)); // Mirrored Lowest (Third Set) Control: x'=144-37=107, angle'=180-0=180
    private final Pose parkPose = new Pose(84, 100, Math.toRadians(145)); // Scoring Pose of our robot. It is facing the goal at a 135 degree angle.


    private Follower follower() {
        return robot.getFollower();
    }

    private Pose offsetPose(Pose pose, double x, double y, double heading) {
        return new Pose(pose.getX() + x, pose.getY() + y, pose.getHeading() + heading);
    }

    private Pose offsetPose(Pose pose, double x, double y) {
        return new Pose(pose.getX() + x, pose.getY() + y, pose.getHeading());
    }

    private Path scorePreloads, park;
    private PathChain getPickup1, launchBatch1, getPickup2, launchBatch2;

    public void buildPaths() {
        scorePreloads = new Path(new BezierLine(startPose, scorePose));
        scorePreloads.setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading());
        park = new Path (new BezierLine(scorePose, parkPose));
        park.setConstantHeadingInterpolation(scorePose.getHeading());

        getPickup1 = robot.getFollower().pathBuilder()
                .addPath(new BezierLine(scorePose, offsetPose(scorePose, 0, -5, Math.toRadians(-60))))
                .setLinearHeadingInterpolation(scorePose.getHeading(), offsetPose(scorePose, 0, 0, Math.toRadians(-60)).getHeading())
                .addPath(new BezierCurve(offsetPose(scorePose, 0, -5, Math.toRadians(-60)), pickup1Control, pickup1Pose))
                .setTangentHeadingInterpolation()
                .addParametricCallback(.99,(()->timer = getRuntime() + 2.5))
//                .setBrakingStart(.4)
                .build();

        launchBatch1 = follower().pathBuilder()
                .addPath(new BezierLine(pickup1Pose, scorePose))
                .setLinearHeadingInterpolation(pickup1Pose.getHeading(), scorePose.getHeading())
                .addParametricCallback(.1,(()->tier = shotTier.NEAR))
                .build();

        getPickup2 = robot.getFollower().pathBuilder()
                .addPath(new BezierLine(scorePose, pickup2Transition))
                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup2Transition.getHeading())
                .addPath(new BezierCurve(pickup2Transition, pickup2Control, pickup2Pose))
                .setTangentHeadingInterpolation()
//                .setBrakingStart(.4)
                .addParametricCallback(.99,(()->timer = getRuntime() + 2.5))
                .build();

        launchBatch2 = follower().pathBuilder()
                .addPath(new BezierLine(pickup2Pose, scorePose))
                .setLinearHeadingInterpolation(pickup2Pose.getHeading(), scorePose.getHeading())
                .addParametricCallback(.1,(()->tier = shotTier.NEAR))
                .build();
        //add more pathchains as see fit
    }


    public void runAuto() {
        switch (pathState) {
            case 0: // Go to Launch 1 location
                robot.robotstate = Robot.robotStates.CALIBRATE;
                if (getRuntime() > time) {
                    robot.getFollower().followPath(scorePreloads, true);
                    setPathState(1);
                }
            case 1: // immediately prep shooter -> if at location, launch
                tier = shotTier.NEAR;
                if (robot.robotstate == Robot.robotStates.IDLE) {
                    robot.robotstate = Robot.robotStates.HAS3;
                    robot.balls = 3;
                }
                if (!follower().isBusy()) {
                    if (foo) {
                        timer = getRuntime() + 1;
                        foo = false;
                    }
                    if (getRuntime() > timer) {
                        robot.autoSwitch = true;
                        setPathState(2);
                        foo = true;
                    }
                }
                break;
            case 2: //is the robot done launching? if yes, go to pickup 1 and stop shooter
                robot.autoSwitch = false;
                if (robot.robotstate == Robot.robotStates.INTAKE && robot.balls == 0) {
                    follower().followPath(getPickup1, .8, true);
                    tier = shotTier.REST;
                    setPathState(3);
                }
                break;
            case 3://if Got all 3 of first batch || patch ended 1.5 secs passed, go to launch pose
                if (!follower().isBusy()) {
                    if (getRuntime() > timer || robot.robotstate == Robot.robotStates.HAS3) {
                        //follow next path
                        follower().followPath(launchBatch1, true);
                        setPathState(4);
                    }
//                    tier = shotTier.NEAR;
                }

                break;
            case 4: //if at launch pos, then shoot
                if (!follower().isBusy()) {
                    if (foo) {
                        timer = getRuntime() + 1;
                        foo = false;
                    }
                    if (getRuntime() > timer) {
                        robot.autoSwitch = true;
                        setPathState(5);
                    }
                }
                break;
            case 5: //if all balls are launched, reset the shooter and go to next position
                robot.autoSwitch = false;
                if (robot.robotstate == Robot.robotStates.INTAKE && robot.balls == 0) {
                    //follow get 2nd batch of balls
                    follower().followPath(getPickup2, 1, true);
                    tier = shotTier.REST;
                    setPathState(6);
                }
                break;
            case 6://if Got all 3 of first batch || patch ended 1.5 secs passed, go to launch pose
                if (!follower().isBusy()) {
                    if (getRuntime() > timer || robot.robotstate == Robot.robotStates.HAS3) {
                        //follow next path
                        follower().followPath(launchBatch2, true);
                        setPathState(7);
//                        tier = shotTier.NEAR;
                    }
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
                if (robot.balls == 0) {
                    //follow get 2nd batch of balls
                    follower().followPath(park, true);
                    tier = shotTier.REST;

                    setPathState(9);
                }
                break;
            case 9:
                if (!follower().isBusy()) {
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
        timer= getRuntime() + 2;
    }

    @Override
    public void loop() {
        telemetryM.update();
        robot.getFollower().update();

        runAuto();
        robot.robotMacro(controller1, getRuntime(),true);

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