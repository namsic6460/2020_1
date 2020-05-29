package setting;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.swing.JOptionPane;

public class Db {
	
	public static Connection con;
	public static Statement stmt;

	public static void main(String[] args) throws IOException {
		try {			
			con = DriverManager.getConnection("jdbc:mysql://localhost/?characterEncoding=UTF-8&serverTimezone=UTC", "root", "1234");
			stmt = con.createStatement();
			create_db();
			String [] t = {"patient", "doctor", "examination", "reservation", "sickroom", "hospitalization"};
			PreparedStatement pst = con.prepareStatement("INSERT INTO patient VALUES(?, ?, ?, ?, ?)");
			PreparedStatement pst2 = con.prepareStatement("INSERT INTO doctor VALUES(?, ?, ?, ?, ?)");
			PreparedStatement pst3 = con.prepareStatement("INSERT INTO examination VALUES(?, ?)");
			PreparedStatement pst4 = con.prepareStatement("INSERT INTO reservation VALUES(?, ?, ?, ?, ?, ?, ?)");
			PreparedStatement pst5 = con.prepareStatement("INSERT INTO sickroom VALUES(?, ?, ?, ?)");
			PreparedStatement pst6 = con.prepareStatement("INSERT INTO hospitalization VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
			
			add_data(t[0], pst);
			add_data(t[1], pst2);
			add_data(t[2], pst3);
			add_data(t[3], pst4);
			add_data(t[4], pst5);
			add_data(t[5], pst6);
			JOptionPane.showMessageDialog(null, "db구축 성공");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void add_data(String t, PreparedStatement pst) throws SQLException, IOException {
		String path = System.getProperty("user.dir") + "\\지급자료\\" + t + ".txt";
//		String path = System.getProperty("user.dir") + "\\2020_1\\지급자료\\" + t + ".txt";
		List<String> data = Files.readAllLines(Paths.get(path));
		
		for (int i = 1; i < data.size(); i++) {			
			String [] split = data.get(i).split("\t");
			
			for (int j = 0; j < split.length; j++)
				pst.setString(j + 1, split[j]);
			
			if (t.equals("hospitalization")) {
				if (split.length == 5) {
					for (int j = 5; j < 8; j++)
						pst.setString(j + 1, "0");
				}
			}
			
			pst.executeUpdate();
		}		
	}
	
	public static void create_db() throws SQLException {
		stmt.execute("DROP DATABASE IF EXISTS `hospital`");
		stmt.execute("DROP USER IF EXISTS 'user'@'%'");
		
		stmt.execute("CREATE DATABASE IF NOT EXISTS `hospital` DEFAULT CHARACTER SET utf8");
		stmt.execute("CREATE USER 'user'@'%' identified by '1234'");
		stmt.execute("GRANT INSERT, SELECT, DELETE, SELECT on movie.* to 'user'@'%'");
		
		stmt.execute("USE hospital");
		stmt.execute("CREATE TABLE patient(p_no int PRIMARY KEY NOT NULL auto_increment, p_name varchar(10), p_id varchar(15), p_pw varchar(10), p_bd date)");
		stmt.execute("CREATE TABLE doctor(d_no int primary key not null auto_increment, d_section varchar(10), d_name varchar(15), d_day varchar(1), d_time varchar(2))");
		stmt.execute("CREATE TABLE examination(e_no int primary key not null auto_increment, e_name varchar(10))");
		stmt.execute("create table reservation(r_no int primary key not null auto_increment, p_no int, d_no int, r_section varchar(10), r_date varchar(14), r_time varchar(10), e_no int, foreign key(p_no) references Patient(p_no), foreign key(d_no) references doctor(d_no), foreign key(e_no) references examination(e_no))");
		stmt.execute("create table sickroom(s_no int primary key not null auto_increment, s_people int, s_room int, s_roomno varchar(20))");
		stmt.execute("create table hospitalization(h_no int primary key not null auto_increment, p_no int, s_no int, h_bedno int, h_sday varchar(14), h_fday varchar(14), h_meal int, h_amount int, foreign key(p_no) references Patient(p_no), foreign key(s_no) references sickroom(s_no))");
	}

}
