import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

/*	sqlite command to create DataBase structure:
 * 
 	CREATE TABLE cards (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,card TEXT NOT NULL,learning INTEGER NOT NULL, questionLang TEXT NOT NULL,answerLang TEXT NOT NULL);
	CREATE TABLE words (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,card TEXT NOT NULL ,question TEXT NOT NULL ,answer TEXT NOT NULL,score INTEGER);
 *	
 */
public class SqlConnection {
	public static Connection conn;
	public static Statement stmt;
	public static ResultSet resSet;

//	set connection to sqlite DataBase
	
	public static void Connect(String dbFileName)  throws ClassNotFoundException, SQLException  {
	   conn = null;
	   Class.forName("org.sqlite.JDBC");
	   conn = DriverManager.getConnection("jdbc:sqlite:"+dbFileName);	
	   stmt = conn.createStatement();
	   if(stmt==null) {
		   System.out.println("Sql Statement is not Created");
		   LearningWords.log.info("Sql Statement is not Created");
	   }
	   else{
		   System.out.println("Sql Connection Open, Statement Created");
		   LearningWords.log.info("Sql Connection Open, Statement Created");		   
	   }
	}
	
//	close Connection, Statement, ResultSet
	
	public static void Close()  {
	   try{
		   if(conn!=null) conn.close();
	       System.out.println("Sql Connection Closed");  
		   LearningWords.log.info("Sql Connection Closed"); 
	   }
	   catch (Exception e) {
			e.printStackTrace();
			LearningWords.log.log(Level.WARNING, "Could not close SQL Connection", e);
	   }
	   
	   try{
		   if(stmt!=null) stmt.close();
	       System.out.println("Sql Statement Closed");  
		   LearningWords.log.info("Sql Statement Closed"); 
	   }
	   catch (Exception e) {
			e.printStackTrace();
			LearningWords.log.log(Level.WARNING, "Could not close Sql Statement", e);
	   }
	   
	   try{
		   if(resSet!=null) resSet.close();	
	       System.out.println("Sql ResultSet Closed");  
		   LearningWords.log.info("Sql ResultSet Closed"); 
	   }
	   catch (Exception e) {
			e.printStackTrace();
			LearningWords.log.log(Level.WARNING, "Could not close Sql ResultSet", e);
	   }
	}
}
