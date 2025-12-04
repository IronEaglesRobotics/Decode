package org.firstinspires.ftc.teamcode;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.HeadingInterpolator;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.pedropathing.paths.PathChain;
import com.pedropathing.paths.Path;

import java.util.function.Supplier;


@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TeleOp")
public class TeleOP extends OpMode {
    private Robot robot;
    private shotTier tier = shotTier.REST;

    private enum shotTier {
        REST, NEAR, FAR
    }

    GamepadEx controller1;
    public TelemetryManager telemetryM;

    private Supplier<PathChain> pathChain;
    private Supplier<PathChain> pathChainFar;
    private boolean automatedDrive;


    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
    private Pose shootPose = new Pose(60, 96, Math.toRadians(145));
    private Pose shootPoseFar = new Pose(75, 18, Math.toRadians(120));


    @Override
    public void init() {
        robot = new Robot().init(hardwareMap);
        robot.getFollower().setStartingPose(new Pose(45, 96, Math.toRadians(180)));
        robot.getFollower().update();
        controller1 = new GamepadEx(gamepad1);
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        pathChain = () -> robot.getFollower().pathBuilder() //Lazy Curve Generation
                .addPath(new Path(new BezierLine(robot.getFollower()::getPose, shootPose)))
                .setHeadingInterpolation(HeadingInterpolator.linearFromPoint(robot.getFollower()::getHeading, shootPose.getHeading(), 0.8))
                .build();

        pathChainFar = () -> robot.getFollower().pathBuilder() //Lazy Curve Generation
                .addPath(new Path(new BezierLine(robot.getFollower()::getPose, shootPoseFar)))
                .setHeadingInterpolation(HeadingInterpolator.linearFromPoint(robot.getFollower()::getHeading, shootPoseFar.getHeading(), 0.8))
                .build();
    }

    @Override
    public void start() {
        robot.getFollower().startTeleopDrive();
    }

    @Override
    public void loop() {
        controller1.readButtons();
        telemetryM.update();

        robot.getFollower().update();
        if (!automatedDrive) {
            robot.getFollower().setTeleOpDrive(
                    controller1.getLeftY(),
                    -controller1.getLeftX(),
                    -controller1.getRightX(), true // Robot Centric
            );
        }


        if (controller1.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
            tier = shotTier.FAR;
        } else if (controller1.wasJustPressed((GamepadKeys.Button.RIGHT_BUMPER))) {
            tier = shotTier.NEAR;
        } else if (controller1.wasJustPressed((GamepadKeys.Button.Y))) {
            tier = shotTier.REST;
        }

        //Auto Drive turning
        if (controller1.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
            robot.getFollower().followPath(pathChain.get(), true);
            automatedDrive = true;
        }

        if (controller1.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0.5) {
            robot.getFollower().followPath(pathChainFar.get(), true);
            automatedDrive = true;
        }

        //Stop automated following if the follower is done
        if (automatedDrive && (controller1.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > .5 || !robot.getFollower().isBusy())) {
            robot.getFollower().startTeleopDrive();
            automatedDrive = false;
        }

        if (controller1.wasJustPressed(GamepadKeys.Button.LEFT_STICK_BUTTON)) {
            robot.getFollower().setPose(new Pose(72, 84, Math.toRadians(135)));
        }

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


        robot.robotMacro(controller1, getRuntime(), false);

//        telemetryM.debug("Shooter PID", String.join(",", Arrays.stream(this.robot.getShooter().getPIDValues()).mapToObj(Double::toString).collect(Collectors.joining(","))));
        telemetryM.addData("shooter_velo ", robot.getShooter().calculatedVelocity());
        telemetryM.debug("state", robot.robotstate);
        telemetryM.debug("x:" + robot.getFollower().getPose().getX());
        telemetryM.debug("y:" + robot.getFollower().getPose().getY());
        telemetryM.debug("heading:" + robot.getFollower().getPose().getHeading());
        telemetryM.addData("at target velocty", robot.shooter.atTargetVelocity());
        telemetryM.addData("target velocity", robot.shooter.targetVelocity);
        telemetryM.addData("balls", robot.balls);

        telemetryM.update(telemetry);

    }
}
