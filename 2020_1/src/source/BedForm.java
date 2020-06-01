package source;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class BedForm extends Base {
	
	HospitalizingForm hospitalizing;

	public BedForm(HospitalizingForm hospitalizing, String room) {
		super("침대", 0, 0, 0, 0);
		this.hospitalizing = hospitalizing;
		
		int roomno = Integer.parseInt(room.substring(0, 1));
		
		if(roomno == 1)
			this.setSize(new Dimension(100, 290));
		else if(roomno == 2)
			this.setSize(new Dimension(220, 290));
		else
			this.setSize(new Dimension(roomno * (110 - roomno), 290));
		System.out.println(roomno);
		
		//North
		JLabel jl = createLabel(room, JLabel.CENTER, new Font("HY견고딕", Font.PLAIN, 22));
		add(jl, BorderLayout.NORTH);
		
		//Center
		JPanel cp = new JPanel(new GridLayout(1, roomno));
		
		for(int i = 1; i <= roomno; i++) {
			JPanel temp = new JPanel(new FlowLayout());
			
			if(roomno == 1) {
				cp = new JPanel(new BorderLayout());
				cp.setSize(new Dimension(0, 80));
			}
			temp.setBorder(new LineBorder(Color.black));
			
			jl = new JLabel(Integer.toString(i));
			jl.setPreferredSize(new Dimension(80, 10));
			jl.setHorizontalAlignment(JLabel.CENTER);
			temp.add(jl, JLabel.CENTER);
			
			JButton jb = new JButton();
			jb.setPreferredSize(new Dimension(102, 200));
			int j = i;
			jb.addActionListener(e -> chooseRoom(Integer.toString(j)));
			temp.add(jb);
			
			cp.add(temp);
		}
		
		add(cp, BorderLayout.CENTER);
	}
	
	private void chooseRoom(String room) {
		
	}
	
}
