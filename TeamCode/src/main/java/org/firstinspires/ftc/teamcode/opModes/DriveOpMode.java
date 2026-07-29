package org.firstinspires.ftc.teamcode.opModes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

@Configurable
@TeleOp(name = "Drive test")
public class DriveOpMode extends OpMode {

    GamepadEx controller1;
    private DcMotor rf;
    private DcMotor lf;
    private DcMotor rr;
    private DcMotor lr;
    private DcMotor intake1;
    private DcMotor intake2;
    private MotorEx launcher1;
    private MotorEx launcher2;
    private Servo turret1;
    private Servo turret2;
    private Servo hood;
    private Servo blocker;

    public static double speedCap = 0.8;
    public static double turretPos = 0.5;
    public static double intakeSpeed = 0;
    public static double rintakeSpeed = 0;
    public static double launcherSpeed = 0;
    public static double launcherCloseSpeed = 4000;
    public static double launcherFarSpeed = 6000;
    public static double hoodPos = 0;
    public static double hoodUp = 0.5;
    public static double blockerPos = 0;

    @Override
    public void init() {
        controller1 = new GamepadEx(gamepad1);

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

        intake1 = hardwareMap.get(DcMotor.class, "intake_l");
        intake2 = hardwareMap.get(DcMotor.class, "intake_r");

        launcher1 = new MotorEx(hardwareMap, "launcher_l", 28, 6000);
        launcher2 = new MotorEx(hardwareMap, "launcher_r", 28, 6000);
        launcher1.motor.setDirection(DcMotorSimple.Direction.REVERSE);
        launcher2.motor.setDirection(DcMotorSimple.Direction.REVERSE);

        turret1 = hardwareMap.get(Servo.class, "turret_l");
        turret2 = hardwareMap.get(Servo.class, "turret_r");

        hood = hardwareMap.get(Servo.class, "hood");
        blocker = hardwareMap.get(Servo.class, "blocker");

        CommandScheduler.getInstance().reset();

        controller1.getGamepadButton(GamepadKeys.Button.A)
                .toggleWhenPressed(
                        new InstantCommand(()-> intakeSpeed = 0),
                        new InstantCommand(()-> intakeSpeed = 1)
                );
        controller1.getGamepadButton(GamepadKeys.Button.X)
                .whenHeld(
                        new InstantCommand(()-> rintakeSpeed = 1).alongWith(new InstantCommand(() -> blockerPos = 0.56))
                ).whenReleased(
                        new InstantCommand(()-> rintakeSpeed = 0.20).andThen(new InstantCommand(() -> blockerPos = 0))
                );


        controller1.getGamepadButton(GamepadKeys.Button.RIGHT_STICK_BUTTON)
                .toggleWhenPressed(
                        new InstantCommand(()-> hoodPos = 0.1),
                        new InstantCommand(()-> hoodPos = 0.5)
                );
    }

    @Override
    public void loop() {
        controller1.readButtons();

        double y  = -gamepad1.left_stick_y;
        double x  =  gamepad1.left_stick_x;
        double rx =  gamepad1.right_stick_x * speedCap;


        if(gamepad1.dpad_down){
            launcherSpeed = 0;
        }

        if(gamepad1.dpad_right){
            launcherSpeed = launcherFarSpeed;
        }
        if(gamepad1.dpad_left){
            launcherSpeed = launcherCloseSpeed;
        }

        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1.0);

        double leftFrontPower  = (y + x + rx) / denominator;
        double leftRearPower   = (y - x + rx) / denominator;
        double rightFrontPower = (y - x - rx) / denominator;
        double rightRearPower  = (y + x - rx) / denominator;

        lf.setPower(leftFrontPower);
        lr.setPower(leftRearPower);
        rf.setPower(rightFrontPower);
        rr.setPower(rightRearPower);

        intake1.setPower(intakeSpeed);
        intake2.setPower(rintakeSpeed);

        launcher1.setVelocity((launcherSpeed/60) * 28);
        launcher2.setVelocity((launcherSpeed/60) * 28);

        turret1.setPosition(turretPos);
        turret2.setPosition(turretPos);

        hood.setPosition(hoodPos);

        blocker.setPosition(blockerPos);


        CommandScheduler.getInstance().run();
    }
}
