package org.firstinspires.ftc.teamcode.opModes;
import com.acmerobotics.dashboard.FtcDashboard;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.Bot;


@TeleOp(name="ll test")
public class limeLightTests extends OpMode {
    Bot robot;

    @Override
    public void init() {
        robot = new Bot().init(hardwareMap,new Pose(0,0,0),"red");
        robot.getCamera().getLimelight().pipelineSwitch(2);
        FtcDashboard.getInstance().startCameraStream(robot.getCamera().getLimelight(), 30);
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
                telemetry.addData("tx: ", robot.getCamera().getFiducialAngle());
            }
        }
    }
}
