package org.firstinspires.ftc.teamcode;

import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

public class Cam extends SubsystemBase {
    Limelight3A limelight;
    Order order;
    public Cam(HardwareMap hardwareMap){
        limelight = hardwareMap.get(Limelight3A.class,"limelight");
        limelight.setPollRateHz(100);
        limelight.start();
    }
    public void setOrder(Order order1){
        order = order1;
    }
    public double getFiducialAngle(){
        return limelight.getLatestResult().getTx();
    }
    public Limelight3A getLimelight() {
        return limelight;
    }
    public Order getOrder(){
        return order;
    }
    public Pose getBotPose(){
        Pose3D pose = limelight.getLatestResult().getBotpose();
        return new Pose(pose.getPosition().x,pose.getPosition().y,pose.getPosition().z);
    }
    public class getMotif extends CommandBase{
        Cam camera;
        Order order;
        public getMotif(Cam temp){
            camera = temp;
            addRequirements();
        }
        public void initialize(){
            camera.limelight.pipelineSwitch(3);
        }
        public void execute(){
            LLResult result = camera.limelight.getLatestResult();
            for (LLResultTypes.FiducialResult fiducial : result.getFiducialResults())
            {
                if (fiducial.getFiducialId() == 21){
                    order = Order.GPP;
                }
                if (fiducial.getFiducialId() == 22){
                    order = Order.PGP;
                }
                if (fiducial.getFiducialId() == 23){
                    order = Order.PPG;
                }
            }
        }
        public boolean isFinished(){
            return camera.limelight.getLatestResult() != null && order != null;
        }
        public void end(boolean i){
            camera.setOrder(order);
        }
    }
    enum Order{
        GPP,
        PGP,
        PPG
    }
}
