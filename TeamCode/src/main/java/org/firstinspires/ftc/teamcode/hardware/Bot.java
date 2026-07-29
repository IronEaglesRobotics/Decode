package org.firstinspires.ftc.teamcode.hardware;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.Robot;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

import java.util.List;

@Configurable
public class Bot extends Robot {
    Drive drive;
    Intake intake;
    Launcher launcher;
    List<LynxModule> allHubs;

    public Bot init(HardwareMap hardwareMap, GamepadEx gamepad){
        if (gamepad == null){
            drive = new Drive(hardwareMap);
        } else {
            drive = new Drive(hardwareMap,gamepad);
        }
        intake = new Intake(hardwareMap);
        launcher = new Launcher(hardwareMap, drive);
//        lift1 = hardwareMap.get(Servo.class,"lift1");
//        lift2 = hardwareMap.get(Servo.class,"lift2");
//        lift2.setDirection(Servo.Direction.REVERSE);
        allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs){
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
        return this;
    }
    public List<LynxModule> getAllHubs(){
        return allHubs;
    }

    public Drive getDrive() {
        return drive;
    }

    public Intake getIntake() {
        return intake;
    }

    public Launcher getLauncher(){
        return launcher;
    }

    public void resetPos(){
        drive.setPose(new Pose(6.82, 7.34, Math.PI));
    }
}
