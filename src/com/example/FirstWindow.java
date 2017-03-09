package com.example;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.ParseException;

public class FirstWindow {
	private JPanel panel;
	private JFormattedTextField inputM;
	private JButton generateBtn;
	private JLabel labelN;
	private JLabel labelM;
	private JFormattedTextField inputN;
	private JLabel errorMessage;
	private static JFrame frame;

	public FirstWindow() {
		generateBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					inputN.commitEdit();
					inputM.commitEdit();
					if (Integer.valueOf(inputN.getText()) < 3) {
						throw new IllegalArgumentException();
					}
					Main.init(Integer.valueOf(inputN.getText()), Integer.valueOf(inputM.getText()));
					SecondWindow.show();
					frame.setVisible(false);
					frame.dispose();
				} catch (ParseException e1) {
					errorMessage.setText("Неправильный формат");
				} catch (IllegalArgumentException e2) {
					errorMessage.setText("N должно быть больше 3");
				}
			}
		});
	}

	public static void show() {
		frame = new JFrame("FirstWindow");
		frame.setContentPane(new FirstWindow().panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	private void createUIComponents() {
		// TODO: place custom component creation code here
		NumberFormat format = NumberFormat.getInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(0);
		formatter.setMaximum(Integer.MAX_VALUE);
		formatter.setAllowsInvalid(true);

		formatter.setCommitsOnValidEdit(true);
		inputN = new JFormattedTextField(formatter);
		inputM = new JFormattedTextField(formatter);
	}
}
