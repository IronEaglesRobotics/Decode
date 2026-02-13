package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

@Configurable
@TeleOp(name = "TestServo",group = "Tests")
public class testServo extends OpMode {

    Servo pusher;

    public static double target;
    JoinedTelemetry panelsTelemetry;
    AnalogInput input1;
    AnalogInput input2;



    @Override
    public void init() {
        pusher = hardwareMap.get(Servo.class, "pusher");
        pusher.setDirection(Servo.Direction.REVERSE);

        panelsTelemetry = new JoinedTelemetry(PanelsTelemetry.INSTANCE.getFtcTelemetry(),telemetry);
        input1 = hardwareMap.get(AnalogInput.class,"input1");
        input2 = hardwareMap.get(AnalogInput.class,"input2");
    }

    @Override
    public void loop() {
        pusher.setPosition(target);

        panelsTelemetry.addData("port1", input1.getVoltage());
        panelsTelemetry.addData("port2", input2.getVoltage());
    }
}
