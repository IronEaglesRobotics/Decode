package org.firstinspires.ftc.teamcode.hardware;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.command.WaitUntilCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;
import com.seattlesolvers.solverslib.pedroCommand.HoldPointCommand;
import com.seattlesolvers.solverslib.pedroCommand.*;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.function.BooleanSupplier;

public class Drive extends SubsystemBase {
    private Telemetry telemetry;
    Follower follower;
    GamepadEx controller;

    public Drive(HardwareMap hardwareMap) {
        follower = Constants.createFollower(hardwareMap);
        follower.update();
    }

    public Drive(HardwareMap hardwareMap, GamepadEx gamepadEx) {
        follower = Constants.createFollower(hardwareMap);
        follower.update();
        controller = gamepadEx;
        follower.startTeleopDrive(true);
    }

    public Drive(HardwareMap hardwareMap, Telemetry telemetry) {
        this(hardwareMap);
        this.telemetry = telemetry;
    }

    public FollowPathCommand pathCommand(PathChain paths) {
        return new FollowPathCommand(follower, paths);
    }

    public RealFollowPathCommand pathCommand(PathChain paths, double speed) {
        return new RealFollowPathCommand(follower, paths, speed);
    }

    public FollowPathCommand moveTo(Pose pose) {
        return pathCommand(new PathChain(new Path(new BezierLine(follower.getPose(), pose))));
    }

    public FollowPathCommand moveTo(double x, double y, double z) {
        Path path = new Path(new BezierLine(follower.getPose(), new Pose(x, y, Math.toRadians(z))));
        path.setConstantHeadingInterpolation(Math.toRadians(z));
        return new FollowPathCommand(follower, path);
    }

    public FollowPathCommand moveToWithSpeed(double x, double y, double z, double speed) {
        Path path = new Path(new BezierLine(follower.getPose(), new Pose(x, y, Math.toRadians(z))));
        path.setConstantHeadingInterpolation(Math.toRadians(z));
        return new FollowPathCommand(follower, path, speed);
    }

    public HoldPointCommand hold(Pose pose) {
        return new HoldPointCommand(follower, pose, true);
    }

    public HoldPointCommand hold(double x, double y, double z) {
        return new HoldPointCommand(follower, new Pose(x, y, z), true);
    }

    public Command turn(double degrees) {
        return new TurnCommand(follower, Math.toRadians(degrees), false);
    }

    public TurnToCommand turnTo(double degrees) {
        return new TurnToCommand(follower, Math.toRadians(degrees));
    }

    public Command cancelablePath(PathChain chain) {
        return new CancelablePathCommand(follower, chain);
    }

    public Command cancelablePath(Path chain) {
        return new CancelablePathCommand(follower, chain);
    }

    public Command cancelablePath(PathChain chain, BooleanSupplier supplier) {
        return new CancelablePathCommand(follower, chain)
                .andThen(new WaitUntilCommand(supplier));
    }

    public Command cancelablePath(Path chain, BooleanSupplier supplier) {
        return new CancelablePathCommand(follower, chain)
                .andThen(new WaitUntilCommand(supplier));
    }


    public Follower getFollower() {
        return follower;
    }

    public void setVector() {
        follower.setTeleOpDrive(-controller.getLeftY(), controller.getLeftX(), controller.getRightX(), true);
    }

    public void setVector(GamepadEx controller1) {
        follower.setTeleOpDrive(controller1.getLeftY(), controller1.getLeftX(), controller1.getRightX(), true);
    }

    public Pose getPose() {
        return follower.getPose();
    }

    public double getX() {
        return follower.getPose().getX();
    }

    public double getY() {
        return follower.getPose().getY();
    }

    public double getZ() {
        return follower.getPose().getHeading();
    }

    @Override
    public void periodic() {
        follower.update();
    }

    public static class CancelablePathCommand extends FollowPathCommand {
        Follower follower;

        public CancelablePathCommand(Follower follower, PathChain pathChain) {
            super(follower, pathChain);
            this.follower = follower;
        }

        public CancelablePathCommand(Follower follower, Path path) {
            super(follower, path);
            this.follower = follower;
        }

        public CancelablePathCommand(Follower follower, Path path, double maxPower) {
            super(follower, path, maxPower);
            this.follower = follower;
        }

        public CancelablePathCommand(Follower follower, PathChain pathChain, double maxPower) {
            super(follower, pathChain, maxPower);
            this.follower = follower;
        }

        @Override
        public void end(boolean interrupted) {
            follower.breakFollowing();
        }
    }

    public class RealFollowPathCommand extends CommandBase {
        private final Follower follower;
        private final PathChain pathChain;
        private final boolean holdEnd;
        private double maxPower = 1.0;

        public RealFollowPathCommand(Follower follower, PathChain pathChain) {
            this(follower, pathChain, true);
        }

        public RealFollowPathCommand(Follower follower, PathChain pathChain, boolean holdEnd) {
            this(follower, pathChain, holdEnd, 1.0);
        }

        public RealFollowPathCommand(Follower follower, PathChain pathChain, double maxPower) {
            this(follower, pathChain, true, maxPower);
        }

        public RealFollowPathCommand(Follower follower, PathChain pathChain, boolean holdEnd, double maxPower) {
            this.follower = follower;
            this.pathChain = pathChain;
            this.holdEnd = holdEnd;
            this.maxPower = maxPower;
        }

        public RealFollowPathCommand(Follower follower, Path pathChain) {
            this(follower, pathChain, true);
        }

        public RealFollowPathCommand(Follower follower, Path pathChain, boolean holdEnd) {
            this(follower, pathChain, holdEnd, 1.0);
        }

        public RealFollowPathCommand(Follower follower, Path pathChain, double maxPower) {
            this(follower, pathChain, true, maxPower);
        }

        public RealFollowPathCommand(Follower follower, Path pathChain, boolean holdEnd, double maxPower) {
            this(follower, new PathChain(pathChain), holdEnd, maxPower);
        }

        /**
         * Sets Global Maximum Power for Follower, and overwrites maxPower in constructor
         *
         * @param globalMaxPower The new globalMaxPower
         * @return This command for compatibility in command groups
         */
        public RealFollowPathCommand setGlobalMaxPower(double globalMaxPower) {
            follower.setMaxPower(globalMaxPower);
            maxPower = globalMaxPower;
            return this;
        }

        @Override
        public void initialize() {
            telemetry.addData("maxPower", this.maxPower);
            if (maxPower != 1.0) {
                follower.followPath(pathChain, maxPower, holdEnd);
            }
            follower.followPath(pathChain, holdEnd);
        }

        @Override
        public void execute() {
            telemetry.addData("maxPower", this.maxPower);
            follower.drivetrain.setMaxPowerScaling(maxPower);
        }

        @Override
        public boolean isFinished() {
            return !follower.isBusy();
        }
    }

}

