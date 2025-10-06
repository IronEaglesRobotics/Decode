package org.firstinspires.ftc.teamcode.opModes;
import android.graphics.Color;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


@TeleOp(name="ColorSensor", group = "sensor")
public class colorsensingtest extends OpMode {


   RevColorSensorV3 colorSensor;


    @Override
    public void init() {
        colorSensor = hardwareMap.get(RevColorSensorV3.class,"cs");

    }

    @Override
    public void loop() {
        int red = colorSensor.red();
        int blue = colorSensor.blue();
        int green = colorSensor.green();
        double distance = colorSensor.getDistance(DistanceUnit.INCH);
        boolean isGreen = green > (red + blue) * .9;
        boolean isPurple = !isGreen && distance < 1.5;
        boolean isNothing = distance > 1.5;
        telemetry.addData("Red: ", red);
        telemetry.addData("Blue: ", blue);
        telemetry.addData("Green: ", green);
        telemetry.addData("distance: ",distance);
        telemetry.addData("Green? ", isGreen);
        telemetry.addData("Purple? ", isPurple);
        telemetry.addData("Nothing? ",isNothing);
        telemetry.update();

    }
}
