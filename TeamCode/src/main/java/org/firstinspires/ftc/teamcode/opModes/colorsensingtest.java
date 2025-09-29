package org.firstinspires.ftc.teamcode.opModes;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name="ColorSensor", group = "sensor")
public class colorsensingtest extends OpMode {


   ColorSensor colorSensor;


    @Override
    public void init() {
        colorSensor = hardwareMap.colorSensor.get("cs");

    }

    @Override
    public void loop() {
        int red = colorSensor.red();
        int blue = colorSensor.blue();
        int green = colorSensor.green();
        boolean isGreen = green > red && green > blue && green > 500;
        boolean isPurple = (red > 500 && blue > 500) && green < 500;
        telemetry.addData("Red: ", red);
        telemetry.addData("Blue: ", blue);
        telemetry.addData("Green: ", green);
        telemetry.addData("Green? ", isGreen);
        telemetry.addData("Purple? ", isPurple);
        telemetry.update();

    }
}
