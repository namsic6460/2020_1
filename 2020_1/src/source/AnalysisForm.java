package source;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class AnalysisForm extends Base {
	
	HashMap<String, HashMap<String, Integer>> reservations;
	JPanel cp, c_cp;

	public AnalysisForm() {
		super("통계", 800, 400, 0, 0);
		this.addWindowListener(defaultWinListener);
		reservations = new HashMap<>();
		
		//West
		JPanel wp = new JPanel(new BorderLayout());
		
		String[] header = {"진료과", "진료건수"};
		String[][] contents = {};
		JTable table = new JTable(new DefaultTableModel(contents, header)) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		
		for(int i = 0; i < header.length; i++)
			table.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		
		try {
			PreparedStatement pstmt = con.prepareStatement("select d_no, r_section from reservation");
			ResultSet rs = pstmt.executeQuery();
			
			ResultSet rs_;
			int d_no;
			String r_section, d_name;
			HashMap<String, Integer> temp;
			while(rs.next()) {
				d_no = rs.getInt("d_no");
				r_section = rs.getString("r_section");
				
				if(!reservations.containsKey(r_section))
					reservations.put(r_section, new HashMap<>());
				
				temp = reservations.get(r_section);
				
				pstmt = con.prepareStatement("select d_name from doctor where d_no = ?");
				pstmt.setInt(1, d_no);
				rs_ = pstmt.executeQuery();
				rs_.next();
				
				d_name = rs_.getString("d_name");
				
				if(temp.containsKey(d_name))
					temp.put(d_name, temp.get(d_name) + 1);
				else
					temp.put(d_name, 1);
			}
			
			DefaultTableModel defaultModel = (DefaultTableModel) table.getModel();
			Object[] row = new Object[2];
			
			TreeMap<String, HashMap<String, Integer>> sortedMap = new TreeMap<>(reservations);
			for(Entry<String, HashMap<String, Integer>> entry : sortedMap.entrySet()) {
				int sum = 0;
				
				row[0] = entry.getKey();
				for(int count : entry.getValue().values())
					sum += count;
				row[1] = sum;
				
				defaultModel.addRow(row);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				loadGraph(table.getValueAt(table.getSelectedRow(), 0).toString());
			}
			
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		
		JScrollPane js = new JScrollPane(table);
		js.setPreferredSize(new Dimension(200, 400));
		
		wp.add(js);
		
		add(wp, BorderLayout.WEST);
		
		//Center
		cp = new JPanel();
		add(cp);
		
		table.setRowSelectionInterval(0, 0);
		loadGraph(table.getValueAt(0, 0).toString());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<String> sortByValue(final Map<String, Integer> map) {
        List<String> list = new ArrayList<>();
        list.addAll(map.keySet());
        
        Collections.sort(list, new Comparator() {
			public int compare(Object o1,Object o2) {
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);
                 
                return ((Comparable) v2).compareTo(v1);
            }
        });

        return list;
    }
	
	private void loadGraph(String section) {
		System.out.println(section);
		List<String> keySet = sortByValue(reservations.get(section));
		double rate = 230.0 / reservations.get(section).get(keySet.get(0));

		cp = new JPanel(new FlowLayout());
		c_cp = new JPanel(new GridLayout(1, 3));
		c_cp.setPreferredSize(new Dimension(580, 330));
		c_cp.setBorder(new EmptyBorder(0, 40, 0, 40));
		
		JLabel jl = createLabel(section, JLabel.CENTER, new Font("굴림", Font.BOLD, 22));
		jl.setPreferredSize(new Dimension(500, 25));
		jl.setHorizontalAlignment(JLabel.CENTER);
		cp.add(jl);
		
		int i = 0;
		int value, height;
		for(String key : keySet) {
			value = reservations.get(section).get(key);
			height = (int) (rate * value);
			System.out.println(rate + ", " + value + ", " + height);
			
			JPanel temp = new JPanel(new FlowLayout());
			temp.setBorder(new EmptyBorder(230 - height, 0, 20, 0));
			
			jl = createLabel(Integer.toString(value), JLabel.CENTER, new Font("HY견고딕", Font.PLAIN, 13));
			jl.setPreferredSize(new Dimension(150, 30));
			temp.add(jl);
			
			JButton jb = new JButton();
			jb.setEnabled(false);
			jb.setPreferredSize(new Dimension(40, height));
			jb.setBorder(new MatteBorder(2, 2, 2, 2, Color.black));
			if(i == 0)
				jb.setBackground(Color.red);
			else if(i == 1)
				jb.setBackground(Color.ORANGE);
			else
				jb.setBackground(Color.yellow);
			
			temp.add(jb);
			
			jl = createLabel(key, JLabel.CENTER, new Font("HY견고딕", Font.PLAIN, 13));
			jl.setPreferredSize(new Dimension(150, 30));
			temp.add(jl);
			
			c_cp.add(temp);
			i++;
		}
		cp.add(c_cp);
		
		this.remove(cp);
		add(cp, BorderLayout.CENTER);
		
		validate();
	}
	
}
