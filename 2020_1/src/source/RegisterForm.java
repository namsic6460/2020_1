package source;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class RegisterForm extends Base {
	
	JTextField[] texts = { new JTextField(15), new JTextField(15), new JTextField(15) };
	@SuppressWarnings("rawtypes")
	JComboBox[] boxes = { new JComboBox<String>(), new JComboBox<String>(), new JComboBox<String>() };
	Calendar cal = Calendar.getInstance();

	@SuppressWarnings("unchecked")
	public RegisterForm() {
		super("회원가입", 310, 225, 0, 3);
		
		//North
		JPanel np = new JPanel(new BorderLayout());
		JPanel n_np = new JPanel(new GridLayout(3, 1, 0, 5));
		
		for(JTextField text : texts)
			text.setFont(new Font(text.getFont().getFontName(), Font.PLAIN, 15));
		
		String[] str = "이름,아이디,비밀번호".split(",");
		JPanel jp;
		JLabel jl;
		for(int i = 0; i < 3; i++) {
			jp = new JPanel(new FlowLayout());
			jl = new JLabel(str[i], JLabel.RIGHT);
			jl.setPreferredSize(new Dimension(60, 20));
			
			jp.add(jl);
			jp.add(texts[i]);
			
			n_np.add(jp);
		}
		
		np.add(n_np);		
		add(np, BorderLayout.NORTH);
		
		//Center
		JPanel cp = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
		
		cp.add(new JLabel("생년월일"));
		cp.add(boxes[0]);
		cp.add(new JLabel("년"));
		cp.add(boxes[1]);
		cp.add(new JLabel("월"));
		cp.add(boxes[2]);
		cp.add(new JLabel("일"));
		
		for(int i = 1940; i <= cal.get(Calendar.YEAR); i++)
			boxes[0].addItem(i);
		for(int i = 1; i <= 12; i++)
			boxes[1].addItem(i);
		boxes[0].addActionListener(e -> setDays());
		boxes[0].setSelectedIndex(-1);
		boxes[1].addActionListener(e -> setDays());
		boxes[1].setSelectedIndex(-1);
		
		add(cp, BorderLayout.CENTER);
		
		//South
		JPanel sp = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
		
		sp.add(createButton("가입완료", e -> register()));
		sp.add(createButton("취소", e -> {
			dispose();
			new LoginForm().setVisible(true);
		}));
		
		sp.setBorder(new EmptyBorder(0, 0, 5, 0));
		add(sp, BorderLayout.SOUTH);
	}

	private void register() {
		String[] inputText = { texts[0].getText(), texts[1].getText(), texts[2].getText() };
		Object[] selectedItem = { boxes[0].getSelectedItem(), boxes[1].getSelectedItem(),
				boxes[2].getSelectedItem() };
		
		if(inputText[0].equals("") || inputText[1].equals("") || inputText[2].equals("")
				|| selectedItem[0] == null || selectedItem[1] == null || selectedItem[2] == null) {
			showDialog(this, "누락된 항목이 있습니다.", "메시지", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			PreparedStatement pstmt = con.prepareStatement("SELECT * FROM patient where p_id = ?");
			pstmt.setString(1, inputText[1]);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				showDialog(this, "아이디가 중복되었습니다.", "메시지", JOptionPane.ERROR_MESSAGE);
				texts[1].setText("");
				texts[1].requestFocus();
				return;
			}
			
			if(inputText[2].length() < 6 || !matchPattern(inputText[2])) {
				showDialog(this, "비밀번호 형식이 일치하지 않습니다.", "메시지", JOptionPane.ERROR_MESSAGE);
				return;
			}

			cal.set(Integer.parseInt(selectedItem[0].toString()), Integer.parseInt(selectedItem[1].toString()),
					Integer.parseInt(selectedItem[2].toString()));
			pstmt = con.prepareStatement("INSERT INTO patient VALUES(0, ?, ?, ?, ?)");
			pstmt.setString(1, inputText[0]);
			pstmt.setString(2, inputText[1]);
			pstmt.setString(3, inputText[2]);
			pstmt.setString(4, new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));

			pstmt.execute();

			showDialog(this, "가입완료 되었습니다.", "메시지", JOptionPane.INFORMATION_MESSAGE);
			dispose();
			new LoginForm().setVisible(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void setDays() {
		Object[] seletedItem = { boxes[0].getSelectedItem(), boxes[1].getSelectedItem() };
		
		if(seletedItem[0] == null || seletedItem[1] == null)
			return;
		
		cal.set((int) seletedItem[0], (int) seletedItem[1] - 1, 1);
		boxes[2].removeAll();
		for (int i = 1; i <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++)
			boxes[2].addItem(i);
	}
	
	private boolean matchPattern(String string) {	
		return Pattern.matches("^[0-9a-zA-Z!@#$%^&*(),.?\\\"';:{}|<>]*$", string);
	}
	
}
