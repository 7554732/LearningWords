import java.io.File;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.*;

public class LearningWords {
	public static Logger log = Logger.getLogger(LearningWords.class.getName());
	private static String fileNameLogProp="logging.properties";
	
	private static String dbFileName="res/cards.db";
	private static String xmlFileName="res/lesson.xml";
	private static String soundFileName="res/snd.wav"; 
	
	public static void main(String args[]) {   
		
//		setup LogManager		
		try {
			LogManager.getLogManager().readConfiguration(LearningWords.class.getResourceAsStream(fileNameLogProp));
	    } catch (Exception e) {
	        System.out.println("Could not setup logger configuration "+fileNameLogProp);
	        e.printStackTrace();
	    }

//		setup Lesson
		
		Lesson.setXmlFileName(xmlFileName);
		Lesson.setSoundFile(soundFileName);
		
//		load configuration from XML file
		Lesson.loadConfigData();
		
//		set connection to DataBase		
		
		try {
			SqlConnection.Connect(dbFileName);	
		} catch (Exception e) {
			e.printStackTrace();
			LearningWords.log.log(Level.SEVERE, "SQL Connect Error", e);
            System.exit(1);
		}

//		create window in new thread
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
				} catch (Exception e) {
					e.printStackTrace();
					LearningWords.log.log(Level.SEVERE, "Unchecked Exceptions Error", e);
	                System.exit(1);
				}
			}
		}) ;		
		
	}
}
