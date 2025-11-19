package org.firstinspires.ftc.teamcode.hardware;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.Robot;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
@Configurable
public class Bot extends Robot {
    Cam camera;
    Drive drive;
    Launcher launcher;
    Intake intake;
    public Bot init(HardwareMap hardwareMap, GamepadEx gamepad){
        camera = new Cam(hardwareMap);
        intake = new Intake(hardwareMap);
        launcher = new Launcher(hardwareMap);
        if (gamepad == null){
            drive = new Drive(hardwareMap);
        } else {
            drive = new Drive(hardwareMap,gamepad);
        }
        return this;
    }

    public Cam getCamera() {
        return camera;
    }

    public Drive getDrive() {
        return drive;
    }

    public Intake getIntake() {
        return intake;
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public Aim aim(){
        return new Aim(this);
    }
    public Command loading(){
        return new Launcher.Loading(launcher,camera.order);
    }

    @Configurable
    public static class Aim extends CommandBase{
        Bot bot;

        double sensitivity = 2;
        double correctionFactor = 0.5;
        Pose holdPoint;

        private boolean turning = false;

        double angleError;
        public static double kP = 1;
        public Aim(Bot robot){
            bot = robot;
            addRequirements(bot.camera, bot.drive);
        }

        @Override
        public void initialize() {
//            bot.getDrive().getFollower().holdPoint(
//                    new Pose(holdPoint.getX(),holdPoint.getY(),bot.camera.getFiducialAngle())
//            );
        }

        @Override
        public void execute() {
            if (!bot.getDrive().getFollower().isTurning()){
                angleError = bot.camera.getFiducialAngle();
                double turnAngle = angleError * kP;
                bot.drive.turn(turnAngle).schedule();
            }
        }

        @Override
        public boolean isFinished() {
            return Math.abs(bot.camera.getFiducialAngle()) <= 5 && !bot.drive.getFollower().isTurning();
        }

        @Override
        public void end(boolean interrupted) {
            bot.getDrive().getFollower().startTeleOpDrive(true);
        }
    }
}
