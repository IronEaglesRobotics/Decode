package org.firstinspires.ftc.teamcode.opModes;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.hardware.Bot;


@TeleOp(name="ll test")
public class limeLightTests extends OpMode {
    Bot robot;

    @Override
    public void init() {
        robot = new Bot().init(hardwareMap,new Pose(0,0,0),"red");
    }

    @Override
    public void loop() {
        LLResult result = robot.getCamera().getLimelight().getLatestResult();
        robot.getCamera().getLimelight().updateRobotOrientation(robot.getDrive().getZ());
        if (result != null && robot.getCamera().getLimelight().getLatestResult().isValid()) {
            Pose pose = robot.getCamera().getBotPose();
            if (pose != null) {
                double x = pose.getX();
                double y = pose.getY();
                telemetry.addData("MT2 Location:", "(" + x + ", " + y + ")");
            }
        }
    }
}
