package org.firstinspires.ftc.teamcode.opModes;

import static java.lang.Thread.sleep;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.hardware.Bot;

@Autonomous(name =  "Auto")
public class Auto extends OpMode{

    private String color;
    private boolean isFar;
    private int lines;
    Bot robot;
    GamepadEx controller; // plz DO NOT feed into Bot
    int delay;
    int state = 0;
    Command farshoot;
    Command closeshoot;
    Command togate;
    Command loadzonepick;
    Command pick3;
    Command pick2;
    Command pick1;
    Command humanplayzone;
    private void makeAuto(int side) {
        farshoot = robot.getDrive().moveTo(53 * side, -13, 160 * side);
        closeshoot = robot.getDrive().moveTo(0, 0, 160 * side);
        togate = robot.getDrive().moveTo(21 * side, -43, 90 * side);
        loadzonepick = robot.getDrive().moveTo(55 * side, -55, -90 * side);
        pick3 = new SequentialCommandGroup(
                robot.getDrive().moveTo(-15 * side, -25, 90 * side),
                new SequentialCommandGroup(
                        robot.getIntake().start(),
                        robot.getDrive().moveToWithSpeed(-15 * side, -44, 90 * side, .3)
                ),
                new WaitCommand(200),
                robot.getIntake().stop()

        );
        pick2 = new SequentialCommandGroup(
                robot.getDrive().moveTo(9 * side, -25, 90 * side),
                new SequentialCommandGroup(
                        robot.getIntake().start(),
                        robot.getDrive().moveToWithSpeed(9 * side, -44, 90 * side, .3)
                ),
                new WaitCommand(200),
                robot.getIntake().stop()

        );
        pick1 = new SequentialCommandGroup(
                robot.getDrive().moveTo(31 * side, -26, 90 * side),
                new SequentialCommandGroup(
                        robot.getIntake().start(),
                        robot.getDrive().moveToWithSpeed(31 * side, -47, 90 * side, .3)
                ),
                new WaitCommand(200),
                robot.getIntake().stop()
        );
        humanplayzone = robot.getDrive().moveToWithSpeed(55 * side, -55, -100 * side, .7);
    }
    public void run(){
        switch (state){
            case -2:
                state = lines;
                lines-=1;
            case -1:
                break;
            case 0:
                new SequentialCommandGroup(
                        isFar ? farshoot:closeshoot,
                        robot.aim()
                )
                        .whenFinished(()->state=-2)
                        .schedule();
                state = -1;
                break;
            case 1:
                state = -1;
                break;
            case 2:
                pick1
                        .whenFinished(()->state = 0)
                        .schedule();
                break;
            case 3:
                humanplayzone
                        .whenFinished(()->state = 0)
                        .schedule();
                break;
            case 4:
                new SequentialCommandGroup(
                        pick2,
                        togate
                )
                        .whenFinished(()->state = 0)
                        .schedule();
                state = -1;
                break;
        }
    }


    @Override
    public void init() {
        robot = new Bot().init(hardwareMap,isFar ? new Pose(60,-20, 180) : new Pose(12,12, 180),color,null);
        controller = new GamepadEx(gamepad1);
    }
    @Override
    public void init_loop(){
        controller.readButtons();
        if(controller.wasJustPressed(GamepadKeys.Button.A)){
            lines = 4;
        }
        if(controller.wasJustPressed(GamepadKeys.Button.B)){
            lines = 3;
        }
        if(controller.wasJustPressed(GamepadKeys.Button.X)){
            lines = 2;
        }
        if(controller.wasJustPressed(GamepadKeys.Button.Y)){
            lines = 1;
        }
        if(controller.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)){
            color = "red";
        }
        if(controller.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)){
            color = "blue";
        }
        if(controller.wasJustPressed(GamepadKeys.Button.RIGHT_STICK_BUTTON)){
            isFar = true;
        }
        if(controller.wasJustPressed(GamepadKeys.Button.LEFT_STICK_BUTTON)){
            isFar = false;
        }
        if(controller.wasJustPressed(GamepadKeys.Button.DPAD_UP)){
            delay += 100;
        }
        if(controller.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)){
            delay -= 100;
        }
        telemetry.addData("lines", lines-1);
        telemetry.addData("if far", isFar);
        telemetry.addData("color", color);
        telemetry.addData("delay",((double)delay)/1000);
        telemetry.addLine("Lines: A:4 B:3 X:2 Y:1");
        telemetry.addLine("Color: Left Bumper: red Right Bumper: blue");
        telemetry.addLine("Start: Right Stick: far Left Stick: close");
        telemetry.addLine("Dpad: delay ^v");
        telemetry.update();
    }
    @Override
    public void start(){
        makeAuto(color.equalsIgnoreCase("blue")?1:-1);
    }

    @Override
    public void loop() {
        if (state != lines){
            run();
        }
       CommandScheduler.getInstance().run();
    }

}
