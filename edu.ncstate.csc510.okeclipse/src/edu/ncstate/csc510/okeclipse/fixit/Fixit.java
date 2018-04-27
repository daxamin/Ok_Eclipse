package edu.ncstate.csc510.okeclipse.fixit;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

public class Fixit {
	
	int delT = 15;
	
	public void fix() {
		
//		displayLogInBg();
		
		for (int i = 0; i < 10; i++) {
			runTheRobot();
			getNextEr();
		}
		
	}

	private void displayLogInBg() {
		// TODO Auto-generated method stub
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("Trying to show dialog");
				final Display display = new Display();
				final Shell shell = new Shell(display);
				shell.setText("StackOverflow");
				shell.setLayout(new GridLayout());

				Button button = new Button(shell, SWT.PUSH);
				button.setText("Open new Shell");
				button.addListener(SWT.Selection, (event) -> {
				    Shell child = new Shell(shell);
				    child.setText("Child");
				    child.setVisible(true);
				    child.setSize(300,200);
				});

				shell.pack();
				shell.open();

				while (!shell.isDisposed())
				{
				    if (!display.readAndDispatch())
				        display.sleep();
				}
//				display.dispose();
			}
						
		};
		
		Thread thread = new Thread(runnable);
		thread.run();

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
