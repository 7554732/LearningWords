import java.awt.EventQueue;

import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	public MultpleChoicePanel multipleChoice;
	public OptionsPanel options;
	public CardEditorPanel cardEditor;
	public WindowFocusListener mFrameWindowFocusListener;
	
//	Create the frame.
	
	public MainFrame() {
		
		mFrameWindowFocusListener = new WindowFocusListener() {
			//	on focus skip waiting and go to lesson
			public void windowGainedFocus(WindowEvent arg0) {
				Lesson.nextLesson.cancel();
				removeWindowFocusListener(mFrameWindowFocusListener);
				System.out.println("windowGainedFocus");
				Lesson.nextCard();
				Lesson.makeQueastion();
			}
			public void windowLostFocus(WindowEvent arg0) {
			}
		};
		
		//	close connection on exit program
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				SqlConnection.Close();                
		        e.getWindow().setVisible(false);
                System.exit(0);
			}
		});
		
		setTitle("Learning Words");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 310);
		
		// create JPanel
		contentPane = new JPanel();
		contentPane.setBackground(UIManager.getColor("Panel.background"));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		//	create layout
		GridBagLayout gblContentPane = new GridBagLayout();
		gblContentPane.columnWidths = new int[] {311, 0};
		gblContentPane.rowHeights = new int[]{51, 0};
		gblContentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gblContentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		//	set layout to contentPane
		contentPane.setLayout(gblContentPane);
		
		//	create JTabbedPane
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbcTabbedPane = new GridBagConstraints();
		gbcTabbedPane.fill = GridBagConstraints.BOTH;
		gbcTabbedPane.gridx = 0;
		gbcTabbedPane.gridy = 0;
		contentPane.add(tabbedPane, gbcTabbedPane);
		
		//	add multipleChoice to tabbedPane
		multipleChoice = new MultpleChoicePanel(this);
		tabbedPane.addTab("Multiple Choice", null, multipleChoice, null);

		//	add options to tabbedPane
		options = new OptionsPanel(this);
		tabbedPane.addTab("Options", null, options, null);
	
		//	add cardEditor to tabbedPane
		cardEditor = new CardEditorPanel(this);
		tabbedPane.addTab("Card Editor", null, cardEditor, null);
		
		//	set this frame as main for Lesson
		Lesson.setMainFrame(this);
		
		//	load next card
		Lesson.nextCard();
		
		//	create new question
		Lesson.makeQueastion();
		
		this.setVisible(true);
		
		
	}
}
