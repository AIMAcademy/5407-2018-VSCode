package org.usfirst.frc.team5407.robot;

//Call-import wpi and other helper classes such as cross the roads here
import edu.wpi.first.wpilibj.Joystick;

public class Inputs {
	// Creates the joystick as an object 
    public Joystick j_leftStick;
    public Joystick j_rightStick;
    public Joystick j_emJoy;
    
    // Create public doubles here
    public double shootSpeed;

    // Create private booleans here to protect the states
    private boolean isCameraButtonPressed;
    private boolean isIntakeButtonPressed;
    private boolean isDualSpeedShifterButtonPressed;
    private boolean isSolenoidFourButtonPressed;
    private boolean isSolenoidTwoButtonPressed;   //x button claw release
    private boolean isSolenoidThreeButtonPressed;
    private boolean isIntakeOutButtonPressed;
    private boolean isSolenoidFiveButtonPressed;
    private boolean isSuperButtonPressed;
    private boolean isemJoyButtonPressed;// end game button
    private boolean isTestJoyButtonPressed; 
    
    // Create private doubles here to protect their values
    private double throttle;//for drivetrain
    private double turn;//for drivetrain 
    private double winchSpeed;//end game lift winch 
    private double liftSpeed;

    // Creates the joystick objects and gives them a port value also used in the Robot class
    public Inputs(int leftJoystickPort, int rightJoystickPort, int emJoy) {
        j_leftStick = new Joystick(leftJoystickPort);
        j_rightStick = new Joystick(rightJoystickPort);
        j_emJoy = new Joystick(emJoy);
    }
    
    // Public Boolean used to protect the state of the boolean from being changed in classes other than inputs
    public boolean getIsCameraButtonPressed() { return isCameraButtonPressed; }
    public boolean getIsDualSpeedShifterButtonPressed() { return isDualSpeedShifterButtonPressed; }
    public boolean getIsSolenoidFourButtonPressed() { return isSolenoidFourButtonPressed; }
    public boolean getIsSolenoidTwoButtonPressed() { return isSolenoidTwoButtonPressed; }  // x button claw release
    public boolean getIsSolenoidThreeButtonPressed() { return isSolenoidThreeButtonPressed; }
    public boolean getIsSolenoidFiveButtonPresses() {return isSolenoidFiveButtonPressed; }
    public boolean getIsIntakeButtonPressed() { return isIntakeButtonPressed;  }
    public boolean getIsIntakeOutButtonPressed() { return isIntakeOutButtonPressed; }
    public boolean getIsSuperButtonPressed() {return isSuperButtonPressed;}
    public boolean getIsemJoyButtonPressed() {return isemJoyButtonPressed;}
    public boolean getIsTestJoyButtonPressed() {return isTestJoyButtonPressed;}
    
    
    // Public doubles used to protect the state of the boolean from being changed in classes other than inputs
    public double getThrottle() {return throttle;}
    public double getTurn() {return turn;}
    public double getWinchSpeed() {return winchSpeed;}
    public double getLiftSpeed() {return liftSpeed;}

    // Read values function gets all the values from the joysticks and returns them and is called in robot under teleop periodic 
    public void ReadValues() {
    	//Driver Controller
		// Private doubles
    	if (j_rightStick.getY() < 0.1 && j_rightStick.getY() > -0.1){
    		throttle = 0.0;
    	}else {
    		throttle = j_rightStick.getY(); // xbox left X, positive is forward
    	}
		
    	if (j_rightStick.getX() < 0.2 && j_rightStick.getX() > -0.2 ){
    		turn = 0.0;
    	}else {
    		turn = j_rightStick.getX(); // xbox right X, positive means turn right
    	}
    	
    	if (j_rightStick.getRawAxis(5) < 0.1 && j_rightStick.getRawAxis(5) > -0.1){
    		winchSpeed = 0.0;
    	}else {
    		winchSpeed = j_rightStick.getRawAxis(5); // xbox left X, positive is forward
    	}

		// Private booleans
        isCameraButtonPressed = j_rightStick.getRawButton(5);
        isDualSpeedShifterButtonPressed = j_rightStick.getRawButton(6);
        isSolenoidFiveButtonPressed = j_rightStick.getRawButton(1);
        isTestJoyButtonPressed = j_rightStick.getRawButton(8);
  
        //Operation Controller       
        // Private doubles
        if(j_leftStick.getRawAxis(1) < 0.2 && j_leftStick.getRawAxis(1) > -0.2){
        	liftSpeed = 0.0;
        }else{
        	liftSpeed = j_leftStick.getRawAxis(1);
        }
        
        // Private booleans
        isSolenoidFourButtonPressed = j_leftStick.getRawButton(6);
        isIntakeButtonPressed = j_leftStick.getRawButton(5);
        isSuperButtonPressed = j_leftStick.getRawAxis(3)>0.1;//super closes intake
        //  isSolenoidThreeButtonPressed = j_rightStick.getRawButton(6);
        isIntakeOutButtonPressed = j_leftStick.getRawAxis(2)>0.1; //moved to drive side
        isSolenoidTwoButtonPressed = j_leftStick.getRawButton(3); //x button claw release
        
        //em joystick 
        isemJoyButtonPressed = j_emJoy.getRawButton(12);//end game switch

        shootSpeed = j_leftStick.getRawAxis(2);

    }
}
