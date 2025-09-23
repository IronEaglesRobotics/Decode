package org.firstinspires.ftc.teamcode.opModes;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.bylazar.panels.Panels;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.LLFieldMap;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandScheduler;

import org.firstinspires.ftc.teamcode.hardware.Bot;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;

import java.util.ArrayList;
import java.util.List;


@TeleOp(name="ll test")
@Config
public class limeLightTests extends OpMode {
    Bot robot;
    public static double gain = 1;
    public static double sensetivity = 1.9;

    @Override
    public void init() {
        robot = new Bot().init(hardwareMap,new Pose(0,0,0),"red");
        AprilTagLibrary tagLibrary = AprilTagGameDatabase.getCurrentGameTagLibrary();
        List<LLFieldMap.Fiducial> list = new ArrayList<>();
        for (AprilTagMetadata tag:tagLibrary.getAllTags()) {
            List<Double> doubles = new ArrayList<>();
            doubles.add((double)tag.fieldPosition.get(0));
            doubles.add((double)tag.fieldPosition.get(1));
            doubles.add((double)tag.fieldPosition.get(2));
            // doubles.add((double)tag.fieldPosition.get(3));
            list.add(new LLFieldMap.Fiducial(tag.id,tag.tagsize,tag.name,doubles,true));
        }
        LLFieldMap fieldMap = new LLFieldMap(list,"ftc");
        robot.getCamera().getLimelight().uploadFieldmap(fieldMap,null);
        //robot.getCamera().getLimelight().pipelineSwitch(2);
        FtcDashboard.getInstance().startCameraStream(robot.getCamera().getLimelight(), 30);
    }
    @Override
    public void start(){
        robot.getCamera().getMotif().schedule();
    }

    @Override
    public void loop() {
        LLResult result = robot.getCamera().getLimelight().getLatestResult();
        robot.getCamera().getLimelight().updateRobotOrientation(robot.getDrive().getZ());
        //if (result != null && robot.getCamera().getLimelight().getLatestResult().isValid()) {
            robot.getDrive().getFollower().turn(Math.toRadians(robot.getCamera().getFiducialAngle()*sensetivity),false);
            telemetry.addData("angle",robot.getCamera().getFiducialAngle());
        //}
        robot.getDrive().getFollower().update();
        telemetry.addData("motif",robot.getCamera().getOrder());
        telemetry.update();
    }
}
