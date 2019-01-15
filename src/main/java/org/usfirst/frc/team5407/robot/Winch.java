package org.usfirst.frc.team5407.robot;

// Call-import wpi and other helper classes such as cross the roads here
import edu.wpi.first.wpilibj.Spark;

public class Winch {
	// Says what speed controller and gives it a name
	Spark mot_Winch;
	
	// Is called in robot and contains the sparks port number
	public Winch(int i_mot_Winch){
		
		// Create a new spark and gives it a port and an initial state of stopped
		mot_Winch = new Spark(i_mot_Winch);
		mot_Winch.set(0.0);
	}
	
	// This function stops the winch and print ln that says the motor is stopped
	public void winchStop() {
		mot_Winch.set(0.0);
		System.out.println("Winch Stopped!");
	}
}
