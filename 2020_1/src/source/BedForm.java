package source;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class BedForm extends Base {
	
	HospitalizingForm hospitalizing;
	JButton[] buttons = new JButton[6];
	String room;

	public BedForm(HospitalizingForm hospitalizing, String room) {
		super("침대", 0, 0, 0, 0);
		this.hospitalizing = hospitalizing;
		this.room = room;
		
		int roomno = Integer.parseInt(room.substring(0, 1));
		
		if(roomno == 1)
			this.setSize(new Dimension(100, 290));
		else if(roomno == 2)
			this.setSize(new Dimension(220, 290));
		else
			this.setSize(new Dimension(roomno * (110 - roomno), 290));
		
		//North
		JLabel jl = createLabel(room, JLabel.CENTER, new Font("HY견고딕", Font.PLAIN, 22));
		add(jl, BorderLayout.NORTH);
		
		//Center
		JPanel cp = new JPanel(new GridLayout(1, roomno));
		
		for(int i = 1; i <= roomno; i++) {
			JPanel temp = new JPanel(new FlowLayout());
			
			jl = new JLabel(Integer.toString(i));
			jl.setPreferredSize(new Dimension(50, 10));
			jl.setHorizontalAlignment(JLabel.CENTER);

			int j = i;
			JButton jb = createButton("", e -> chooseRoom(Integer.toString(j)));
			buttons[i - 1] = jb;
			jb.setPreferredSize(new Dimension(102, 200));
			
			if(roomno == 1) {
				cp = new JPanel(new BorderLayout());
				cp.setPreferredSize(new Dimension(90, 200));
				
				temp.add(jl);
				temp.setPreferredSize(new Dimension(90, 20));
				
				cp.add(temp, BorderLayout.NORTH);
				cp.add(jb, BorderLayout.CENTER);
				
				add(cp);
				return;
			}
			
			
			temp.add(jl, JLabel.CENTER);
			temp.add(jb);
			
			cp.add(temp);
		}
		
		try {
			int s_no;
			
			PreparedStatement pstmt = con.prepareStatement("select s_no from sickroom where s_room = ?");
			pstmt.setInt(1, Integer.parseInt(room.substring(0, 3)));
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			
			s_no = rs.getInt("s_no");
			
			pstmt = con.prepareStatement("select h_bedno from hospitalization where s_no = ?");
			pstmt.setInt(1, s_no);
			rs = pstmt.executeQuery();
			
			while(rs.next())
				buttons[rs.getInt("h_bedno") - 1].setEnabled(false);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		add(cp, BorderLayout.CENTER);
	}
	
	private void chooseRoom(String roomno) {
		hospitalizing.texts[0].setText(this.room.substring(0, 3));
		hospitalizing.texts[1].setText(roomno);
		this.dispose();
		hospitalizing.bed = null;
	}
	
}
