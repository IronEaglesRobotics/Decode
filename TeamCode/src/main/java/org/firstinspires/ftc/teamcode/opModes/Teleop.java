package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

import org.firstinspires.ftc.teamcode.hardware.Bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configurable
@TeleOp(name = "Main Teleop")
public class Teleop extends OpMode {
    Bot robot;
    GamepadEx controller1;
    GamepadEx controller2;

    public Teleop() throws IOException {
    }

    @Override
    public void init() {
        controller1 = new GamepadEx(gamepad1);
        robot = new Bot().init(hardwareMap,controller1);
        controller2 = new GamepadEx(gamepad2);
        robot.getDrive().getFollower().update();
        CommandScheduler.getInstance().reset();
        CommandScheduler.getInstance().registerSubsystem(robot.getDrive());
    }
    @Override
    public void loop(){
        controller1.readButtons();
        controller2.readButtons();
        List<Double> voltages = new ArrayList<>();
        List<Double> current = new ArrayList<>();

            double driveY = controller1.getLeftY();
            double driveX = -controller1.getLeftX();
            double manualTurn = -controller1.getRightX();
            double turnOutput = manualTurn;

            robot.getDrive().getFollower().setTeleOpDrive(
                    driveY,
                    driveX,
                    turnOutput,
                    true
            );
        }
    }