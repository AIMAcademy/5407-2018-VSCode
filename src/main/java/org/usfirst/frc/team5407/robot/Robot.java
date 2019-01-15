/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5407.robot;

// Call-import wpi and other helper classes such as cross the roads here
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;

/**
 * This program contains FRC team 5407's code for the 2018 competition season
 */

public class Robot extends IterativeRobot {
	// Create new classes and call them here
	Sensors sensors;
	Air air;
	Inputs inputs;
	Variables variables;
	Lift lift;
	DriveTrain drivetrain;
	Intake intake;
	Winch winch;
	Vision vision;
	Timer timer;
	DriverStation ds;
	//PowerDistributionPanel pdp;

	// Autos, creating string for new auto and has a sendable chooser at the end
	// of it
	final String doNothingAuton = "Do Nothing!!";
	final String driveBaseLineStraight = "Drive Straight To BaseLine "; // Needs
	// Testing
	final String driveBaseline = "Drive to Baseline";
	final String baseLineAndSwitch = "Base Line And Switch";
	final String switchThenScale = "Switch Then Scale";
	final String scaleThenSwitch = "Scale Then Switch";
	final String eitherScale = "Either Scale";
	final String finals = "Finals";
	final String testAuton = "Test Auton";
	final String sameSideSwitchAndScale = "Same Side Switch And Scale";
	final String sameSideScaleAndSwitch = "Same Side Scale And Switch";
	private String autonChooser;
	private SendableChooser<String> AutonChooser;

	final String leftSideStart = "Left Side Start";
	final String centerStartThenRight = "Center Start Then Right";
	final String centerStartThenLeft = "Center Start Then Left";
	final String rightSideStart = "Right Side Start";
	final String centerStart = "Center Start";
	private String startSelected;
	private SendableChooser<String> StartChooser;

	String ownership;
	String ownership0;
	String ownership1;
	String ownership2;

	// Lift goes to this height at the beginning of each match to avoid cube hitting floor
	final double autonLiftStart = 150;

	//test and change this number
	final double maxLiftHeight = 320;
	
	final double minLiftHeight = 75;

	final double distanceAdjustment = 1.376;  //REMOVE THE 1.042 when we switch to the real robot
	int autonStep = 1;
	double turnDirection;
	
	Timer matchtimer = new Timer();
	Timer ledtimer = new Timer();
	boolean b_led;
	
	// variables for PID turning for auto
	double turnPIDError;
	double turnPIDthreshold = 3;

	@Override
	public void robotInit() {
		// Makes classes recognized in program and execute
		drivetrain = new DriveTrain();
		sensors = new Sensors();
		inputs = new Inputs(0, 1, 2);
		variables = new Variables();
		lift = new Lift(0);
		intake = new Intake(1, 2);
		winch = new Winch(3);
		vision = new Vision();
		timer = new Timer();
		ds = DriverStation.getInstance();
	//	pdp = new PowerDistributionPanel();
	//	pdp.clearStickyFaults();

		vision.setJeVoisVideoMode();

		// Calls 4 solenoids in the air class
		air = new Air(0, 1, 2, 3, 4, 5, 6, 7);

		AutonChooser = new SendableChooser<String>();
		AutonChooser.addDefault("Do Nothing!!", doNothingAuton);
		/*AutonChooser.addObject("Drive Straight To BaseLine ", driveBaseLineStraight);
		AutonChooser.addObject("Center Drive To Left Of Pile", centerDriveBaseLineToLeftOfPile);
		AutonChooser.addObject("Center Drive To Right Of Pile", centerDriveBaseLineToRightOfPile);
		AutonChooser.addObject("Left Drive to Left Side Scale", leftDrivetoLeftSideScale);*/
		AutonChooser.addObject("Drive to Baseline", driveBaseline);
		AutonChooser.addObject("Test Auton", testAuton);
		AutonChooser.addObject("Switch Then Scale", switchThenScale);
		AutonChooser.addObject("Scale Then Switch", scaleThenSwitch);
		AutonChooser.addObject("Either Scale", eitherScale);
		AutonChooser.addObject("Finals", finals);
		AutonChooser.addDefault("Base Line And Switch", baseLineAndSwitch);
		AutonChooser.addObject("Same Side Switch And Scale", sameSideSwitchAndScale);
		AutonChooser.addObject("Same Side Scale And Switch", sameSideScaleAndSwitch);
		SmartDashboard.putData("Auton Choices", AutonChooser);

		StartChooser = new SendableChooser<String>();
		StartChooser.addDefault("Center Start Then Right", centerStartThenRight);
		StartChooser.addObject("Center Start Then Left", centerStartThenLeft);
		StartChooser.addObject("Left Side Start", leftSideStart);
		StartChooser.addObject("Right Side Start", rightSideStart);
		StartChooser.addObject("Center Start", centerStart);
		SmartDashboard.putData("Start Choices", StartChooser);

		// SmartDashboard.updateValues();
		
		air.s_sol2.set(true);
		
		ownership = null; 
	}

	public void robotPeriodic() {}

	// What happened before the robot is initialized 
	public void disabledInit() {
		// Sets the solenoid state we want
		air.s_sol2.set(true);
	}

	// When the robot is disabled, below is what it does
	public void disabledPeriodic() {

		autonChooser = AutonChooser.getSelected();
		SmartDashboard.putString("My Selected Auton is ", autonChooser);

		startSelected = StartChooser.getSelected();
		SmartDashboard.putString("Robot Start Position is ", startSelected);
		
		// Sets the solenoid state we want
		air.s_sol2.set(true);
		
		if (air.s_DSShifter.get() == false){
			air.s_sol7.set(true);
		}
		else {
			air.s_sol7.set(false);
		}
	}

	public void autonomousInit() {
		// Zero and initialize values for auton
		air.initializeAir();
		drivetrain.frontLeftDriveMotor.setNeutralMode(NeutralMode.Brake);
		drivetrain.backLeftDriveSlave.setNeutralMode(NeutralMode.Brake);
		drivetrain.frontRightDriveMotor.setNeutralMode(NeutralMode.Brake);
		drivetrain.backRightDriveSlave.setNeutralMode(NeutralMode.Brake);

		// Set starting state of solenoids for auto
		air.s_sol6.set(true);


		// resets both drive encoders to zero
		drivetrain.frontLeftDriveMotor.setSelectedSensorPosition(variables.encoderpos, 0, 10);
		drivetrain.frontRightDriveMotor.setSelectedSensorPosition(variables.encoderpos, 0, 10);

		// resets gyro to zero
		sensors.ahrs.reset();

		// Reset encoders
		drivetrain.resetEncoders();

		// Sets initial value of autonStep to 1 
		autonStep = 1;

		// Resets and starts auton timer
		timer.reset();
		timer.start();

		// Gets game data from fms
		getGameData();
	}

	public void autonomousPeriodic() {


		//getGameData();

		// Gets auto choosen and displays it on SmartDashboard

		autonChooser = AutonChooser.getSelected();
		SmartDashboard.putString("My Selected Auton is ", autonChooser);

		startSelected = StartChooser.getSelected();
		SmartDashboard.putString("Robot Start Position is ", startSelected);

		// If else statement for auton selection
		if (autonChooser == doNothingAuton) {
			// do nothing
		} 
		else if (autonChooser == driveBaseline) {
			driveBaseline();
		} 
		else if (autonChooser == baseLineAndSwitch){
			baseLineAndSwitch();
		}
		else if (autonChooser == switchThenScale){
			switchThenScale();
		}
		else if (autonChooser == scaleThenSwitch){
			scaleThenSwitch();
		}
		else if (autonChooser == eitherScale){
			eitherScale();
		}
		else if (autonChooser == sameSideSwitchAndScale){
			sameSideSwitchAndScale();
		}
		else if (autonChooser == sameSideScaleAndSwitch){
			sameSideScaleAndSwitch();
		}
		else if (autonChooser == finals){
			finals();
		}
		else if (autonChooser == testAuton) {
			testAuton();
		}

		// Puts values on SmartDashboard in Auto
		SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());
		SmartDashboard.putNumber("Air PSI", sensors.getAirPressurePsi());
		SmartDashboard.putNumber("left side inches", drivetrain.getLeftQuadPosition());
		SmartDashboard.putNumber("right side inches", drivetrain.getRightQuadPosition());
		SmartDashboard.updateValues();

	}

	public void teleopInit() {
		// Zero and initialize all inputs and sensors for teleop
		air.initializeAir();
		
		// Starts a match timer to let us know what the aprox time is 
		matchtimer.start();

		// Sets the neutral mode of the drive speed controllers which is either brake mode or coast mode
		drivetrain.frontLeftDriveMotor.setNeutralMode(NeutralMode.Coast);
		drivetrain.frontRightDriveMotor.setNeutralMode(NeutralMode.Coast);		
		drivetrain.backLeftDriveSlave.setNeutralMode(NeutralMode.Coast);
		drivetrain.backRightDriveSlave.setNeutralMode(NeutralMode.Coast);
		

		// resets both drive encoders to zero
		drivetrain.frontLeftDriveMotor.setSelectedSensorPosition(variables.encoderpos, 0, 10);
		drivetrain.frontRightDriveMotor.setSelectedSensorPosition(variables.encoderpos, 0, 10);

		// resets gyro to zero
		sensors.ahrs.reset();

		// Reset encoders
		drivetrain.resetEncoders();

	}

	public void teleopPeriodic() {
		// This calls the function in inputs that reads the joystick inputs
		inputs.ReadValues();

		// Camera toggle between PassThrough and ObjectTracker
		boolean setCameraToTrackObjects = inputs.getIsCameraButtonPressed();
		if (setCameraToTrackObjects && vision._currentCameraSettings.getIsUsingDefaultSettings()) {
			vision._currentCameraSettings.setObjectTrackerSettings();
			vision.setJeVoisVideoMode();
			vision.setJeVoisConfigParameters();
		} else if (!setCameraToTrackObjects && !vision._currentCameraSettings.getIsUsingDefaultSettings()) {
			vision._currentCameraSettings.setDefaultSettings();
			vision.setJeVoisVideoMode();
			vision.setJeVoisConfigParameters();
		}

		// put all buttons here
		//	air.s_DSShifter.set(inputs.getIsDualSpeedShifterButtonPressed());
		air.s_sol4.set(inputs.getIsSolenoidFourButtonPressed()); // open intake
		air.s_sol3.set(inputs.getIsSuperButtonPressed()); // super squeeze
		air.s_sol1.set(inputs.getIsSolenoidThreeButtonPressed()); //
		air.s_sol5.set(inputs.getIsSolenoidFiveButtonPresses()); //

		// If else statement opens the solenoids on the claw
		if (inputs.getIsSolenoidTwoButtonPressed() && inputs.getIsemJoyButtonPressed()) {
			air.s_sol2.set(!inputs.getIsSolenoidTwoButtonPressed()); // release
			// arm
		} else {
			air.s_sol2.set(true);
		}

		// If else statement for controlling the lift winch
		if (inputs.getIsemJoyButtonPressed() && inputs.getWinchSpeed() < 0) {
			winch.mot_Winch.set(inputs.getWinchSpeed());
		} else if (inputs.getIsCameraButtonPressed() && inputs.getIsDualSpeedShifterButtonPressed()) {
			winch.mot_Winch.set(-inputs.getWinchSpeed());
		}else {
			winch.mot_Winch.set(0.0);
		}

		// Expiremental led light strip if else statement  
		if (inputs.getIsDualSpeedShifterButtonPressed() == true && matchtimer.get() < 90){
			b_led = false;
			air.s_sol7.set(b_led);
		} else if (inputs.getIsDualSpeedShifterButtonPressed() == false && matchtimer.get() < 90){
			b_led = true;
			air.s_sol7.set(true);
		}
		if (matchtimer.get() > 90){
			b_led  = false;
			air.s_sol7.set(b_led);
		}
		
		// If else statement for intake motors
		if (inputs.getIsIntakeButtonPressed()) {
			intake.intakeIn();
		} else if (inputs.getIsIntakeOutButtonPressed()) {
			intake.mot_rightSideIntake.set(-inputs.shootSpeed);
			intake.mot_leftSideIntake.set(inputs.shootSpeed);
		} else {
			intake.intakeStop();
		}

		// Sets the lifts dart based on the operators y input and its reversed
		lift.mot_liftDart.set(inputs.getLiftSpeed());

		
		// This top part was for auto shifting be was removed for relibilty reasons 
//		if (drivetrain.getAverageVelocity() > 1200){
//			timer.reset();
//			timer.start();
//			if (timer.get() > 2){
//				air.s_DSShifter.set(false);
//			}else if (timer.get() <= 2){
//				air.s_DSShifter.set(true);
//			}
//		}else if(drivetrain.getAverageVelocity() < 1200) {
			if (inputs.getIsDualSpeedShifterButtonPressed()){
				air.s_DSShifter.set(false);
			}else {
				air.s_DSShifter.set(true);
			}
		
		//Test Button
			if (inputs.getIsTestJoyButtonPressed()){
				turnToPID(90);
			}
			


		// Getting the encoder values for the drivetrain and cooking and
		// returning them
		drivetrain.getLeftQuadPosition();
		drivetrain.getRightQuadPosition();

		// BEGIN NAVX Gyro Code //
		// Creates a boolean for enabling or disabling NavX
		// Move to wolfDrive once created!!!
		boolean b_EnableGyroNAVX = false;

		// If robot is going forward or back ward with thin certain values,
		// enable NavX drive straight
		if (inputs.getTurn() <= .05 && inputs.getTurn() >= -0.05) {
			if (b_EnableGyroNAVX == false) {
				sensors.setFollowAngleNAVX(0);
			}
			b_EnableGyroNAVX = true;
			drivetrain.drive.arcadeDrive(inputs.getThrottle(),
					(sensors.getFollowAngleNAVX() - sensors.getPresentAngleNAVX()) * variables.GyroKp);
		}
		// If robot is doing anything other than forward or backward turn NavX
		// Drive straight off
		else {
			drivetrain.drive.arcadeDrive(inputs.getThrottle(), inputs.getTurn());
			b_EnableGyroNAVX = false;
		}

		// Puts values on SmartDashBoard
		SmartDashboard.putNumber("Gyro-NAVX", sensors.ahrs.getAngle());
		SmartDashboard.putNumber("Air PSI", sensors.getAirPressurePsi());
		SmartDashboard.putNumber("left side inches", drivetrain.getLeftQuadPosition());
		SmartDashboard.putNumber("right side inches", drivetrain.getRightQuadPosition());
		SmartDashboard.putNumber("Lift Pot", sensors.analogLiftPot.get());
		SmartDashboard.putNumber("AverageVelocity", drivetrain.getAverageVelocity());
		SmartDashboard.putNumber("Average Positon", drivetrain.getAveragePosition());
		SmartDashboard.putNumber("Right Vel", drivetrain.getRightSideVelocity);
		SmartDashboard.putNumber("Left Vel", drivetrain.getLeftSideVelocity);
		

		// Updating the values put on SmartDashboard
		SmartDashboard.updateValues();
	}


	
	// Below are the functions that you put the individual autons into  

	//subtract 5 from any angle you want to go to

	// choses which version of drive= -1;Baseline to use based on the starting position
	public void driveBaseline() {

		if (startSelected == leftSideStart || startSelected == rightSideStart) {
			driveBaselineSides();
		} 
		else if (startSelected == centerStartThenRight) {
			//driveBaselineCenterThenRight();
			centerRightDoubleTwoPointO();
		} 
		else if (startSelected == centerStartThenLeft) {
			centerLeftDouble();
		}

	}

	public void baseLineAndSwitch() {
		if ((startSelected == leftSideStart && ownership0 == "L")||(startSelected == rightSideStart && ownership0 == "R")){
			driveBaseline();
		}

		else if ((startSelected == leftSideStart && ownership0 == "R")||
				(startSelected == rightSideStart && ownership0 == "L")){
			aroundTheBack();
		}
		else if ((startSelected == centerStart)|| (startSelected == centerStartThenLeft) || (startSelected == centerStartThenRight)){

			// determines if it should go right or left around the pile
			// and changes the startSelcted from centerStart to centerStartThenLeft or centerStartThenRight
			if (ownership0 == "L"){
				startSelected = centerStartThenLeft;
			}
			else {
				startSelected = centerStartThenRight;
			}

			// then runs the appropriate version of driveBaseline
			driveBaseline();
		}
	}

	public void switchThenScale(){
		if ((startSelected == leftSideStart && ownership0 == "L")||(startSelected == rightSideStart && ownership0 == "R")){
			driveBaseline();
		}

		else if ((startSelected == leftSideStart && ownership1 == "L")||(startSelected == rightSideStart && ownership1 == "R")){
			if (startSelected == leftSideStart && ownership1 == "L"){
				closeScaleLeft();
			}else 
				closeScaleRight();
		}


		else if ((startSelected == leftSideStart && ownership0 == "R")||
				(startSelected == rightSideStart && ownership0 == "L")){
			aroundTheBack();
		}
		else if (startSelected == centerStart){

			// determines if it should go right or left around the pile
			// and changes the startSelcted from centerStart to centerStartThenLeft or centerStartThenRight
			if (ownership0 == "L"){
				startSelected = centerStartThenLeft;
			}
			else {
				startSelected = centerStartThenRight;
			}

			// then runs the appropriate version of driveBaseline
			driveBaseline();
		}
	}

	public void scaleThenSwitch(){

		if ((startSelected == leftSideStart && ownership1 == "L")||(startSelected == rightSideStart && ownership1 == "R")){
			if (startSelected == leftSideStart && ownership1 == "L"){
				closeScaleLeft();
			}else 
				closeScaleRight();
				SmartDashboard.putString("My Selected Auton is ", "Close Scale");
				SmartDashboard.updateValues();
		}
		else if ((startSelected == leftSideStart && ownership0 == "L")||(startSelected == rightSideStart && ownership0 == "R")){
			driveBaseline();
			SmartDashboard.putString("My Selected Auton is ", "Close Switch");
			SmartDashboard.updateValues();
		}

		else if ((startSelected == leftSideStart && ownership0 == "R")||    //uncomment after match 48
				(startSelected == rightSideStart && ownership0 == "L")){
			aroundTheBack();
			SmartDashboard.putString("My Selected Auton is ", "Around The Back");
			SmartDashboard.updateValues();
		}
		else if (startSelected == centerStart){

			// determines if it should go right or left around the pile
			// and changes the startSelcted from centerStart to centerStartThenLeft or centerStartThenRight
			if (ownership0 == "L"){
				startSelected = centerStartThenLeft;
			}
			else {
				startSelected = centerStartThenRight;
			}

			// then runs the appropriate version of driveBaseline
			driveBaseline();
		}
	}

	public void eitherScale(){
		if ((startSelected == leftSideStart && ownership1 == "L")||(startSelected == rightSideStart && ownership1 == "R")){
			if (startSelected == leftSideStart && ownership1 == "L"){
				closeScaleLeft();
			}else 
				closeScaleRight();
		}
		else if ((startSelected == leftSideStart && ownership1 == "R")){
			farScaleLeftStart();
		}
		else if ((startSelected == rightSideStart && ownership1 == "L")){
			farScaleRightStart();
		}
		else if (startSelected == centerStart){

			// determines if it should go right or left around the pile
			// and changes the startSelcted from centerStart to centerStartThenLeft or centerStartThenRight
			if (ownership0 == "L"){
				startSelected = centerStartThenLeft;
			}
			else {
				startSelected = centerStartThenRight;
			}

			// then runs the appropriate version of driveBaseline
			driveBaseline();
		}
	}

	public void sameSideSwitchAndScale(){
		if ((startSelected == leftSideStart && ownership0 == "L")||(startSelected == rightSideStart && ownership0 == "R")){
			driveBaseline();
		}

		else if ((startSelected == leftSideStart && ownership1 == "L")||(startSelected == rightSideStart && ownership1 == "R")){
			if (startSelected == leftSideStart && ownership1 == "L"){
				closeScaleLeft();
			}else 
				closeScaleRight();
		}


		else if ((startSelected == leftSideStart && ownership0 == "R")||
				(startSelected == rightSideStart && ownership0 == "L")){
			driveBaseline();
		}
		else if (startSelected == centerStart){

			// determines if it should go right or left around the pile
			// and changes the startSelcted from centerStart to centerStartThenLeft or centerStartThenRight
			if (ownership0 == "L"){
				startSelected = centerStartThenLeft;
			}
			else {
				startSelected = centerStartThenRight;
			}

			// then runs the appropriate version of driveBaseline
			driveBaseline();
		}
	}
		
	public void sameSideScaleAndSwitch(){

		if ((startSelected == leftSideStart && ownership1 == "L")||(startSelected == rightSideStart && ownership1 == "R")){
			if(startSelected == leftSideStart && ownership1 == "L"){
				closeScaleLeft();
			}else 
				closeScaleRight();
		}
		else if ((startSelected == leftSideStart && ownership0 == "L")||(startSelected == rightSideStart && ownership0 == "R")){
			driveBaseline();
		}

		else if ((startSelected == leftSideStart && ownership0 == "R")||    //uncomment after match 48
				(startSelected == rightSideStart && ownership0 == "L")){
			driveBaseline();
		}
		else if (startSelected == centerStart){

			// determines if it should go right or left around the pile
			// and changes the startSelcted from centerStart to centerStartThenLeft or centerStartThenRight
			if (ownership0 == "L"){
				startSelected = centerStartThenLeft;
			}
			else {
				startSelected = centerStartThenRight;
			}

			// then runs the appropriate version of driveBaseline
			driveBaseline();
		}
	}
	
	public void finals(){
		if (ownership1 == "R"){
			closeScaleRight();
			SmartDashboard.putString("My Selected Auton is ", "Close Scale");
		}
		else if (ownership0 == "R"){
			driveBaseline();
			SmartDashboard.putString("My Selected Auton is " , "Close Switch");
		}
		else {
			SmartDashboard.putString("My Selected Auton is ", "Far Switch");
			aroundTheBack();
		}
	}
	
	
	// Individual autons with all their steps 
	
	// When no Auton is called this one will be run, we just sit there
	public void DoNothingAuton() {
		if (autonChooser == doNothingAuton) {
			// Do nothing
		}
	}

	public void driveBaselineSides() {

		
		if (autonStep == 1) {
			liftTo(autonLiftStart, 1);
		}

		else if (autonStep == 2) {
			driveTo(150, 1, 5);
			if ((startSelected == leftSideStart && ownership0 == "R")
					|| (startSelected == rightSideStart && ownership0 == "L")){
			}
		}
		 

		else if (autonStep == 3){
			if ((startSelected == leftSideStart && ownership0 == "L")
					|| (startSelected == rightSideStart && ownership0 == "R")){
				if (startSelected == leftSideStart){
					turnTo(85, 0.80);
				}
				else {
					turnTo(85, -0.80);
				}
			}
			
			
		}
		else if (autonStep == 4){
			
			if ((startSelected == leftSideStart && ownership0 == "L")
					|| (startSelected == rightSideStart && ownership0 == "R")){
				driveTo(30, 0.90, 2);
			}
			
		}
		else if (autonStep == 5){
			if ((startSelected == leftSideStart && ownership0 == "L")
					|| (startSelected == rightSideStart && ownership0 == "R")){
				eject();
			}
		}
	}

	public void driveBaselineSidesFast(){

		 
		if (autonStep == 1){
			liftAndDrive(autonLiftStart, 1, 150, 1, 5);
		}
		

		else if (autonStep == 2){
			if ((startSelected == leftSideStart && ownership0 == "L")
					|| (startSelected == rightSideStart && ownership0 == "R")){
				if (startSelected == leftSideStart){
					turnTo(85, 0.80);
				}
				else {
					turnTo(85, -0.80);
				}
			}
			
			
		}
		else if (autonStep == 3){
			
			if ((startSelected == leftSideStart && ownership0 == "L")
					|| (startSelected == rightSideStart && ownership0 == "R")){
				driveTo(30, 0.90, 2);
			}
			
		}
		else if (autonStep == 4){
			if ((startSelected == leftSideStart && ownership0 == "L")
					|| (startSelected == rightSideStart && ownership0 == "R")){
				eject();
			}
		}
	}
	
	public void driveBaselineCenterThenRight() {


	
		if (autonStep == 1) {
			liftTo(autonLiftStart,1);
		}
		else if (autonStep == 2) {
			driveTo(12, 1, 1);
		} 
		else if (autonStep == 3) {
			turnTo(50, 0.65);
		} 
		else if (autonStep == 4) {
			driveTo(48, .90, 4);
		} 
		else if (autonStep == 5) {
			turnTo(65, -0.65);
		} 
		else if (autonStep == 6){
			driveTo(78,1, 2);
		} 
		else if (autonStep == 7){
			if (ownership0 == "R"){
				eject();
			}
		}
	}

	public void driveBaselineCenterThenRightFast(){

		
		if (autonStep == 1){
			liftAndDrive(autonLiftStart, 1, 12, 1, 1);
		}
		else if (autonStep == 2) {
			turnTo(50, 0.65);
		} 
		else if (autonStep == 3) {
			driveTo(48, .90, 4);
		} 
		else if (autonStep == 4) {
			turnTo(65, -0.65);
		} 
		else if (autonStep == 5){
			driveTo(78,1, 2);
		} 
		else if (autonStep == 6){
			if (ownership0 == "R"){
				eject();
			}
		}
	}
	
	public void driveBaselineCenterThenLeft() {

		
		if (autonStep == 1) {
			liftTo(autonLiftStart,1);
		}
		else if (autonStep == 2) {
			driveTo(12, 1, 1);
		} 
		else if (autonStep == 3) {
			turnTo(50, -0.65);
		} 
		else if (autonStep == 4) {
			driveTo(90, 0.90, 4);
		} 
		else if (autonStep == 5) {
			turnTo(40, 0.65);
		} 
		else if (autonStep == 6){
			driveTo(55,1, 2);
		} 
		else if (autonStep == 7){
			if (ownership0 == "L"){
				eject();
			}
		}
	}

	public void driveBaselineCenterThenLeftFast(){
		
		if (autonStep == 1){
			liftAndDrive(autonLiftStart, 1, 12, 1, 1);
		}
		else if (autonStep == 2) {
			turnTo(50, -0.65);
		} 
		else if (autonStep == 3) {
			driveTo(90, 0.90, 4);
		} 
		else if (autonStep == 4) {
			turnTo(40, 0.65);
		} 
		else if (autonStep == 5){
			driveTo(55,1, 2);
		} 
		else if (autonStep == 6){
			if (ownership0 == "L"){
				eject();
			}
		}
	}
	
	public void aroundTheBack(){

		if (autonStep == 1) {
			liftTo(autonLiftStart, 1);
		}
		else if (autonStep == 2) {
			driveTo(212, 1, 5);
		}
		else if (autonStep == 3){
			if (startSelected == leftSideStart){
				turnTo(70,0.80);
			}
			else {
				turnTo(85,-0.80);
			}
		}
		else if (autonStep == 4){
			driveTo(168, 0.90, 6);
		}
		else if (autonStep == 5){
			if (startSelected == leftSideStart){
				turnTo(85,0.80);
			}
			else {
				turnTo(85,-0.80);
			}
		}
		else if (autonStep == 6){
			driveTo(30, .90, 1.5);
		}
		else if (autonStep == 7){
			eject();
		}
	}	

	public void aroundTheBackFast(){

		if (autonStep == 1){
			liftAndDrive(autonLiftStart, 1, 212, 1, 5);
		}
		else if (autonStep == 2){
			if (startSelected == leftSideStart){
				turnTo(70,0.80);
			}
			else {
				turnTo(85,-0.80);
			}
		}
		else if (autonStep == 3){
			driveTo(168, 0.90, 6);
		}
		else if (autonStep == 4){
			if (startSelected == leftSideStart){
				turnTo(85,0.80);
			}
			else {
				turnTo(85,-0.80);
			}
		}
		else if (autonStep == 5){
			driveTo(30, .90, 1.5);
		}
		else if (autonStep == 6){
			eject();
		}
	}
	
	public void closeScaleLeft(){
		if (startSelected == leftSideStart){
			turnDirection = 1;
		}
		else {
			turnDirection = -1;
		}
		if (autonStep == 1){
			liftTo(autonLiftStart,1);
		}
		else if (autonStep == 2){
			driveTo(290, 1, 7);
		}
		else if (autonStep == 3){
			liftTo(maxLiftHeight, 1);
		}
		else if (autonStep == 4){
			turnTo(60, turnDirection * .75); //85
		}
		else if (autonStep == 5){
			eject();
		}
	}

	public void closeScaleLeftFast(){
		if (startSelected == leftSideStart){
			turnDirection = 1;
		}
		else {
			turnDirection = -1;
		}
		

		if (autonStep == 1){
			liftAndDrive(autonLiftStart, 1, 240, 1, 6);
		}
		else if (autonStep == 2){
			liftAndDrive(maxLiftHeight, 1, 50, 1, 3);
		}
		else if (autonStep == 3){
			turnTo(60, turnDirection * .75); //85
		}
		else if (autonStep == 4){
			eject();
		}
	}
	
	// NEEDs to be changed for right side left side version rn
	public void closeScaleRight(){
		if (startSelected == leftSideStart){
			turnDirection = 1;
		}
		else {
			turnDirection = -1;
		}
		if (autonStep == 1){
			liftTo(autonLiftStart,1);
		}
		else if (autonStep == 2){
			driveTo(284, 1, 7);
		}
		else if (autonStep == 3){
			liftTo(maxLiftHeight, 1);
		}
		else if (autonStep == 4){
			turnTo(70, turnDirection * .75); //85
		}
		else if (autonStep == 5){
			eject();
		}
		else if (autonStep == 6){
			driveTo(20, -1, 2);
		}
		else if (autonStep == 7){
			liftTo(minLiftHeight, 1);
		}
		else if (autonStep == 8){
			turnTo(45, 1);
		}
	}

	public void closeScaleRightFast(){
		if (startSelected == leftSideStart){
			turnDirection = 1;
		}
		else {
			turnDirection = -1;
		}
		
		

		if (autonStep == 1){
			liftAndDrive(autonLiftStart, 1, 240, 1, 6);
		}
		else if (autonStep == 2){
			liftAndDrive(maxLiftHeight, 1, 44, 1, 3);
		}
		else if (autonStep == 3){
			turnTo(70, turnDirection * .75); //85
		}
		else if (autonStep == 4){
			eject();
		}
		
		
		else if (autonStep == 5){
			driveTo(20, -1, 2);
		}
		else if (autonStep == 6){
			liftTo(minLiftHeight, 1);
		}
		else if (autonStep == 7){
			turnTo(45, 1);
		}
	}
	
	public void farScaleLeftStart(){

		if (startSelected == leftSideStart){
			turnDirection = 1;
		}
		
		if (autonStep == 1){
			liftTo(autonLiftStart, 1);
		}
		else if (autonStep == 2){
			driveTo(210, 1, 5);
		}
		else if (autonStep == 3){
			turnTo(75, turnDirection * .80);
		}
		else if (autonStep == 4){
			driveTo(188, 1, 5);
		}
		else if (autonStep ==5){
			liftTo(maxLiftHeight, 1);
		}
		else if (autonStep == 6){
			turnTo(95, turnDirection * -.85);
		}
		else if (autonStep == 7){
			driveTo(15, .80, 1);
		}
		else if (autonStep == 8){
			drop();
		}
		else if (autonStep == 9){
			driveTo(30, -1, 1);
		}
		else if (autonStep == 10){
			liftTo(autonLiftStart, 1);
		}
	}
	
	public void farScaleLeftStartFast(){

		if (startSelected == leftSideStart){
			turnDirection = 1;
		}
		
	
		if (autonStep ==1){
			liftAndDrive(autonLiftStart, 1, 210, 1, 5);
		}
		else if (autonStep == 2){
			turnTo(75, turnDirection * .80);
		}
		else if (autonStep == 3){
			driveTo(138, 1, 5);
		}
		else if(autonStep == 4){
			liftAndDrive(maxLiftHeight, 1, 50, 1, 5);
		}
		else if (autonStep == 5){
			turnTo(95, turnDirection * -.85);
		}
		else if (autonStep == 6){
			driveTo(15, .80, 1);
		}
		else if (autonStep == 7){
			drop();
		}
		
		
		else if (autonStep == 8){
			driveTo(30, -1, 1);
		}
		else if (autonStep == 9){
			liftTo(autonLiftStart, 1);
		}
	}
	
	//change was for left side before
	public void farScaleRightStart(){

		if (startSelected == leftSideStart){
			turnDirection = 1;
		}
		else {
			turnDirection = -1;
		}
		
		
		if (autonStep == 1){
			liftTo(autonLiftStart, 1);
		}
		else if (autonStep == 2){
			driveTo(205, 1, 5);
		}
		else if (autonStep == 3){
			turnTo(70, turnDirection * .80);
		}
		else if (autonStep == 4){
			driveTo(200, 1, 5);
		}
		else if (autonStep ==5){
			liftTo(maxLiftHeight, 1);
		}
		else if (autonStep == 6){
			turnTo(85, turnDirection * -.85);
		}
		else if (autonStep == 7){
			driveTo(35, .80, 1);
		}
		else if (autonStep == 8){
			drop();
		}
		else if (autonStep == 9){
			driveTo(24, -.80, 2);
		}
	}
	
	public void farScaleRightStartFast(){

		if (startSelected == leftSideStart){
			turnDirection = 1;
		}
		else {
			turnDirection = -1;
		}
		
		
		
		if (autonStep == 1){
			liftAndDrive(autonLiftStart, 1, 205, 1, 5);
		}
		else if (autonStep == 2){
			turnTo(70, turnDirection * .80);
		}
		else if (autonStep == 3){
			driveTo(150, 1, 5);
		}
		else if (autonStep == 4){
			liftAndDrive(maxLiftHeight, 1, 50, 1, 3);
		}
		else if (autonStep == 5){
			turnTo(85, turnDirection * -.85);
		}
		else if (autonStep == 6){
			driveTo(35, .80, 1);
		}
		else if (autonStep == 7){
			drop();
		}
		
		
		else if (autonStep == 8){
			driveTo(24, -.80, 2);
		}
	}
	
	public void centerRightDouble(){
		
		if (autonStep == 1) {
		//	air.s_DSShifter.set(true);
			driveTo(12, 1, 1);
		} 
		else if (autonStep == 2){
			liftTo(autonLiftStart,1);
		}
		else if (autonStep == 3) {
			turnTo(50, 0.65);
		} 
		else if (autonStep == 4) {
			driveTo(55, .90, 4);
		} 
		else if (autonStep == 5) {
			turnTo(65, -0.65);
		} 
		else if (autonStep == 6){
			driveTo(78,1, 2);
		} 
		else if (autonStep == 7){
			if (ownership0 == "R"){
				eject();
				
			}
		}
		//start of second cube routine 
		// Drive backwards away from switch 
		else if (autonStep == 8){
			air.s_DSShifter.set(false);
			driveTo(36, -1, 2);
		}
		// Goes to lowest arm height 
		else if (autonStep == 9){
			liftTo(minLiftHeight, 1);
		}
		// Turns towards the cube pile
		else if (autonStep == 10){
			air.s_DSShifter.set(true);
			turnTo(35, -1);
		}
		// Drives towards cube pile
		else if (autonStep == 11){
			driveTo(70, .60, 2);
			intake();
		}
		else if (autonStep == 12){
			closeAndIntake();
		}
		// Drives backwards away from cubes
		else if (autonStep == 13){
			//closeAndIntake();
			driveTo(70, -.80, 1);
		}
		// Turns towards switch 
		else if (autonStep == 14){
			
			liftTo(autonLiftStart, 1);
		}
		else if (autonStep == 15){
			turnTo(35, 1);
		}
		// Drives towards switch
		else if (autonStep == 16){
			driveTo(74, .80, 2);
		}
		// Ejects cube into Switch
		else if (autonStep == 17){
			eject();
		}  
	}

	public void centerRightDoubleTwoPointO(){
	System.out.println(autonStep);
		
		if (autonStep == 1) {
		//	air.s_DSShifter.set(true);
			driveTo(12, 1, 1);
		} 
		else if (autonStep == 2){
			liftTo(autonLiftStart,1);
		}
		else if (autonStep == 3) {
			turnTo(50, 0.65);
		} 
		else if (autonStep == 4) {
			driveTo(55, .90, 4);
		} 
		else if (autonStep == 5) {
			turnTo(55, -0.65);
		} 
		else if (autonStep == 6){
			driveTo(78,1, 2);
		}else if (autonStep == 7){
			if (ownership0 == "R"){
				eject();	
				drop();
			}
			// Start of two cube
			// Turn towards fence and shifts to high gear
		}else if (autonStep == 8){
			air.s_DSShifter.set(true);
			turnTo(30, .90);
			// Drives backwards
		}else if (autonStep == 9){
			liftAndDrive(minLiftHeight, 1, 12, -.50, 2);
			
			// Turn towards cubes
		}else if (autonStep == 10){
			liftAndTurn(minLiftHeight, 1,70, -.60);
			// Drives towards the cubes and intakes
		}else if (autonStep == 11){
			driveTo(35, 1, 2);
			intake();
			// Closes and intakes
		}else if (autonStep == 12){
			closeAndIntake();
			// Drives away from cubes
		}else if (autonStep == 13){
			driveTo(35, -1, 2);
			// Turn towards switch
		}else if (autonStep ==14){
			turnTo(40, 1);
			// Drive to switch
		}else if (autonStep == 15){
			driveTo(36, 1, 1);
			// Eject then drop cube
		}else if (autonStep ==16){
			eject();	
			drop();
		}
		
	}

	public void centerLeftDouble(){
		if (autonStep == 1) {
			//air.s_DSShifter.set(true);
			liftTo(autonLiftStart,1);
		}
		else if (autonStep == 2) {
			driveTo(12, 1, 1);
		} 
		else if (autonStep == 3) {
			turnTo(50, -0.65);
		} 
		else if (autonStep == 4) {
			driveTo(90, 0.90, 4);
		} 
		else if (autonStep == 5) {
			turnTo(40, 0.65);
		} 
		else if (autonStep == 6){
			driveTo(55,1, 2);
		} 
		else if (autonStep == 7){
			if (ownership0 == "L"){
				eject();
			}
		}
		//start of second cube routine 
		// Drive backwards away from switch 
		else if (autonStep == 8){
			driveTo(36, -1, 2);
		}
		// Goes to lowest arm height 
		else if (autonStep == 9){
			liftTo(minLiftHeight, 1);
		}
		// Turns towards the cube pile
		else if (autonStep == 10){
			turnTo(45, 1);
		}
		// Drives towards cube pile
		else if (autonStep == 11){
			driveTo(85, 1, 2);
			intake();
		}
		else if (autonStep == 12){
			closeAndIntake();
		}
		// Drives backwards away from cubes
		else if (autonStep == 13){
			//closeAndIntake();
			driveTo(85, -1, 1);
		}
		// Turns towards switch 
		else if (autonStep == 14){
			liftTo(autonLiftStart, 1);
		}
		else if (autonStep == 15){
			
			turnTo(45, -1);
		}
		// Drives towards switch
		else if (autonStep == 16){
			driveTo(85, 1, 2);
		}
		// Ejects cube into Switch
		else if (autonStep == 17){
			eject();
		}  
		
	}
	
	public void testAuton() {
		
	}

	//Auton Steps to create autos
	
	// This function gets and separates game data and then prints it out
	public void getGameData() {
		ownership = ds.getGameSpecificMessage();

		if (ownership.length() > 0) {
			if (ownership.charAt(0) == 'L') {
				ownership0 = "L";
			} else {
				ownership0 = "R";
			}
			if (ownership.charAt(1) == 'L') {
				ownership1 = "L";
			} else {
				ownership1 = "R";
			}
			if (ownership.charAt(2) == 'L') {
				ownership2 = "L";
			} else {
				ownership2 = "R";
			}
			System.out.println(ownership0 + ownership1 + ownership2);
		}
	}

	// Next step goes through the auton counter, stops drive, and resets all sensors
	public void nextStep() {
		drivetrain.stop();
		sensors.ahrs.reset();
		drivetrain.resetEncoders();
		timer.reset();
		timer.start();
		air.s_sol4.set(false);
		intake.intakeStop();
		autonStep++;
		System.out.println("Next Step "+ autonStep);
	}

	// drives to distance in inches at given speed. Then calls nextStep()
	// IMPORTANT: distance always positive. Speed determines forward/backward

	public void driveTo(double distance, double speed, double time) {
		distance = distanceAdjustment * Math.abs(distance);

		if (timer.get() > time){
			drivetrain.autonDrive(0,0);
			nextStep();
		}else{
			if ((drivetrain.getAveragePosition() > (distance - 10) && drivetrain.getAveragePosition() < (distance + 10))){
				drivetrain.autonDrive(0,0);
				nextStep();
			}else if (drivetrain.getAveragePosition() < distance - 25){
				drivetrain.autonDrive(speed, -(sensors.getPresentAngleNAVX() * variables.autoDriveStraightKp));
			}else if (drivetrain.getAveragePosition() < distance - 15){
				drivetrain.autonDrive((speed / 2), -(sensors.getPresentAngleNAVX() * variables.autoDriveStraightKp));
			}else if (drivetrain.getAveragePosition() > distance + 25){
				drivetrain.autonDrive(-speed, -(sensors.getPresentAngleNAVX() * variables.autoDriveStraightKp));
			}else if (drivetrain.getAveragePosition() > distance + 15){
				drivetrain.autonDrive(-(speed / 2), -(sensors.getPresentAngleNAVX() * variables.autoDriveStraightKp));
			}
		}
	}

	// turns to the given angle. Positive is to the right. Then calls nextStep()
	// IMPORTANT: angle always positive. Speed determines forward/backward

	public void turnTo(double angle, double speed) {
		angle = Math.abs(angle);

		if ((sensors.ahrs.getAngle() > (angle - 5) && sensors.ahrs.getAngle() > (angle + 5))){
			drivetrain.autonDrive(0,0);
			nextStep();
		}
		else if (sensors.ahrs.getAngle() < (angle - 5) && sensors.ahrs.getAngle() < (angle + 5)){
			if (speed > 0) {
				if (sensors.ahrs.getAngle() < angle) {
					drivetrain.autonDrive(0, speed);
				} 
				else {
					straightenOut(angle);
				}
			} 
			else {
				if (sensors.ahrs.getAngle() > -angle) {
					drivetrain.autonDrive(0, speed);
				} 
				else {
					straightenOut(angle);
				}
			}	
		} 

	}	

	// Function straightens out the robots angle 
	public void straightenOut(double angle){
		if (timer.get() < 0.25){
			drivetrain.autonDrive(0, (angle -(sensors.getPresentAngleNAVX()))* 0.038) ;
		}
		else {
			nextStep();
		}
	}

	// This function implements dumb pid with no feedback while moving the lift
	public void liftTo(double height, double speed) {

		System.out.println(sensors.analogLiftPot.get());
		System.out.println(speed);
		
		if (timer.get() > 2){
			lift.mot_liftDart.set(0);
			nextStep();
		}
		else {
			if ((sensors.analogLiftPot.get() >= (height - 10)) && (sensors.analogLiftPot.get() <= (height + 10))) {
				lift.mot_liftDart.set(0);
				nextStep();
			}
			else if (sensors.analogLiftPot.get() < height - 20) {
				lift.mot_liftDart.set(speed);
			} else if (sensors.analogLiftPot.get() < height - 10) {
				lift.mot_liftDart.set(speed / 2);
			} else if (sensors.analogLiftPot.get() > height + 20) {
				lift.mot_liftDart.set(-speed);
			} else if (sensors.analogLiftPot.get() > height + 10) {
				lift.mot_liftDart.set(-speed / 2);
			} 
		}
	}

	
	public void liftPlus(double height, double speed) {


		System.out.println(sensors.analogLiftPot.get());
		System.out.println(speed);
		
		if (timer.get() > 2){
			lift.mot_liftDart.set(0);
		}
		else {
			if ((sensors.analogLiftPot.get() >= (height - 10)) && (sensors.analogLiftPot.get() <= (height + 10))) {
				lift.mot_liftDart.set(0);
			}
			else if (sensors.analogLiftPot.get() < height - 20) {
				lift.mot_liftDart.set(speed);
			} else if (sensors.analogLiftPot.get() < height - 10) {
				lift.mot_liftDart.set(speed / 2);
			} else if (sensors.analogLiftPot.get() > height + 20) {
				lift.mot_liftDart.set(-speed);
			} else if (sensors.analogLiftPot.get() > height + 10) {
				lift.mot_liftDart.set(-speed / 2);
			} 
		}
	}

	// This function moves the lift and drives for auto
	public void liftAndDrive(double height, double liftSpeed, double distance, double driveSpeed, double time){
		liftPlus(height, liftSpeed);
		driveTo(distance, driveSpeed, time);
	}

	// This function moves the lift and turns for auto 
	public void liftAndTurn(double height, double liftSpeed, double angle, double turnSpeed){
		liftPlus(height, liftSpeed);
		turnTo(angle, turnSpeed);
	}

	// This function is used to eject the cube
	public void eject() {
		if (timer.get() < 1) {
			intake.mot_leftSideIntake.set(0.6);
			intake.mot_rightSideIntake.set(-0.6);
		} else {
			intake.intakeStop();
			nextStep();
		}
	}

	// This function is used to intake the cube
	public void intake() {
			intake.intakeIn();
			air.s_sol4.set(true);
	}
	
	// This function opens the intake
	public void drop() {
		if (timer.get() <0.5){
			air.s_sol4.set(true);
		}
		else {
			air.s_sol4.set(false);
			nextStep();
		}
	}

	// This function closes the inake and then intakes it
	public void closeAndIntake(){
		if (timer.get() < 1.5){
		air.s_sol4.set(false);
		intake.intakeIn();
		}
		else if (timer.get() > 1.5) {
			nextStep();
		}
	}
	
	// This function was an experimental use of pid for turning and will be worked on over the off season and was never used in competition   
	public void turnToPID(double targetAngle){
		turnPIDError = targetAngle - sensors.ahrs.getAngle();
		if (turnPIDError > turnPIDthreshold){
			drivetrain.autonDrive(0, (turnPIDError*variables.pidAutoTurnkP)/100);
			//keep going
		}
		else {
			drivetrain.autonDrive(0,0);
			//move to next step
		}
	}

	
}
