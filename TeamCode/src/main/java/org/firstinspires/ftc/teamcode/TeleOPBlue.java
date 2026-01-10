package org.firstinspires.ftc.teamcode;

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

//    @Override
//    public void init_loop(){
//    }

    @Override
    public void start() {
        robot.getFollower().startTeleopDrive();

    }

    @Override
    public void loop() {
        controller1.readButtons();
        telemetryM.update();

        if (controller1.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
            far = !far;
        }

        if (far) {
            robot.getShooter().farShot();
        } else {
            robot.getShooter().nearShot();
        }

        if (controller1.isDown(GamepadKeys.Button.DPAD_DOWN)) {
            robot.getIntake().open();
            robot.getIntake().transfer();
        } else {
            robot.getIntake().close();
            robot.getIntake().transfer();
        }


        robot.getFollower().update();
        if (!automatedDrive) {
            robot.getFollower().setTeleOpDrive(
                    controller1.getLeftY(),
                    -controller1.getLeftX(),
                    -controller1.getRightX(), true // Robot Centric
            );
        }

        if (controller1.isDown(GamepadKeys.Button.RIGHT_BUMPER)) {
            robot.getTurret().calibrate();
            if (robot.getTurret().turret.getCurrent(CurrentUnit.AMPS) > 3) {
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
        }else {
            if (robot.getFollower().getHeading() < Math.toRadians(63) && robot.getFollower().getHeading() > Math.toRadians(-165)) {
//            if (robot.getTurret().getTicks() > 0 && robot.getTurret().getTicks() < 918){
                robot.getTurret().aim(robot.getTurret().calculateAngle(aimPose.getX(), aimPose.getY(), robot.getFollower().getPose().getX(), robot.getFollower().getPose().getY()), (robot.getFollower().getHeading()));
            } else {
                robot.getTurret().off();
            }
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
        telemetryM.debug("target:" + robot.getTurret().calculateAngle(aimPose.getX(), aimPose.getY(), robot.getFollower().getPose().getX(), robot.getFollower().getPose().getY()), (robot.getFollower().getHeading()));
        telemetryM.update(telemetry);

    }
}
