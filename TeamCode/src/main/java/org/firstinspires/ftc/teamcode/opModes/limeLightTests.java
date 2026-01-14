package org.firstinspires.ftc.teamcode.opModes;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

import org.firstinspires.ftc.teamcode.hardware.Bot;


@TeleOp(name="ll test",group = "Tests")
public class limeLightTests extends CommandOpMode {
    Bot robot;
    public static double gain = 1;
    public static double sensetivity = 1.9;

    @Override
    public void initialize() {
        robot = new Bot().init(hardwareMap,new GamepadEx(gamepad1));
    }
    public void run(){
        telemetry.addData("item location",robot.getCamera().getLimelight().getLatestResult().getColorResults().get(0).getTargetPoseRobotSpace());
        telemetry.addData("pos: ",robot.getCamera().getBotPose());
        telemetry.addData("angle: ",robot.getCamera().getFiducialAngle());
        telemetry.update();
    }
}
