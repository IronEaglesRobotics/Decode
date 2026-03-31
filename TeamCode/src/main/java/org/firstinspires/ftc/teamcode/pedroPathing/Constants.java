package org.firstinspires.ftc.teamcode.pedroPathing;

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
    public static FollowerConstants followerConstants = new FollowerConstants()
//            .forwardZeroPowerAcceleration(-33)
//            .lateralZeroPowerAcceleration(-58)
            .useSecondaryTranslationalPIDF(true)
            .useSecondaryHeadingPIDF(true)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.075, 0, 0.0068, 0.03))
            .secondaryTranslationalPIDFCoefficients(new PIDFCoefficients(0.15,0,.02,0.03))
            .headingPIDFCoefficients(new PIDFCoefficients(.7, 0, 0.02, 0.0325))
            .secondaryHeadingPIDFCoefficients(new PIDFCoefficients(1.2,0,0.03,0.02))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.006,0,0.0001,0.6,0.06));
//            .centripetalScaling(.0008);
    public static PathConstraints pathConstraints = new PathConstraints(0.99,.1,.1,.007,100,1, 10, 1);
    public static FilteredPIDFCoefficients driveCoefficients = new FilteredPIDFCoefficients(0.003,0,0.000002,0,0.09);
    public static PIDFCoefficients headingCoefficients = new PIDFCoefficients(.5,0.005,0.001,0.03);
    public static PIDFCoefficients translationalCoefficients = new PIDFCoefficients(0.03,0.0001,0.0001,0.03);
    public static double centripetalScaling = 0;

    public static Follower createFollower(HardwareMap hardwareMap) {
        followerConstants.setMass(15.4221);
//        followerConstants.setCoefficientsDrivePIDF(driveCoefficients);
//        followerConstants.setCoefficientsHeadingPIDF(headingCoefficients);
//        followerConstants.setCoefficientsTranslationalPIDF(translationalCoefficients);
        followerConstants.forwardZeroPowerAcceleration(-54.45);
        followerConstants.lateralZeroPowerAcceleration(-91.61);
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
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(51.79)
            .yVelocity(51.05)
            .useBrakeModeInTeleOp(true);
    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(14.21/25.4)
            .strafePodX(26.5/25.4)
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("pinpoint")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED);
}
