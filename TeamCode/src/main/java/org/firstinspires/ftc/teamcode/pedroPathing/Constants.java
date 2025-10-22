package org.firstinspires.ftc.teamcode.pedroPathing;

import com.acmerobotics.dashboard.config.Config;
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
    public static PIDFCoefficients headingCoefficients = new PIDFCoefficients(1,0.01,0.0001,0);
    public static PIDFCoefficients translationalCoefficients = new PIDFCoefficients(0.06,0,0.007,0);
    public static double centripetalScaling = 0;

    public static Follower createFollower(HardwareMap hardwareMap) {
        followerConstants.setMass(20);
        followerConstants.setCoefficientsDrivePIDF(driveCoefficients);
        followerConstants.setCoefficientsHeadingPIDF(headingCoefficients);
        followerConstants.setCoefficientsTranslationalPIDF(translationalCoefficients);
        followerConstants.setCentripetalScaling(centripetalScaling);
        followerConstants.forwardZeroPowerAcceleration(-30.25);
        followerConstants.lateralZeroPowerAcceleration(-46.84);
        followerConstants.setCentripetalScaling(0.0002);
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
            .leftFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .leftRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .xVelocity(63.54)
            .yVelocity(67.17)
            .useBrakeModeInTeleOp(true);
    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(-.355)
            .strafePodX(1.66)
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("pinpoint")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED);
}
