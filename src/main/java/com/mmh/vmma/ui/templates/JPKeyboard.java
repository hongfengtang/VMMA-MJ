/**
 * 
 */
package com.mmh.vmma.ui.templates;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Shape;
import java.awt.Toolkit;

import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * @author hongftan
 *
 */
public class JPKeyboard extends JCommonPanel {

	private static final long serialVersionUID = 4339159853374003395L;
	
	private boolean isCapsLockOn = false;
	private boolean isShiftOn = false;
	
	Robot robot = null;
	
	private JKeyButton vkBackQuote;
	private JKeyButton vk1;
	private JKeyButton vk2;
	private JKeyButton vk3;
	private JKeyButton vk4;
	private JKeyButton vk5;
	private JKeyButton vk6;
	private JKeyButton vk7;
	private JKeyButton vk8;
	private JKeyButton vk9;
	private JKeyButton vk0;
	private JKeyButton vkMinus;
	private JKeyButton vkEqual;
	private JKeyButton vkBack;
	private JKeyButton vkA;
	private JKeyButton vkB;
	private JKeyButton vkC;
	private JKeyButton vkD;
	private JKeyButton vkE;
	private JKeyButton vkF;
	private JKeyButton vkG;
	private JKeyButton vkH;
	private JKeyButton vkI;
	private JKeyButton vkJ;
	private JKeyButton vkK;
	private JKeyButton vkL;
	private JKeyButton vkM;
	private JKeyButton vkN;
	private JKeyButton vkO;
	private JKeyButton vkP;
	private JKeyButton vkQ;
	private JKeyButton vkR;
	private JKeyButton vkS;
	private JKeyButton vkT;
	private JKeyButton vkU;
	private JKeyButton vkV;
	private JKeyButton vkW;
	private JKeyButton vkX;
	private JKeyButton vkY;
	private JKeyButton vkZ;
	private JKeyButton vkOpenBracket;	//[
	private JKeyButton vkCloseBracket;	//]
	private JKeyButton vkBackSlash;		//\
	private JKeyButton vkSemicolon;		//;
	private JKeyButton vkQuote;			//'
	private JKeyButton vkComma;			//,
	private JKeyButton vkPeriod;		//.
	private JKeyButton vkSlash;			///
	private JKeyButton vkShift;
	private JKeyButton vkEnter;
	private JKeyButton vkCapsLock;
	private JKeyButton vkLeft;
	private JKeyButton vkRight;

	public JPKeyboard() {
		try {
			robot = new Robot();
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				updateKeysText();
				super.componentShown(e);
			}

			@Override
			public void componentResized(ComponentEvent e) {
				int width = getWidth();
				int height = getHeight();
				int gap = 3;
				int wKey = 0, hKey = 0;
				hKey = (height - gap * 5) / 4;
				wKey = (width - gap * 15) / 15;
				
				//line 1
				int x = gap, y = gap;
				vkBackQuote.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vk1.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vk2.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vk3.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vk4.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vk5.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vk6.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vk7.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vk8.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vk9.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vk0.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkMinus.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkEqual.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkBack.setBounds(x, y, width - gap - x, hKey);
				
				//line 2
				x = 2* gap + wKey * 3 / 2;
				y += hKey + gap;
				vkQ.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkW.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkE.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkR.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkT.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkY.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkU.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkI.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkO.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkP.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkOpenBracket.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkCloseBracket.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkBackSlash.setBounds(x, y, wKey, hKey);
				
				//line 3
				x = gap;
				y += hKey + gap;
				vkCapsLock.setBounds(x, y, 2 * wKey, hKey);
				
				x += 2 * wKey + gap;
				vkA.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkS.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkD.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkF.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkG.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkH.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkJ.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkK.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkL.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkSemicolon.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkQuote.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkEnter.setBounds(x, y, width - gap - x, hKey);

				//line 4
				x = gap;
				y += hKey + gap;
				vkShift.setBounds(x, y, gap + 5 * wKey / 2, hKey);
				
				x += gap + 5 * wKey / 2 + gap;
				vkZ.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkX.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkC.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkV.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkB.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkN.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkM.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkComma.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkPeriod.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkSlash.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				wKey = (width - 2 * gap - x) / 2;
				vkLeft.setBounds(x, y, wKey, hKey);
				
				x += wKey + gap;
				vkRight.setBounds(x, y, wKey, hKey);

			}
		});
		setBackground(Color.GRAY);
		setLayout(null);
		
		vkBackQuote = new JKeyButton("`");
		vkBackQuote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_BACK_QUOTE);
				robot.keyRelease(KeyEvent.VK_BACK_QUOTE);
			}
		});
		add(vkBackQuote);
		
		vk1 = new JKeyButton("1");
		vk1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_1);
				robot.keyRelease(KeyEvent.VK_1);
			}
		});
		add(vk1);
		vk2 = new JKeyButton("2");
		vk2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_2);
				robot.keyRelease(KeyEvent.VK_2);
			}
		});
		add(vk2);
		vk3 = new JKeyButton("3");
		vk3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_3);
				robot.keyRelease(KeyEvent.VK_3);
			}
		});
		add(vk3);
		vk4 = new JKeyButton("4");
		vk4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_4);
				robot.keyRelease(KeyEvent.VK_4);
			}
		});
		add(vk4);
		vk5 = new JKeyButton("5");
		vk5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_5);
				robot.keyRelease(KeyEvent.VK_5);
			}
		});
		add(vk5);
		vk6 = new JKeyButton("6");
		vk6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_6);
				robot.keyRelease(KeyEvent.VK_6);
			}
		});
		add(vk6);
		vk7 = new JKeyButton("7");
		vk7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_7);
				robot.keyRelease(KeyEvent.VK_7);
			}
		});
		add(vk7);
		vk8 = new JKeyButton("8");
		vk8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_8);
				robot.keyRelease(KeyEvent.VK_8);
			}
		});
		add(vk8);
		vk9 = new JKeyButton("9");
		vk9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_9);
				robot.keyRelease(KeyEvent.VK_9);
			}
		});
		add(vk9);
		vk0 = new JKeyButton("0");
		vk0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_0);
				robot.keyRelease(KeyEvent.VK_0);
			}
		});
		add(vk0);
		vkMinus = new JKeyButton("-");
		vkMinus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_MINUS);
				robot.keyRelease(KeyEvent.VK_MINUS);
			}
		});
		add(vkMinus);
		vkEqual = new JKeyButton("=");
		vkEqual.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_EQUALS);
				robot.keyRelease(KeyEvent.VK_EQUALS);
			}
		});
		add(vkEqual);
		vkBack = new JKeyButton("Delete");
		vkBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_BACK_SPACE);
				robot.keyRelease(KeyEvent.VK_BACK_SPACE);
			}
		});
		add(vkBack);
		vkA = new JKeyButton("a");
		vkA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_A);
				robot.keyRelease(KeyEvent.VK_A);
			}
		});
		add(vkA);
		vkB = new JKeyButton("b");
		vkB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_B);
				robot.keyRelease(KeyEvent.VK_B);
			}
		});
		add(vkB);
		vkC = new JKeyButton("c");
		vkC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_C);
				robot.keyRelease(KeyEvent.VK_C);
			}
		});
		add(vkC);
		vkD = new JKeyButton("d");
		vkD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_D);
				robot.keyRelease(KeyEvent.VK_D);
			}
		});
		add(vkD);
		vkE = new JKeyButton("e");
		vkE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_E);
				robot.keyRelease(KeyEvent.VK_E);
			}
		});
		add(vkE);
		vkF = new JKeyButton("f");
		vkF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_F);
				robot.keyRelease(KeyEvent.VK_F);
			}
		});
		add(vkF);
		vkG = new JKeyButton("g");
		vkG.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_G);
				robot.keyRelease(KeyEvent.VK_G);
			}
		});
		add(vkG);
		vkH = new JKeyButton("h");
		vkH.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_H);
				robot.keyRelease(KeyEvent.VK_H);
			}
		});
		add(vkH);
		vkI = new JKeyButton("i");
		vkI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_I);
				robot.keyRelease(KeyEvent.VK_I);
			}
		});
		add(vkI);
		vkJ = new JKeyButton("j");
		vkJ.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_J);
				robot.keyRelease(KeyEvent.VK_J);
			}
		});
		add(vkJ);
		vkK = new JKeyButton("k");
		vkK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_K);
				robot.keyRelease(KeyEvent.VK_K);
			}
		});
		add(vkK);
		vkL = new JKeyButton("l");
		vkL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_L);
				robot.keyRelease(KeyEvent.VK_L);
			}
		});
		add(vkL);
		vkM = new JKeyButton("m");
		vkM.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_M);
				robot.keyRelease(KeyEvent.VK_M);
			}
		});
		add(vkM);
		vkN = new JKeyButton("n");
		vkN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_N);
				robot.keyRelease(KeyEvent.VK_N);
			}
		});
		add(vkN);
		vkO = new JKeyButton("o");
		vkO.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_O);
				robot.keyRelease(KeyEvent.VK_O);
			}
		});
		add(vkO);
		vkP = new JKeyButton("p");
		vkP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_P);
				robot.keyRelease(KeyEvent.VK_P);
			}
		});
		add(vkP);
		vkQ = new JKeyButton("q");
		vkQ.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_Q);
				robot.keyRelease(KeyEvent.VK_Q);
			}
		});
		add(vkQ);
		vkR = new JKeyButton("r");
		vkR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_R);
				robot.keyRelease(KeyEvent.VK_R);
			}
		});
		add(vkR);
		vkS = new JKeyButton("s");
		vkS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_S);
				robot.keyRelease(KeyEvent.VK_S);
			}
		});
		add(vkS);
		vkT = new JKeyButton("t");
		vkT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_T);
				robot.keyRelease(KeyEvent.VK_T);
			}
		});
		add(vkT);
		vkU = new JKeyButton("u");
		vkU.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_U);
				robot.keyRelease(KeyEvent.VK_U);
			}
		});
		add(vkU);
		vkV = new JKeyButton("v");
		vkV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_V);
				robot.keyRelease(KeyEvent.VK_V);
			}
		});
		add(vkV);
		vkW = new JKeyButton("w");
		vkW.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_W);
				robot.keyRelease(KeyEvent.VK_W);
			}
		});
		add(vkW);
		vkX = new JKeyButton("x");
		vkX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_X);
				robot.keyRelease(KeyEvent.VK_X);
			}
		});
		add(vkX);
		vkY = new JKeyButton("y");
		vkY.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_Y);
				robot.keyRelease(KeyEvent.VK_Y);
			}
		});
		add(vkY);
		vkZ = new JKeyButton("z");
		vkZ.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_Z);
				robot.keyRelease(KeyEvent.VK_Z);
			}
		});
		add(vkZ);
		vkOpenBracket = new JKeyButton("[");
		vkOpenBracket.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_OPEN_BRACKET);
				robot.keyRelease(KeyEvent.VK_OPEN_BRACKET);
			}
		});
		add(vkOpenBracket);
		vkCloseBracket = new JKeyButton("]");
		vkCloseBracket.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_CLOSE_BRACKET);
				robot.keyRelease(KeyEvent.VK_CLOSE_BRACKET);
			}
		});
		add(vkCloseBracket);
		vkBackSlash = new JKeyButton("\\");
		vkBackSlash.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_BACK_SLASH);
				robot.keyRelease(KeyEvent.VK_BACK_SLASH);
			}
		});
		add(vkBackSlash);
		vkSemicolon = new JKeyButton(";");
		vkSemicolon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_SEMICOLON);
				robot.keyRelease(KeyEvent.VK_SEMICOLON);
			}
		});
		add(vkSemicolon);
		vkQuote = new JKeyButton("'");
		vkQuote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_QUOTE);
				robot.keyRelease(KeyEvent.VK_QUOTE);
			}
		});
		add(vkQuote);
		vkComma = new JKeyButton(",");
		vkComma.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_COMMA);
				robot.keyRelease(KeyEvent.VK_COMMA);
			}
		});
		add(vkComma);
		vkPeriod = new JKeyButton(".");
		vkPeriod.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_PERIOD);
				robot.keyRelease(KeyEvent.VK_PERIOD);
			}
		});
		add(vkPeriod);
		vkSlash = new JKeyButton("/");
		vkSlash.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_SLASH);
				robot.keyRelease(KeyEvent.VK_SLASH);
			}
		});
		add(vkSlash);
		vkShift = new JKeyButton("Shit");
		vkShift.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isShiftOn = !isShiftOn;
				if(isShiftOn) {
					vkShift.setTextColor(Color.GREEN);
					robot.keyPress(KeyEvent.VK_SHIFT);
				}
				else {
					vkShift.setTextColor(Color.WHITE);
					robot.keyRelease(KeyEvent.VK_SHIFT);
				}
				vkShift.setSelected(isShiftOn);
				updateKeysText();
			}
		});
		add(vkShift);
		vkEnter = new JKeyButton("Enter");
		vkEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
			}
		});
		add(vkEnter);
		vkCapsLock = new JKeyButton("Caps Lock");
		vkCapsLock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_CAPS_LOCK);
				robot.keyRelease(KeyEvent.VK_CAPS_LOCK);
				updateKeysText();
			}
		});
		add(vkCapsLock);
		vkLeft = new JKeyButton("<");
		vkLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_LEFT);
				robot.keyRelease(KeyEvent.VK_LEFT);
			}
		});
		add(vkLeft);
		vkRight = new JKeyButton(">");
		vkRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_RIGHT);
				robot.keyRelease(KeyEvent.VK_RIGHT);
			}
		});
		add(vkRight);
		
	}
	
	private void updateKeysText() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isCapsLockOn = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
		System.out.println("*******************************isCapsLockOn = " + isCapsLockOn);
		if(isCapsLockOn) {
			vkCapsLock.setTextColor(Color.GREEN);
			if(isShiftOn) {
				lowerCaseKeys();
				shiftKeys();
				return;
			}
			
			capitalKeys();
			noShiftKeys();
			return;
		}
		vkCapsLock.setTextColor(Color.WHITE);
		if(isShiftOn) {
			capitalKeys();
			shiftKeys();
			return;
		}
		
		lowerCaseKeys();
		noShiftKeys();
		return;

	}
	
	private void capitalKeys() {
		vkA.setText("A");
		vkB.setText("B");
		vkC.setText("C");
		vkD.setText("D");
		vkE.setText("E");
		vkF.setText("F");
		vkG.setText("G");
		vkH.setText("H");
		vkI.setText("I");
		vkJ.setText("J");
		vkK.setText("K");
		vkL.setText("L");
		vkM.setText("M");
		vkN.setText("N");
		vkO.setText("O");
		vkP.setText("P");
		vkQ.setText("Q");
		vkR.setText("R");
		vkS.setText("S");
		vkT.setText("T");
		vkU.setText("U");
		vkV.setText("V");
		vkW.setText("W");
		vkX.setText("X");
		vkY.setText("Y");
		vkZ.setText("Z");
	}
	private void lowerCaseKeys() {
		vkA.setText("a");
		vkB.setText("b");
		vkC.setText("c");
		vkD.setText("d");
		vkE.setText("e");
		vkF.setText("f");
		vkG.setText("g");
		vkH.setText("h");
		vkI.setText("i");
		vkJ.setText("j");
		vkK.setText("k");
		vkL.setText("l");
		vkM.setText("m");
		vkN.setText("n");
		vkO.setText("o");
		vkP.setText("p");
		vkQ.setText("q");
		vkR.setText("r");
		vkS.setText("s");
		vkT.setText("t");
		vkU.setText("u");
		vkV.setText("v");
		vkW.setText("w");
		vkX.setText("x");
		vkY.setText("y");
		vkZ.setText("z");
	}
	
	private void shiftKeys() {
		vkBackQuote.setText("~");
		vk1.setText("!");
		vk2.setText("@");
		vk3.setText("#");
		vk4.setText("$");
		vk5.setText("%");
		vk6.setText("^");
		vk7.setText("&");
		vk8.setText("*");
		vk9.setText("(");
		vk0.setText(")");
		vkMinus.setText("_");
		vkEqual.setText("+");
		vkOpenBracket.setText("{");
		vkCloseBracket.setText("}");
		vkBackSlash.setText("|");
		vkSemicolon.setText(":");
		vkQuote.setText("\"");
		vkComma.setText("<");
		vkPeriod.setText(">");
		vkSlash.setText("?");
	}

	private void noShiftKeys() {
		vkBackQuote.setText("`");
		vk1.setText("1");
		vk2.setText("2");
		vk3.setText("3");
		vk4.setText("4");
		vk5.setText("5");
		vk6.setText("6");
		vk7.setText("7");
		vk8.setText("8");
		vk9.setText("9");
		vk0.setText("0");
		vkMinus.setText("-");
		vkEqual.setText("=");
		vkOpenBracket.setText("[");
		vkCloseBracket.setText("]");
		vkBackSlash.setText("\\");
		vkSemicolon.setText(";");
		vkQuote.setText("'");
		vkComma.setText(",");
		vkPeriod.setText(".");
		vkSlash.setText("/");
	}
	
	class JKeyButton extends JButton {

		private static final long serialVersionUID = -8349909027796798219L;
		
		private Font BT_FONT = new Font("ו", Font.BOLD, 20);
		private Color BT_BG_COLOR = Color.BLACK;
	    private Color BUTTON_FOREGROUND_COLOR = Color.WHITE;  
	    private boolean hover = false;  
	    private boolean isSelected = false;

	    public JKeyButton(){
			initButton();
		}
		public JKeyButton(String strText){
			setText(strText);
			initButton();
		}
		
		
		private void initButton(){
			setBorderPainted(false);
			setFocusPainted(false);
			setContentAreaFilled(false);
			setFont(BT_FONT);
			setToolTipText(getText());
			setForeground(BUTTON_FOREGROUND_COLOR);  
			setBackground(BT_BG_COLOR);
			
			addMouseListener(new MouseAdapter() {
				// TODO Auto-generated method stub
				public void mouseEntered(MouseEvent e) {

//					setContentAreaFilled(true);
//					setBorderPainted(true);
	                setForeground(BUTTON_FOREGROUND_COLOR);  
	                hover = true;  
	                repaint();  
				}

				/* (non-Javadoc)
				 * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
				 */
				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
//					setContentAreaFilled(false);
//					setBorderPainted(false);
	                setForeground(BUTTON_FOREGROUND_COLOR);  
	                hover = false;  
	                repaint();  
				}

				/* (non-Javadoc)
				 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
				 */
				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					setFocusable(false);
				}
				
			});
		}
		
		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}
		
		public void setTextColor(Color color) {
			BUTTON_FOREGROUND_COLOR = color;
			setForeground(BUTTON_FOREGROUND_COLOR);
		}

	    @Override  
	    protected void paintComponent(Graphics g) {  
	        Graphics2D g2d = (Graphics2D) g.create();  
	        int h = getHeight();  
	        int w = getWidth();  
	        float tran = 1F;  
	        if (!hover) {  
	            tran = 1F; // 0.3F;  
	        }  
	  
	        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
	                RenderingHints.VALUE_ANTIALIAS_ON);  
	        GradientPaint p1;  
	        GradientPaint p2;  
	        if (getModel().isPressed() || isSelected) {  
	        	BT_BG_COLOR = Color.GRAY;
	            p1 = new GradientPaint(0, 0, new Color(0, 0, 0), 0, h - 1,  
	                    new Color(100, 100, 100));  
	            p2 = new GradientPaint(0, 1, new Color(0, 0, 0, 50), 0, h - 3,  
	                    new Color(255, 255, 255, 100));  
	        } else {
	        	BT_BG_COLOR = Color.BLACK;
	            p1 = new GradientPaint(0, 0, new Color(100, 100, 100), 0, h - 1,  
	                    new Color(0, 0, 0));  
	            p2 = new GradientPaint(0, 1, new Color(255, 255, 255, 100), 0,  
	                    h - 3, new Color(0, 0, 0, 50));  
	        }  
	        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,  
	                tran));  
	        RoundRectangle2D.Float r2d = new RoundRectangle2D.Float(0, 0, w - 1,  
	                h - 1, 20, 20);  
	        Shape clip = g2d.getClip();  
	        g2d.clip(r2d);  
	        GradientPaint gp = new GradientPaint(0.0F, 0.0F, BT_BG_COLOR, 0.0F,  
	                0, BT_BG_COLOR, true);  
	        g2d.setPaint(gp);  
	        g2d.fillRect(0, 0, w, h);  
	        g2d.setClip(clip);  
	        g2d.setPaint(p1);  
	        g2d.drawRoundRect(0, 0, w - 1, h - 1, 20, 20);  
	        g2d.setPaint(p2);  
	        g2d.drawRoundRect(1, 1, w - 3, h - 3, 18, 18);  
	        g2d.dispose();  
	        super.paintComponent(g);  
	    }  
	}

}
