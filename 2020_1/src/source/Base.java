package source;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class Base extends JFrame {
	
	public static Connection con;
	public static Statement state;
	
	static {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost/hospital?characterEncoding=UTF-8&serverTimezone=UTC", "root", "1234");
			state = con.createStatement();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Base(String title, int width, int height, int wgap, int hgap) {
		setTitle(title);
		setSize(width, height);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(2);
		setLayout(new BorderLayout(wgap, hgap));
	}
	
	public static JButton createButton(String text, ActionListener e) {
		JButton btn = new JButton(text);
		btn.addActionListener(e);
		
		return btn;
	}
	
	public static JLabel createLabel(String text, int alignment, Font font) {
		JLabel label = new JLabel();
		
		label.setText(text);
		label.setFont(font);
		label.setHorizontalAlignment(alignment);
		
		return label;
	}
	
	public static void showDialog(Component component, String message, String title, int messageType) {
		JOptionPane.showMessageDialog(component, message, title, messageType);
	}
	
}
