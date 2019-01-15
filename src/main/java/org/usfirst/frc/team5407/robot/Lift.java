package org.usfirst.frc.team5407.robot;

// Call-import wpi and other helper classes such as cross the roads here
import edu.wpi.first.wpilibj.Spark;


public class Lift {
	// Says what speed controller is used and gives it a name
	Spark mot_liftDart;
	
	// This is called in robot and says what the port number is and sets the motors initial state to stopped or 0.0
	public Lift(int i_PWM_LiftSpark) {
		mot_liftDart = new Spark(i_PWM_LiftSpark);
		mot_liftDart.set(0.0);
	}

}
