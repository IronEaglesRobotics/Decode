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
            // Original: (26, 130, 145) -> New: (118, 130, 35)
            .startPose(new Pose(122, 130, Math.toRadians(35)))
            // Original: (60, 96, 145) -> New: (84, 96, 35)
            .scorePose(new Pose(86, 96, Math.toRadians(36)))
            // Original: (22, 86, 180) -> New: (122, 86, 0)
            .pickup1Pose(new Pose(120, 86, Math.toRadians(0)))
            // Original: (48, 84, 0) -> New: (96, 84, 180)
            .pickup1Control(new Pose(96, 84, Math.toRadians(180)))
            // Original: (16, 78, 90) -> New: (128, 78, 90)
            .openGate(new Pose(129.5, 78, Math.toRadians(90)))
            // Original: (48, 75, 210) -> New: (96, 75, -30)
            .pickup2Transition(new Pose(96, 75, Math.toRadians(180 - 210)))
            // Original: (16, 62, 180) -> New: (128, 62, 0)
            .pickup2Pose(new Pose(128, 62, Math.toRadians(0)))
            // Original: (37, 63, 0) -> New: (107, 63, 180)
            .pickup2Control(new Pose(107, 63, Math.toRadians(180)))
            // Original: (56, 75, 255) -> New: (88, 75, -75)
            .pickup3Transition(new Pose(88, 75, Math.toRadians(180 - 255)))
            // Original: (12, 40, 180) -> New: (132, 40, 0)
            .pickup3Pose(new Pose(132, 40, Math.toRadians(0)))
            // Original: (44, 38, 0) -> New: (100, 38, 180)
            .pickup3Control(new Pose(100, 38, Math.toRadians(180)))
            // Original: (55, 115, 145) -> New: (89, 115, 35)
            .parkPose(new Pose(89, 115, Math.toRadians(35)))
            .build();

    public static final Config redFar = Config.builder()
            .team("red")
            // Original: (55, 9, 90) -> New: (89, 9, 90)
            .startPose(new Pose(91, 10, Math.toRadians(90)))
            // Original: (60, 20, 112) -> New: (84, 20, 68)
            .scorePose(new Pose(83, 18, Math.toRadians(66)))
            // Original: (10, 40, 180) -> New: (134, 40, 0)
            .pickup1Pose(new Pose(127, 32, Math.toRadians(0)))
            // Original: (54, 34, 0) -> New: (90, 34, 180)
            .pickup1Control(new Pose(86, 30, Math.toRadians(0)))
            // Original: (7, 30, 255) -> New: (137, 30, -75)
            .pickup2Transition(new Pose(125, 27, Math.toRadians(-30)))
            // Original: (17.3, 50.3, 0) -> New: (126.7, 50.3, 180)
            .pickup2Control(new Pose(118, 50.3, Math.toRadians(0)))
            // Original: (5, 17, 195) -> New: (139, 17, -15)
            .pickup2Pose(new Pose(126, 13, Math.toRadians(-15)))
            .build();

    public static final Config blue = Config.builder()
            .team("blue")
            .goalPose(new Pose(5, 136, 0))

            .startPose(new Pose(27.5, 130.410, Math.toRadians(53)))
            .scorePose(new Pose(60, 86, Math.toRadians(0)))
            .pickup1Pose(new Pose(19, 80, Math.toRadians(180)))
            .pickup1Control(new Pose(41, 78, Math.toRadians(0)))
//            .pickup2Transition(new Pose(48, 75, Math.toRadians(210)))
            .pickup2Pose(new Pose(14, 60, Math.toRadians(180)))
            .pickup2Control(new Pose(57, 57, Math.toRadians(0)))
//            .pickup2Control2(new Pose(43, 100, Math.toRadians(0)))
            .pickup3Pose(new Pose(13, 61.2, Math.toRadians(180)))
            .pickup3Control(new Pose(36, 50, Math.toRadians(0)))
            .pickup3Control2(new Pose(42, 58, Math.toRadians(0)))
            .pickup4Pose(new Pose(20, 35.5, Math.toRadians(180)))
            .pickup4Control(new Pose(51, 36, Math.toRadians(0)))
            .pickup4End(new Pose(12, 35, Math.toRadians(0)))
//            .pickup3Control2(new Pose(46, 32, Math.toRadians(0)))
            .build();

    public static final Config blueFar = Config.builder()
            .team("blue")
            .startPose(new Pose(55, 9, Math.toRadians(90)))
            .scorePose(new Pose(60, 20, Math.toRadians(112)))
            .pickup1Pose(new Pose(10, 40, Math.toRadians(180)))
            .pickup1Control(new Pose(54, 34, Math.toRadians(0)))
            .pickup2Transition(new Pose(7, 30, Math.toRadians(210)))
            .pickup2Control(new Pose(17.3, 50.3, Math.toRadians(0)))
            .pickup2Pose(new Pose(5, 17, Math.toRadians(195)))
            .build();

    public static final Config blueTeleOp = Config.builder()
            .team("blue")
//            .startPose(new Pose(55, 115, Math.toRadians(145)))
//            .shootPose(new Pose(62, 94, Math.toRadians(140)))
            .shootPoseFar(new Pose(72, 17.5, Math.toRadians(118)))
            .resetPose(new Pose(135, 8, Math.toRadians(90)))
            .goalPose(new Pose(3, 136, 0))
            .build();

    public static final Config redTeleOp = Config.builder()
            .team("red")
            // Original: (55, 115, 145) -> New: (89, 115, 35)
            .shootPoseFar(new Pose(72, 17.5, Math.toRadians(118)))
//            .resetPose(new Pose(9, 8, Math.toRadians(90)))
            .resetPose(new Pose(135, 8, Math.toRadians(90)))
            .goalPose(new Pose(141, 143, 0))
            .build();


}