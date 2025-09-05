package org.firstinspires.ftc.teamcodeMentor.interfaces;

public interface DriveTrain {


    void reverseWheelDirection(String wheelName);
    void rotateLeft();
    void rotateRight();
    void run();


    /***
     * Set power level for wheels (motors).
     *
     * @param fl_power - front-left wheel
     * @param bl_power - back-left wheel
     * @param fr_power - front-right wheel
     * @param br_power - back-right wheel
     */
    void setPower(double fl_power, double bl_power, double fr_power, double br_power);

    void stop();

}
