package org.firstinspires.ftc.teamcode.hardware;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.Robot;

public class Bot extends Robot {
    Cam camera;
    Drive drive;
    public Bot init(HardwareMap hardwareMap, Pose start, String color){
        camera = new Cam(hardwareMap,color);
        drive = new Drive(hardwareMap, start);
        return this;
    }

    public Cam getCamera() {
        return camera;
    }

    public Drive getDrive() {
        return drive;
    }

    public Aim aim(){
        return new Aim(this);
    }

    public class Aim extends CommandBase{
        Bot bot;
        double sensitivity = .3;
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
                    drive.follower.turn(Math.toRadians(camera.getFiducialAngle()),false);
                }
            }
        }

        @Override
        public boolean isFinished() {
            return camera.getFiducialAngle() < sensitivity && camera.getFiducialAngle() > -sensitivity
                    && !drive.follower.isBusy();
        }
    }
}
