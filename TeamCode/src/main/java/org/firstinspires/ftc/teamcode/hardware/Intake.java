package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class Intake extends SubsystemBase {
    DcMotor bar;
    public Intake(HardwareMap hardwareMap){
        bar = hardwareMap.get(DcMotor.class,"bar");
    }
    public Command start(){
        return new InstantCommand(()->bar.setPower(-.7));
    }
    public Command reverse(){
        return new InstantCommand(()->bar.setPower(1));
    }
    public Command stop(){
        return new InstantCommand(()->bar.setPower(0));
    }
}
