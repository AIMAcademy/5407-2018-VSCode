package org.usfirst.frc.team5407.robot;


// Import WPI and other helper classes here
import edu.wpi.first.wpilibj.Solenoid;

public class Air {
	// Create solenoids here and name them
	Solenoid s_DSShifter; 
	Solenoid s_sol1; //
	Solenoid s_sol2; // release arm
	Solenoid s_sol3; // super squeeze
	Solenoid s_sol4; // open intake
	Solenoid s_sol5; // 
	Solenoid s_sol6; //Auto open intake
	Solenoid s_sol7; //LED lights drive and climb
	
	// Make an int with the solenoid port number to be called in robot when you declare what port everything is in 
	public Air(int i_sol0, int i_sol1, int i_sol2, int i_sol3, int i_sol4, int i_sol5, int i_sol6, int i_sol7) {
		// Calls solenoids name, creates solenoid and gives them a port number
		s_DSShifter = new Solenoid(i_sol0);
		s_sol1 = new Solenoid(i_sol1); //
		s_sol2 = new Solenoid(i_sol2); // release arm
		s_sol3 = new Solenoid(i_sol3); // super squeeze
		s_sol4 = new Solenoid(i_sol4); // open intake
		s_sol5 = new Solenoid(i_sol5); //
		s_sol6 = new Solenoid(i_sol6); //Auto open intake
		s_sol7 = new Solenoid(i_sol7); //LED lights drive and climb
		
		// Gives the default values of the solenoids
		initializeAir();
	}

	// Set the default values of the solenoids here
	public void initializeAir() {
		s_DSShifter.set(false);
		s_sol1.set(true);
		s_sol2.set(false);
		s_sol3.set(false);
		s_sol4.set(false);
		s_sol5.set(false);
		s_sol6.set(false);
		s_sol7.set(false);
	}
}
