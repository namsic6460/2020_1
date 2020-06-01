package source;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class CurrentForm extends Base {
	
	JTable table;

	public CurrentForm() {
		super("진료예약현황", 550, 500, 0, 0);
		this.addWindowListener(defaultWinListener);
		
		//North
		JLabel jl = createLabel(name + "님 진료예약현황", JLabel.CENTER, new Font("굴림", Font.BOLD, 22));
		add(jl, BorderLayout.NORTH);
		
		//Center
		JPanel cp = new JPanel(new BorderLayout());
		cp.setBorder(new EmptyBorder(10, 5, 5, 5));
		
		String[] header = {"진료과", "의사", "진료날짜", "시간"};
		String[][] contents = {};
		table = new JTable(new DefaultTableModel(contents, header)) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		
		for(int i = 0; i < header.length; i++)
			table.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		
		table.getColumnModel().getColumn(0).setPreferredWidth(30);
		table.getColumnModel().getColumn(1).setPreferredWidth(20);
		table.getColumnModel().getColumn(3).setPreferredWidth(30);
		
		try {
			PreparedStatement pstmt = con.prepareStatement("select r_section, d_no, r_date, r_time from reservation where p_no = ?");
			pstmt.setInt(1, number);
			ResultSet rs = pstmt.executeQuery();
			
			DefaultTableModel defaultModel = (DefaultTableModel) table.getModel();
			Object[] row = new Object[4];
			ResultSet rs_;
			while(rs.next()) {
				row[0] = rs.getString("r_section");
				
				pstmt = con.prepareStatement("select d_name from doctor where d_no = ?");
				pstmt.setInt(1, rs.getInt("d_no"));
				rs_ = pstmt.executeQuery();
				rs_.next();
				row[1] = rs_.getString("d_name");
				
				row[2] = rs.getString("r_date");
				row[3] = rs.getString("r_time");
				
				defaultModel.addRow(row);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		JScrollPane js = new JScrollPane(table);
		
		cp.add(js);
		add(cp, BorderLayout.CENTER);
		
		//South
		JPanel sp = new JPanel(new FlowLayout());
		sp.setBorder(new EmptyBorder(0, 365, 0, 0));
		
		sp.add(createButton("예약취소", e -> cancle()));
		sp.add(createButton("닫기", e -> close()));
		
		add(sp, BorderLayout.SOUTH);
	}
	
	private void cancle() {
		int row = table.getSelectedRow();
		
		if(row == -1) {
			showDialog(this, "삭제할 예약을 선택해주세요", "메시지", JOptionPane.ERROR_MESSAGE);
		}
		
		try {
			PreparedStatement pstmt = con.prepareStatement("delete from reservation where r_date = ? and r_time = ?");
			pstmt.setString(1, table.getValueAt(row, 2).toString());
			pstmt.setString(2, table.getValueAt(row, 3).toString());
			pstmt.execute();
			
			DefaultTableModel defaultModel = (DefaultTableModel) table.getModel();
			defaultModel.removeRow(row);
			
			showDialog(this, "취소되었습니다", "메시지", JOptionPane.INFORMATION_MESSAGE);
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void close() {
		int size = frames.size() - 1;
		
		frames.get(size).dispose();
		frames.remove(size);
		frames.get(size - 1).setVisible(true);
	}
	
}
