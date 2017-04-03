import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import java.awt.Insets;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class WordEditorDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private DialogTableModel model;
	private JComboBox questionLang = new JComboBox();
	private JComboBox answerLang = new JComboBox();

	private Map<String, String> langHashMap = new LinkedHashMap<>();	//	pairs of languages and their codes
	
//	 add data to TableModel
	
	class DialogTableModel extends DefaultTableModel{  
		public DialogTableModel(String inner_cardName){
			this.addColumn("Question");
			this.addColumn("Answer");
			try{
				SqlConnection.resSet = SqlConnection.stmt.executeQuery("SELECT * FROM words WHERE card='"+inner_cardName+"';");
				while(SqlConnection.resSet.next()){	     	
				   String[] data= {  SqlConnection.resSet.getString("question"),SqlConnection.resSet.getString("answer") };  
				   this.addRow(data);   
				}	
			} 
			catch (SQLException e) {
				e.printStackTrace();
				LearningWords.log.log(Level.SEVERE, "SQL executeQuery Error", e);
	            System.exit(1);
			}
		}
		
//		save data from Table to DataBase
		
		public void saveData(String inner_cardName){
			try {
				//	delete all old values
	    		SqlConnection.stmt.execute("DELETE FROM words WHERE card='"+inner_cardName+"';");
			} 
			catch (SQLException e) {
				e.printStackTrace();
				LearningWords.log.log(Level.SEVERE, "SQL execute Error", e);
	            System.exit(1);
			}
			//	add new values to table 'words' in DataBase
            for (int i = 0; i < table.getRowCount(); i++) {
            	String question="";
            	String answer="";
            	question=(String)this.getValueAt(i,0);
            	answer=(String)this.getValueAt(i,1);
                if(question!="" & answer!=""){
                	System.out.println(question + ":" + answer);
        			try {
        				SqlConnection.stmt.execute("INSERT INTO words ('card','question','answer') VALUES ('"+inner_cardName+"','"+question+"','"+answer+"');");
        			} 
        			catch (SQLException e) {
        				e.printStackTrace();
        				LearningWords.log.log(Level.SEVERE, "SQL execute Error", e);
        	            System.exit(1);
        			}
        		}
            }
		}
	}
	
//		set HashMap 
	
	private void setLangHashMap(){
		langHashMap.put("Afrikaans","af");
		langHashMap.put("Albanian", "sq");
		langHashMap.put("Amharic", "am");
		langHashMap.put("Arabic", "ar");
		langHashMap.put("Armenian", "hy");
		langHashMap.put("Azeerbaijani", "az");
		langHashMap.put("Basque", "eu");
		langHashMap.put("Belarusian", "be");
		langHashMap.put("Bengali", "bn");
		langHashMap.put("Bosnian", "bs");
		langHashMap.put("Bulgarian", "bg");
		langHashMap.put("Catalan", "ca");
		langHashMap.put("Cebuano", "ceb");
		langHashMap.put("Chichewa", "ny");
		langHashMap.put("Chinese (Simplified)", "zh-CN");
		langHashMap.put("Chinese (Traditional)", "zh-TW");
		langHashMap.put("Corsican", "co");
		langHashMap.put("Croatian", "hr");
		langHashMap.put("Czech", "cs");
		langHashMap.put("Danish", "da");
		langHashMap.put("Dutch", "nl");
		langHashMap.put("English", "en");
		langHashMap.put("Esperanto", "eo");
		langHashMap.put("Estonian", "et");
		langHashMap.put("Filipino", "tl");
		langHashMap.put("Finnish", "fi");
		langHashMap.put("French", "fr");
		langHashMap.put("Frisian", "fy");
		langHashMap.put("Galician", "gl");
		langHashMap.put("Georgian", "ka");
		langHashMap.put("German", "de");
		langHashMap.put("Greek", "el");
		langHashMap.put("Gujarati", "gu");
		langHashMap.put("Haitian Creole", "ht");
		langHashMap.put("Hausa", "ha");
		langHashMap.put("Hawaiian", "haw");
		langHashMap.put("Hebrew", "iw");
		langHashMap.put("Hindi", "hi");
		langHashMap.put("Hmong", "hmn");
		langHashMap.put("Hungarian", "hu");
		langHashMap.put("Icelandic", "is");
		langHashMap.put("Igbo", "ig");
		langHashMap.put("Indonesian", "id");
		langHashMap.put("Irish", "ga");
		langHashMap.put("Italian", "it");
		langHashMap.put("Japanese", "ja");
		langHashMap.put("Javanese", "jw");
		langHashMap.put("Kannada", "kn");
		langHashMap.put("Kazakh", "kk");
		langHashMap.put("Khmer", "km");
		langHashMap.put("Korean", "ko");
		langHashMap.put("Kurdish", "ku");
		langHashMap.put("Kyrgyz", "ky");
		langHashMap.put("Lao", "lo");
		langHashMap.put("Latin", "la");
		langHashMap.put("Latvian", "lv");
		langHashMap.put("Lithuanian", "lt");
		langHashMap.put("Luxembourgish", "lb");
		langHashMap.put("Macedonian", "mk");
		langHashMap.put("Malagasy", "mg");
		langHashMap.put("Malay", "ms");
		langHashMap.put("Malayalam", "ml");
		langHashMap.put("Maltese", "mt");
		langHashMap.put("Maori", "mi");
		langHashMap.put("Marathi", "mr");
		langHashMap.put("Mongolian", "mn");
		langHashMap.put("Burmese", "my");
		langHashMap.put("Nepali", "ne");
		langHashMap.put("Norwegian", "no");
		langHashMap.put("Pashto", "ps");
		langHashMap.put("Persian", "fa");
		langHashMap.put("Polish", "pl");
		langHashMap.put("Portuguese", "pt");
		langHashMap.put("Punjabi", "ma");
		langHashMap.put("Romanian", "ro");
		langHashMap.put("Russian", "ru");
		langHashMap.put("Samoan", "sm");
		langHashMap.put("Scots Gaelic", "gd");
		langHashMap.put("Serbian", "sr");
		langHashMap.put("Sesotho", "st");
		langHashMap.put("Shona", "sn");
		langHashMap.put("Sindhi", "sd");
		langHashMap.put("Sinhala", "si");
		langHashMap.put("Slovak", "sk");
		langHashMap.put("Slovenian", "sl");
		langHashMap.put("Somali", "so");
		langHashMap.put("Spanish", "es");
		langHashMap.put("Sundanese", "su");
		langHashMap.put("Swahili", "sw");
		langHashMap.put("Swedish", "sv");
		langHashMap.put("Tajik", "tg");
		langHashMap.put("Tamil", "ta");
		langHashMap.put("Telugu", "te");
		langHashMap.put("Thai", "th");
		langHashMap.put("Turkish", "tr");
		langHashMap.put("Ukrainian", "uk");
		langHashMap.put("Urdu", "ur");
		langHashMap.put("Uzbek", "uz");
		langHashMap.put("Vietnamese", "vi");
		langHashMap.put("Welsh", "cy");
		langHashMap.put("Xhosa", "xh");
		langHashMap.put("Yiddish", "yi");
		langHashMap.put("Yoruba", "yo");
		langHashMap.put("Zulu", "zu");
	}
	
//		get language by its code
	
	private String getLang(String cardName, String QALang){
		try{
			SqlConnection.resSet = SqlConnection.stmt.executeQuery("SELECT * FROM cards WHERE card='"+cardName+"';");
			if(SqlConnection.resSet.next()){	     	
				String langVal= SqlConnection.resSet.getString(QALang); 
				Set<Map.Entry<String, String>> entries = langHashMap.entrySet(); 
				Iterator<Map.Entry<String, String>> entriesIterator = entries.iterator();
				int i = 0;
				while(entriesIterator.hasNext()){
				    Map.Entry mapping = (Map.Entry) entriesIterator.next();
				    if(langVal.equals(mapping.getValue().toString())) return mapping.getKey().toString();
				    i++;
				}
			}	
		} 
		catch (SQLException e) {
			e.printStackTrace();
			LearningWords.log.log(Level.SEVERE, "SQL executeQuery Error", e);
            System.exit(1);
		}
		return null;
	}

//		save current languages to DataBase
	
	private void saveLang(String cardName){
		try {SqlConnection.stmt.execute("UPDATE cards "
				+ "SET questionLang='"+langHashMap.get(questionLang.getSelectedItem())+"', "
					+ "answerLang='"+langHashMap.get(answerLang.getSelectedItem())+"'  "
				+ "WHERE card='"+cardName+"';");
		} 
		catch (SQLException e) {
			e.printStackTrace();
			LearningWords.log.log(Level.SEVERE, "SQL execute Error", e);
            System.exit(1);
		}		
	}
	
//		create and set Title for WordEditorDialog
	
	private String createTitle(String cardName){
		String title="Card Name: "+cardName+" , Words: "+model.getRowCount();
		this.setTitle(title);	//	set Title
		return title;
	}
	
	public WordEditorDialog(JFrame owner, String cardName) {	
		super(owner);	
		setLangHashMap();		
		setBounds(100, 100, 435, 295);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gblContentPanel = new GridBagLayout();
		gblContentPanel.columnWidths = new int[] {10, 10, 10, 10, 10, 0, 10};
		gblContentPanel.rowHeights = new int[]{0, 0, 0, 0};
		gblContentPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0};
		gblContentPanel.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gblContentPanel);
		
//		add question language JComboBox questionLang
		
		{
			questionLang.setModel(new DefaultComboBoxModel(langHashMap.keySet().toArray()));
			questionLang.setSelectedItem(getLang(cardName,"questionLang"));
			GridBagConstraints gbcQuestionLang = new GridBagConstraints();
			gbcQuestionLang.gridwidth = 2;
			gbcQuestionLang.fill = GridBagConstraints.HORIZONTAL;
			gbcQuestionLang.insets = new Insets(0, 0, 5, 5);
			gbcQuestionLang.gridx = 1;
			gbcQuestionLang.gridy = 0;
			contentPanel.add(questionLang, gbcQuestionLang);
		}

//		add JTable on JScrollPane
		
		{			
			model = new DialogTableModel(cardName);
			
			createTitle(cardName);	//	set Title
			
			//	add answer language JComboBox answerLang
			{
				answerLang.setModel(new DefaultComboBoxModel(langHashMap.keySet().toArray()));
				answerLang.setSelectedItem(getLang(cardName,"answerLang"));
				GridBagConstraints gbcAnswerLang = new GridBagConstraints();
				gbcAnswerLang.gridwidth = 2;
				gbcAnswerLang.fill = GridBagConstraints.HORIZONTAL;
				gbcAnswerLang.insets = new Insets(0, 0, 5, 5);
				gbcAnswerLang.gridx = 3;
				gbcAnswerLang.gridy = 0;
				contentPanel.add(answerLang, gbcAnswerLang);
			}

			JScrollPane scrollPane = new JScrollPane();   
			table = new JTable(model);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			scrollPane.setViewportView(table);   
			GridBagConstraints gbcTable = new GridBagConstraints();
			gbcTable.gridwidth = 5;
			gbcTable.insets = new Insets(0, 0, 5, 5);
			gbcTable.fill = GridBagConstraints.BOTH;
			gbcTable.gridx = 1;
			gbcTable.gridy = 1;
			contentPanel.add(scrollPane, gbcTable);
		}
		
//		add JButton btnAdd that add Row in the table
		
		{
			JButton btnAdd = new JButton("Add");
			btnAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int selectedRow=table.getSelectedRow();
					String[] data= { "",""};
					if(selectedRow==-1){
						model.insertRow(0,data);						
					}
					else
					{
						model.insertRow(selectedRow,data);
					}
					createTitle(cardName);	//	set Title
				}
			});
			GridBagConstraints gbcBtnAdd = new GridBagConstraints();
			gbcBtnAdd.anchor = GridBagConstraints.WEST;
			gbcBtnAdd.ipadx = 35;
			gbcBtnAdd.insets = new Insets(0, 0, 0, 5);
			gbcBtnAdd.gridx = 1;
			gbcBtnAdd.gridy = 2;
			contentPanel.add(btnAdd, gbcBtnAdd);
		}
		
//		add JButton btnDelete that delete selected Row from the table
		
		{
			JButton btnDelete = new JButton("Delete");
			btnDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int selectedRow=table.getSelectedRow();
					if(selectedRow!=-1){
						model.removeRow(selectedRow);
					}
					createTitle(cardName);	//	set Title
				}
			});
			GridBagConstraints gbcBtnDelete = new GridBagConstraints();
			gbcBtnDelete.ipadx = 20;
			gbcBtnDelete.anchor = GridBagConstraints.WEST;
			gbcBtnDelete.insets = new Insets(0, 0, 0, 5);
			gbcBtnDelete.gridx = 2;
			gbcBtnDelete.gridy = 2;
			contentPanel.add(btnDelete, gbcBtnDelete);
		}
		
//		add JButton btnTranslate that translate Question in selected row
		
		{
			JButton btnTranslate = new JButton("Translate");
			btnTranslate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(table.isEditing()){
		            	JOptionPane.showMessageDialog(owner, "Finish cell Editing and Select Row to translate word.", "No Row Selection", JOptionPane.WARNING_MESSAGE);	
					}
					else if(table.getSelectedRow()==-1){
		            	JOptionPane.showMessageDialog(owner, "Select Row to translate word.", "No Row Selection", JOptionPane.WARNING_MESSAGE);	
					}
					else{
						GoogleTranslator gt = new GoogleTranslator(langHashMap.get(questionLang.getSelectedItem()),langHashMap.get(answerLang.getSelectedItem()));
						String transAnswer=gt.translate(table.getValueAt(table.getSelectedRow(),0).toString());
						System.out.println(table.getValueAt(table.getSelectedRow(),0).toString()+" : "+transAnswer);
						if(transAnswer!=null){
							table.setValueAt(transAnswer,table.getSelectedRow(),1);
						}				
					}
				}
			});
			GridBagConstraints gbcBtnTranslate = new GridBagConstraints();
			gbcBtnTranslate.anchor = GridBagConstraints.WEST;
			gbcBtnTranslate.insets = new Insets(0, 0, 0, 5);
			gbcBtnTranslate.gridx = 3;
			gbcBtnTranslate.gridy = 2;
			contentPanel.add(btnTranslate, gbcBtnTranslate);
		}
		
//		add JButton btnSave that save data to DataBase
		
		{
			JButton btnSave = new JButton("Save");
			btnSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					btnSave.setText("Wait...");
					btnSave.update(btnSave.getGraphics());
					model.saveData(cardName);
					saveLang(cardName);
					btnSave.setText("Save");
				}
			});
			GridBagConstraints gbcBtnSave = new GridBagConstraints();
			gbcBtnSave.anchor = GridBagConstraints.WEST;
			gbcBtnSave.ipadx = 35;
			gbcBtnSave.insets = new Insets(0, 0, 0, 5);
			gbcBtnSave.gridx = 4;
			gbcBtnSave.gridy = 2;
			contentPanel.add(btnSave, gbcBtnSave);
		}
	}

}
