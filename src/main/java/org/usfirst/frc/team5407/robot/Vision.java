package org.usfirst.frc.team5407.robot;

// Call-import wpi and other helper classes such as cross the roads here
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoMode.PixelFormat;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.SerialPort;

public class Vision {
	
	// Brings in inputs
	Inputs inputs;

	// JeVois Variables
	private SerialPort jevois = null;
	private int loopCount;
	private UsbCamera jevoisCam;

	public ICameraSettings _currentCameraSettings;
	
	// This initiates the camera and specifices the parameters needed  
	public Vision() {
		// BEGIN JeVois Code //
		// Get default camera settings
		_currentCameraSettings = new CameraSettings();

		// Tries to reach camera camera and if not, it prints out a failed 
		// Without this if it did not connect, the whole program would crash
		int tryCount = 0;
		do {
			try {
				System.out.print("Trying to create jevois SerialPort...");
				jevois = new SerialPort(9600, SerialPort.Port.kUSB);
				System.out.println("jevois: " + jevois);
				tryCount = 99;
				System.out.println("success!");
			} catch (Exception e) {
				tryCount += 1;
				System.out.println("failed!");
			}
		} while (tryCount < 3);

		// Creating video stream and setting video mode which is mapped to the object tracker module
		System.out.println("Starting CameraServer");
		if (jevoisCam == null) {
			try {
				jevoisCam = CameraServer.getInstance().startAutomaticCapture();
				jevoisCam.setVideoMode(PixelFormat.kYUYV, _currentCameraSettings.getWidth(),
						_currentCameraSettings.getHeight(), _currentCameraSettings.getFps());
				VideoMode vm = jevoisCam.getVideoMode();
				jevois.writeString("setcam brightness " + _currentCameraSettings.getBrightness() + "\n");
				jevois.writeString("setcam bluebal" + _currentCameraSettings.getBluebal() + "\n");
				jevois.writeString("setcam autogain" + _currentCameraSettings.getAutogain() + "\n");
				jevois.writeString("setcam gain" + _currentCameraSettings.getGain() + "\n");
				jevois.writeString("setcam saturation" + _currentCameraSettings.getSaturation() + "\n");
				jevois.writeString("setcam absexp" + _currentCameraSettings.getAbsexp() + "\n");
				jevois.writeString("setpar hrange" + _currentCameraSettings.getHRange() + "\n");
				jevois.writeString("setpar srange" + _currentCameraSettings.getSRange() + "\n");
				jevois.writeString("setpar vrange" + _currentCameraSettings.getVRange() + "\n");
				System.out.println("jevoisCam pixel: " + vm.pixelFormat);
				System.out.println("jevoisCam res: " + vm.width + "x" + vm.height);
				System.out.println("jevoisCam fps: " + vm.fps);
			} catch (Exception e) {
				System.out.println(e.toString());
				System.out.println("no camera connection");
			}

			// Below code done not work on our robot 
			// Keeping here in case of trouble shooting later
			//jevoisCam.setResolution(320, 254);
			//jevoisCam.setPixelFormat(PixelFormat.kYUYV);
			//jevoisCam.setFPS(60);
		}

		if (tryCount == 99) {
			writeJeVois("info\n");
		}
		loopCount = 0;
		// END JeVois Code // 
		
	}
	
	public void cameraToggle() {

	}
	
	// This function sets the Jevois' camera mode 
	public void setJeVoisVideoMode() {
		jevoisCam.setVideoMode(PixelFormat.kYUYV, _currentCameraSettings.getWidth(),
				_currentCameraSettings.getHeight(), _currentCameraSettings.getFps());
	}
	
	// Sets the Jevois config parameters 
	public void setJeVoisConfigParameters() {
		if (jevois == null) return;
			jevois.writeString("setcam brightness " + _currentCameraSettings.getBrightness() + "\n");
			jevois.writeString("setcam bluebal" + _currentCameraSettings.getBluebal() + "\n");
			jevois.writeString("setcam autogain" + _currentCameraSettings.getAutogain() + "\n");
			jevois.writeString("setcam gain" + _currentCameraSettings.getGain() + "\n");
			jevois.writeString("setcam saturation" + _currentCameraSettings.getSaturation() + "\n");
			jevois.writeString("setpar hrange" + _currentCameraSettings.getHRange() + "\n");
			jevois.writeString("setpar srange" + _currentCameraSettings.getSRange() + "\n");
			jevois.writeString("setpar vrange" + _currentCameraSettings.getVRange() + "\n");

			System.out.println("wrote setcam brightness " + _currentCameraSettings.getBrightness());
			System.out.println("wrote setcam bluebal " + _currentCameraSettings.getBluebal());
			System.out.println("wrote setcam autogain " + _currentCameraSettings.getAutogain());
			System.out.println("wrote setcam gain " + _currentCameraSettings.getGain());
			System.out.println("wrote setcam saturation " + _currentCameraSettings.getSaturation());
			System.out.println("wrote setcam absexp " + _currentCameraSettings.getAbsexp());
			System.out.println("wrote setpar hrange " + _currentCameraSettings.getHRange());
			System.out.println("wrote setpar srange " + _currentCameraSettings.getSRange());
			System.out.println("wrote setpar vrange " + _currentCameraSettings.getVRange());

	}

	// Writes to console
	public void writeJeVois(String cmd) {
		if (jevois == null)
			return;

		int bytes = jevois.writeString(cmd);
		System.out.println("wrote " + bytes + "/" + cmd.length() + " bytes");
		loopCount = 0;
	}

	// Private camera settings code
	public interface ICameraSettings {
		// Any class that "implements" this interface must define these methods.
		// This way we know any camera settings class can getWidth, getHeight, and getFps, etc.
		public int getWidth();
		public int getHeight();
		public int getFps();
		public boolean getIsUsingDefaultSettings();
		public void setDefaultSettings();
		public void setObjectTrackerSettings();

		// Additional parameters for sending configuration through serial
		// Replaces the need for config file edits on the JeVois entirely
		public int getBrightness();
		public int getBluebal();
		public int getAutogain();
		public int getGain();
		public int getSaturation();
		public int getAbsexp();
		public String getHRange();
		public String getSRange();
		public String getVRange();
		
		// needs additional settings - stopping here for testing purposes
	}

	// This class implements the interface camera settings, gets the wanted parameters, and holds the  
	public class CameraSettings implements ICameraSettings {
		private int width;
		private int height;
		private int fps;
		private boolean isUsingDefaultSettings;
		private int brightness;
		private int bluebal;
		private int autogain;
		private int gain;
		private int saturation;
		private int absexp;
		private String hrange;
		private String srange;
		private String vrange;
		
		public CameraSettings() {
			setDefaultSettings();
		}

		public int getWidth() { return width; }
		public int getHeight() { return height; }
		public int getFps() { return fps;}
		public boolean getIsUsingDefaultSettings() { return isUsingDefaultSettings; }
		public int getBrightness() { return brightness; }
		
		public int getBluebal() { return bluebal; }
		public int getAutogain() { return autogain; }
		public int getGain() { return gain; }
		public int getSaturation() { return saturation; }
		public int getAbsexp() { return absexp; }
		
		public String getHRange() { return hrange; }
		public String getSRange() { return srange; }
		public String getVRange() { return vrange; }		

		
		public void setDefaultSettings() {
			width = 352;
			height = 288;
			fps = 30;

			isUsingDefaultSettings = true;
			
			brightness = 3;			
			bluebal = 128;
			autogain = 1;
			gain = 16;
			saturation = 2;
			absexp = 1000;
			
			hrange = "10...245";
			srange = "10...245";
			vrange = "10...245";
		}
		public void setObjectTrackerSettings() {
			width = 320;
			height = 254;
			fps = 60;

			isUsingDefaultSettings = false;
			
			brightness = -3;			
			bluebal = 170;
			autogain = 0;
			gain = 16;
			saturation = 0;
			absexp = 500;
			
			hrange = "60...119";
			srange = "30...186";
			vrange = "225...255";

		}
	}
	// End private camera settings
	
}
