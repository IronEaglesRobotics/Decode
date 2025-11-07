package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.hardware.motors.CRServoEx;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

@Configurable
@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Test")
public class Test extends OpMode {
    private MotorEx motor;
    public CRServoEx servoL;
    public CRServoEx servoR;
    public CRServoEx servoL2;
    public CRServoEx servoR2;
    public ServoEx lever;
    public static double leverPose = .3;
    public static double leverRest = 0.52;


//    private double maxSpeed = 5.4;
//    private double minSpeed = 3;
//    private double numIncrements = 10;
//    private double increment = (maxSpeed-minSpeed)/numIncrements;

    private double velocity = 1;

    private GamepadEx controller1;

    @Override
    public void init() {
        motor = new MotorEx(hardwareMap, "motor", MotorEx.GoBILDA.BARE);
        servoL = new CRServoEx(hardwareMap, "ServoL");
        servoR = new CRServoEx(hardwareMap, "ServoR");
        servoL2 = new CRServoEx(hardwareMap, "ServoL2");
        servoR2 = new CRServoEx(hardwareMap, "ServoR2");
        lever = new ServoEx(hardwareMap, "lever");
        servoR.setInverted(true);
        servoR2.setInverted(true);
        motor.setRunMode(MotorEx.RunMode.RawPower);

//        motor.setVeloCoefficients(0.05, 0.01, 0.31);
//        double[] coeffs = motor.getVeloCoefficients();
//        double kP = coeffs[0];
//        double kI = coeffs[1];
//        double kD = coeffs[2];
        controller1 = new GamepadEx(gamepad1);

    }

    @Override
    public void loop() {
        controller1.readButtons();

//        if(velocity < 1.01 && velocity > 0.09) {
        if (controller1.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
            servoL.set(1);
            servoR.set(1);
        }
        if (controller1.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
            servoL.set(-1);
            servoR.set(-1);
        }

        if (controller1.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)) {
            servoL2.set(1);
            servoR2.set(1);
        }
        if (controller1.wasJustPressed(GamepadKeys.Button.DPAD_LEFT)) {
            servoL2.set(-1);
            servoR2.set(-1);
        }

        if (controller1.isDown(GamepadKeys.Button.X)) {
            lever.set(leverPose);
        } else {
            lever.set(leverRest);
        }
//        }

        motor.set(velocity);

        if (controller1.wasJustPressed(GamepadKeys.Button.Y)){
            velocity=0;
        }
        if (controller1.wasJustPressed(GamepadKeys.Button.A)){
            velocity=1;
        }

///
//        telemetry.addData("velocity: ",motor.getVelocity());
//        telemetry.addData("value: ", velocity);
        telemetry.update();


    }
}
