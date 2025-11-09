package org.firstinspires.ftc.teamcode;

import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.bylazar.telemetry.PanelsTelemetry;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;


@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TeleOp")
public class TeleOP extends OpMode {
    private Robot robot;
    private shotTier tier = shotTier.REST;
    private enum shotTier {
        REST, NEAR, FAR
    }
    GamepadEx controller1;
    public TelemetryManager telemetryM;
    private double shotPower = .63;

    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;


    @Override
    public void init() {
        robot = new Robot().init(hardwareMap);
        robot.getFollower().setPose(new Pose());
        robot.getFollower().update();
        controller1 = new GamepadEx(gamepad1);
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        robot.shooter.setNear(shotPower);
    }

    @Override
    public void start() {
//        robot.getFollower().update();
        robot.getFollower().startTeleopDrive();
    }

    @Override
    public void loop() {
        controller1.readButtons();
        telemetryM.update();

        robot.getFollower().update();
        robot.getFollower().setTeleOpDrive(controller1.getLeftY(), -controller1.getLeftX(), -controller1.getRightX(), true // Robot Centric
        );


        if (controller1.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
            tier = shotTier.FAR;
        } else if (controller1.wasJustPressed((GamepadKeys.Button.RIGHT_BUMPER))){
            tier = shotTier.NEAR;
        } else if (controller1.wasJustPressed((GamepadKeys.Button.Y))){
            tier = shotTier.REST;
        }

        if (controller1.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)) {
            shotPower++;
    } else if (controller1.wasJustPressed((GamepadKeys.Button.DPAD_LEFT))){
            shotPower--;
        }

        switch (tier){
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


        robot.robotMacro(controller1,getRuntime());

        telemetryM.debug("shooter velo: ", robot.getShooter().getVelocity() / 28.0 * 60);
        telemetryM.debug("state", robot.robotstate);

        telemetryM.update(telemetry);
        panelsTelemetry.getFtcTelemetry().update();

    }
}
