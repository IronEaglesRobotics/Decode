package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.teamcode.hardware.Bot;
import org.firstinspires.ftc.teamcode.hardware.Intake;

@Configurable
@TeleOp(name = "jack test")
public class JackPractice extends OpMode {

    private boolean intakeOn = false;
    private boolean lastIntakeButtonPressed = false;
    private boolean flywheelOn = false;
    private boolean lastFlywheelButtonPressed = false;

    private DcMotor rf;
    private DcMotor lf;
    private DcMotor rr;
    private DcMotor lr;
    private MotorEx launcher1;
    private MotorEx launcher2;
    private Servo turret1;
    private Servo turret2;
    private Servo hood;
    private Servo stopper;
    public Limelight3A limelight;
    Bot robot;

    public static double turretPos = 0.5;
    public static double intakeSpeed = 1800;
    public static double launcherSpeed = 6000;
    public static double hoodPos = 0;

    GamepadEx controller1;

    @Override
    public void init() {
        rf = hardwareMap.get(DcMotor.class, "rf");
        lf = hardwareMap.get(DcMotor.class, "lf");
        rr = hardwareMap.get(DcMotor.class, "rr");
        lr = hardwareMap.get(DcMotor.class, "lr");

        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        lr.setDirection(DcMotorSimple.Direction.REVERSE);
        rf.setDirection(DcMotorSimple.Direction.REVERSE);
        rr.setDirection(DcMotorSimple.Direction.REVERSE);

        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        limelight = hardwareMap.get(Limelight3A.class,"limelight");

        robot = new Bot().init(hardwareMap, controller1);

        controller1 = new GamepadEx(gamepad1);

        CommandScheduler.getInstance().registerSubsystem(robot.getLauncher());

        controller1.getGamepadButton(GamepadKeys.Button.A)
                .whenPressed(robot.getIntake().FlipFront());

        controller1.getGamepadButton(GamepadKeys.Button.B)
                .whenPressed(robot.getIntake().StartFrontReverse());

        controller1.getGamepadButton((GamepadKeys.Button.DPAD_DOWN))
                .whenHeld(robot.getIntake().IntakesOn().alongWith(robot.getIntake().BlockerOut()))
                .whenReleased(robot.getIntake().StopBack().alongWith(robot.getIntake().BlockerIn()));

        controller1.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
                .whenPressed(robot::resetPos);

    }

    @Override
    public void loop() {
            double y = -gamepad1.left_stick_y;
            double x = -gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1.0);

            double leftFrontPower = (y + x + rx) / denominator;
            double leftRearPower = (y - x + rx) / denominator;
            double rightFrontPower = (y - x - rx) / denominator;
            double rightRearPower = (y + x - rx) / denominator;

            lf.setPower(leftFrontPower);
            lr.setPower(leftRearPower);
            rf.setPower(rightFrontPower);
            rr.setPower(rightRearPower);

            controller1.readButtons();

            telemetry.addData("Pose", robot.getDrive().getPose());

            CommandScheduler.getInstance().run();
    }
}
