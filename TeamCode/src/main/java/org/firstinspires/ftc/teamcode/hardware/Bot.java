package org.firstinspires.ftc.teamcode.hardware;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.PerpetualCommand;
import com.seattlesolvers.solverslib.command.Robot;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

import org.firstinspires.ftc.teamcode.opModes.Auto;

import java.util.ArrayList;
import java.util.List;

@Configurable
public class Bot extends Robot {
    Cam camera;
    Drive drive;
    Launcher launcher;
    Intake intake;
    Servo lift1;
    Servo lift2;
    static boolean started = false;
    static long time = 0;
    List<LynxModule> allHubs;
    Auto.Alliance team = null;

    public Bot init(HardwareMap hardwareMap, GamepadEx gamepad){
        camera = new Cam(hardwareMap);
        intake = new Intake(hardwareMap);
        launcher = new Launcher(hardwareMap);
        if (gamepad == null){
            drive = new Drive(hardwareMap);
        } else {
            drive = new Drive(hardwareMap,gamepad);
        }
        lift1 = hardwareMap.get(Servo.class,"lift1");
        lift2 = hardwareMap.get(Servo.class,"lift2");
        lift2.setDirection(Servo.Direction.REVERSE);
        allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs){
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
        updateColor();
        return this;
    }
    public Bot init(HardwareMap hardwareMap, GamepadEx gamepad, Auto.Alliance alliance){
        camera = new Cam(hardwareMap);
        intake = new Intake(hardwareMap);
        launcher = new Launcher(hardwareMap);
        if (gamepad == null){
            drive = new Drive(hardwareMap);
        } else {
            drive = new Drive(hardwareMap,gamepad);
        }
        lift1 = hardwareMap.get(Servo.class,"lift1");
        lift2 = hardwareMap.get(Servo.class,"lift2");
        lift2.setDirection(Servo.Direction.REVERSE);
        allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs){
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
        team = alliance;
        return this;
    }
    public List<LynxModule> getAllHubs(){
        return allHubs;
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
        return new Launcher.Loading(launcher);
    }
    public Command Lift(){
        return new InstantCommand(()-> {
            lift1.setPosition(1);
            lift2.setPosition(1);
            }
        );
    }
    public Command Drop(){
        return new InstantCommand(()-> {
            lift1.setPosition(0);
            lift2.setPosition(0);
        }
        );
    }
    public static boolean hasBeen(int millis){
        if (!started){
            time = System.currentTimeMillis();
            started = true;
        }
        boolean done = time + millis < System.currentTimeMillis();
        if (done){
            started = false;
            time = 0;
        }
        return done;
    }
    public void updateColor(){
        if (team != null){
            if (camera.getArea() > .6 ){
                if (Math.abs(camera.getFiducialAngle()) <= 2 && camera.getFiducialAngle() != 0){
                    launcher.light.setPosition(.444);
                }
                else {
                    launcher.light.setPosition(.29);
                }
            }
            else{
                if (Math.abs(camera.getFiducialAngle()) + (3 * (team == Auto.Alliance.Blue ? -1 : 1)) <= 2 && camera.getFiducialAngle() != 0){
                    launcher.light.setPosition(.444);
                }
                else {
                    launcher.light.setPosition(.29);
                }
            }
        }
        else{
            new PerpetualCommand(new SequentialCommandGroup(
                    new InstantCommand(()->{
                        launcher.light.setPosition(launcher.light.getPosition() + .001);
                        if (launcher.light.getPosition() > .788){
                            launcher.light.setPosition(.28);
                        }
                    }),
                    new WaitCommand(100)
            )).schedule();
        }
    }

    @Configurable
    public static class Aim extends CommandBase{
        Bot bot;

        public static double kP = 0.0169;
        public static double kI = 0.002;
        public static double kD = 0.02;

        double headingIntegral = 0;
        double lastHeadingError = 0;
        public Aim(Bot robot){
            bot = robot;
            addRequirements(bot.camera, bot.drive);
        }

        @Override
        public void initialize() {
            bot.getDrive().getFollower().holdPoint(
                    bot.drive.getPose()
            );
        }

        @Override
        public void execute() {
            double tx = bot.getCamera().getFiducialAngle(); // Limelight horizontal offset

            // If Limelight sees a tag:
            if (!Double.isNaN(tx)) {

                // PID compute
                headingIntegral += tx;
                double derivative = tx - lastHeadingError;
                lastHeadingError = tx;

                double pid = (kP * tx) + (kI * headingIntegral) + (kD * derivative);

                // limit
                pid = Math.max(-0.3, Math.min(0.3, pid));

                bot.getDrive().turn(-pid);
            }
        }

        @Override
        public boolean isFinished() {
            return Math.abs(bot.camera.getFiducialAngle()) <= 3;
        }

        @Override
        public void end(boolean interrupted) {
            bot.getDrive().getFollower().breakFollowing();
        }
    }
}
