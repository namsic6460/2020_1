package source;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class HospitalizingForm extends Base {
	
	JPanel cp, c_cp;
	JPanel sp = new JPanel(new FlowLayout());
	String currentFloor = "999999";
	JComboBox<String> floor = new JComboBox<String>();
	JButton find = new JButton();
	String clickedButton;
	JTextField[] texts = new JTextField[2];
	JFrame bed;
	String date;
	
	public HospitalizingForm() {
		super("입원", 0, 0, 0, 10);
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeThisEvent();
			}
			
			public void windowOpened(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
		});
		
		//North
		JPanel np = new JPanel(new GridLayout(1, 5));
		np.setBorder(new EmptyBorder(10, 0, 0, 0));
		
		JLabel jl = new JLabel("입원날짜");
		jl.setHorizontalAlignment(JLabel.CENTER);
		np.add(jl);
		
		JTextField jt = new JTextField();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		date = format.format(Calendar.getInstance().getTime());
		jt.setText(date);
		jt.setHorizontalAlignment(JTextField.CENTER);
		jt.setEnabled(false);
		np.add(jt);
		
		jl = new JLabel("층");
		jl.setHorizontalAlignment(JLabel.CENTER);
		np.add(jl);
		
		for(int i = 1; i <= 6; i++)
			floor.addItem(Integer.toString(i));
		np.add(floor);
		
		find.setText("검색");
		find.addActionListener(e -> chooseFloor());
		np.add(find);
		
		add(np, BorderLayout.NORTH);
		
		//Center
		cp = new JPanel();
		this.add(cp, BorderLayout.SOUTH);
		chooseFloor();
		
		//South
		sp.add(new JLabel("호실"));
		
		jt = new JTextField(5);
		texts[0] = jt;
		sp.add(jt);
		
		sp.add(new JLabel("침대번호"));
		
		jt = new JTextField(5);
		texts[1] = jt;
		sp.add(jt);
		
		JButton jb = createButton("예약", e -> reservate());
		sp.add(jb);
		
		add(sp, BorderLayout.SOUTH);
	}
	
	private void chooseFloor() {
		String preFloor = currentFloor;
		currentFloor = floor.getSelectedItem().toString();

		if(preFloor.equals(currentFloor))
			return;
		
		this.remove(cp);
		
		cp = new JPanel(new BorderLayout());
		cp.setBorder(new EmptyBorder(0, 10, 0, 10));
		c_cp = new JPanel(new GridLayout(1, 10));
		c_cp.setBorder(new LineBorder(Color.black));
		
		JPanel temp;
		JLabel jl;
		int num = (Integer.parseInt(currentFloor) + 1) / 2;
		
		for(int i = 1; i <= (6 - num) * 2; i++) {
			temp = new JPanel(new FlowLayout());
			
			jl = new JLabel(currentFloor + "0" + i + "호");
			if(i == 10)
				jl.setText(currentFloor + "10호");
			
			temp.add(jl);

			String text = jl.getText();
			JButton jb = createButton("", e -> chooseRoom(text));
			jb.setPreferredSize(new Dimension(100, 175));
			
			temp.add(jb);
			
			c_cp.add(temp);
		}
		
		this.setSize(new Dimension(1350 - 220 * num, 350));
		sp.setBorder(new EmptyBorder(0, 1040 - 220 * num, 0, 0));
		
		cp.add(c_cp);
		add(cp, BorderLayout.CENTER);
		this.revalidate();
	}
	
	private void chooseRoom(String text) {
		if(bed != null)
			bed.dispose();
		
		bed = new BedForm(this, text);
		bed.setVisible(true);
	}
	
	private void reservate() {
		boolean flag = true;
		for(JTextField tf : texts) {
			if(tf.getText().equals("")) {
				flag = false;
				break;
			}
		}
		
		if(!flag) {
			showDialog(this, "침대를 선택해주세요.", "메시지", JOptionPane.ERROR);
			return;
		}
		
		try {
			int s_no;
			
			PreparedStatement pstmt = con.prepareStatement("select s_no from sickroom where s_room = ?");
			pstmt.setInt(1, Integer.parseInt(texts[0].getText()));
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next())
				s_no = rs.getInt("s_no");
			else
				return;
			
			pstmt = con.prepareStatement("insert into hospitalization values(0, ?, ?, ?, ?, 0, 0, 0)");
			pstmt.setInt(1, number);
			pstmt.setInt(2, s_no);
			pstmt.setInt(3, Integer.parseInt(texts[1].getText()));
			pstmt.setString(4, date);
			pstmt.execute();
			
			showDialog(this, "입원이 완료되었습니다", "메시지", JOptionPane.INFORMATION_MESSAGE);
			closeThisEvent();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void closeThisEvent() {
		int size = frames.size() - 1;
		
		if(bed != null)
			bed.dispose();
		frames.get(size).dispose();
		frames.remove(size);
		frames.get(size - 1).setVisible(true);
	}

}
