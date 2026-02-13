package org.firstinspires.ftc.teamcode.hardware;

import com.pedropathing.geometry.Pose;

import org.firstinspires.ftc.teamcode.opModes.Auto;

public class Paths {
    public Pose Path1;
    public Pose Path1Ex;
    public Pose Path2;
    public Pose Path3;
    public Pose Path3Ex;
    public Pose Path6;
    public Pose Path7;
    public Pose Path7Ex;
    public Pose Path9;
    public Pose Path10;
    public Pose Path11;
    public Pose Path12;
    public Pose Path13;

    public Paths(boolean isBlue) {
        double shootX = isBlue ? 42 : 106;
        double prePickX = isBlue ? 65 : 109;
        double postPickX1 = isBlue ? 4 : 142;
        double postPickEx = isBlue ? 25 : 117;
        double postPickX2 = isBlue ? 9 : 137;
        double farShootX = isBlue ? 42 : 99;
        double cornerPickX = isBlue ? -3 : 137;

        double closeAim = isBlue ? 131 : 52;
        double seeObelisk = isBlue ? 70 : 110;
        double pickUp = isBlue ? 187 : 0;
        double farAim = isBlue ? 115 : 72;
        double cornerZ = !isBlue ? 245 : 335;

        Path1 = new Pose(shootX, 95.500, Math.toRadians(closeAim));
        Path1Ex = new Pose(shootX, 105.500, Math.toRadians(seeObelisk));

        Path2 = new Pose(prePickX, 71.000, Math.toRadians(pickUp));

        Path3 = new Pose(postPickX1, 70.000, Math.toRadians(pickUp));
        Path3Ex = new Pose(postPickEx, 70.000, Math.toRadians(pickUp));

        Path6 = new Pose(prePickX, 93.000, Math.toRadians(pickUp));

        Path7 = new Pose(postPickX2, 93.500, Math.toRadians(pickUp));
        Path7Ex = new Pose(isBlue ? 4.5 : 137, 86, Math.toRadians(90));

        Path9 = new Pose(prePickX, 44.000, Math.toRadians(pickUp));

        Path10 = new Pose(postPickX1, 49.000, Math.toRadians(pickUp));

        Path11 = new Pose(farShootX, 32, Math.toRadians(farAim));

        Path12 = new Pose(cornerPickX, 40, Math.toRadians(cornerZ));

        Path13 = new Pose(cornerPickX, 20, Math.toRadians(cornerZ));
    }
}