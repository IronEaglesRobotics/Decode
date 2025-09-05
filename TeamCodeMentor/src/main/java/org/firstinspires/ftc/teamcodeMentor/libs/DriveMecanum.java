package org.firstinspires.ftc.teamcodeMentor.libs;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.sun.tools.javac.comp.Check;

import org.firstinspires.ftc.teamcodeMentor.configs.RobotConfig;
import org.firstinspires.ftc.teamcodeMentor.interfaces.DriveTrain;

import java.util.Objects;


/**
 * Mecanum drivetrain built with REV HD Hex motors and goBilda 96mm mecanum wheels.
 */
public class DriveMecanum implements DriveTrain {
    public double maxPower = .5; // scale power percentage, i.e., 0.5 = 50%
    private String fl_name, fr_name, bl_name, br_name;
    private double fl_power, fr_power, bl_power, br_power, forwardBackward, strafe, turn ;
    private boolean isStart = false;

    // Hardware
    private DcMotor fl, fr, bl, br;
    private Gamepad gamepad;

    // Motor and wheel
    static final double rpm = 300; // max rpm for Rev HD Hex motor is 300
    static final double ticks_per_revolution = 28; // Rev Robotics HD Hex Motor
    static final double wheel_circumference = 96 * Math.PI; // goBilda Mecanum wheel unit in millimeters.
    static final double gear_reduction = 30.21; // Rev Robotics UltraPlanetary with 4:1 & 5:1 gear cartridges.
    static final double ticks_per_wheel_rev = ticks_per_revolution * gear_reduction;
    static final double ticks_per_mm = ticks_per_wheel_rev / wheel_circumference;
    static final double ticks_per_second = (rpm/60) * ticks_per_wheel_rev;

    /***
     * Instantiate class with defaults names of wheels (motors).
     *
     * @param hardwareMap - HardwareMap object
     * @param gamepad - Gamepad object
     */
    public DriveMecanum(HardwareMap hardwareMap, Gamepad gamepad){
        this(hardwareMap, gamepad, RobotConfig.FL_WHEEL, RobotConfig.FR_WHEEL, RobotConfig.BL_WHEEL, RobotConfig.BR_WHEEL );
    }

    /***
     * Instantiate class with provided wheel names. The wheel names must match with
     * the configuration settings in the Driver Hub.
     *
     * @param hardwareMap - HardwareMap object
     * @param gamepad - Gamepad object
     * @param frontLeftWheelName - name of front left wheel (motor)
     * @param frontRightWheelName - name of front right wheel (motor)
     * @param backLeftWheelName - name of back left wheel (motor)
     * @param backRightWheelName - name of back right wheel (motor)
     */
    public DriveMecanum(HardwareMap hardwareMap, Gamepad gamepad, String frontLeftWheelName, String frontRightWheelName, String backLeftWheelName, String backRightWheelName){
        this.fl_name = frontLeftWheelName;
        this.fr_name = frontRightWheelName;
        this.bl_name = backLeftWheelName;
        this.br_name = backRightWheelName;
        this.gamepad = gamepad;

        // Check that the wheels are spinning in the forward direction (relative to the robot)
        // when positive power is applied. Call reverseWheelDirection(), if needed.
        fl = hardwareMap.get(DcMotor.class, frontLeftWheelName);
        fr = hardwareMap.get(DcMotor.class, frontRightWheelName);
        bl = hardwareMap.get(DcMotor.class, backLeftWheelName);
        br = hardwareMap.get(DcMotor.class, backRightWheelName);

        // Use braking to slow motors down faster.
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Use RUN_USING_ENCODER mode for greater accuracy
        fl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }


    /***
     * Reverse the direction that the wheel spins when positive power is applied so that
     * the wheel spins in the forward direction relative to the robot.
     *
     * @param wheelName - name of wheel to reverse spin direction (positive power)
     */
    @Override
    public void reverseWheelDirection(String wheelName) {
        if (Objects.equals(wheelName, this.fl_name)) {
            fl.setDirection(DcMotorSimple.Direction.REVERSE);
        } else if (Objects.equals(wheelName, this.fr_name)) {
            fr.setDirection(DcMotorSimple.Direction.REVERSE);
        } else if (Objects.equals(wheelName, this.bl_name)) {
            bl.setDirection(DcMotorSimple.Direction.REVERSE);
        } else if (Objects.equals(wheelName, this.br_name)) {
            br.setDirection(DcMotorSimple.Direction.REVERSE);
        }
//        else {
//            // ToDo: log message
//        }

    }


    @Override
    public void rotateLeft() {
        setPower(-1, -1, 1, 1);
    }

    @Override
    public void rotateRight() {
        setPower(1, 1, -1, -1);
    }

    @Override
    public void run(){

        // Read the joysticks power levels
        forwardBackward = -gamepad.left_stick_y;
        strafe =  gamepad.left_stick_x;
        turn = gamepad.right_stick_x;

        // Calculate power levels to drive mecanum wheels
        fl_power = forwardBackward + strafe + turn;
        bl_power = forwardBackward - strafe + turn;
        fr_power = forwardBackward - strafe - turn;
        br_power = forwardBackward + strafe - turn;

        setPower(fl_power, bl_power, fr_power, br_power);

    }

    private double scale(double power){
        return (power > 1) ? maxPower : (power < -1)? -maxPower : power * maxPower;
    }

    public void setPower(double fl_power, double bl_power, double fr_power, double br_power){
        fl.setPower(scale(fl_power));
        fr.setPower(scale(fr_power));
        bl.setPower(scale(bl_power));
        br.setPower(scale(br_power));
    }

    @Override
    public void stop() {
        setPower(0, 0, 0, 0 );
    }

}
