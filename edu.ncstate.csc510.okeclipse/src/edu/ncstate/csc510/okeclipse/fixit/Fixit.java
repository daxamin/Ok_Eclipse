package edu.ncstate.csc510.okeclipse.fixit;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.eclipse.ui.IWorkbenchWindow;

public class Fixit {
	
	int delT = 10;
	
	public void fix() {
		
		for (int i = 0; i < 2; i++) {
			runTheRobot();
			getNextEr();
		}
		
	}

	private void getNextEr() {
		// TODO Auto-generated method stub
		try {
			Robot r = new Robot();
			int keyCmd = KeyEvent.VK_META;
			int keyDot = KeyEvent.VK_PERIOD;
			
			Thread.sleep(delT);
			r.keyPress(keyCmd);
			Thread.sleep(delT);
			r.keyPress(keyDot);
			Thread.sleep(delT);
			r.keyRelease(keyDot);
			Thread.sleep(delT);
			r.keyRelease(keyCmd);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void runTheRobot() {
		// TODO Auto-generated method stub
		try { 
			Thread.sleep(delT);
			System.out.println("Debug: Trying.");
			Robot r = new Robot();
			int keyCmd = KeyEvent.VK_META; 
			int key1 = KeyEvent.VK_1;
			int keyEn = KeyEvent.VK_ENTER;
			
			System.out.println("Debug: 1.");
			r.keyPress(keyCmd);
			Thread.sleep(delT);
			System.out.println("Debug: 2.");
			r.keyPress(key1);
			Thread.sleep(delT);
			System.out.println("Debug: 3.");
			r.keyRelease(key1);
			Thread.sleep(delT);
			System.out.println("Debug: 4.");
			r.keyRelease(keyCmd);
			Thread.sleep(delT);
			System.out.println("Debug: 5.");
			
			r.keyPress(keyEn);
			Thread.sleep(delT);
			r.keyRelease(keyEn);
			
			
		}catch(Exception e) {
			System.out.println("Debug: Got into catch");
			e.printStackTrace();
		}
	}
}
