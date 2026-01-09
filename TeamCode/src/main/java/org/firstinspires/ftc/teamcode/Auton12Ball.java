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
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

@Disabled
@Autonomous(name = "12 ball auto")
public class Auton12Ball extends OpMode {


    private RobotNew robot;
    GamepadEx controller1;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private int pathState;
    public TelemetryManager telemetryM;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
    private double timer;
    private boolean foo = true;

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
    private PathChain getPickup1, launchBatch1, getPickup2, launchBatch2, getPickup3, launchBatch3;

    public void buildPaths() {
        scorePreloads = new Path(new BezierLine(this.config.getStartPose(), this.config.getScorePose()));
        scorePreloads.setLinearHeadingInterpolation(this.config.getStartPose().getHeading(), this.config.getStartPose().getHeading(), .8);
//        park = new Path(new BezierLine(this.config.getStartPose(), this.config.getParkPose()));
//        park.setLinearHeadingInterpolation(this.config.getStartPose().getHeading(), this.config.getParkPose().getHeading());
//        openGate = new Path(new BezierLine(this.config.getPickup1Pose(), this.config.getOpenGate()));
//        openGate.setLinearHeadingInterpolation(this.config.getPickup1Pose().getHeading(), this.config.getOpenGate().getHeading(),.75 );


        getPickup1 = robot.getFollower().pathBuilder()
//                .addPath(new BezierLine(this.config.getStartPose(), offsetPose(this.config.getStartPose(), 0, -5, Math.toRadians(60))))
//                .setLinearHeadingInterpolation(this.config.getStartPose().getHeading(), offsetPose(this.config.getScorePose(), 0, 0, Math.toRadians(60)).getHeading())
                .addPath(new BezierCurve(this.config.getScorePose(), this.config.getPickup1Control(), this.config.getPickup1Pose()))
                .setTangentHeadingInterpolation()
                .setReversed()
//                .addParametricCallback(.99, (() -> timer = getRuntime() + 2.5))
                .build();

        launchBatch1 = robot.getFollower().pathBuilder()
                .addPath(new BezierLine(this.config.getPickup1Pose(), this.config.getScorePose()))
                .setTangentHeadingInterpolation()
                .build();

        getPickup2 = robot.getFollower().pathBuilder()
//                .addPath(new BezierLine(this.config.getScorePose(), this.config.getPickup2Transition()))
//                .setLinearHeadingInterpolation(this.config.getScorePose().getHeading(), this.config.getPickup2Transition().getHeading())
                .addPath(new BezierCurve(this.config.getScorePose(), this.config.getPickup2Control(), this.config.getPickup2Pose()))
                .setTangentHeadingInterpolation()
                .setReversed()
//                .addParametricCallback(.99, (() -> timer = getRuntime() + 2.5))

                .build();

        launchBatch2 = follower().pathBuilder()
                .addPath(new BezierLine(this.config.getPickup2Pose(), this.config.getScorePose()))
                .setTangentHeadingInterpolation()
                .setReversed()
                .build();

        getPickup3 = robot.getFollower().pathBuilder()
//                .addPath(new BezierLine(this.config.getScorePose(), this.config.getPickup3Transition()))
//                .setLinearHeadingInterpolation(this.config.getScorePose().getHeading(), this.config.getPickup3Transition().getHeading())
                .addPath(new BezierCurve(this.config.getScorePose(), this.config.getPickup3Control(), this.config.getPickup3Control2(), this.config.getPickup3Pose()))
//                .setBrakingStart(.75)
                .setTangentHeadingInterpolation()
                .setReversed()
//                .addParametricCallback(.99, (() -> timer = getRuntime() + 2.5))
                .build();

        launchBatch3 = follower().pathBuilder()
                .addPath(new BezierLine(this.config.getPickup3Pose(), this.config.getScorePose()))
                .setLinearHeadingInterpolation(this.config.getPickup2Pose().getHeading(), this.config.getScorePose().getHeading())
                .setTangentHeadingInterpolation()
                .build();
        //add more pathchains as see fit
    }


    public void runAuto() {
        switch (pathState) {
            case 0: // Go to Launch 1 location
                robot.getFollower().followPath(scorePreloads, true);
                setPathState(1);
            case 1: // if at location, launch
                if (!follower().isBusy() && robot.shooter.atTargetVelocity()) {
                    robot.getIntake().open();
                    setPathState(2);
                    timer = getRuntime() + .8;
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
                }
            case 4: //if at launch pos and shooter fast enough, then shoot
                if (!follower().isBusy() && robot.getShooter().atTargetVelocity()) {
                    robot.intake.open();
                    setPathState(5);
                    timer = getRuntime() + .8;
                }
                break;
            case 5: //if all balls are launched, reset the shooter and go to next position
                if (getRuntime() > timer) {
                    robot.intake.close();
                    follower().followPath(getPickup2, true);
                    setPathState(-1);
                }
                break;
//            case 6://if Got all 3 of second batch || patch ended 1.5 secs passed, go to launch pose
//                if (!follower().isBusy()) {                     //follow next path
//                    follower().followPath(launchBatch2, true);
//                    setPathState(7);
//                }
//                break;
//            case 7: //if at launch pos, then shoot
//                if (!follower().isBusy() && robot.getShooter().atTargetVelocity()) {
//                    robot.autoSwitch = true;
//                    setPathState(8);
//                    timer = getRuntime() + 3;
//                }
//                break;
//            case 8: //if all balls are launched, reset the shooter and go to next position
//                robot.autoSwitch = false;
//                if (robot.robotstate == Robot.robotStates.INTAKE && robot.balls == 0 && timer < getRuntime()) {
//                    follower().followPath(getPickup3, true);
//                    setPathState(9);
//                }
//                break;
//            case 9://if Got all 3 of Third batch || patch ended 1.5 secs passed, go to launch pose
//                if (!follower().isBusy() || robot.robotstate == Robot.robotStates.HAS3) {
//                    if (getRuntime() > timer || robot.robotstate == Robot.robotStates.HAS3) {
//                        follower().breakFollowing();
//                        //follow next path
//                        follower().followPath(launchBatch3, true);
//                        setPathState(10);
//                    }
//                }
//
//                break;
//            case 10: //if at launch pos, then shoot
//                if (!follower().isBusy() && robot.getShooter().atTargetVelocity()) {
//                    robot.autoSwitch = true;
//                    setPathState(11);
//                    timer = getRuntime() + 3;
//                }
//                break;
//            case 11:
//                robot.autoSwitch = false;
//                if (robot.balls == 0 && timer < getRuntime()) {
//                    //park the robot
//                    follower().followPath(park, true);
//                    tier = shotTier.REST;
//                    setPathState(12);
//                }
//                break;
//            case 12:
//                if (!follower().isBusy()) {
//                    setPathState(-1);
//                    stop();
//                }
//                break;
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
        robot.getShooter().nearShot();
        if (robot.getFollower().getHeading() < Math.toRadians(63) && robot.getFollower().getHeading() > Math.toRadians(-165)) {
            robot.getTurret().aim(robot.getTurret().calculateAngle(config.getGoalPose().getX(), config.getGoalPose().getY(), robot.getFollower().getPose().getX(), robot.getFollower().getPose().getY()), (robot.getFollower().getHeading()));
        } else {
            robot.getTurret().off();
        }

        runAuto();

//        telemetryM.debug("state", robot.robotstate);
        telemetryM.debug("step", pathState);
        telemetryM.debug("x:" + robot.getFollower().getPose().getX());
        telemetryM.debug("y:" + robot.getFollower().getPose().getY());
        telemetryM.debug("heading:" + robot.getFollower().getPose().getHeading());
        telemetryM.update(telemetry);
        panelsTelemetry.getFtcTelemetry().update();

    }


}