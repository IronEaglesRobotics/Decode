package org.firstinspires.ftc.teamcode.hardware;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.Robot;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

public class Bot extends Robot {
    Cam camera;
    Drive drive;
    Launcher launcher;
    Intake intake;
    public Bot init(HardwareMap hardwareMap, Pose start, String color, GamepadEx gamepad){
        camera = new Cam(hardwareMap,color);
        intake = new Intake(hardwareMap);
        launcher = new Launcher(hardwareMap);
        if (gamepad == null){
            drive = new Drive(hardwareMap, start);
        } else {
            drive = new Drive(hardwareMap,start,gamepad);
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



    public class Aim extends CommandBase{
        Bot bot;
        double sensitivity = 1.5;
        public Aim(Bot robot){
            bot = robot;
            addRequirements(bot.camera, bot.drive);
        }

        @Override
        public void initialize() {
            drive.moveTo(-50,12,15).schedule();
        }
        @Override
        public void execute() {
            if(!drive.follower.isBusy()){
                if (camera.getFiducialAngle() < sensitivity && camera.getFiducialAngle() > -sensitivity){
                    drive.follower.turn(Math.toRadians(camera.getFiducialAngle()*(10-camera.getFiducialAngle())),false);
                }
            }
        }

        @Override
        public boolean isFinished() {
            return camera.getFiducialAngle() < sensitivity && camera.getFiducialAngle() > -sensitivity
                    && !drive.follower.isBusy();
        }

        @Override
        public void end(boolean interrupted) {
            bot.getLauncher().shoot().schedule();
        }
    }
}
