package source;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class DischargingForm extends Base {

	public DischargingForm() {
		super("퇴원", 300, 370, 0, 0);
		this.addWindowListener(defaultWinListener);
		
		//North
		JPanel np = new JPanel(new BorderLayout());
		np.add(createLabel("계산서", JLabel.CENTER, new Font("굴림", Font.BOLD, 25)), BorderLayout.NORTH);
		
		add(np, BorderLayout.NORTH);
		
		//Center
		JPanel cp = new JPanel(new GridLayout(1, 2));
		String[] labels = "환자번호,환자명,호실,입원날짜,퇴원날짜,식사횟수,총금액".split(",");
		
		for(int i = 0; i < 7; i++) {
			JPanel temp = new JPanel(new FlowLayout());
			JLabel jl = new JLabel(labels[i]);
			JTextField jt = new JTextField(15);
			
			if(i != 5)
				jt.setEnabled(false);
			
			temp.add(jl);
			temp.add(jt);
			cp.add(temp);
		}
		
		add(cp, BorderLayout.CENTER);
		
		//South
	}
	
}
