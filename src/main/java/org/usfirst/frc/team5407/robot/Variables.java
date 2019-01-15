package org.usfirst.frc.team5407.robot;

public class Variables {
	
	// Declare variables below not well used but will be used more in future along with a constants class 
	
	//used to set encoder to 0 before auto
	public final int encoderpos = 0;
	
	// Gyro kp, the smaller the value the small the corrections get
	public final double GyroKp = 0.015;
	
	//Auto Turn Kp
	public final double autoTurnKp = 1.00;
	
	// kp used in drive straight auto
	public final double autoDriveStraightKp = 0.060;
	
	// kp for the experimental turn pid 
	public double pidAutoTurnkP = .05;
}
