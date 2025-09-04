package org.firstinspires.ftc.teamcodeMentor;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


/**
 * Use this class as a template for creating OpModes.
 */

//@Autonomous(name="MyAutonomous")
@TeleOp(name="MyTemplate")
@Disabled   // Delete this line
public class TemplateOpMode extends LinearOpMode {
    // Declare variables

    @Override
    public void runOpMode() {
        // Init hardware
  
        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
           // Do something
        }
    }
}
