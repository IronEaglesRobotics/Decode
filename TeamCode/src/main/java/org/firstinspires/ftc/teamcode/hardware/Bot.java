package org.firstinspires.ftc.teamcode.hardware;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.Robot;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

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


    public class Aim extends CommandBase{
        Bot bot;
        double sensitivity = 2;
        double correctionFactor = 0.5;
        Pose holdPoint;

        private boolean turning = false;
        public Aim(Bot robot){
            bot = robot;
            addRequirements(bot.camera, bot.drive);
            holdPoint = robot.drive.getPose();
        }

        @Override
        public void initialize() {
            bot.getDrive().getFollower().holdPoint(
                    new Pose(holdPoint.getX(),holdPoint.getY(),bot.camera.getFiducialAngle())
            );
        }

        @Override
        public void execute() {
            bot.drive.follower.setPose(
                    new Pose(bot.drive.getX(),bot.drive.getY(),bot.camera.getBotPose().getHeading())
            );
        }

        @Override
        public boolean isFinished() {
            double angleError = bot.camera.getFiducialAngle();
            return Math.abs(angleError) <= sensitivity && !bot.drive.follower.isTurning();
        }

        @Override
        public void end(boolean interrupted) {
            bot.getDrive().getFollower().startTeleopDrive(true);
            bot.getLauncher().fire().schedule();
        }
    }
}
