package org.firstinspires.ftc.teamcode.opModes;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.hardware.Bot;

@TeleOp(name = "spinnerTest")
public class spinnerTest extends OpMode {
    Bot robot;
    GamepadEx controller1;
    @Override
    public void init() {
       robot = new Bot().init(hardwareMap,new Pose(0,0),"blue",null);
       controller1 = new GamepadEx(gamepad1);
    }

    @Override
    public void loop() {
        int[] locations = new int[6];
        final int[] index = {0};
        if(gamepad1.dpad_up) {
            robot.getLauncher().spinner.setPower(-.2);
        }
        else if(gamepad1.dpad_down){
            robot.getLauncher().spinner.setPower(.2);
        }
        else {
            robot.getLauncher().spinner.setPower(0);
        }
        telemetry.addData("spinner",robot.getLauncher().spinner.getCurrentPosition());
        controller1.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(()->{
                    locations[index[0]] = robot.getLauncher().spinner.getCurrentPosition();
                    index[0]++;}
                );
        CommandScheduler.getInstance().run();
        telemetry.addData("saved locations",locations);

    }
}
