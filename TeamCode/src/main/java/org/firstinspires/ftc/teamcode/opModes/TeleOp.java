package org.firstinspires.ftc.teamcode.opModes;


import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Bot;
import org.firstinspires.ftc.teamcode.hardware.Drive;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TeleOp")
public class TeleOp extends OpMode {
    Bot robot;

    GamepadEx controller1;
    GamepadEx controller2;



    @Override
    public void init() {
        robot = new Bot().init(hardwareMap, new Pose(), "red", new GamepadEx(this.gamepad1));
        controller1 = new GamepadEx(gamepad1);
        controller2 = new GamepadEx(gamepad1);
    }

    @Override
    public void loop() {
        robot.getDrive().getFollower().update();
        robot.getDrive().setVector();

        controller1.readButtons();
        controller2.readButtons();

//        if (controller2.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0){
//            robot.getLauncher().flywheelOn();
//        } sorry this doesn't work
        controller2.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER) //use it like this
                .whenPressed(robot.getLauncher().flywheelOn(true))
                .whenReleased(robot.getLauncher().flywheelOff());
//        if (controller2.getButton(GamepadKeys.Button.LEFT_BUMPER)){this works but is kinda janky
//            robot.getLauncher().flywheelOn().schedule();
//        }
//        if (controller2.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) == 0){
//            robot.getLauncher().flywheelOff();
//        }
        if (controller2.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0 ) { // plz try it here
            robot.getIntake().start();
        }
        else if (controller2.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) == 0) {
            robot.getIntake().stop();
        }

        if (gamepad1.x) {
            robot.getLauncher().fan();
        }
//        if (gamepad1.y) {
//            robot.getLauncher().Shoot();
//        }
        if (gamepad1.b) {
            robot.getLauncher().Zero();
        }

        telemetry.addData("current: ",robot.getLauncher().spinner.getCurrentPosition());
        telemetry.addData("target", robot.getLauncher().current);
        telemetry.update();
        //  .andThen(new WaitCommand()));
//        controller1.getGamepadButton(GamepadKeys.Button.A)
//                .whenPressed(robot.getDrive().getFollower().turnToDegrees(40),);

    }

    private void getClass(Drive drive) {
    }

    public void setController1(GamepadEx controller1) {
        this.controller1 = controller1;
    }

    public Bot getRobot() {
        return robot;
    }

    @Override
    public void updateTelemetry(Telemetry telemetry) {
        super.updateTelemetry(telemetry);
    }
}




