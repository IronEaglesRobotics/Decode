package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class Intake extends SubsystemBase {
    public DcMotor intakeF;
    public DcMotor intakeR;
    public Servo blocker;

    public Intake(HardwareMap hardwareMap){
        intakeF = hardwareMap.get(DcMotor.class, "intake_l");
        intakeR = hardwareMap.get(DcMotor.class, "intake_r");
        blocker = hardwareMap.get(Servo.class, "blocker");
    }

    public Command StartFront(){
        return new InstantCommand(()->intakeF.setPower(1));
    }
    public Command StopFront(){
        return new InstantCommand(()->intakeF.setPower(0));
    }
    public Command StartFrontReverse(){
        return new InstantCommand(()->intakeF.setPower(-1));
    }
    public Command FlipFront(){
        return intakeF.getPower() == 0 ? StartFront() : StopFront();
    }
    public Command StartBack(){
        return new InstantCommand(()->intakeR.setPower(1));
    }
    public Command StopBack(){
        return new InstantCommand(()-> intakeR.setPower(0));
    }
    public Command IntakesOn(){
        return new InstantCommand(()->{
            intakeF.setPower(1);
            intakeR.setPower(1);
        });
    }
    public Command IntakesOff(){
        return new InstantCommand(()->{
            intakeF.setPower(0);
            intakeR.setPower(0);
        });
    }
    public Command BlockerIn(){
        return new InstantCommand(()->blocker.setPosition(0));
    }
    public Command BlockerOut(){
        return new InstantCommand(()->blocker.setPosition(0.55));
    }
}
