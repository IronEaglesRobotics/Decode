//package org.firstinspires.ftc.teamcode;
//
//
//import com.bylazar.telemetry.PanelsTelemetry;
//import com.bylazar.telemetry.TelemetryManager;
//import com.pedropathing.follower.Follower;
//import com.pedropathing.geometry.BezierCurve;
//import com.pedropathing.geometry.BezierLine;
//import com.pedropathing.geometry.Pose;
//import com.pedropathing.paths.Path;
//import com.pedropathing.paths.PathChain;
//import com.pedropathing.util.Timer;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.seattlesolvers.solverslib.gamepad.GamepadEx;
//import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
//@Disabled
//@Autonomous(name = "Far 9")
//public class AutonFar extends OpMode {
//    private Robot robot;
//    GamepadEx controller1;
//    private Timer pathTimer, actionTimer, opmodeTimer;
//    private int pathState;
//    public TelemetryManager telemetryM;
//    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
//    private double timer;
//
//    private shotTier tier = shotTier.FAR;
//
//    private enum shotTier {
//        REST, NEAR, FAR
//    }
//
//
//    private Config config;
//
//    private Follower follower() {
//        return robot.getFollower();
//    }
//
//    private Pose offsetPose(Pose pose, double x, double y, double heading) {
//        return new Pose(pose.getX() + x, pose.getY() + y, pose.getHeading() + heading);
//    }
//
//    private Pose offsetPose(Pose pose, double x, double y) {
//        return new Pose(pose.getX() + x, pose.getY() + y, pose.getHeading());
//    }
//
//    private Path scorePreloads, park, openGate;
//    private PathChain getPickup1, launchBatch1, getPickup2, launchBatch2, getPickup3, launchBatch3,getBalls2;
//
//    public void buildPaths() {
//        scorePreloads = new Path(new BezierLine(this.config.getStartPose(), this.config.getScorePose()));
//        scorePreloads.setLinearHeadingInterpolation(this.config.getStartPose().getHeading(), this.config.getScorePose().getHeading());
//
//
//        getPickup1 = robot.getFollower().pathBuilder()
//                .addPath(new BezierCurve(this.config.getScorePose(), this.config.getPickup1Control(), this.config.getPickup1Pose()))
//                .setTangentHeadingInterpolation()
//                .addParametricCallback(.99, (() -> timer = getRuntime() + 2.5))
//                .build();
////
//        launchBatch1 = follower().pathBuilder()
//                .addPath(new BezierLine(this.config.getPickup1Pose(), this.config.getScorePose()))
//                .setLinearHeadingInterpolation(this.config.getPickup1Pose().getHeading(), this.config.getScorePose().getHeading(),.8)
//                .build();
////
//        getPickup2 = robot.getFollower().pathBuilder()
//                .addPath(new BezierCurve(this.config.getScorePose(),this.config.getPickup2Control(), this.config.getPickup2Transition()))
//                .setTangentHeadingInterpolation()
//                .build();
//
//        getBalls2 = robot.getFollower().pathBuilder()
//                .addPath(new BezierLine(this.config.getPickup2Transition(),this.config.getPickup2Pose()))
//                .setLinearHeadingInterpolation(this.config.getPickup2Transition().getHeading(),this.config.getPickup2Pose().getHeading())
//                .addParametricCallback(.99, (() -> timer = getRuntime() + 2.5))
//                .build();
////
//        launchBatch2 = follower().pathBuilder()
//                .addPath(new BezierLine(this.config.getPickup2Pose(), this.config.getScorePose()))
//                .setLinearHeadingInterpolation(this.config.getPickup2Pose().getHeading(), this.config.getScorePose().getHeading())
//                .build();
////        //add more pathchains as see fit
//    }
//
//
//    public void runAuto() {
//        switch (pathState) {
//            case 0: // Go to Launch 1 location
//                robot.robotstate = Robot.robotStates.CALIBRATE;
//                if (getRuntime() > time) {
//                    robot.getFollower().followPath(scorePreloads, true);
//                    setPathState(1);
//                }
//            case 1: // immediately prep shooter -> if at location, launch
//                if (robot.robotstate == Robot.robotStates.IDLE) {
//                    robot.robotstate = Robot.robotStates.HAS3;
//                    robot.balls = 3;
//                }
//                if (!follower().isBusy() && robot.shooter.atTargetVelocity()) {
//                        setPathState(2);
//                        timer = getRuntime() + 3;
//                }
//                break;
//            case 2: //is the robot done launching? if yes, go to pickup 1 and stop shooter
//                robot.autoSwitch = true;
//                if (robot.robotstate == Robot.robotStates.INTAKE && robot.balls == 0 && timer < getRuntime()) {
//                    follower().followPath(getPickup1, true);
//                    setPathState(3);
//                }
//                break;
//            case 3://if Got all 3 of first batch || patch ended 1.5 secs passed, go to launch pose
//                robot.autoSwitch = false;
//                if (!follower().isBusy()  || robot.robotstate == Robot.robotStates.HAS3) {
//                    if (getRuntime() > timer  || robot.robotstate == Robot.robotStates.HAS3) {
////                        follower().breakFollowing();
//                        //follow next path
//                        follower().breakFollowing();
//                        follower().followPath(launchBatch1,.7, true);
//                        setPathState(4);
//                        timer = getRuntime() + 1;
//                    }
//                }
//            case 4: //if at launch pos and shooter fast enough, then shoot
//                if (!follower().isBusy() && robot.getShooter().atTargetVelocity() && getRuntime() > timer ) {
//                        robot.autoSwitch = true;
//                        setPathState(5);
////                        timer = getRuntime() + 3;
//                }
//                break;
//            case 5: //if all balls are launched, reset the shooter and go to next position
//                robot.autoSwitch = false;
//                if (robot.robotstate == Robot.robotStates.INTAKE && robot.balls == 0 && timer < getRuntime()) {
//                    follower().followPath(getPickup2, true);
//                    setPathState(6);
//                }
//                break;
//            case 6: //if all balls are launched, reset the shooter and go to next position
//                if (!follower().isBusy()) {
//                    follower().followPath(getBalls2,.5, true);
//                    setPathState(7);
//                }
//                break;
//            case 7://if Got all 3 of second batch || patch ended 1.5 secs passed, go to launch pose
//                if (!follower().isBusy()  || robot.robotstate == Robot.robotStates.HAS3) {
//                    follower().breakFollowing();
//                    if (getRuntime() > timer || robot.robotstate == Robot.robotStates.HAS3) {
//                        //follow next path
//                        follower().breakFollowing();
//                        follower().followPath(launchBatch2,.85, true);
//                        setPathState(8);
//                        timer = getRuntime() + 2;
//                    }
//                }
//                break;
//            case 8: //if at launch pos, then shoot
//                if (!follower().isBusy() && robot.getShooter().atTargetVelocity() && getRuntime()>timer ) {
//                    robot.autoSwitch = true;
//                    setPathState(-1);
////                    timer = getRuntime() + 3;
//                }
//                break;
//
//                //ADD MOVEMENT OFF LINE
//        }
//    }
//
//    public void setPathState(int pState) {
//        pathState = pState;
//        pathTimer.resetTimer();
//    }
//
//
//    @Override
//    public void init() {
//        pathTimer = new Timer();
//        opmodeTimer = new Timer();
//        opmodeTimer.resetTimer();
//
//        robot = new Robot().init(hardwareMap);
//        controller1 = new GamepadEx(gamepad1);
//        robot.getFollower().update();
//        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
//    }
//
//    @Override
//    public void init_loop() {
//        if(this.controller1.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
//            this.config = Config.blueFar;
//        } else if(this.controller1.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)){
//            this.config = Config.redFar;
//        }
//        telemetry.addData("Team:", this.config==null? null: this.config.getTeam());
//        controller1.readButtons();
//
//    }
//
//    @Override
//    public void start() {
//        robot.getFollower().setPose(this.config.getStartPose());
//        buildPaths();
//        robot.getFollower().update();
//        opmodeTimer.resetTimer();
//        setPathState(0);
//        timer = getRuntime() + 2;
//    }
//
//    @Override
//    public void loop() {
//        telemetryM.update();
//        robot.getFollower().update();
//
//        runAuto();
//        robot.robotMacro(controller1, getRuntime(), true);
//
//        switch (tier) {
//            case FAR:
//                robot.getShooter().farShot();
//                break;
//            case NEAR:
//                robot.getShooter().nearShot();
//                break;
//            case REST:
//                robot.getShooter().rest();
//                break;
//        }
//
//        telemetryM.debug("state", robot.robotstate);
//        telemetryM.debug("step", pathState);
//        telemetryM.debug("balls:", robot.balls);
//        telemetryM.addData("shooter_velo: ", robot.getShooter().calculatedVelocity());
//        telemetryM.update(telemetry);
//        panelsTelemetry.getFtcTelemetry().update();
//    }
//
//
//}