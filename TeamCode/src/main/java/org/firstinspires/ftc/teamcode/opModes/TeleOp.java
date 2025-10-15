package org.firstinspires.ftc.teamcode.opModes;


import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.Subsystem;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Bot;
import org.firstinspires.ftc.teamcode.hardware.Drive;

public class TeleOp extends OpMode {
    Bot robot;

    GamepadEx controller1;


    @Override
    public void init() {
        robot = new Bot().init(hardwareMap, new Pose(), "red");
    }

    @Override
    public void loop() {
        robot.getDrive().setVector(controller1);
        controller1.readButtons();

        controller1.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(robot.aim());
        controller1.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(robot.getDrive().moveTo(2,5,67));
        controller1.getGamepadButton(GamepadKeys.Button.X)
                .whenPressed(robot.getDrive().moveTo(67,67,67));
        controller1.getGamepadButton(GamepadKeys.Button.Y)
                .whenHeld(robot.getIntake().start())
                .whenReleased(robot.getIntake().stop());
        








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




