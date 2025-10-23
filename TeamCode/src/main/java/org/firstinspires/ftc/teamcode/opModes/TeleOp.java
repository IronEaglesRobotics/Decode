package org.firstinspires.ftc.teamcode.opModes;


import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.Subsystem;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

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
    }

    @Override
    public void loop() {
        robot.getDrive().getFollower().update();
        robot.getDrive().setVector();

        controller1.readButtons();
        controller2.readButtons();

        if (controller2.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0){
            robot.getLauncher().flywheelOn();
        }
        else if (controller2.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) == 0){
            robot.getLauncher().flywheelOff();
        }
        if (controller2.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0 ) {
            robot.getIntake().start();
        }
        else if (controller2.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) == 0) {
            robot.getIntake().stop();
        }

        if (gamepad1.x) {
            robot.getLauncher().fan();
        }




        








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




