package org.firstinspires.ftc.teamcode.pedroPathing;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants();
    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);
    public static FilteredPIDFCoefficients driveCoefficients = new FilteredPIDFCoefficients(0.012,0,0.00001,0,0);
    public static PIDFCoefficients headingCoefficients = new PIDFCoefficients(4,0.8,0.0001,0);
    public static PIDFCoefficients translationalCoefficients = new PIDFCoefficients(0.08,0,0.007,0);
    public static double centripetalScaling = 0;

    public static Follower createFollower(HardwareMap hardwareMap) {
        followerConstants.setMass(20);
        followerConstants.setCoefficientsDrivePIDF(driveCoefficients);
        followerConstants.setCoefficientsHeadingPIDF(headingCoefficients);
        followerConstants.setCoefficientsTranslationalPIDF(translationalCoefficients);
        followerConstants.setCentripetalScaling(centripetalScaling);
        followerConstants.forwardZeroPowerAcceleration(-62.20);
        followerConstants.lateralZeroPowerAcceleration(-93.88);
        followerConstants.setCentripetalScaling(0.0002);
//        followerConstants.setTurnHeadingErrorThreshold(.001);
        return new FollowerBuilder(followerConstants,hardwareMap)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(localizerConstants)
                .build();
    }
    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName("rf")
            .rightRearMotorName("rr")
            .leftRearMotorName("lr")
            .leftFrontMotorName("lf")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(50.26)
            .yVelocity(51.89)
            .useBrakeModeInTeleOp(true);
    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(1.66)
            .strafePodX(-.355)
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("pinpoint")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED);
}
