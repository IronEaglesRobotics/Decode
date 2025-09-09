package org.firstinspires.ftc.teamcode;
import com.pedropathing.follower.Follower;
import com.qualcomm.hardware.limelightvision.LLFieldMap;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;


public class limeLightTests extends OpMode {
    LLFieldMap field = new LLFieldMap();
    Limelight3A limelight;
    Follower follower;

    @Override
    public void init() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.start();
        follower = Constants.createFollower(hardwareMap);
    }

    @Override
    public void loop() {
        LLResult result = limelight.getLatestResult();
        limelight.updateRobotOrientation(follower.getPose().getHeading());
        if (result != null && limelight.getLatestResult().isValid()) {
            Pose3D botpose_mt2 = result.getBotpose_MT2();
            if (botpose_mt2 != null) {
                double x = botpose_mt2.getPosition().x;
                double y = botpose_mt2.getPosition().y;
                telemetry.addData("MT2 Location:", "(" + x + ", " + y + ")");
            }
        }

        assert result != null;
        if (result.getFiducialResults().get(0).getFiducialId() == field.getFiducials().get(0).getId()){
            telemetry.addData("fiducial: ",result.getFiducialResults().get(0).getFiducialId());
        }
    }
}
