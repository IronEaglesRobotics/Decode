package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.TwoWheelConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(9)
            .forwardZeroPowerAcceleration(-27.78)
            .lateralZeroPowerAcceleration(-53.28)
            .useSecondaryTranslationalPIDF(true)
            .useSecondaryHeadingPIDF(true)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.15, 0.000006, 0.01, 0.02))
            .secondaryTranslationalPIDFCoefficients(new PIDFCoefficients(0.01,0,.0005,0.03))
            .headingPIDFCoefficients(new PIDFCoefficients(.75, 0, .05, 0.025))
            .secondaryHeadingPIDFCoefficients(new PIDFCoefficients(1,0,0.07,0.015))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(.012,0,.000001,0.06,0.02))
            .centripetalScaling(.0006)
            ;


    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, .98, .6);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)

                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .twoWheelLocalizer(localizerConstants)
                .build();
    }

    public static TwoWheelConstants localizerConstants = new TwoWheelConstants()
            .forwardEncoder_HardwareMapName("rightBack")
            .strafeEncoder_HardwareMapName("leftBack")
            .IMU_HardwareMapName("IMU")
            .forwardPodY(2.656)
            .strafePodX(-2.82)
            .strafeEncoderDirection(Encoder.REVERSE)
            .forwardEncoderDirection(Encoder.FORWARD)
            .forwardTicksToInches(0.002)
            .IMU_Orientation(
                    new RevHubOrientationOnRobot(
                            RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                            RevHubOrientationOnRobot.UsbFacingDirection.UP
                    )
            );

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .xVelocity(79.68)
            .yVelocity(57.8)
            .rightFrontMotorName("rightFront")
            .rightRearMotorName("rightBack")
            .leftRearMotorName("leftBack")
            .leftFrontMotorName("leftFront")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .useBrakeModeInTeleOp(true);


}
