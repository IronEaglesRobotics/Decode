package org.firstinspires.ftc.teamcode.hardware;

import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.ArrayList;
import java.util.List;

public class Cam extends SubsystemBase {
    Limelight3A limelight;
    int order;
    String teamColor;
    Pose lastPose;
    List<Ball> foundBalls = new ArrayList<>();
    public Cam(HardwareMap hardwareMap, String team){
        limelight = hardwareMap.get(Limelight3A.class,"limelight");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(1);
        limelight.start();
        teamColor = team;
    }
    public void setOrder(int order1){
        order = order1;
    }
    public double getFiducialAngle(){
//        if (limelight.getLatestResult().isValid() && order != null)
//        {
            return limelight.getLatestResult().getTx();
//        }
//        return 99.9;
    }
    public Limelight3A getLimelight() {
        return limelight;
    }
    public int getOrder() {
        return order;
    }
    public int getTeam(){
        return teamColor.equalsIgnoreCase("red") ? 1:2;
    }
    public Pose getBotPose(){
        Pose3D pose = limelight.getLatestResult().getBotpose();
        return new Pose(pose.getPosition().x,pose.getPosition().y,pose.getPosition().z);
    }
    public void scanFor(int millies,Pose currentPose){
        long start = System.currentTimeMillis();
        limelight.pipelineSwitch(4);
        while (start + millies > System.currentTimeMillis()){
            for (LLResultTypes.ColorResult ball : limelight.getLatestResult().getColorResults()) {
                foundBalls.add(new Ball("green",ball.getCameraPoseTargetSpace()));
            }
        }
    }
    public getMotif getMotif(){
        return new getMotif(this);
    }
    public static class getMotif extends CommandBase{
        Cam camera;
        int order = 0;
        public getMotif(Cam temp){
            camera = temp;
            addRequirements(camera);
        }
        public void initialize(){
            camera.limelight.pipelineSwitch(3);
        }
        public void execute(){
            LLResult result = camera.limelight.getLatestResult();
            for (LLResultTypes.FiducialResult fiducial : result.getFiducialResults())
            {
                if (fiducial.getFiducialId() == 21){
                    order = 1;
                }
                if (fiducial.getFiducialId() == 22){
                    order = 2;
                }
                if (fiducial.getFiducialId() == 23){
                    order = 3;
                }
            }
        }
        public boolean isFinished(){
            return camera.limelight.getLatestResult() != null && order != 0;
        }
        public void end(boolean i){
            camera.setOrder(order);
            camera.limelight.pipelineSwitch(camera.getTeam());
        }
    }
    class Ball{
        String color;
        Pose pose;
        public Ball(String color, Pose3D pose3D){

        }
    }
}
