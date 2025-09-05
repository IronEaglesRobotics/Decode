package org.firstinspires.ftc.teamcodeMentor.libs;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcodeMentor.interfaces.DriveTrain;

/**
 * Mentor's robot
 */
public class MentorBot {
    public DriveTrain drive;

    public MentorBot(HardwareMap hardwareMap, Gamepad gamepad){
        drive = new DriveMecanum(hardwareMap, gamepad);
    }
}

