package org.usfirst.frc.team5407.robot;

//Call-import wpi and other helper classes such as cross the roads here
import edu.wpi.first.wpilibj.Spark;

public class Intake {
	
	// Tells what speed controller is used and gives it a name
	Spark mot_rightSideIntake;
	Spark mot_leftSideIntake;
	
	// This is called in the robot class and includes what port the speed controllers are in and sets its initial state to stopped
	public Intake(int i_mot_rightSideIntake, int i_mot_leftSideIntake) {
		mot_rightSideIntake = new Spark(i_mot_rightSideIntake);
		mot_rightSideIntake.set(0.0);
		mot_leftSideIntake = new Spark(i_mot_leftSideIntake);
		mot_leftSideIntake.set(0.0);
	}
	
	// This function sets the motors power so the cube is intaked
	public void intakeIn() {
		mot_leftSideIntake.set(-0.8);
		mot_rightSideIntake.set(0.8);
	}
	
	// This function sets the motors power so the cube spits out
	public void intakeOut() {
		mot_leftSideIntake.set(0.8);
		mot_rightSideIntake.set(-0.8);
	}
	
	// This function stops the intake
	public void intakeStop() {
		mot_leftSideIntake.set(0.0);
		mot_rightSideIntake.set(0.0);
	}
}
