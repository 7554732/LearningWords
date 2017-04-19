import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Lesson {
	private static MainFrame mFrame;
	private static String xmlFileName="lesson.xml";
	private static File soundFile = new File("snd.wav"); 

	public static Integer repeatTime=60;	// time between Lessons
	public static Integer questionsNumber=10;	//number questions in lesson
	public static Integer wordMaxScore=3;	//	after this score  the word considered to be learned
	public static Double cardMaxStatistic=90.0;	//	then the card considered to be learned
	
	private static Double currentCardStatistic=0.0;
	private static Integer currentQuestionsNumber=0;
	private static String currentCard=null;
	private static String questionLang=null;
	private static String answerLang=null;
	private static ArrayList<RowWords> words;	//	all words in the card
	private static ArrayList<RowWords> lstQuestion;		//	words in question
	private static Integer answerNumber=-1;	//	number of correct answer
	
	private static Date date=new Date();
	private static Random rnd=new Random(date.getTime());	

	private static Timer timer = new Timer();
	public static NextLessonTask nextLesson;	
	
	private static GoogleTranslator googleTranslatort = null;	
	private static HttpURLConnection connToSpeech=null;
	private static ConnThread connThread=null;
	
//		 set variable mFrame
	
	public static void setMainFrame(MainFrame arg_mFrame){
		mFrame=arg_mFrame;		
	}
	
//	 set variable xmlFileName

	public static void setXmlFileName(String arg_xmlFileName){
		xmlFileName=arg_xmlFileName;		
	}
	
//	 set variable soundFile

	public static void setSoundFile(String arg_soundFileName){
		soundFile = new File(arg_soundFileName);		
	}

//		reset card statistic to 0 and set it to database table 'words'
	
	public static void resetCardStatistic(){		
		try{
			SqlConnection.stmt.execute("UPDATE words SET score=0 WHERE card='"+currentCard+"';");
			currentCardStatistic=0.0;
			mFrame.multipleChoice.lblCardStatistic.setText("Card Statistic : "+String.format("%.1f",currentCardStatistic)+" %");
			System.out.println("Card Statistic Reset");
			LearningWords.log.info("Card Statistic Reset");
		}
		catch (SQLException e) {
			e.printStackTrace();
			LearningWords.log.log(Level.SEVERE, "SQL execute Error", e);
            System.exit(1);
		}
	}

//		Resolve Next Card to learn, crate googleTranslatort.
	
	public static String nextCard(){
		
		//	if no selected Cards to learn clear variables
		if (mFrame.cardEditor.listModelLearningCards.isEmpty()){	
			currentCard=null;
    		googleTranslatort = null;	
			return null;
		}
		 
		//	currentCard	set to first card from listModelLearningCards in case:
		//	at first time (if currentCard is NULL) OR
		//	if just deleted from listModelLearningCards OR 
		//	last in listModelLearningCards
		if (currentCard==null | mFrame.cardEditor.listModelLearningCards.contains(currentCard)==false | currentCard==mFrame.cardEditor.listModelLearningCards.lastElement()){
			currentCard = mFrame.cardEditor.listModelLearningCards.firstElement();
		}
		else{
			//	currentCard set to next card 
			int indexCurrentCard= mFrame.cardEditor.listModelLearningCards.indexOf(currentCard);
			currentCard=mFrame.cardEditor.listModelLearningCards.get(indexCurrentCard+1);				
		}
		
		try{
			SqlConnection.resSet = SqlConnection.stmt.executeQuery("SELECT * FROM words WHERE  card='"+currentCard+"';");
			if(!SqlConnection.resSet.next()){
		    	//	delete currentCard from learning if it have not words
		    	if(currentCard!=null){
					try {
						SqlConnection.stmt.execute("UPDATE cards SET learning=0 WHERE card='"+currentCard+"';");
						System.out.println("No words in the card. Delete it from Learning: " + currentCard);
						LearningWords.log.info("No words in the card. Delete it from Learning: " + currentCard);
					} 
					catch (SQLException e) {
						e.printStackTrace();
						LearningWords.log.log(Level.SEVERE, "SQL execute Error", e);
			            System.exit(1);
					}
		        	if (mFrame.cardEditor.listModelLearningCards.contains(currentCard)==true) mFrame.cardEditor.listModelLearningCards.removeElement(currentCard);
				}
				nextCard();
				return currentCard;
			}
			else{
				SqlConnection.resSet = SqlConnection.stmt.executeQuery("SELECT * FROM cards WHERE  card='"+currentCard+"' AND learning=1;");
				questionLang=SqlConnection.resSet.getString("questionLang");
				answerLang=SqlConnection.resSet.getString("answerLang");
				//	Create GoogleTranslator and setn up languages
				googleTranslatort = new GoogleTranslator(questionLang,answerLang);				
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			LearningWords.log.log(Level.SEVERE, "SQL executeQuery Error", e);
            System.exit(1);
		}			
        System.out.println("currentCard : "+currentCard+" questionLang "+questionLang+" answerLang "+answerLang);
        LearningWords.log.info("currentCard : "+currentCard);
		mFrame.multipleChoice.lblCard.setText("Card : "+currentCard);		
		return currentCard;
	}     	
	
//	run separate Tread to make pause

	private static class PauseThread extends Thread {
		int pause_time;
		public PauseThread(int arg_pause_time){
			pause_time=arg_pause_time;
		}
	    public void run(){ 	
	        	try {
					sleep(pause_time);
				} catch (InterruptedException e) {
					e.printStackTrace();
					LearningWords.log.log(Level.WARNING, "pause Error", e);
				}	         	
	    }
	}

//	on "Next Word" button pressed
	
	public static void nextWord(){		
	   	//	restore defaults and make next Question
		mFrame.multipleChoice.lblQuestion.setForeground(new Color(0,0,0));
		//	hide button
		mFrame.multipleChoice.btnNextWord.setVisible(false);
		makeQueastion();
	}	

//		check the correctness of answer, count score, show and play correct answer
	
	public static void checkAnswer(){
		mFrame.multipleChoice.lstAnswer.removeMouseListener(mFrame.multipleChoice.lstAnswerMouseListener);		
        System.out.println("selected answer index: "+mFrame.multipleChoice.lstAnswer.getSelectedIndex());     
        LearningWords.log.info("selected answer index: "+mFrame.multipleChoice.lstAnswer.getSelectedIndex());        
        
        // Check the answer
        boolean correct_answer=false; 
		if(mFrame.multipleChoice.lstAnswer.getSelectedIndex()==answerNumber){
			lstQuestion.get(answerNumber).score++;
			mFrame.multipleChoice.lblQuestion.setForeground(new Color(0,0,255));
			correct_answer=true;
		}
		else{
			lstQuestion.get(answerNumber).score--;	
			mFrame.multipleChoice.lblQuestion.setForeground(new Color(255,0,0));
			correct_answer=false;
		}	
		mFrame.multipleChoice.lblQuestion.update(mFrame.multipleChoice.lblQuestion.getGraphics());
		
		//	Set score to the dataBase 
		try{
			if(mFrame.multipleChoice.rbFwdQuizDirection.isSelected()){
				SqlConnection.stmt.execute("UPDATE words SET score="+lstQuestion.get(answerNumber).score+" WHERE card='"+currentCard+"' AND question='"+lstQuestion.get(answerNumber).question+"';");
			}
			else{
				SqlConnection.stmt.execute("UPDATE words SET score="+lstQuestion.get(answerNumber).score+" WHERE card='"+currentCard+"' AND question='"+lstQuestion.get(answerNumber).answer+"';");				
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			LearningWords.log.log(Level.SEVERE, "SQL execute Error", e);
            System.exit(1);
		}	
		System.out.println("Score :"+lstQuestion.get(answerNumber).score);
		LearningWords.log.info("Score :"+lstQuestion.get(answerNumber).score);
		mFrame.multipleChoice.lblCurrentWordStatistic.setText("score : "+lstQuestion.get(answerNumber).score.toString()+" of "+wordMaxScore);
		
    	//	Remove wrong answers from JList
    	for(int i=0; i<mFrame.multipleChoice.listModelWords.getSize(); i++){
    		if(i!=answerNumber)mFrame.multipleChoice.listModelWords.set(i, " ");
    	}
    	mFrame.multipleChoice.lstAnswer.clearSelection();
		mFrame.multipleChoice.lstAnswer.update(mFrame.multipleChoice.lstAnswer.getGraphics());
    	
    	//	Play word if connection to Google answer exist
       	if(connToSpeech!=null){
       		if(mFrame.multipleChoice.chbSound.isSelected())
       			googleTranslatort.playURL(connToSpeech);
       	}
       	else if(connThread!=null){
       		connThread.interrupt();
       		connThread=null;
       	}		  
		
		if(correct_answer){
			mFrame.setEnabled(false);
			
			//	Pause 1 second
	    	PauseThread pause_thread = new PauseThread(1000);
	    	pause_thread.start();  
	    	try {
				pause_thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				LearningWords.log.log(Level.WARNING, "correct answer pause join Error", e);
			};    	
			
			//	restore defaults and make next Question or Show btnNextWord button
	    	mFrame.setEnabled(true);
			mFrame.multipleChoice.lblQuestion.setForeground(new Color(0,0,0));		
 			makeQueastion();        		
    	}
    	else{	        		
    		mFrame.multipleChoice.btnNextWord.setVisible(true);	
    	}
	}

//	Discribe structure for containing pair Answer-Question

	private static class RowWords{
		public String question;
		public String answer;
		public Integer score;
		
		RowWords(String arg_question,String arg_answer,Integer arg_score){
			question=arg_question;
			answer=arg_answer;
			score=arg_score;
		}
		public  Integer getScore(){
	        return score;
		}		
		public  String toString(){
	        return question+" "+answer+" "+score;
		}
	};
	
//	count  number of unlearned words
	private static int sizeLessMaxScore(ArrayList<RowWords> arg_words, int arg_wordMaxScore){
		for(int i=0;i<arg_words.size();i++){
	        if(arg_words.get(i).getScore()>=arg_wordMaxScore) return i;
		}						
		return arg_words.size();
	}

//	Play Sound and Show main Window when next Lesson beginning

	public static class NextLessonTask extends TimerTask{
		public void run(){
			System.out.println("NextLessonTask");
			//	Play sound	 
	        try {
	        	AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
	        	Clip clip = AudioSystem.getClip();
	        	clip.open(ais);	        	
	        	clip.setFramePosition(0); 
	        	clip.start();
	        	Thread.sleep(clip.getMicrosecondLength()/1000);
	        	clip.stop(); 
	        	clip.close(); 
	        } catch (Exception e) {
	        	e.printStackTrace();
				LearningWords.log.log(Level.WARNING, "Play sound Error", e);
	        }
	        //	Set active MultpleChoicePanel
	        mFrame.tabbedPane.setSelectedIndex(0);
	        //	Restore Window
	        mFrame.setExtendedState(JFrame.NORMAL);
		}
	
//	Confirm with Input Dialog value of repeatTime
	
		public void inputRepeatTime(){
			String value=null;
			value=(String)JOptionPane.showInputDialog(mFrame,"See you after (min):","Lesson Finished",JOptionPane.PLAIN_MESSAGE,null,null,repeatTime);
			
			mFrame.options.textFieldRepeatTime.removeCaretListener(mFrame.options.textFieldRepeatTimeCaretListener);
			
			if((value != null) && (value.length() != 0) && saveValue("repeatTime" , value)){
				mFrame.options.textFieldRepeatTime.setText(value);
				System.out.println("repeatTime: " + repeatTime);				
			}
			else{
		        System.out.println("Input error.");
	    		JOptionPane.showMessageDialog(mFrame,"See you after "+repeatTime+" min.", "Input error.", JOptionPane.WARNING_MESSAGE);						
			}
			
			mFrame.options.textFieldRepeatTime.addCaretListener(mFrame.options.textFieldRepeatTimeCaretListener);
	        mFrame.setExtendedState(JFrame.ICONIFIED); 
		}
	}

//	Get Connection to Google Speech in the separate Tread

	private static class ConnThread extends Thread {
	    public void run(){
			if(mFrame.multipleChoice.rbFwdQuizDirection.isSelected()){
				connToSpeech=googleTranslatort.getConnToSpeech(lstQuestion.get(answerNumber).question);
			}
			else{
				connToSpeech=googleTranslatort.getConnToSpeech(lstQuestion.get(answerNumber).answer);				
			}
	    }
	}
	
//		set previous Question,Answer and Statistic to show on multipleChoice panel
	
	private static void  setPerviosWordStatistic(){
		if(answerNumber>-1){
			mFrame.multipleChoice.lblPervQuestion.setText("Perv Word : "+lstQuestion.get(answerNumber).question);
			mFrame.multipleChoice.lblTranslate.setText("Translate : "+lstQuestion.get(answerNumber).answer);
			mFrame.multipleChoice.lblScore.setText("Score : "+lstQuestion.get(answerNumber).score+" of "+wordMaxScore);				
		}		
	}

//		check: is any card to learn?
	
	private static boolean checkCardsToLearn(){	
		try{
			SqlConnection.resSet = SqlConnection.stmt.executeQuery("SELECT * FROM cards WHERE learning=1;");
			if (SqlConnection.resSet.isBeforeFirst()==false){
            	JOptionPane.showMessageDialog(mFrame, "No Cards Selected To Learn.", "Error", JOptionPane.WARNING_MESSAGE);
    			mFrame.multipleChoice.lblQuestion.setText("Select Cards To Learn");
    			mFrame.multipleChoice.lblCurrentWordStatistic.setText("and press 'Next Card'");	
            	mFrame.multipleChoice.listModelWords.clear();					
				return true;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			LearningWords.log.log(Level.SEVERE, "SQL executeQuery Error", e);
            System.exit(1);
		}			
		return false;
		
	}

//	Load words for question and Count their statistic
	
	private static void countCardStatistric(){
		try{	
			SqlConnection.resSet = SqlConnection.stmt.executeQuery("SELECT * FROM words WHERE card='"+currentCard+"';");
			
			currentCardStatistic=0.0;
			words = new ArrayList<RowWords>();
			while(SqlConnection.resSet.next()){
				RowWords el;
				//	Check Quiz Direction and Set ArrayList<RowWords> 
				if(mFrame.multipleChoice.rbFwdQuizDirection.isSelected()){
					el = new RowWords(SqlConnection.resSet.getString("question"),SqlConnection.resSet.getString("answer"),SqlConnection.resSet.getInt("score"));					
				}
				else{
					el = new RowWords(SqlConnection.resSet.getString("answer"),SqlConnection.resSet.getString("question"),SqlConnection.resSet.getInt("score"));					
				}
				words.add(el);
				currentCardStatistic=currentCardStatistic+Double.valueOf(el.score);
			}
			if(words.size()>0)	currentCardStatistic=100*currentCardStatistic/(wordMaxScore*words.size());
			else currentCardStatistic=0.0;
			mFrame.multipleChoice.lblCardStatistic.setText("Card Statistic : "+String.format("%.1f",currentCardStatistic)+" %");
		}
		catch (Exception e) {
			e.printStackTrace();
	        System.exit(1);
		}			
		
	}
	
//		check Card statistic. If Card is learned remove it from learning cards list and get Next Card to learn
	
	private static boolean checkCardStatistic(){
		if(currentCardStatistic>=cardMaxStatistic){
        	if(currentCard!=null){
            	if (mFrame.cardEditor.listModelLearningCards.contains(currentCard)==true) mFrame.cardEditor.listModelLearningCards.removeElement(currentCard);
            	JOptionPane.showMessageDialog(mFrame, "Card "+currentCard +" Learned.", "information", JOptionPane.INFORMATION_MESSAGE);	
            	String learnedCard=currentCard.toString();
    			nextCard();
    			try {
    				SqlConnection.stmt.execute("UPDATE cards SET learning=0 WHERE card='"+learnedCard+"';");
    				SqlConnection.stmt.execute("UPDATE words SET score=0 WHERE card='"+learnedCard+"';");
    			} 
    			catch (SQLException e) {
    				e.printStackTrace();
    				LearningWords.log.log(Level.SEVERE, "SQL execute Error", e);
    	            System.exit(1);
    			}
    			makeQueastion();
    			return true;
			}
		}	
		return false;
	}

//		get Random Words from Array of Questions
	
	private static void getRandomWordsForQuestion(){
		lstQuestion = new ArrayList<RowWords>();	
		
		int numberQuestion=5;
		if (words.size()<numberQuestion) numberQuestion=words.size();
		
		for(int i=0;i<numberQuestion;){
			if(words.size()>0){
				//	get random word from number of unlearned words OR from number all words
				if(sizeLessMaxScore(words,wordMaxScore)>0){
					int random_question=rnd.nextInt(sizeLessMaxScore(words,wordMaxScore));
					lstQuestion.add(words.get(random_question));
					words.remove(random_question);
					i++;	
				}
				else{
					int random_question=rnd.nextInt(words.size());
					lstQuestion.add(words.get(random_question));
					words.remove(random_question);
					i++;								
				}
			}
			else{
				break;	
			}
		}	
		answerNumber=rnd.nextInt(lstQuestion.size());
		mFrame.multipleChoice.lblQuestion.setText(lstQuestion.get(answerNumber).question);
		mFrame.multipleChoice.lblCurrentWordStatistic.setText("score : "+lstQuestion.get(answerNumber).score.toString()+" of "+wordMaxScore);
	}
	
//		turn between Tahoma 18 and swing default font for Hieroglyphs imagination
	
	private static void setQuestionFont(){
		
		String [] hieroglyphLang = {"zh-CN", "zh-TW", "hi", "ja", "ko", "yi"};
		
		mFrame.multipleChoice.lblQuestion.setFont(new Font("Tahoma", Font.PLAIN, 18));
		mFrame.multipleChoice.lstAnswer.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		if(mFrame.multipleChoice.rbFwdQuizDirection.isSelected()){
			if (Arrays.asList(hieroglyphLang).contains(questionLang)){
				mFrame.multipleChoice.lblQuestion.setFont(UIManager.getFont("Button.font"));				
			}
			if (Arrays.asList(hieroglyphLang).contains(answerLang)){
				mFrame.multipleChoice.lstAnswer.setFont(UIManager.getFont("Button.font"));				
			}
		}
		else{
			if (Arrays.asList(hieroglyphLang).contains(answerLang)){
				mFrame.multipleChoice.lblQuestion.setFont(UIManager.getFont("Button.font"));	
			}
			if (Arrays.asList(hieroglyphLang).contains(questionLang)){
				mFrame.multipleChoice.lstAnswer.setFont(UIManager.getFont("Button.font"));				
			}
		}				
	}
	
//	not count as answer pressing button "Next Card"
	
	public static void decreaseCurQuestionsNumber(){
		if(currentQuestionsNumber>0) currentQuestionsNumber--;
	}
	
//	schedule start Next Lesson after repeatTime
	
	public static void scheduleNextLesson(){
		currentQuestionsNumber=0;	
        // Pause before Next Lesson 
        nextLesson = new NextLessonTask();
		//	input Time to Pause before the Next Lesson
        nextLesson.inputRepeatTime();
        timer.schedule(nextLesson, repeatTime*60000);
        timer.schedule(new TimerTask(){
        	public void run(){
	            mFrame.addWindowFocusListener(mFrame.mFrameWindowFocusListener);
            }
        },1000);
	}
	
//		Crate new Question
	
	public static void makeQueastion(){	
	// 	Disables Chosing Answer action listener 
		mFrame.multipleChoice.lstAnswer.removeMouseListener(mFrame.multipleChoice.lstAnswerMouseListener);
		
	//	Set status of Pervios question word
		setPerviosWordStatistic();
		
	//	Check Cards to Learn existing	
		if(checkCardsToLearn()) return;
		
	//	Count Card Statistic	
		countCardStatistric();

	//	check statistic and next card if this is learned
		if(checkCardStatistic()) return;
	
	//	schedule Next Lesson
		if(currentQuestionsNumber>=questionsNumber){
			scheduleNextLesson();
            return;
		}		
		
		//	sort the words by score
		words.sort(Comparator.comparing(RowWords::getScore));	

		// 	get words for question
		getRandomWordsForQuestion();

		//	turn font for question imagination
		setQuestionFont();
		
		//	print Questions
	    mFrame.multipleChoice.listModelWords.clear();
		for(RowWords el:lstQuestion){
	        mFrame.multipleChoice.listModelWords.addElement(el.answer);
	        System.out.println("Questions: "+el);
	        LearningWords.log.info("Questions: "+el);
		}			
	
		//	Get Connection to GoogleSpeech in new Thread
    	connToSpeech=null;
		if(googleTranslatort!=null & mFrame.multipleChoice.chbSound.isSelected()){
	    	connThread = new ConnThread();
	    	connThread.start();				
		}			

        System.out.println("currentQuestionsNumber "+currentQuestionsNumber);
        LearningWords.log.info("currentQuestionsNumber "+currentQuestionsNumber);
		currentQuestionsNumber++;
		mFrame.multipleChoice.lblQuestionsNumber.setText(currentQuestionsNumber+"/"+questionsNumber);
		
		mFrame.multipleChoice.lstAnswer.addMouseListener(mFrame.multipleChoice.lstAnswerMouseListener);
	}
	
//		Save cofig data value to file
	
	public static boolean saveValue(String valueName, String value){
		try{
			//	Convert input value to variable 
			switch(valueName){
				case "repeatTime":
					repeatTime=Integer.valueOf(value);
					break;
				case "questionsNumber":
					questionsNumber=Integer.valueOf(value);
					break;
				case "wordMaxScore":
					wordMaxScore=Integer.valueOf(value);
					break;
				case "cardMaxStatistic":
					cardMaxStatistic=Double.valueOf(value);
					break;
				default:{
			        System.out.println("Input valueName error.");
					return false;
				}
			}
	        try {
		        System.out.println("Save config to : "+xmlFileName);
		        LearningWords.log.info("Save config to : "+xmlFileName);
				//	Create Document Builder
		        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		        // Create Tree of XML file
		        Document document = documentBuilder.parse(xmlFileName);
		
		        //	get Root element
		        Node root = document.getDocumentElement();
		        
		        // 	find subroot element with same name as valueName
		        NodeList nodeList = root.getChildNodes();
		        for (int i = 0; i < nodeList.getLength(); i++) {
		            Node node = nodeList.item(i);
		            if (node.getNodeType() != Node.TEXT_NODE) {
		                if(node.getNodeName()==valueName) {
		                	node.setTextContent(value);
		                }
		            }		
		        }	
	            // Write XML to file
	            writeDocument(xmlFileName,document);	
		    }
			catch (ParserConfigurationException e) {
				e.printStackTrace();
				LearningWords.log.log(Level.WARNING, "newDocumentBuilder Error", e);
				return false;
			}
			catch (SAXException | IOException e) {
				e.printStackTrace();
				LearningWords.log.log(Level.WARNING, "documentBuilder.parse Error", e);
				return false;
			}
		}catch(NumberFormatException e){
	        e.printStackTrace();
	        System.out.println("Input type error.");
			LearningWords.log.log(Level.INFO, "Input type error.", e);
			return false;
		}
		return true;
	}
	
	
//		Load data from XML config
	
	public static void loadConfigData(){		
        try {
	        System.out.println("load config from : "+xmlFileName);
	        LearningWords.log.info("load config from : "+xmlFileName);
			//	Create Document Builder
	        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    // Create Tree of XML file
	        Document document = documentBuilder.parse(xmlFileName);
	
	        //	get Root element
	        Node root = document.getDocumentElement();
	        
	        // Read all Subnodes of Root Node
	        NodeList nodeList = root.getChildNodes();
	        for (int i = 0; i < nodeList.getLength(); i++) {
	            Node node = nodeList.item(i);
	            if (node.getNodeType() != Node.TEXT_NODE) {
	                if(node.getNodeName()=="repeatTime") repeatTime=new Integer(node.getTextContent());
	                if(node.getNodeName()=="questionsNumber") questionsNumber=new Integer(node.getTextContent());
	                if(node.getNodeName()=="wordMaxScore") wordMaxScore=new Integer(node.getTextContent());	
	                if(node.getNodeName()=="cardMaxStatistic") cardMaxStatistic=new Double(node.getTextContent());
	            }	
	        }		       		

	    } 
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			LearningWords.log.log(Level.WARNING, "newDocumentBuilder Error", e);
		}
		catch (SAXException | IOException e) {
			e.printStackTrace();
			LearningWords.log.log(Level.WARNING, "documentBuilder.parse Error", e);
		}
	} 
	
// 		Write XML DOM to file
	
    private static void writeDocument(String fileName,Document document) throws TransformerFactoryConfigurationError {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            DOMSource source_doc = new DOMSource(document);
            FileOutputStream output_stream = new FileOutputStream(fileName);
            StreamResult result = new StreamResult(output_stream);
            transformer.transform(source_doc, result);
            output_stream.close();
        }
        catch (TransformerException | IOException e) {
			e.printStackTrace();
			LearningWords.log.log(Level.WARNING, "Could not Write XML DOM to file", e);
		}
    }

}
