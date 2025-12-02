package org.firstinspires.ftc.teamcode;

import com.pedropathing.geometry.Pose;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class AutoConfig {
    private String team = null;
    private Pose startPose; // Start Pose of our robot.
    private Pose scorePose; // Scoring Pose of our robot. It is facing the goal at a 135 degree angle.
    private Pose pickup1Pose; // Highest (First Set) of Artifacts from the Spike Mark.
    private Pose pickup1Control; // Highest (First Set) of Artifacts from the Spike Mark.
    private Pose openGate;
    private Pose pickup2Transition; // Middle (Second Set) of Artifacts from the Spike Mark.
    private Pose pickup2Pose; // Lowest (Third Set) of Artifacts from the Spike Mark.
    private Pose pickup2Control; // Lowest (Third Set) of Artifacts from the Spike Mark.
    private Pose pickup3Pose;
    private Pose pickup3Control;
    private Pose pickup3Transition;
    private Pose parkPose; // Scoring Pose of our robot. It is facing the goal at a 135 degree angle.

    public static final AutoConfig red = AutoConfig.builder()
            .team("red")
            .startPose(new Pose(118, 130, Math.toRadians(35)))
            .scorePose(new Pose(84, 96, Math.toRadians(35)))
            .pickup1Pose(new Pose(122, 87, Math.toRadians(0)))
            .pickup1Control(new Pose(96, 85, Math.toRadians(180)))
            .pickup2Transition(new Pose(96, 77, Math.toRadians(298)))
            .pickup2Pose(new Pose(124, 64, Math.toRadians(0)))
            .pickup2Control(new Pose(107, 65, Math.toRadians(180)))
            .parkPose(new Pose(90, 115, Math.toRadians(145)))
            .build();
    public static final AutoConfig blue = AutoConfig.builder()
            .team("blue")
            .startPose(new Pose(26, 130, Math.toRadians(145)))
            .scorePose(new Pose(60, 96, Math.toRadians(145)))
            .pickup1Pose(new Pose(22, 86, Math.toRadians(180)))
            .pickup1Control(new Pose(48, 84, Math.toRadians(0)))
            .openGate(new Pose(16,78, Math.toRadians(90)))
            .pickup2Transition(new Pose(48, 75, Math.toRadians(210)))
            .pickup2Pose(new Pose(16, 62, Math.toRadians(180)))
            .pickup2Control(new Pose(37, 63, Math.toRadians(0)))
            .pickup3Transition(new Pose(56, 75, Math.toRadians(255)))
            .pickup3Pose(new Pose (12,40,Math.toRadians(180)))
            .pickup3Control(new Pose(44,38,Math.toRadians(0)))
            .parkPose(new Pose(55, 115, Math.toRadians(145)))
            .build();

    public static final AutoConfig blueFar = AutoConfig.builder()
            .team("blue")
            .startPose(new Pose(55,9,Math.toRadians(90)))
            .scorePose(new Pose(60,20,Math.toRadians(112)))
            .pickup1Pose(new Pose(10,40,Math.toRadians(180)))
            .pickup1Control(new Pose(54,34,Math.toRadians(0)))
            .pickup2Transition(new Pose(7,30,Math.toRadians(255)))
            .pickup2Control(new Pose(17.3,50.3,Math.toRadians(0)))
            .pickup2Pose(new Pose (5,17,Math.toRadians(195)))
            .build();
}