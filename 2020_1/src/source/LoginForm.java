package source;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class LoginForm extends Base {
	
	JTextField[] texts = { new JTextField(13), new JPasswordField(13) };

	public LoginForm() {
		super("로그인", 340, 190, 10, 4);
		
		//Notrh
		add(createLabel("병원예약시스템", JLabel.CENTER, new Font("굴림", Font.BOLD, 22)), BorderLayout.NORTH);

		//Center
		JPanel cp = new JPanel(new BorderLayout());
		JPanel c_cp = new JPanel(new GridLayout(2, 1));
		
		JPanel jp = new JPanel(new FlowLayout());
		jp.add(new JLabel("   ID :", JLabel.LEFT));
		jp.add(texts[0]);
		c_cp.add(jp);
		
		jp = new JPanel(new FlowLayout());
		jp.add(new JLabel("PW :", JLabel.LEFT));
		jp.add(texts[1]);
		c_cp.add(jp);
		
		cp.add(createButton("로그인", e -> loginTest()), BorderLayout.EAST);
		cp.add(c_cp);
		cp.setBorder(new EmptyBorder(8, 20, 0, 15));
		
		add(cp);
		
		
		//South
		JPanel sp = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		sp.setBorder(new EmptyBorder(0, 135, 0, 0));
		sp.add(createButton("회원가입", e -> {
			dispose();
			new RegisterForm().setVisible(true);
		}));
		sp.add(createButton("종료", e -> dispose()));
		
		add(sp, BorderLayout.SOUTH);
	}
	
	private void loginTest() {
		String[] inputText = { texts[0].getText(), new String(((JPasswordField) texts[1]).getPassword()) };
		
		if(inputText[0].equals("") || inputText[1].equals("")) {
			showDialog(this, "빈칸이 존재합니다.", "메시지", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		else if(inputText[0].equals("admin") && inputText[1].equals("1234")) {
			dispose();
			new AdminForm().setVisible(true);;
			return;
		}
		
		else {
			try {
				PreparedStatement pstmt = con.prepareStatement("SELECT * FROM patient where p_id = ? and p_pw = ?");
				pstmt.setString(1, inputText[0]);
				pstmt.setString(2, inputText[1]);
			
				ResultSet rs = pstmt.executeQuery();
			
				if(rs.next()) {
					dispose();
					
					new MainForm().setVisible(true);
				}
				
				else {
					showDialog(this, "회원정보가 틀립니다.  다시입력해주세요.", "메시지", JOptionPane.ERROR_MESSAGE);
					return;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new LoginForm().setVisible(true);
	}

}
