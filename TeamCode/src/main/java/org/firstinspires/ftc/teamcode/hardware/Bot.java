package org.firstinspires.ftc.teamcode.hardware;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
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
    public static boolean hasBeen(int millis){
        if (!started){
            time = System.currentTimeMillis();
            started = true;
        }
        boolean done = started && time + millis < System.currentTimeMillis();
        if (done){
            started = false;
            time = 0;
        }
        return done;
    }

    @Configurable
    public static class Aim extends CommandBase{
        Bot bot;

        private boolean turning = false;

        double angleError;
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
                double error = tx;
                headingIntegral += error;
                double derivative = error - lastHeadingError;
                lastHeadingError = error;

                double pid = (kP * error) + (kI * headingIntegral) + (kD * derivative);

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
