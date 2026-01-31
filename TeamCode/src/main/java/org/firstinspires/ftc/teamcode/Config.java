package org.firstinspires.ftc.teamcode;

import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@Disabled
public class Config {
    private String team = null;
    private Pose startPose; // Start Pose of our robot.
    private Pose scorePose; // Scoring Pose of our robot. It is facing the goal at a 135 degree angle.
    private Pose pickup1Pose; // Highest (First Set) of Artifacts from the Spike Mark.
    private Pose pickup1Control; // Highest (First Set) of Artifacts from the Spike Mark.
    private Pose openGate;
    private Pose gateControl;
    private Pose pickup2Transition; // Middle (Second Set) of Artifacts from the Spike Mark.
    private Pose pickup2Pose; // Lowest (Third Set) of Artifacts from the Spike Mark.
    private Pose pickup2Control; // Lowest (Third Set) of Artifacts from the Spike Mark.
    private Pose pickup2Control2; // Lowest (Third Set) of Artifacts from the Spike Mark.
    private Pose pickup3Pose;
    private Pose pickup3Control;
    private Pose pickup3Control2;
    private Pose pickup3Transition;
    private Pose parkPose; // Scoring Pose of our robot. It is facing the goal at a 135 degree angle.
    private Pose pickup4Pose;
    private Pose pickup4Control;
    private Pose pickup4End;
    private Pose shootPose;
    private Pose shootPoseFar;
    private Pose resetPose;
    private Pose goalPose;

    public static final Config red = Config.builder()
            .team("red")
            .goalPose(new Pose(135, 133, Math.toRadians(180)))

            .startPose(new Pose(116.5, 130.410, Math.toRadians(127))) // 180 - 53
            .scorePose(new Pose(84, 86, Math.toRadians(160)))

            .pickup1Pose(new Pose(118, 80, Math.toRadians(180)))
            .pickup1Control(new Pose(103, 78, Math.toRadians(180)))

            .pickup2Pose(new Pose(123, 57, Math.toRadians(0)))
            .pickup2Control(new Pose(87, 57, Math.toRadians(180)))

            .pickup3Pose(new Pose(125, 60.5, Math.toRadians(0)))
            .pickup3Control(new Pose(108, 56, Math.toRadians(205 + 180)))
            .pickup3Control2(new Pose(102, 58, Math.toRadians(180)))

            .pickup4Pose(new Pose(120, 35, Math.toRadians(0)))
            .pickup4Control(new Pose(70, 30, Math.toRadians(180)))
            .pickup4End(new Pose(120, 35, Math.toRadians(180)))
            .parkPose(new Pose(105, 60, Math.toRadians(180)))


            .build();

    public static final Config redFar = Config.builder()
            .team("red")
            .goalPose(new Pose(135, 133, Math.toRadians(180)))

            .startPose(new Pose(100, 6, Math.toRadians(180)))
            .scorePose(new Pose(90, 12, Math.toRadians(180)))

            .pickup1Pose(new Pose(131, 7, Math.toRadians(150)))
            .pickup1Control(new Pose(128, 25, Math.toRadians(0)))

            .pickup2Transition(new Pose(125, 27, Math.toRadians(-30)))
            .pickup2Control(new Pose(130, 3, Math.toRadians(0)))
            .pickup2Pose(new Pose(131, 30, Math.toRadians(180)))

            .build();

    public static final Config blue = Config.builder()
            .team("blue")
            .goalPose(new Pose(0, 140, 0))

            .startPose(new Pose(27.5, 130.410, Math.toRadians(53)))
            .scorePose(new Pose(63, 83, Math.toRadians(20)))
            .pickup1Pose(new Pose(22, 80, Math.toRadians(0)))
            .pickup1Control(new Pose(41, 78, Math.toRadians(0)))
//            .pickup2Transition(new Pose(48, 75, Math.toRadians(210)))
            .pickup2Pose(new Pose(16, 57, Math.toRadians(180)))
            .pickup2Control(new Pose(57, 57, Math.toRadians(0)))
//            .pickup2Control2(new Pose(43, 100, ath.toRadians(0)))
            .pickup3Pose(new Pose(13.25, 57.5, Math.toRadians(180)))
//            .pickup3Control(new Pose(39, 60, Math.toRadians(0)))
            .pickup3Control(new Pose(40, 56, Math.toRadians(330 - 180)))
            .pickup3Control2(new Pose(42, 58, Math.toRadians(0)))
            .pickup4Pose(new Pose(18, 35.5, Math.toRadians(180)))
            .pickup4Control(new Pose(60, 30, Math.toRadians(0)))
            .pickup4End(new Pose(18, 35, Math.toRadians(0)))
//            .parkPose(new Pose(62, 100, Math.toRadians(0)))
            .parkPose(new Pose(25, 60, Math.toRadians(0)))

//            .pickup3Control2(new Pose(46, 32, Math.toRadians(0)))
            .build();

    public static final Config blueFar = Config.builder()
            .team("blue")
            .goalPose(new Pose(6.5, 133, Math.toRadians(0)))
            .startPose(new Pose(44, 6, Math.toRadians(0)))
            .scorePose(new Pose(54, 12, Math.toRadians(0)))

            .pickup1Pose(new Pose(13, 7, Math.toRadians(35)))
            .pickup1Control(new Pose(12, 25, Math.toRadians(180)))

//            .pickup2Transition(new Pose(19, 27, Math.toRadians(210-180)))
            .pickup2Transition(new Pose(19, 36, Math.toRadians(210-180)))
            .pickup2Control(new Pose(9, 3, Math.toRadians(180)))
            .pickup2Pose(new Pose(13, 30, Math.toRadians(-60)))
            .build();

    public static final Config blueTeleOp = Config.builder()
            .team("blue")
//            .startPose(new Pose(55, 115, Math.toRadians(145)))
//            .shootPose(new Pose(62, 94, Math.toRadians(140)))
            .shootPoseFar(new Pose(72, 13, Math.toRadians(118)))
            .resetPose(new Pose(135, 8, Math.toRadians(90)))
            .goalPose(new Pose(5, 136, 0))
            .build();

    public static final Config redTeleOp = Config.builder()
            .team("red")
            // Original: (55, 115, 145) -> New: (89, 115, 35)
            .shootPoseFar(new Pose(72, 13, Math.toRadians(118)))
            .resetPose(new Pose(9, 8, Math.toRadians(90)))
//            .resetPose(new Pose(135, 8, Math.toRadians(90)))
            .goalPose(new Pose(136, 143, 0))
            .build();


}