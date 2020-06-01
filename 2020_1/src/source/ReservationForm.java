package source;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class ReservationForm extends Base {
	
	@SuppressWarnings("rawtypes")
	public JComboBox[] boxes = new JComboBox[3];
	public JTextField date = new JTextField();
	public CalendarForm cal;
	JTextField countTextField = new JTextField();
	JTable table = new JTable();
	JScrollPane js = new JScrollPane();
	String[] checkList = {"선택안함", "CT검사", "MRI검사", "UBT검사", "X-RAY검사", "초음파검사"};
	JLabel imageLabel = new JLabel();
	ReservationForm reservation = this;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ReservationForm() {
		super("진료예약", 620, 585, 0, 5);
		
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
		JPanel np = new JPanel(new GridLayout(3, 4, 0, 10));
		np.setBorder(new EmptyBorder(5, 0, 0, 0));
		
		JTextField textField;
		
		np.add(createLabel("환자번호", JLabel.CENTER, new Font("굴림", Font.BOLD, 15)));
		textField = new JTextField(Integer.toString(number));
		textField.setHorizontalAlignment(JLabel.CENTER);
		textField.setOpaque(true);
		textField.setBackground(Color.white);
		textField.setEnabled(false);
		np.add(textField);
		
		np.add(createLabel("환자명", JLabel.CENTER, new Font("굴림", Font.BOLD, 15)));
		textField = new JTextField(name);
		textField.setHorizontalAlignment(JLabel.CENTER);
		textField.setOpaque(true);
		textField.setBackground(Color.WHITE);
		textField.setEnabled(false);
		np.add(textField);
		
		np.add(createLabel("진료과", JLabel.CENTER, new Font("굴림", Font.BOLD, 15)));
		boxes[0] = new JComboBox<>();
		boxes[0].addActionListener(e -> selectBox1(boxes[0].getSelectedItem().toString()));
		np.add(boxes[0]);
		
		np.add(createLabel("의사", JLabel.CENTER, new Font("굴림", Font.BOLD, 15)));
		boxes[1] = new JComboBox<>();
		boxes[1].addActionListener(e -> loadTable());
		np.add(boxes[1]);
		
		initBoxes();
		
		np.add(createLabel("날짜", JLabel.CENTER, new Font("굴림", Font.BOLD, 15)));
		date.setOpaque(true);
		date.setBackground(Color.white);
		date.setHorizontalAlignment(JLabel.CENTER);
		date.setEnabled(false);
		date.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(cal != null)
					cal.dispose();
				
				cal = new CalendarForm(reservation);
				cal.setVisible(true);
			}
			
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		np.add(date);
		
		np.add(createLabel("진료이력", JLabel.CENTER, new Font("굴림", Font.BOLD, 15)));
		countTextField.setOpaque(true);
		countTextField.setBackground(Color.white);
		countTextField.setHorizontalAlignment(JLabel.CENTER);
		countTextField.setEnabled(false);
		np.add(countTextField);
		
		add(np, BorderLayout.NORTH);
		
		//Center
		JPanel cp = new JPanel(new BorderLayout());
		cp.setBorder(new EmptyBorder(0, 2, 0, 5));
		
		JPanel c_cp1 = new JPanel(new BorderLayout());
		
		String[] header = {"진료과", "의사", "진료날짜", "시간", "시간대"};
		String[][] contents = {};
		table = new JTable(new DefaultTableModel(contents, header)) {
			public boolean isCellEditable(int row, int column) {                
	                return false;               
	        };
		};
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		
		table.getTableHeader().setReorderingAllowed(false);
		for(int i = 0; i < header.length; i++)
			table.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		
		table.getColumnModel().getColumn(0).setPreferredWidth(40);
		table.getColumnModel().getColumn(4).setPreferredWidth(40);
		
		js = new JScrollPane(table);
		js.setPreferredSize(new Dimension(400, 275));
		loadTable();
		
		c_cp1.add(js, BorderLayout.NORTH);
		cp.add(c_cp1);

		JPanel c_cp2 = new JPanel(new FlowLayout());
		c_cp2.setBorder(new EmptyBorder(0, 0, 0, 350));
		c_cp2.add(createLabel("검사", JLabel.CENTER, new Font("굴림", Font.BOLD, 15)));
		
		boxes[2] = new JComboBox();
		for(String s : checkList)
			boxes[2].addItem(s);
		boxes[2].addActionListener(e -> changeImage());
		c_cp2.add(boxes[2]);
		
		imageLabel.setPreferredSize(new Dimension(100, 100));
		imageLabel.setBorder(new EtchedBorder());
		c_cp2.add(imageLabel);
		cp.add(c_cp2, BorderLayout.SOUTH);
		
		add(cp, BorderLayout.CENTER);
		
		//South
		JPanel sp = new JPanel(new FlowLayout());
		sp.setBorder(new EmptyBorder(0, 460, 0, 0));
		
		sp.add(createButton("예약", e -> reservate()));
		sp.add(createButton("닫기", e -> 	closeThisEvent()));
		
		add(sp, BorderLayout.SOUTH);
	}
	
	@SuppressWarnings("unchecked")
	private void initBoxes() {
		String[] sections = {"내과", "정형외과", "안과", "치과"};
		
		for(String s : sections)
			boxes[0].addItem(s);
	}
	
	@SuppressWarnings("unchecked")
	private void selectBox1(String section) {
		boxes[1].removeAllItems();
		
		try {
			PreparedStatement pstmt = con.prepareStatement("select d_name from doctor where d_section = ? group by d_name");
			pstmt.setString(1, section);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next())
				boxes[1].addItem(rs.getString("d_name"));
			
			pstmt = con.prepareStatement("select count(r_section) as count from reservation where p_no = ? and r_section = ?");
			pstmt.setString(1, Integer.toString(number));
			pstmt.setString(2, section);
			rs = pstmt.executeQuery();
			rs.next();
			
			int count = rs.getInt("count");
			if(count == 0)
				countTextField.setText("초진");
			else
				countTextField.setText("재진");
			
			loadTable();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void loadTable() {
		DefaultTableModel defaultModel = (DefaultTableModel) table.getModel();
		defaultModel.setRowCount(0);
		
		if(date.getText().equals("") || boxes[1].getItemCount() == 0)
			return;
		
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			
			try {
				date = format.parse(this.date.getText());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			format = new SimpleDateFormat("EEE", Locale.KOREAN);
			String day = format.format(date);
			
			PreparedStatement pstmt = con.prepareStatement("select d_no, d_time from doctor where d_name = ? and d_day = ?");
			pstmt.setString(1, boxes[1].getSelectedItem().toString());
			pstmt.setString(2, day);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				String d_time = rs.getString("d_time");
				
				if(d_time.equals("오전")) {
					for(int i = 0; i < 7; i++) {
						String time = Integer.toString(i / 2 + 9);
						time += ":" + (i % 2 == 1 ? "30" : "00");
						
						String[] row = { boxes[0].getSelectedItem().toString(), boxes[1].getSelectedItem().toString(),
								this.date.getText(), time, d_time };
						defaultModel.addRow(row);
					}
				}
				
				else {
					for(int i = 0; i < 7; i++) {
						String time = Integer.toString(i / 2 + 14) + ":" + (i % 2 == 1 ? "30" : "00");
						
						String[] row = { boxes[0].getSelectedItem().toString(), boxes[1].getSelectedItem().toString(),
								this.date.getText(), time, d_time };
						
						defaultModel.addRow(row);
					}

					table.setModel(defaultModel);
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void changeImage() {
		ImageIcon icon = null;
		
		switch(boxes[2].getSelectedItem().toString()) {
		case "선택안함":
			icon = new ImageIcon();
			break;
			
		case "CT검사":
			icon = new ImageIcon("지급자료/이미지/CT검사.jpg");
			break;
			
		case "MRI검사":
			icon = new ImageIcon("지급자료/이미지/MRI검사.jpg");
			break;
			
		case "UBT검사":
			icon = new ImageIcon("지급자료/이미지/UBT검사.jpg");
			break;
			
		case "X-RAY검사":
			icon = new ImageIcon("지급자료/이미지/X-RAY검사.jpg");
			break;
			
		case "초음파검사":
			icon = new ImageIcon("지급자료/이미지/초음파검사.jpg");
			break;
		}
		
		Image image = icon.getImage();
		if(image != null)
			image = image.getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH);
		imageLabel.setIcon(new ImageIcon(image));
	}
	
	private void reservate() {
		if(table.getSelectedRow() == -1) {
			showDialog(this, "예약할 시간을 정해주세요.", "메시지", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			PreparedStatement pstmt = con.prepareStatement("select p_no, r_section, r_date, r_time from reservation where p_no = ? or r_date = ?");
			pstmt.setString(1, Integer.toString(number));
			pstmt.setString(2, this.date.getText());
			ResultSet rs = pstmt.executeQuery();
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			
			try {
				date = format.parse(this.date.getText());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			String time = table.getValueAt(table.getSelectedRow(), 3).toString();
			while(rs.next()) {
				int p_no = rs.getInt("p_no");
				String r_section = rs.getString("r_section");
				Date r_date = rs.getDate("r_date");
				String r_time = rs.getString("r_time");
				
				if(r_date.equals(date)) {
					if(number == p_no) {
						if(r_section.equals(boxes[0].getSelectedItem().toString())) {
							showDialog(this, "같은날짜에 같은 과 진료를 볼 수 없습니다.", "메시지", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						if(r_time.equals(time)) {
							showDialog(this, "같은날짜에 같은 시간 진료를 볼 수 없습니다.", "메시지", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					
					else if(r_time.equals(time)) {
						showDialog(this, "이미 예약되어있는 시간대입니다.", "메시지", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}

			pstmt = con.prepareStatement("select d_no from doctor where d_name = ?");
			pstmt.setString(1, boxes[1].getSelectedItem().toString());
			rs = pstmt.executeQuery();
			rs.next();
			int d_no = rs.getInt("d_no");
			
			pstmt = con.prepareStatement("insert into reservation (r_no, p_no, d_no, r_section, r_date, r_time, e_no) values(0, ?, ?, ?, ?, ?, ?)");
			pstmt.setInt(1, number);
			pstmt.setInt(2, d_no);
			pstmt.setString(3, boxes[0].getSelectedItem().toString());
			pstmt.setDate(4, new java.sql.Date(date.getTime()));
			pstmt.setString(5, time);
			pstmt.setInt(6, boxes[2].getSelectedIndex() + 1);
			pstmt.execute();

			showDialog(this, "예약되었습니다.", "메시지", JOptionPane.INFORMATION_MESSAGE);
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void closeThisEvent() {
		int size = frames.size() - 1;
		
		if(cal != null)
			cal.dispose();
		frames.get(size).dispose();
		frames.remove(size);
		frames.get(size - 1).setVisible(true);
	}
	
}
