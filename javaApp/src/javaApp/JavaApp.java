package javaApp;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.ProcStat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaApp {
	private static Sigar oneSigar = new Sigar();
	private static ProcStat procStat = new ProcStat();
	
	public static void main(String[] args){		
		String url = "jdbc:mysql://localhost:3306/test";
		String user = "root";
		String password = "the_hackerati@cooper";
		url = makeSchema(url,user,password);
		makeTable(url,user,password);
		while(true){
			try{
				

				insertThreadCount(url, user, password);

				
			} catch (SQLException ex){
				System.out.println("Latest thread count result not stored");
			}

			try{
				Thread.currentThread();
				Thread.sleep((long)(1000));
			}catch (InterruptedException e){
				System.out.println("60 seconds haven't passed yet!");
			}
		}
		
	}
	private static String makeSchema(String url, String user, String password){
		Connection con = null;
		PreparedStatement pst = null;
		try{
			con = DriverManager.getConnection(url, user, password);
			pst = con.prepareStatement("CREATE DATABASE ThreadCountStorage");
			pst.executeUpdate();
			String newUrl = "jdbc:mysql://localhost:3306/threadcountstorage";
			return newUrl;
			
		}catch (SQLException ex2){
			System.out.println("Schema already exists, doesn't need to be created.");
			String newUrl = "jdbc:mysql://localhost:3306/threadcountstorage";
			return newUrl;
		}
	}
	private static void makeTable(String url, String user, String password){
		Connection con = null;
		PreparedStatement pst = null;
		try{
			con = DriverManager.getConnection(url, user, password);
			pst = con.prepareStatement("CREATE TABLE threadcount( id INT NOT NULL AUTO_INCREMENT,"
					+ "threads INT, PRIMARY KEY(id))");
			pst.executeUpdate();
		}catch (SQLException ex){
			System.out.println("A table by that name already exists, it will be "
					+ "cleared to make room for new data.");
			try{
				con = DriverManager.getConnection(url, user, password);
				pst = con.prepareStatement("TRUNCATE TABLE threadcount");
				pst.executeUpdate();
			} catch (SQLException ex2){
				Logger lgr = Logger.getLogger(JavaApp.class.getName());
				lgr.log(Level.WARNING, ex2.getMessage(),ex2);
			}
			
		}

	}
	/*private static void makeTable(String url, String user, String password){
		Connection con = null;
		PreparedStatement pst = null;
		try{
			con = DriverManager.getConnection(url, user, password);
			pst = con.prepareStatement("CREATE TABLE threadcount( id INT NOT NULL AUTO_INCREMENT,"
					+ "threads INT, PRIMARY KEY(id))");
			pst.executeUpdate();
		}catch (SQLException ex){
			System.out.println("A table by that name already exists, it will be "
					+ "cleared to make room for new data.");
			try{
				con = DriverManager.getConnection(url, user, password);
				pst = con.prepareStatement("TRUNCATE TABLE threadcount");
				pst.executeUpdate();
			} catch (SQLException ex2){
				Logger lgr = Logger.getLogger(JavaApp.class.getName());
				lgr.log(Level.WARNING, ex2.getMessage(),ex2);
			}
			
		}

	}*/
	private static void insertThreadCount(String url, String user, String password) throws SQLException{
		
		Connection con = null;
		PreparedStatement pst = null;
		
		int totalThreads = processThreads();
		con = DriverManager.getConnection(url, user, password);
		pst = con.prepareStatement("INSERT INTO threadcount(threads) VALUES(?)");
		pst.setInt(1,totalThreads);
		pst.executeUpdate();
		System.out.println("most recently acquired thread count: " + totalThreads);
		
	}
	private  static int processThreads(){
		try { 
			procStat.gather(oneSigar);
		} catch (SigarException sigExc){
			sigExc.printStackTrace();
		}
		long processThreads = procStat.getThreads();
		return (int) processThreads;
	}
}