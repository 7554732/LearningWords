import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MultpleChoicePanel extends JPanel {
	
	public JLabel lblCard;
	public JLabel lblQuestion;
	public JLabel lblCurrentWordStatistic;
	public JLabel lblQuestionsNumber;
	public JLabel lblPervQuestion;
	public JLabel lblTranslate;
	public JLabel lblScore;
	public JLabel lblCardStatistic;
	public JButton btnNextWord;
	public JList lstAnswer;
	public DefaultListCellRenderer lstRenderer;
	public MouseAdapter lstAnswerMouseListener;
	public JRadioButton rbFwdQuizDirection;
	public JCheckBox chbSound;
	public DefaultListModel<String> listModelWords;
	
	MultpleChoicePanel(MainFrame mFrame) {
		
		GridBagLayout gblMultipleChoice = new GridBagLayout();
		gblMultipleChoice.columnWidths = new int[] {170, 170,0};
		gblMultipleChoice.rowHeights = new int[]{209, 0};
		gblMultipleChoice.columnWeights = new double[]{1.0, 0.0, 1.0};
		gblMultipleChoice.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		this.setLayout(gblMultipleChoice);
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(180,209));
		panel.setBorder(UIManager.getBorder("TextField.border"));
		GridBagConstraints gbcPanel = new GridBagConstraints();
		gbcPanel.insets = new Insets(0, 0, 0, 5);
		gbcPanel.fill = GridBagConstraints.BOTH;
		gbcPanel.gridx = 0;
		gbcPanel.gridy = 0;
		this.add(panel, gbcPanel);
		GridBagLayout gblPanel = new GridBagLayout();
		gblPanel.columnWidths = new int[] {120, 50};
		gblPanel.rowHeights = new int[] {10, 30, 10, 110, 10, 30, 10};
		gblPanel.columnWeights = new double[]{1.0, 1.0, 0.0, 0.0};
		gblPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0};
		panel.setLayout(gblPanel);
		
		lblQuestion = new JLabel("Word");
		lblQuestion.setFont(new Font("Tahoma", Font.PLAIN, 18));
		GridBagConstraints gbcLblQuestion = new GridBagConstraints();
		gbcLblQuestion.gridwidth = 4;
		gbcLblQuestion.anchor = GridBagConstraints.NORTH;
		gbcLblQuestion.insets = new Insets(0, 0, 5, 0);
		gbcLblQuestion.gridx = 0;
		gbcLblQuestion.gridy = 1;
		panel.add(lblQuestion, gbcLblQuestion);	

		lblCurrentWordStatistic = new JLabel("current Word Statistic");
		GridBagConstraints gbcCurrentwordstatistic = new GridBagConstraints();
		gbcCurrentwordstatistic.gridwidth = 4;
		gbcCurrentwordstatistic.insets = new Insets(0, 0, 5, 0);
		gbcCurrentwordstatistic.gridx = 0;
		gbcCurrentwordstatistic.gridy = 2;
		panel.add(lblCurrentWordStatistic, gbcCurrentwordstatistic);
		
		//	add JList with answers 
		listModelWords = new DefaultListModel<String>();
		lstAnswer = new JList(listModelWords);
		lstAnswer.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lstAnswerMouseListener = new MouseAdapter() {
			public void mouseReleased(MouseEvent arg0) {
				if(Lesson.nextLesson!=null){
					Lesson.titleTask.restoreTitle();	
					Lesson.nextLesson.cancel();	
				}
				Lesson.checkAnswer();	
			}
		};
		lstAnswer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstRenderer =  (DefaultListCellRenderer)lstAnswer.getCellRenderer();  
		lstRenderer.setHorizontalAlignment(JLabel.CENTER);
		GridBagConstraints gbcLstAnswer = new GridBagConstraints();
		gbcLstAnswer.fill = GridBagConstraints.BOTH;
		gbcLstAnswer.gridwidth = 4;
		gbcLstAnswer.insets = new Insets(0, 5, 5, 5);
		gbcLstAnswer.gridx = 0;
		gbcLstAnswer.gridy = 3;
		panel.add(lstAnswer, gbcLstAnswer);
				
		btnNextWord = new JButton("Next Word");
		btnNextWord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Lesson.nextWord();
			}
		});
		btnNextWord.setVisible(false);
		GridBagConstraints gbcButton = new GridBagConstraints();
		gbcButton.insets = new Insets(0, 0, 5, 5);
		gbcButton.fill = GridBagConstraints.NONE;
		gbcButton.gridx = 0;
		gbcButton.gridy = 5;
		panel.add(btnNextWord, gbcButton);

		lblQuestionsNumber = new JLabel("currentQuestionsNumber");
		GridBagConstraints gbcLblQuestionsNumber = new GridBagConstraints();
		gbcLblQuestionsNumber.insets = new Insets(0, 0, 5, 5);
		gbcLblQuestionsNumber.gridx = 1;
		gbcLblQuestionsNumber.gridy = 5;
		panel.add(lblQuestionsNumber, gbcLblQuestionsNumber);
		
		JPanel panelRight = new JPanel();
		panelRight.setPreferredSize(new Dimension(170,209));
		panelRight.setBorder(UIManager.getBorder("TextField.border"));
		GridBagConstraints gbcPanelRight = new GridBagConstraints();
		gbcPanelRight.gridwidth = 3;
		gbcPanelRight.fill = GridBagConstraints.BOTH;
		gbcPanelRight.insets = new Insets(0, 0, 0, 5);
		gbcPanelRight.gridx = 1;
		gbcPanelRight.gridy = 0;
		this.add(panelRight, gbcPanelRight);
		GridBagLayout gblPanelRight = new GridBagLayout();
		gblPanelRight.columnWidths = new int[] {170};
		gblPanelRight.rowHeights = new int[]{0, 14, 14, 14, 10, 14, 14, 23, 0, 0, 0, 0, 0};
		gblPanelRight.columnWeights = new double[]{1.0};
		gblPanelRight.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panelRight.setLayout(gblPanelRight);
		
		lblPervQuestion = new JLabel("Perv Word");
		lblPervQuestion.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbclblPervQuestion = new GridBagConstraints();
		gbclblPervQuestion.anchor = GridBagConstraints.WEST;
		gbclblPervQuestion.insets = new Insets(0, 0, 5, 0);
		gbclblPervQuestion.gridx = 0;
		gbclblPervQuestion.gridy = 1;
		panelRight.add(lblPervQuestion, gbclblPervQuestion);
		
		lblTranslate = new JLabel("Translate");
		lblTranslate.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbcLblTranslate = new GridBagConstraints();
		gbcLblTranslate.anchor = GridBagConstraints.NORTHWEST;
		gbcLblTranslate.insets = new Insets(0, 0, 5, 0);
		gbcLblTranslate.gridx = 0;
		gbcLblTranslate.gridy = 2;
		panelRight.add(lblTranslate, gbcLblTranslate);
		
		lblScore = new JLabel("Score");
		GridBagConstraints gbcLblScore = new GridBagConstraints();
		gbcLblScore.anchor = GridBagConstraints.NORTHWEST;
		gbcLblScore.insets = new Insets(0, 0, 5, 0);
		gbcLblScore.gridx = 0;
		gbcLblScore.gridy = 3;
		panelRight.add(lblScore, gbcLblScore);
		
		JSeparator separator = new JSeparator();
		GridBagConstraints gbcSeparator = new GridBagConstraints();
		gbcSeparator.fill = GridBagConstraints.BOTH;
		gbcSeparator.insets = new Insets(0, 0, 5, 0);
		gbcSeparator.gridx = 0;
		gbcSeparator.gridy = 4;
		panelRight.add(separator, gbcSeparator);
		
		lblCard = new JLabel("Card :");
		GridBagConstraints gbcLblcard = new GridBagConstraints();
		gbcLblcard.anchor = GridBagConstraints.NORTHWEST;
		gbcLblcard.insets = new Insets(0, 0, 5, 0);
		gbcLblcard.gridx = 0;
		gbcLblcard.gridy = 5;
		panelRight.add(lblCard, gbcLblcard);
		
		lblCardStatistic = new JLabel("Card Statistic");
		GridBagConstraints gbcLblCardStatistic = new GridBagConstraints();
		gbcLblCardStatistic.anchor = GridBagConstraints.NORTH;
		gbcLblCardStatistic.fill = GridBagConstraints.HORIZONTAL;
		gbcLblCardStatistic.insets = new Insets(0, 0, 5, 0);
		gbcLblCardStatistic.gridx = 0;
		gbcLblCardStatistic.gridy = 6;
		panelRight.add(lblCardStatistic, gbcLblCardStatistic);
		
		JButton btnNextCard = new JButton("Next Card");
		btnNextCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Lesson.nextCard();
				Lesson.decreaseCurQuestionsNumber();
				Lesson.makeQueastion();
			}
		});
		GridBagConstraints gbcBtnNextCard = new GridBagConstraints();
		gbcBtnNextCard.ipadx = 25;
		gbcBtnNextCard.insets = new Insets(0, 0, 5, 0);
		gbcBtnNextCard.anchor = GridBagConstraints.NORTH;
		gbcBtnNextCard.gridx = 0;
		gbcBtnNextCard.gridy = 7;
		panelRight.add(btnNextCard, gbcBtnNextCard);
		
		JButton btnResetStatistic = new JButton("Reset Statistic");
		btnResetStatistic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Lesson.resetCardStatistic();
			}
		});
		GridBagConstraints gbcBtnResetStatistic = new GridBagConstraints();
		gbcBtnResetStatistic.insets = new Insets(0, 0, 5, 0);
		gbcBtnResetStatistic.gridx = 0;
		gbcBtnResetStatistic.gridy = 8;
		panelRight.add(btnResetStatistic, gbcBtnResetStatistic);
		
		rbFwdQuizDirection = new JRadioButton("Fwd Quiz Direction");
		rbFwdQuizDirection.setSelected(true);
		GridBagConstraints gbcRbFwdQuizDirection = new GridBagConstraints();
		gbcRbFwdQuizDirection.insets = new Insets(0, 0, 5, 0);
		gbcRbFwdQuizDirection.gridx = 0;
		gbcRbFwdQuizDirection.gridy = 9;
		panelRight.add(rbFwdQuizDirection, gbcRbFwdQuizDirection);
		
		chbSound = new JCheckBox("Sound");
		chbSound.setSelected(true);
		GridBagConstraints gbcChbSound = new GridBagConstraints();
		gbcChbSound.insets = new Insets(0, 0, 5, 0);
		gbcChbSound.gridx = 0;
		gbcChbSound.gridy = 10;
		panelRight.add(chbSound, gbcChbSound);
		
	}

}
