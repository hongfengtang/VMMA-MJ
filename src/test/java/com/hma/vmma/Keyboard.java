/**
 * 
 */
package com.hma.vmma;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * @author hongftan
 *
 */
public class Keyboard {
	  public static void main(String[] args) {
	   try {
//	    try {
//	     Runtime.getRuntime().exec("notepad");//打开notepad
//	    } catch (IOException e) {
//	     // TODO 自动生成 catch 块
//	     e.printStackTrace();
//	    }
	    Robot robot = new Robot();
	    //定义3秒的延迟开始写
	    boolean isOn = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
	    System.out.println("CapsLock button is " + (isOn ? "on" : "off"));
	    
	    robot.delay(3000);
	    robot.keyPress(KeyEvent.VK_SHIFT);
	    robot.keyPress(KeyEvent.VK_BACK_QUOTE);
	    robot.keyRelease(KeyEvent.VK_BACK_QUOTE);
	    robot.keyPress(KeyEvent.VK_H);
	    robot.keyRelease(KeyEvent.VK_H);
	    robot.keyPress(KeyEvent.VK_I);
	    robot.keyRelease(KeyEvent.VK_I);
	    robot.keyPress(KeyEvent.VK_SPACE);
	    robot.keyRelease(KeyEvent.VK_SPACE);
	    robot.keyPress(KeyEvent.VK_S);
	    robot.keyRelease(KeyEvent.VK_S);
	    robot.keyPress(KeyEvent.VK_X);
	    robot.keyRelease(KeyEvent.VK_X);
	    robot.keyPress(KeyEvent.VK_C);
	    robot.keyRelease(KeyEvent.VK_C);
	    robot.keyRelease(KeyEvent.VK_SHIFT);
	    System.out.println("111");
	    robot.delay(3000);
	    robot.keyPress(KeyEvent.VK_BACK_SPACE);
	    robot.keyRelease(KeyEvent.VK_BACK_SPACE);
	    System.out.println("aaa");
	    robot.keyPress(KeyEvent.VK_CAPS_LOCK);
	    robot.delay(3000);
	    System.out.println("bbb");
	    robot.keyRelease(KeyEvent.VK_CAPS_LOCK);
	   
	   } catch (AWTException e) {
	    e.printStackTrace();
	    }
	   }
}
