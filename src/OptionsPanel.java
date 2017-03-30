import java.awt.*;
import java.util.logging.Level;

import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;


public class OptionsPanel  extends JPanel {
	
	public JTextField textFieldRepeatTime;
	public CaretListener textFieldRepeatTimeCaretListener;
	
	OptionsPanel(MainFrame mFrame) {
		GridBagLayout gbl_options = new GridBagLayout();
		gbl_options.columnWidths = new int[]{10, 155, 108, 0};
		gbl_options.rowHeights = new int[] {30, 0, 0, 0, 0, 0};
		gbl_options.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_options.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		this.setLayout(gbl_options);
		
		JLabel lblRepeatTime = new JLabel("Repeat Time (minutes)");
		GridBagConstraints gbcLlblRepeatTime = new GridBagConstraints();
		gbcLlblRepeatTime.insets = new Insets(0, 0, 5, 5);
		gbcLlblRepeatTime.gridx = 1;
		gbcLlblRepeatTime.gridy = 1;
		this.add(lblRepeatTime, gbcLlblRepeatTime);
		
		textFieldRepeatTime = new JTextField(Lesson.repeatTime.toString());
		textFieldRepeatTimeCaretListener = new CaretListener() {
			public void caretUpdate(CaretEvent arg0) {
				String value=textFieldRepeatTime.getText();
				try{
					if(Lesson.repeatTime.intValue()!=Integer.parseInt(value)) Lesson.saveValue("repeatTime",value);
				}catch(Exception e){
			        e.printStackTrace();
			        System.out.println("Input type error.");
					LearningWords.log.log(Level.INFO, "Input type error.", e);
					return;
				}
			}
		};
		textFieldRepeatTime.addCaretListener(textFieldRepeatTimeCaretListener);
		GridBagConstraints gbcTextField = new GridBagConstraints();
		gbcTextField.insets = new Insets(0, 0, 5, 0);
		gbcTextField.gridx = 2;
		gbcTextField.gridy = 1;
		this.add(textFieldRepeatTime, gbcTextField);
		textFieldRepeatTime.setColumns(10);
		
		JLabel lblQuestionInSession = new JLabel("Questions in Lesson");
		GridBagConstraints gbcLblQuestionInSession = new GridBagConstraints();
		gbcLblQuestionInSession.insets = new Insets(0, 0, 5, 5);
		gbcLblQuestionInSession.gridx = 1;
		gbcLblQuestionInSession.gridy = 2;
		this.add(lblQuestionInSession, gbcLblQuestionInSession);
		
		JTextField textFieldQuestionsNumber = new JTextField(Lesson.questionsNumber.toString());
		textFieldQuestionsNumber.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent arg0) {
				String value=textFieldQuestionsNumber.getText();
				try{
					if(Lesson.questionsNumber.intValue()!=Integer.parseInt(value)) Lesson.saveValue("questionsNumber",value);

				}catch(Exception e){
			        e.printStackTrace();
			        System.out.println("Input type error.");
					LearningWords.log.log(Level.INFO, "Input type error.", e);
					return;
				}
			}
		});
		GridBagConstraints gbcQuestionsNumber = new GridBagConstraints();
		gbcQuestionsNumber.insets = new Insets(0, 0, 5, 0);
		gbcQuestionsNumber.gridx = 2;
		gbcQuestionsNumber.gridy = 2;
		this.add(textFieldQuestionsNumber, gbcQuestionsNumber);
		textFieldQuestionsNumber.setColumns(10);
		
		JLabel lblMaxRightAnswer = new JLabel("Max Right Answer per Word");
		GridBagConstraints gbcLblMaxRightAnswer = new GridBagConstraints();
		gbcLblMaxRightAnswer.insets = new Insets(0, 0, 5, 5);
		gbcLblMaxRightAnswer.gridx = 1;
		gbcLblMaxRightAnswer.gridy = 3;
		add(lblMaxRightAnswer, gbcLblMaxRightAnswer);
		
		JTextField textFieldWordMaxScore = new JTextField(Lesson.wordMaxScore.toString());
		textFieldWordMaxScore.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent arg0) {
				String value=textFieldWordMaxScore.getText();
				try{
					if(Lesson.wordMaxScore.intValue()!=Integer.parseInt(value)) Lesson.saveValue("wordMaxScore",value);
				}catch(Exception e){
			        e.printStackTrace();
			        System.out.println("Input type error.");
					LearningWords.log.log(Level.INFO, "Input type error.", e);
					return;
				}
			}
		});
		GridBagConstraints gbcTextFieldWordMaxScore = new GridBagConstraints();
		gbcTextFieldWordMaxScore.insets = new Insets(0, 0, 5, 0);
		gbcTextFieldWordMaxScore.gridx = 2;
		gbcTextFieldWordMaxScore.gridy = 3;
		add(textFieldWordMaxScore, gbcTextFieldWordMaxScore);
		textFieldWordMaxScore.setColumns(10);
		
		JLabel lblMaxStatisticPer = new JLabel("Max Statistic per Card (%)");
		GridBagConstraints gbcLblMaxStatisticPer = new GridBagConstraints();
		gbcLblMaxStatisticPer.insets = new Insets(0, 0, 0, 5);
		gbcLblMaxStatisticPer.gridx = 1;
		gbcLblMaxStatisticPer.gridy = 4;
		add(lblMaxStatisticPer, gbcLblMaxStatisticPer);
		
		JTextField textFieldCardMaxStatistic = new JTextField(Lesson.cardMaxStatistic.toString());
		textFieldCardMaxStatistic.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent arg0) {
				String value=textFieldCardMaxStatistic.getText();
				try{
					if(Lesson.cardMaxStatistic.doubleValue()!=Double.parseDouble(value)) Lesson.saveValue("cardMaxStatistic",value);
				}catch(Exception e){
			        e.printStackTrace();
			        System.out.println("Input type error.");
					LearningWords.log.log(Level.INFO, "Input type error.", e);
					return;
				}
			}
		});
		GridBagConstraints gbcTextFieldCardMaxStatistic = new GridBagConstraints();
		gbcTextFieldCardMaxStatistic.gridx = 2;
		gbcTextFieldCardMaxStatistic.gridy = 4;
		add(textFieldCardMaxStatistic, gbcTextFieldCardMaxStatistic);
		textFieldCardMaxStatistic.setColumns(10);
	
	}

}
