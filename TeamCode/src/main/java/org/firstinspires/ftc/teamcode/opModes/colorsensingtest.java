package org.firstinspires.ftc.teamcode.opModes;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.hardware.Launcher;


@TeleOp(name="ColorSensor", group = "Tests")
public class colorsensingtest extends OpMode {


   RevColorSensorV3 cs1;
    RevColorSensorV3 cs2;
    Launcher launcher;

    GamepadEx cont1;


    @Override
    public void init() {
        cs1 = hardwareMap.get(RevColorSensorV3.class,"cs1");
        cs2 = hardwareMap.get(RevColorSensorV3.class,"cs2");
        launcher = new Launcher(hardwareMap);
        cont1 = new GamepadEx(gamepad1);
    }

    @Override
    public void loop() {
        if (cont1.wasJustPressed(GamepadKeys.Button.A)){
            launcher.toNext();
        }

        if(cont1.wasJustPressed(GamepadKeys.Button.B)){
            launcher.toZero();
        }
        int red = cs1.red();
        int blue = cs1.blue();
        int green = cs1.green();
        double distance = cs1.getDistance(DistanceUnit.INCH);
        telemetry.addData("Red", red);
        telemetry.addData("Blue", blue);
        telemetry.addData("Green", green);
        telemetry.addData("distance",distance);
        telemetry.addData("color", launcher.getColor(cs1));
        int red2 = cs2.red();
        int blue2 = cs2.blue();
        int green2 = cs2.green();
        double distance2 = cs2.getDistance(DistanceUnit.INCH);
        telemetry.addData("Red2", red2);
        telemetry.addData("Blue2", blue2);
        telemetry.addData("Green2", green2);
        telemetry.addData("distance2",distance2);
        telemetry.addData("color2", launcher.getColor(cs2));
        telemetry.update();

    }
}
