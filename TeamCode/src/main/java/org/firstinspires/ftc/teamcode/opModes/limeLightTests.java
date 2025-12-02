package org.firstinspires.ftc.teamcode.opModes;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

import org.firstinspires.ftc.teamcode.hardware.Bot;


@TeleOp(name="ll test")
@Config
public class limeLightTests extends CommandOpMode {
    Bot robot;
    public static double gain = 1;
    public static double sensetivity = 1.9;

    @Override
    public void initialize() {
        robot = new Bot().init(hardwareMap,new GamepadEx(gamepad1));
        FtcDashboard.getInstance().startCameraStream(robot.getCamera().getLimelight(), 60);
        //robot.aim().schedule();
    }
    public void run(){
//        if (robot.getCamera().getFiducialAngle() > .2 && robot.getCamera().getFiducialAngle() < -.2){
//            robot.getDrive().getFollower().turn(robot.getCamera().getFiducialAngle(),false);
//        }
        telemetry.addData("item location",robot.getCamera().getLimelight().getLatestResult().getColorResults().get(0).getTargetPoseRobotSpace());
        telemetry.addData("pos: ",robot.getCamera().getBotPose());
        telemetry.addData("angle: ",robot.getCamera().getFiducialAngle());
        telemetry.update();
    }
}
