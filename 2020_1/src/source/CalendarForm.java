package source;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class CalendarForm extends Base {
	
	ReservationForm reservation;
	Color[] colors = { Color.RED, Color.black, Color.black, Color.black, Color.black, Color.black, Color.blue };
	String[] weekday = "일,월,화,수,목,금,토".split(",");
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	JLabel titleLabel = new JLabel();
	JPanel cp;
	JPanel c_cp2 = new JPanel();
	String string;

	public CalendarForm(ReservationForm reservation) {
		super("기간선택", 420, 450, 0, 0);
		this.reservation = reservation;
		
		this.string = reservation.date.getText();
		if(this.string.equals(""))
			this.string = format.format(new Date());

		//Notrh
		JPanel np = new JPanel(new FlowLayout());
		np.setBorder(new EmptyBorder(0, 0, 5, 0));
		
		np.add(createButton("◀", e -> changeMonth(-1)));
		changeTitleLabel();
		np.add(titleLabel);
		np.add(createButton("▶", e -> changeMonth(1)));
		
		add(np, BorderLayout.NORTH);
		
		//Center
		cp = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
		
		JPanel c_cp1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 45, 5));
		
		for(int i = 0; i < 7; i++) {
			JLabel label = new JLabel(weekday[i]);
			label.setForeground(colors[i]);
			c_cp1.add(label);
		}
		
		cp.add(c_cp1, BorderLayout.NORTH);
		cp.add(c_cp2, BorderLayout.CENTER);
		changeMonth(0);
		
		add(cp);
	}
	
	private void changeTitleLabel() {
		SimpleDateFormat format = new SimpleDateFormat("MM");
		
		Date date = null;
		try {
			date = this.format.parse(this.string);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		String text = format.format(date);
		if(!text.equals("10"))
			text.replace("0", "");
		
		format = new SimpleDateFormat("yyyy 년 ");
		text = format.format(date) + text + " 월";
		titleLabel.setText(text);
	}
	
	private void changeMonth(int change) {
		Calendar cal = Calendar.getInstance();
		
		try {
			cal.setTime(this.format.parse(this.string));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + change);
		this.string = this.format.format(new Date(cal.getTimeInMillis()));
		
		changeTitleLabel();
		
		cp.remove(c_cp2);
		c_cp2 = new JPanel(new GridLayout(7, 7));

		cal.set(Calendar.DATE, 1);
		int month = cal.get(Calendar.MONTH) - 1;
		int day = cal.get(Calendar.DAY_OF_WEEK);
		Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.set(Calendar.DATE, nowCalendar.get(Calendar.DATE) - 1);
		
		for(int i = 1; i < day; i++)
			c_cp2.add(new JLabel());
		
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				if (cal.get(Calendar.MONTH) - 1 == month) {
					JButton jb = new JButton(Integer.toString(cal.get(Calendar.DATE)));
					
					if(cal.getTimeInMillis() < nowCalendar.getTimeInMillis())
						jb.setEnabled(false);
					jb.setPreferredSize(new Dimension(58, 50));
					jb.setForeground(colors[j]);
					jb.addActionListener(e -> selectDay(jb));
					
					c_cp2.add(jb);					
				}
				
				else
					c_cp2.add(new JLabel());
					
				
				cal.add(Calendar.DATE, 1);
			}
		}
		c_cp2.add(new JLabel());
		cp.add(c_cp2, BorderLayout.CENTER);
	}
	
	private void selectDay(JButton jb) {
		String text = jb.getText();
		
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(this.format.parse(this.string));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		cal.set(Calendar.DATE, Integer.parseInt(text));
		
		SimpleDateFormat format = new SimpleDateFormat("EEE", Locale.KOREAN);
		String day = format.format(new Date(cal.getTimeInMillis()));
		
		try {
			PreparedStatement pstmt = con.prepareStatement("select d_time from doctor where d_name = ? and d_day = ?");
			pstmt.setString(1, reservation.boxes[1].getSelectedItem().toString());
			pstmt.setString(2, day);
			ResultSet rs = pstmt.executeQuery();
			
			if(!rs.next()) {
				showDialog(this, "해당날짜에 해당 교수님 진료는 없습니다", "메시지", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		reservation.date.setText(this.format.format(new Date(cal.getTimeInMillis())));
		this.dispose();
		reservation.loadTable();
		reservation.cal = null;
	}

}
