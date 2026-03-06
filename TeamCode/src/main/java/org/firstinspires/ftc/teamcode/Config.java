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
    private Pose pickup1Control2; // Highest (First Set) of Artifacts from the Spike Mark.
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
            .goalPose(new Pose(4, 141, 0).mirror())

            .startPose(new Pose(27.5, 131.5, Math.toRadians(270+53)).mirror())
            .scorePose(new Pose(60, 82, Math.toRadians(20)).mirror())
            .pickup1Pose(new Pose(22, 83, Math.toRadians(0)).mirror())
            .pickup1Control(new Pose(41, 78, Math.toRadians(0)).mirror())

            .pickup2Pose(new Pose(17, 55, Math.toRadians(180)).mirror())
            .pickup2Control(new Pose(57, 57, Math.toRadians(0)).mirror())

            .openGate(new Pose (22,65,Math.toRadians(360-30)).mirror())

            .pickup3Control(new Pose(20, 54, Math.toRadians(0)).mirror())
            .pickup3Pose(new Pose(17, 50 , Math.toRadians(360-30)).mirror())
            .pickup3Transition(new Pose(15, 56, Math.toRadians(360-30)).mirror())
            .pickup3Control2(new Pose(27, 53, Math.toRadians(0)).mirror())

            .pickup4Pose(new Pose(23, 28, Math.toRadians(0)).mirror())

            .pickup4Control(new Pose(72, 27, Math.toRadians(0)).mirror())
            .pickup4End(new Pose(15, 28, Math.toRadians(0)).mirror())
            .parkPose(new Pose(25, 60, Math.toRadians(0)).mirror())
            .build();


    public static final Config redFar = Config.builder()
            .team("red")
            .goalPose(new Pose(8, 133, Math.toRadians(0)).mirror())
            .startPose(new Pose(47, 6, Math.toRadians(0)).mirror())
            .scorePose(new Pose(54, 12, Math.toRadians(0)).mirror())

            .pickup1Pose(new Pose(14.5, 11, Math.toRadians(90)).mirror())
            .pickup1Control(new Pose(6, 28, Math.toRadians(180)).mirror())

            .pickup2Control(new Pose(2, -4, Math.toRadians(180)).mirror())
            .pickup2Pose(new Pose(15, 20, Math.toRadians(360-60)).mirror())
            .build();

    public static final Config blue = Config.builder()
            .team("blue")
            .goalPose(new Pose(4, 137, 0))

            .startPose(new Pose(31.5, 129.410, Math.toRadians(270+53)))
            .scorePose(new Pose(60, 83, Math.toRadians(20)))
            .pickup1Pose(new Pose(22, 82, Math.toRadians(0)))
            .pickup1Control(new Pose(41, 78, Math.toRadians(0)))

            .pickup2Pose(new Pose(17, 55, Math.toRadians(180)))
            .pickup2Control(new Pose(57, 57, Math.toRadians(0)))

            .openGate(new Pose (21.75,65.5,Math.toRadians(360-30)))

            .pickup3Control(new Pose(20, 54, Math.toRadians(0)))
            .pickup3Pose(new Pose(17, 50 , Math.toRadians(360-30)))
            .pickup3Transition(new Pose(15, 56, Math.toRadians(360-30)))
            .pickup3Control2(new Pose(27, 53, Math.toRadians(0)))

            .pickup4Pose(new Pose(23, 28, Math.toRadians(0)))

            .pickup4Control(new Pose(72, 27, Math.toRadians(0)))
            .pickup4End(new Pose(15, 28, Math.toRadians(0)))
            .parkPose(new Pose(25, 60, Math.toRadians(0)))
            .build();

    public static final Config blueGate = Config.builder()
            .team("blue")
            .goalPose(new Pose(4, 137, 0))

            .startPose(new Pose(31.5, 129.410, Math.toRadians(270+53)))
            .scorePose(new Pose(60, 83, Math.toRadians(20)))

            .pickup1Pose(new Pose(20, 73, Math.toRadians(10)))
            .pickup1Control(new Pose(15, 84, Math.toRadians(0)))
//            .pickup1Control2(new Pose(32, 73, Math.toRadians(0)))

            .pickup2Pose(new Pose(27, 55, Math.toRadians(0)))
            .pickup2Control(new Pose(58, 53, Math.toRadians(0)))
//            .pickup2Control2(new Pose(30, 65, Math.toRadians(0)))


            .openGate(new Pose (20,68,Math.toRadians(90)))

            .pickup3Control(new Pose(20, 54, Math.toRadians(0)))
            .pickup3Pose(new Pose(17, 50 , Math.toRadians(360-30)))
            .pickup3Transition(new Pose(15, 56, Math.toRadians(360-30)))
            .pickup3Control2(new Pose(27, 53, Math.toRadians(0)))

            .pickup4Pose(new Pose(23, 28, Math.toRadians(0)))

            .pickup4Control(new Pose(72, 27, Math.toRadians(0)))
            .pickup4End(new Pose(15, 28, Math.toRadians(0)))
            .parkPose(new Pose(25, 60, Math.toRadians(0)))
            .build();


    public static final Config blueFar = Config.builder()
            .team("blue")
            .goalPose(new Pose(7.5, 133, Math.toRadians(0)))
            .startPose(new Pose(47, 6, Math.toRadians(0)))
            .scorePose(new Pose(54, 12, Math.toRadians(0)))

            .pickup1Pose(new Pose(13, 11, Math.toRadians(90)))
            .pickup1Control(new Pose(6, 28, Math.toRadians(180)))

            .pickup2Control(new Pose(2, -4, Math.toRadians(180)))
            .pickup2Pose(new Pose(14, 20, Math.toRadians(360-60)))
            .build();

    public static final Config blueFarStack = Config.builder()
            .team("blue")
            .goalPose(new Pose(7.5, 133, Math.toRadians(0)))
            .startPose(new Pose(47, 6, Math.toRadians(0)))
            .scorePose(new Pose(54, 12, Math.toRadians(0)))

            .pickup1Pose(new Pose(13, 11, Math.toRadians(90)))
            .pickup1Control(new Pose(6, 28, Math.toRadians(180)))

            .pickup2Control(new Pose(55, 40, Math.toRadians(0)))
            .pickup2Pose(new Pose(15, 40, Math.toRadians(0)))

            .pickup3Control(new Pose(51.4,5.3,Math.toRadians(30)))
            .pickup3Pose(new Pose(13,6,Math.toRadians(0)))

            .pickup4Control(new Pose(36,15,Math.toRadians(0)))
            .pickup4Pose(new Pose(13,19,Math.toRadians(0)))

            .build();

    public static final Config redFarStack = Config.builder()
            .team("red")
            .goalPose(new Pose(10, 133, Math.toRadians(0)).mirror())
            .startPose(new Pose(47, 6, Math.toRadians(0)).mirror())
            .scorePose(new Pose(54, 12, Math.toRadians(0)).mirror())

            .pickup1Pose(new Pose(14.5, 10, Math.toRadians(85)).mirror())
            .pickup1Control(new Pose(6, 28, Math.toRadians(180)).mirror())

            .pickup2Control(new Pose(55, 40, Math.toRadians(0)).mirror())
            .pickup2Pose(new Pose(15, 40, Math.toRadians(0)).mirror())

            .pickup3Control(new Pose(51.4,3.3,Math.toRadians(0)).mirror())
            .pickup3Pose(new Pose(13,4,Math.toRadians(30)).mirror())

            .pickup4Control(new Pose(36,18,Math.toRadians(0)).mirror())
            .pickup4Pose(new Pose(13,21,Math.toRadians(0)).mirror())

            .build();

    public static final Config blueTeleOp = Config.builder()
            .team("blue")
//            .startPose(new Pose(55, 115, Math.toRadians(145)))
//            .shootPose(new Pose(62, 94, Math.toRadians(140)))
            .shootPoseFar(new Pose(72, 13, Math.toRadians(118)))
            .resetPose(new Pose(135, 8, Math.toRadians(90)))
            .goalPose(new Pose(4, 132, 0))
            .build();

    public static final Config redTeleOp = Config.builder()
            .team("red")
            // Original: (55, 115, 145) -> New: (89, 115, 35)
            .shootPoseFar(new Pose(72, 13, Math.toRadians(118)))
            .resetPose(new Pose(9, 8, Math.toRadians(90)))
//            .resetPose(new Pose(135, 8, Math.toRadians(90)))
            .goalPose(new Pose(140, 132, 0))
            .build();


}