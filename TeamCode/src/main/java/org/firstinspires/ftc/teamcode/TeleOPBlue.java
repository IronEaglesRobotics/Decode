package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.RobotNew.Shooter.getInterpolatedValue;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.HeadingInterpolator;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.pedropathing.paths.PathChain;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.util.function.Supplier;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TeleOp Blue")
public class TeleOPBlue extends OpMode {
    private RobotNew robot;
    private final Config config = Config.blueTeleOp;

    GamepadEx controller1;
    public TelemetryManager telemetryM;

    private Supplier<PathChain> pathChain;
    private Supplier<PathChain> pathChainFar;
    private boolean automatedDrive;


    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    private boolean far = true;

    private final Pose shootPoseFar = this.config.getShootPoseFar();
    private final Pose resetPose = this.config.getResetPose();
    private final Pose aimPose = this.config.getGoalPose();

    private double power = .3;
    private double hood = .7;
    private double distance = 50;
    private double offset = 0;
    private double dOffset = 0;
    private double error = 0;


    @Override
    public void init() {
        robot = new RobotNew().init(hardwareMap);
        robot.getFollower().update();
        controller1 = new GamepadEx(gamepad1);
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();

        pathChainFar = () -> robot.getFollower().pathBuilder() //Lazy Curve Generation
                .addPath(new Path(new BezierLine(robot.getFollower()::getPose, shootPoseFar)))
                .setHeadingInterpolation(HeadingInterpolator.facingPoint(shootPoseFar))
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
        error = robot.getTurret().getLimeError(true);

        distance = robot.getGoalDistance(robot.getFollower().getPose().getX(), robot.getFollower().getPose().getY(), aimPose.getX(), aimPose.getY());

        if (controller1.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
            far = !far;
        }

//        power = robot.getShooter().calculateShooterPower(distance + dOffset );
//        hood = robot.getShooter().calculateHoodPose(distance +dOffset);

        RobotNew.Shooter.Metrics result = getInterpolatedValue(distance + dOffset);
        power = result.y1;
        hood = result.z;

        robot.getShooter().setShot(power, hood);

        if (controller1.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER) && (error < -2 || error > 2) && distance > 120) {
            offset -= error;
        }

        if (controller1.isDown(GamepadKeys.Button.DPAD_DOWN)) {
            robot.getIntake().open();
            robot.getIntake().transfer(distance);

//        }
        } else {
            robot.getIntake().close();
            robot.getIntake().intake();
        }

        robot.getLights().blinker(getRuntime());

        robot.getLights().shooterCheck(robot.getShooter().atTargetVelocity());

        robot.getFollower().update();
        if (!automatedDrive) {
            robot.getFollower().setTeleOpDrive(
                    -controller1.getLeftY(),
                    controller1.getLeftX(),
                    -controller1.getRightX() * .6, true // Robot Centric
            );
        }
//
//        if (!automatedDrive) {
//            robot.getFollower().setTeleOpDrive(
//                    controller1.getLeftY(),
//                    -controller1.getLeftX(),
//                    -controller1.getRightX()*.6, true // Robot Centric
//            );
//        }

        if (controller1.isDown(GamepadKeys.Button.RIGHT_BUMPER)) {
            robot.getTurret().calibrate();
            if (robot.getTurret().turret.getCurrent(CurrentUnit.AMPS) > 2) {
                robot.getTurret().turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                robot.getTurret().turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                robot.getFollower().setPose(new Pose(resetPose.getX(), resetPose.getY(), Math.toRadians(90)));
            }
        } else if (controller1.isDown(GamepadKeys.Button.LEFT_STICK_BUTTON)) {
            robot.getTurret().calibrate();
            if (robot.getTurret().turret.getCurrent(CurrentUnit.AMPS) > 3) {
                robot.getTurret().turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                robot.getTurret().turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                robot.getFollower().setPose(new Pose(robot.getFollower().getPose().getX(), robot.getFollower().getPose().getY(), Math.toRadians(90)));
            }
        } else {
            robot.getTurret().aim(offset + robot.getTurret().calculateAngle(aimPose.getX(), aimPose.getY(), robot.getFollower().getPose().getX(), robot.getFollower().getPose().getY()), Math.toDegrees(robot.getFollower().getHeading()));
        }

        if (controller1.wasJustPressed(GamepadKeys.Button.DPAD_LEFT)) {
            offset++;
        } else if (controller1.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)) {
            offset--;
        }

        if (controller1.wasJustPressed(GamepadKeys.Button.SQUARE)) {
            dOffset--;
        } else if (controller1.wasJustPressed(GamepadKeys.Button.CIRCLE)) {
            dOffset++;
        }

        //Auto Drive turning
        if (controller1.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0.5) {
            robot.getFollower().followPath(pathChainFar.get(), true);
            automatedDrive = true;
        }

//        Stop automated following if the follower is done
        if (automatedDrive && (controller1.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > .5 || !robot.getFollower().isBusy())) {
            robot.getFollower().startTeleopDrive();
            automatedDrive = false;
        }

        telemetryM.addData("shooter_velo ", robot.getShooter().calculatedVelocity());
        telemetryM.debug("x:" + robot.getFollower().getPose().getX());
        telemetryM.debug("y:" + robot.getFollower().getPose().getY());
        telemetryM.debug("heading:" + Math.toDegrees(robot.getFollower().getPose().getHeading()));
        telemetryM.addData("at target velocty", robot.shooter.atTargetVelocity());
        telemetryM.addData("amps", robot.turret.getCurrent());
        telemetryM.debug("target:" + robot.getTurret().calculateAngle(aimPose.getX(), aimPose.getY(), robot.getFollower().getPose().getX(), robot.getFollower().getPose().getY()), (robot.getFollower().getHeading()));
        telemetryM.addData("distance from Goal", distance);
        telemetryM.addData("shooter Power", power);
        telemetryM.addData("hood", hood);
        telemetryM.addData("turret current",robot.getTurret().getCurrent() );
        telemetryM.addData("lime x", error);
        telemetryM.addData("intake current", robot.intake.getCurrent());
        telemetryM.update(telemetry);
    }
}
