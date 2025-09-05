package org.firstinspires.ftc.teamcodeMentor;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcodeMentor.libs.MentorBot;


@TeleOp(name="TestDrive")
public class TestDriveOpMode extends LinearOpMode {
    // Declare variables
    MentorBot mentorBot;

    @Override
    public void runOpMode() {
        mentorBot = new MentorBot(hardwareMap, gamepad1);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            mentorBot.drive.run();
        }
    }
}
