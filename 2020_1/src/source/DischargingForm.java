package source;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class DischargingForm extends Base {

	JTextField mealJt;
	JTextField priceJt;
	long days = 0;
	int roomCost;
	int h_no;
	String h_sday;
	String h_fday;
	
	public DischargingForm(int h_no, String h_sday) {
		super("퇴원", 300, 350, 0, 0);
		this.addWindowListener(defaultWinListener);
		this.h_no = h_no;
		this.h_sday = h_sday;
		
		//North
		JPanel np = new JPanel(new BorderLayout());
		np.add(createLabel("계산서", JLabel.CENTER, new Font("굴림", Font.BOLD, 25)), BorderLayout.NORTH);
		
		add(np, BorderLayout.NORTH);
		
		//Center
		JPanel cp = new JPanel(new GridLayout(7, 2));
		String[] labels = "환자번호,환자명,호실,입원날짜,퇴원날짜,식사횟수,총금액".split(",");

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		for(int i = 0; i < 7; i++) {
			JPanel temp = new JPanel(new FlowLayout());
			
			JLabel jl = new JLabel(labels[i]);
			jl.setPreferredSize(new Dimension(75, 25));
			jl.setHorizontalAlignment(JLabel.CENTER);
			
			JTextField jt = new JTextField(15);
			
			if(i != 5)
				jt.setEnabled(false);
			
			PreparedStatement pstmt;
			ResultSet rs;
			
			switch(i) {
			case 0:
				jt.setText(Integer.toString(number));
				break;
				
			case 1:
				jt.setText(name);
				break;
				
			case 2:
				try {
					int s_no;
					
					pstmt = con.prepareStatement("select s_no from hospitalization where p_no = ?");
					pstmt.setInt(1, number);
					rs = pstmt.executeQuery();
					rs.next();
					
					s_no = rs.getInt("s_no");
					
					pstmt = con.prepareStatement("select s_room, s_people from sickroom where s_no = ?");
					pstmt.setInt(1, s_no);
					rs = pstmt.executeQuery();
					rs.next();
					
					jt.setText(rs.getString("s_room"));
					roomCost = 300000 - (Integer.parseInt(rs.getString("s_people")) - 1) * 50000;
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
				
			case 3:
				jt.setText(this.h_sday);
				
				try {
					Calendar cal = Calendar.getInstance();
					cal.setTime(format.parse(this.h_sday));
					days = cal.getTimeInMillis();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				break;
				
			case 4:
				Calendar cal = Calendar.getInstance();
				this.h_fday = format.format(cal.getTime());
				jt.setText(h_fday);
				
				days = (long) Math.floor((cal.getTimeInMillis() - days) / 86400000.0);
				break;
				
			case 5:
				mealJt = jt;
				mealJt.addKeyListener(new KeyListener() {
					@Override
					public void keyReleased (KeyEvent e) {
						
					}
					
					public void keyTyped(KeyEvent e) {}
					public void keyPressed(KeyEvent e) {
						int meal = 0;
						String text = mealJt.getText();
						
						if(e.getKeyChar() == 8) {
							if(text.length() > 0)
								text = text.substring(0, text.length() - 1);
						}
						
						else
							text += e.getKeyChar();
						
						try {
							if(text.equals("")) {
								calculatePrice(0);
								return;
							}
							meal = Integer.parseInt(text);
						} catch(NumberFormatException e_) {
							priceJt.setText("");
							return;
						}
						
						calculatePrice(meal);
					}
				});
				break;
				
			case 6:
				priceJt = jt;
				calculatePrice(0);

				break;
			}
			
			temp.add(jl, JLabel.CENTER);
			temp.add(jt);
			cp.add(temp);
		}
		
		add(cp, BorderLayout.CENTER);
		
		//South
		JPanel sp = new JPanel(new FlowLayout());
		
		sp.add(createButton("퇴원", e -> discharge()));
		sp.add(createButton("닫기", e -> {
			int size = frames.size() - 1;
			
			frames.get(size).dispose();
			frames.remove(size);
			frames.get(size - 1).setVisible(true);
		}));
		
		add(sp, BorderLayout.SOUTH);
	}
	
	private void discharge() {
		String meal = mealJt.getText();
		int meal_;
		
		if(meal.equals("")) {
			showDialog(this, "식사 횟수를 입력해주세요.", "메시지", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			meal_ = Integer.parseInt(meal);
		} catch(NumberFormatException e) {
			showDialog(this, "숫자로만 입력하세요.", "메시지", JOptionPane.ERROR_MESSAGE);
			mealJt.requestFocusInWindow();
			return;
		}
		
		if(meal_ > days * 3) {
			showDialog(this, (days * 3) + "번이 최대입니다.", "메시지", JOptionPane.ERROR_MESSAGE);
			mealJt.requestFocusInWindow();
			return;
		}
		
		if(h_sday.equals(h_fday)) {
			showDialog(this, "당일 퇴원은 불가합니다.", "메시지", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			PreparedStatement pstmt = con.prepareStatement("update hospitalization set h_fday = ?, h_meal = ?, h_amount = ? where h_no = ?");
			pstmt.setString(1, h_fday);
			pstmt.setInt(2, meal_);
			pstmt.setInt(3, Integer.parseInt(priceJt.getText()));
			pstmt.setInt(4, h_no);
			pstmt.execute();
			
			showDialog(this, "퇴원이 완료되었습니다.", "메시지", JOptionPane.INFORMATION_MESSAGE);
			
			
			int size = frames.size() - 1;
			
			frames.get(size).dispose();
			frames.remove(size);
			frames.get(size - 1).setVisible(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void calculatePrice(int meal) {
		long total = (roomCost * days) + (meal * 5000);
		priceJt.setText(Long.toString(total));
	}
	
}
