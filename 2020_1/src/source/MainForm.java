package source;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class MainForm extends Base {

	public MainForm() {
		super("메인", 285, 350, 0, 0);

		this.addWindowListener(new WindowListener() {
			@Override
			public void windowClosing(WindowEvent e) {
				new LoginForm().setVisible(true);
			}
			
			public void windowClosed(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
		});
		
		//North
		JLabel jLabel = createLabel(name + "환자", JLabel.CENTER, new Font("굴림", Font.BOLD, 22));
		jLabel.setBorder(new EmptyBorder(5, 0, 10, 0));
		add(jLabel, BorderLayout.NORTH);
		
		//South
		JPanel sp = new JPanel(new GridLayout(5, 1));
		sp.add(createButton("진료예약", e -> reservate()));
		sp.add(createButton("입퇴원 신청", e -> apply()));
		sp.add(createButton("예약현황", e -> current()));
		sp.add(createButton("진료과별 분석", e -> analysis()));
		sp.add(createButton("종료", e -> {
			frames.remove(0);
			this.dispose();
			new LoginForm().setVisible(true);
		}));
		sp.setBorder(new EmptyBorder(0, 0, 5, 0));
		
		add(sp);
	}
	
	private void reservate() {
		dispose();
		
		ReservationForm reservation = new ReservationForm();
		Base.frames.add(reservation);
		reservation.setVisible(true);
	}
	
	private void apply() {
		dispose();
		
		Base form = null;
		int h_no = 0;
		String h_sday = null;
		boolean flag = false;
		try {
			PreparedStatement pstmt = con.prepareStatement("select h_no, h_sday, h_fday from hospitalization where p_no = ? and h_fday = '0'");
			pstmt.setInt(1, number);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				h_no = rs.getInt("h_no");
				h_sday = rs.getString("h_sday");
				if(rs.getString("h_fday").equals("0"))
					flag = true;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		if(flag)
			form = new DischargingForm(h_no, h_sday);
		else
			form = new HospitalizingForm();
		
		Base.frames.add(form);
		form.setVisible(true);
	}
	
	private void current() {
		dispose();
		
		CurrentForm current = new CurrentForm();
		Base.frames.add(current);
		current.setVisible(true);
	}
	
	private void analysis() {
		dispose();
		
		AnalysisForm analysis = new AnalysisForm();
		Base.frames.add(analysis);
		analysis.setVisible(true);
	}
	
}
