package com.mashibing.nettystudy.s09;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientFrame extends Frame {
	
	public static final ClientFrame INSTANCE = new ClientFrame();
	
	TextArea ta = new TextArea();
	TextField tf = new TextField();
	
	Client c = null;
	
	public ClientFrame() {
		this.setSize(600, 400);
		this.setLocation(100, 20);
		this.add(ta, BorderLayout.CENTER);
		this.add(tf, BorderLayout.SOUTH);
		
		tf.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//把字符串发送到服务器
				c.send(tf.getText());
				//ta.setText(ta.getText() + tf.getText());
				tf.setText("");
			}
		});
		
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				c.closeConnect();
				System.exit(0);
			}
			
		});
		
	}
	
	private void connectToServer() {
		c = new Client();
		c.connect();
	}

	public static void main(String[] args) {
		ClientFrame frame = ClientFrame.INSTANCE ;
		frame.setVisible(true);
		frame.connectToServer();
	}

	public void updateText(String msgAccepted) {
		this.ta.setText(ta.getText() + System.getProperty("line.separator") + msgAccepted);
	}
}
