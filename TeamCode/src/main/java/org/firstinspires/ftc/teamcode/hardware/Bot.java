package org.firstinspires.ftc.teamcode.hardware;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.Robot;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

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
