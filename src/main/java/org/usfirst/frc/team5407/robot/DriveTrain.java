package org.usfirst.frc.team5407.robot;

// Call-import wpi and other helper classes here
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class DriveTrain {

	// Declaring all the drive train motors and making it public
	public WPI_TalonSRX frontLeftDriveMotor, frontRightDriveMotor;
	public WPI_VictorSPX backLeftDriveSlave, backRightDriveSlave;
	// Creating a differentialDrive and naming it 
	public DifferentialDrive drive;
	// Creating doubles for getting encoder quad positioning and setting them to equal 0 at beginning
	double RightsideQuadraturePosition = 0.0;
	double LeftsideQuadraturePosition = 0.0;
	double leftSideVeloctiy = 0.0;
	double rightSideVelocity = 0.0;
	double getLeftSideVelocity = 0.0;
	double getRightSideVelocity = 0.0;
	double getAverageVelocity = 0.0;
	
	public DriveTrain(){	

		/* talons for arcade drive */
		// Both front motors have the encoders attached 
		frontLeftDriveMotor = new WPI_TalonSRX(11); 		/* device IDs here (1 of 2) */
		frontRightDriveMotor = new WPI_TalonSRX(16); 	
		
		frontLeftDriveMotor.configOpenloopRamp(0, 20);
		frontRightDriveMotor.configOpenloopRamp(0, 20);

		backLeftDriveSlave = new WPI_VictorSPX(17);
		backRightDriveSlave = new WPI_VictorSPX(18);

		// The commands are sent to the first TalonSRX's then the same sent to the back ones
		backLeftDriveSlave.follow(frontLeftDriveMotor);
		backRightDriveSlave.follow(frontRightDriveMotor);

		// Calling differentalDrive and says what motors are in it, we only need the front ones because the back ones follow them
		drive = new DifferentialDrive(frontLeftDriveMotor, frontRightDriveMotor);
	}

	// Encoder for left side, gets value from encoder, and returns a value in inches divided by 3313 then multiples by the wheel diameter * Pi 
	public double getLeftQuadPosition(){		   	
		//return  (-frontRightDriveMotor.getSensorCollection().getQuadraturePosition()*1.0 / 3313 * 4 * Math.PI);
		LeftsideQuadraturePosition = this.frontLeftDriveMotor.getSensorCollection().getQuadraturePosition();
		// System.out.println("Right side quad position: " + RightsideQuadraturePosition); // Prints position to console
		return (LeftsideQuadraturePosition / 3000 * 6 * Math.PI); //needs tuning 
	}

	// Encoder for right side, gets value from encoder, and returns a value in inches divided by 3313 then multiples by the wheel diameter * Pi 
	public double getRightQuadPosition(){
		//return  (-frontLeftDriveMotor.getSensorCollection().getQuadraturePosition()*1.0 / 3313 * 4 * Math.PI);
		RightsideQuadraturePosition = this.frontRightDriveMotor.getSensorCollection().getQuadraturePosition();
		// System.out.println("Left side quad position: " + LeftsideQuadraturePosition);  // Prints position to console
		return -(RightsideQuadraturePosition / 3000 * 6 * Math.PI); //needs tuning 
		//negative 1 added for invert sensor, needs testing!!!
	}
	
	// Just gets both values, adds them and divides them by 2
	public double getAveragePosition(){
		
		return ((getRightQuadPosition() + getLeftQuadPosition())/2);
	}
	
	
	// We never used this for anything but it was a test for velocity reading and to make it readable convert to real world units, which in this case are feet per second
	public double getAverageVelocity(){
		//gets each sides velocity
		leftSideVeloctiy = frontLeftDriveMotor.getSelectedSensorVelocity(0);
		rightSideVelocity = frontRightDriveMotor.getSelectedSensorVelocity(0);
		
		//adds the two sides together and divides them to get an average
		getAverageVelocity =(Math.abs(leftSideVeloctiy) + (Math.abs(rightSideVelocity))/2);

		//returns velocity and does the math to get the average
		return (getAverageVelocity);
	}
	
	
	// Resets the encoder values to 0 and the 10 is for millisecond delay
	public void resetEncoders() {
		this.frontLeftDriveMotor.getSensorCollection().setQuadraturePosition(0, 10);
		this.frontRightDriveMotor.getSensorCollection().setQuadraturePosition(0, 10);
	}

	
	// Stops both drive motors
	public void stop(){
		drive.arcadeDrive(0, 0);
	}
	
	// The WPI drive is square rooted so this fuction makes it possible to just type in the power wanted without doing initial math
	public void autonDrive(double speed, double rotate){
		
		drive.arcadeDrive(- root(speed), rotate);
	}
	
	// To help with the autonDrive function above a root function was needed to do the math 
	public double root(double num){
		if (num >= 0){
			return Math.sqrt(num);
		}
		else {
			return -1*Math.sqrt(Math.abs(num));
		}
	}


}
