import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.swing.*;

public class CardEditorPanel extends JPanel{
	public DefaultListModel<String> listModelLearningCards;

	CardEditorPanel(MainFrame mFrame){
		GridBagLayout gblCardEditor = new GridBagLayout();
		gblCardEditor.columnWidths = new int[] {30, 70, 30, 30, 30, 30, 0};
		gblCardEditor.rowHeights = new int[] {30, 30, 30, 30, 30, 30, 30, 0};
		gblCardEditor.columnWeights = new double[]{1.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0};
		gblCardEditor.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0};
		this.setLayout(gblCardEditor);
		
		{
			JLabel lblCards = new JLabel("Cards");
			GridBagConstraints gbcLblCards = new GridBagConstraints();
			gbcLblCards.insets = new Insets(0, 0, 5, 5);
			gbcLblCards.gridx = 1;
			gbcLblCards.gridy = 0;
			this.add(lblCards, gbcLblCards);
		}
		
		{
			JLabel lblLearningCards = new JLabel("Learning Cards");
			GridBagConstraints gbcLblLearningCards = new GridBagConstraints();
			gbcLblLearningCards.gridwidth = 2;
			gbcLblLearningCards.insets = new Insets(0, 0, 5, 5);
			gbcLblLearningCards.gridx = 4;
			gbcLblLearningCards.gridy = 0;
			this.add(lblLearningCards, gbcLblLearningCards);
		}
		
		DefaultListModel<String> listModelCards = new DefaultListModel<String>();
		//	add elements from DataBase to listModelCards
		try{
			SqlConnection.resSet = SqlConnection.stmt.executeQuery("SELECT * FROM cards;");
			while(SqlConnection.resSet.next())
			{
				String  card = SqlConnection.resSet.getString("card");
		         System.out.println( "load card = " + card );
				 LearningWords.log.info("load card = " + card);
		         listModelCards.addElement(card);
			}	
		} 
		catch (SQLException e) {
			e.printStackTrace();
			LearningWords.log.log(Level.SEVERE, "SQL executeQuery Error", e);
            System.exit(1);
		}
		//	add JList on JScrollPane
		JScrollPane scrollPaneCards = new JScrollPane();
		JList<String> listCards = new JList<String>(listModelCards);
		listCards.setVisibleRowCount(-1);
		listCards.setLayoutOrientation(JList.VERTICAL);
		listCards.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneCards.setViewportView(listCards);
		GridBagConstraints gbcListCards = new GridBagConstraints();
		gbcListCards.gridwidth = 2;
		gbcListCards.gridheight = 6;
		gbcListCards.insets = new Insets(0, 0, 5, 5);
		gbcListCards.fill = GridBagConstraints.BOTH;
		gbcListCards.gridx = 1;
		gbcListCards.gridy = 1;
		this.add(scrollPaneCards, gbcListCards);
		
		listModelLearningCards = new DefaultListModel<String>();
		//	add elements from DataBase to listModelLearningCards		
		try{
			SqlConnection.resSet = SqlConnection.stmt.executeQuery("SELECT * FROM cards WHERE learning=1;");
			while(SqlConnection.resSet.next())
			{
				String  card = SqlConnection.resSet.getString("card");
		         System.out.println( "load learning card = " + card );
				 LearningWords.log.info("load learning card = " + card);
		         listModelLearningCards.addElement(card);
			}	
		} 
		catch (SQLException e) {
			e.printStackTrace();
			LearningWords.log.log(Level.SEVERE, "SQL executeQuery Error", e);
            System.exit(1);
		}
		//	add JList on JScrollPane
		JScrollPane scrollPaneLearningCards = new JScrollPane();
		JList<String> listLearningCards = new JList<String>(listModelLearningCards);
		listLearningCards.setVisibleRowCount(-1);
		listLearningCards.setLayoutOrientation(JList.VERTICAL);
		listLearningCards.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneLearningCards.setViewportView(listLearningCards);
		GridBagConstraints gbcListLearningCards = new GridBagConstraints();
		gbcListLearningCards.insets = new Insets(0, 0, 5, 5);
		gbcListLearningCards.gridwidth = 2;
		gbcListLearningCards.gridheight = 6;
		gbcListLearningCards.fill = GridBagConstraints.BOTH;
		gbcListLearningCards.gridx = 4;
		gbcListLearningCards.gridy = 1;
		this.add(scrollPaneLearningCards, gbcListLearningCards);

//		add JButton btnAdd that add card to Learning
		
		JButton btnAddToLearning = new JButton("<html>&nbsp;&nbsp;&nbsp;&nbsp;Add To<br>Learning &nbsp;&nbsp;\t&#8250;&#8250;");
		btnAddToLearning.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {				
				String cardName=null;
				//	get selected cardName
				if(listCards.isSelectionEmpty()==false){
					cardName=listCards.getSelectedValue();
				}
				else{
	            	JOptionPane.showMessageDialog(mFrame, "Select Card.", "Empty Selection", JOptionPane.WARNING_MESSAGE);						
				}				
	        	//	set changes to DataBase and JList
	        	if(cardName!=null){
	    			try {
	    				SqlConnection.stmt.execute("UPDATE cards SET learning=1 WHERE card='"+cardName+"';");
	    				System.out.println("Add card to Learning: " + cardName);
	    				LearningWords.log.info("Add card to Learning: " + cardName);
	    			}
	    			catch (SQLException e) {
    					e.printStackTrace();
    					LearningWords.log.log(Level.SEVERE, "SQL execute Error", e);
    		            System.exit(1);
    				}
	    			if (listModelLearningCards.contains(cardName)==false) listModelLearningCards.addElement(cardName);
				}
			}
		});
		GridBagConstraints gbcBtnAddToLearning = new GridBagConstraints();
		gbcBtnAddToLearning.gridheight = 2;
		gbcBtnAddToLearning.fill = GridBagConstraints.BOTH;
		gbcBtnAddToLearning.insets = new Insets(0, 0, 5, 5);
		gbcBtnAddToLearning.gridx = 3;
		gbcBtnAddToLearning.gridy = 1;
		this.add(btnAddToLearning, gbcBtnAddToLearning);	

//		add JButton btnDelFromLearning that delete card from Learning
		
		JButton btnDelFromLearning = new JButton("<html>&nbsp;&nbsp; Del From<br>&#8249;&#8249;&nbsp;&nbsp;Learning");
		btnDelFromLearning.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String cardName=null;
				//	get selected cardName				
				if(listLearningCards.isSelectionEmpty()==false){
					cardName=listLearningCards.getSelectedValue();
				}
				else{
					JOptionPane.showMessageDialog(mFrame, "Select Card.", "Empty Selection", JOptionPane.WARNING_MESSAGE);						
				}
				
		    	//	set changes to DataBase and JList
		    	if(cardName!=null){
					try {
						SqlConnection.stmt.execute("UPDATE cards SET learning=0 WHERE card='"+cardName+"';");
						System.out.println("Delete card from Learning: " + cardName);
						LearningWords.log.info("Delete card from Learning: " + cardName);
					} 
					catch (SQLException e) {
						e.printStackTrace();
						LearningWords.log.log(Level.SEVERE, "SQL execute Error", e);
			            System.exit(1);
					}
		        	if (listModelLearningCards.contains(cardName)==true) listModelLearningCards.removeElement(cardName);
				}
			}
		});
		GridBagConstraints gbcBtnDelFromLearning = new GridBagConstraints();
		gbcBtnDelFromLearning.gridheight = 2;
		gbcBtnDelFromLearning.fill = GridBagConstraints.BOTH;
		gbcBtnDelFromLearning.insets = new Insets(0, 0, 5, 5);
		gbcBtnDelFromLearning.gridx = 3;
		gbcBtnDelFromLearning.gridy = 3;
		this.add(btnDelFromLearning, gbcBtnDelFromLearning);
		
//		add JButton btnNewCard that add new card
		
		JButton btnNewCard = new JButton("New Card");
		btnNewCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {		    
				//	input cardName	        					
				String cardName = (String)JOptionPane.showInputDialog(mFrame,"Enter Card Name:","Card Name",JOptionPane.PLAIN_MESSAGE, null, null, null);
				if((cardName == null) || (cardName.length() == 0)){
					return;
				}			
				
	        	System.out.println("Card: " + cardName);
	        	//	add  cardName to DataBase and JList
				try {
					SqlConnection.resSet = SqlConnection.stmt.executeQuery("SELECT * FROM cards WHERE card="+cardName+";");
					if(SqlConnection.resSet.next()==false){
		    				SqlConnection.stmt.execute("INSERT INTO cards ('card','learning','questionLang','answerLang') VALUES ('"+cardName+"',0,'en','ru');");
			            	if (listModelCards.contains(cardName)==false) listModelCards.addElement(cardName);
							System.out.println("Add New card: " + cardName);
							LearningWords.log.info("Add New card: " + cardName);
					}
					else {
		            	JOptionPane.showMessageDialog(mFrame, "Card Already Exist.", "information", JOptionPane.WARNING_MESSAGE);						
					}
				} 
    			catch (SQLException e) {
					e.printStackTrace();
					LearningWords.log.log(Level.SEVERE, "SQL executeQuery Error", e);
		            System.exit(1);
				}
			}
		});
		GridBagConstraints gbcBtnNewCard = new GridBagConstraints();
		gbcBtnNewCard.fill = GridBagConstraints.HORIZONTAL;
		gbcBtnNewCard.insets = new Insets(0, 0, 5, 5);
		gbcBtnNewCard.gridx = 0;
		gbcBtnNewCard.gridy = 1;
		this.add(btnNewCard, gbcBtnNewCard);
		
//		add JButton btnEditCard that create WordEditorDialog to edit selected card 
		
		JButton btnEditCard = new JButton("Edit Card");
		btnEditCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String cardName=null;
				if(listCards.isSelectionEmpty()==false){
					cardName=listCards.getSelectedValue();
					System.out.println("Edit Card: " + cardName);
					LearningWords.log.info("Edit Card: " + cardName);
					
					WordEditorDialog dialog = new WordEditorDialog(mFrame, cardName);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);					
				}
				else{
	            	JOptionPane.showMessageDialog(mFrame, "Select Card.", "Empty Selection", JOptionPane.WARNING_MESSAGE);						
				}	        	
			}
		});
		GridBagConstraints gbcBtnEditCard = new GridBagConstraints();
		gbcBtnEditCard.fill = GridBagConstraints.HORIZONTAL;
		gbcBtnEditCard.insets = new Insets(0, 0, 5, 5);
		gbcBtnEditCard.gridx = 0;
		gbcBtnEditCard.gridy = 2;
		this.add(btnEditCard, gbcBtnEditCard);
		
//		add JButton btnDelCard that delete card from DataBase and JList
		
		JButton btnDelCard = new JButton("Del Card");
		btnDelCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String cardName=null;
				//	get selected cardName	
				if(listCards.isSelectionEmpty()==true){
	            	JOptionPane.showMessageDialog(mFrame, "Select Card.", "Empty Selection", JOptionPane.WARNING_MESSAGE);
					return;
				}
				else{						
					cardName=listCards.getSelectedValue();
	            	Integer dialogAnswer = JOptionPane.showConfirmDialog(mFrame, "Delete " + cardName + " Card?", "Delete confirmation", JOptionPane.YES_NO_OPTION);
		        	if (dialogAnswer != JOptionPane.YES_OPTION) {
						return;		        		
		        	}
	            	
	            	//	delete cardName from DataBase and JList
					try {
						SqlConnection.resSet = SqlConnection.stmt.executeQuery("SELECT * FROM cards WHERE card='"+cardName+"';");
						if(SqlConnection.resSet.next()==true){
			    			SqlConnection.stmt.execute("DELETE FROM cards WHERE card='"+cardName+"';");
			    			SqlConnection.stmt.execute("DELETE FROM words WHERE card='"+cardName+"';");
				            if (listModelCards.contains(cardName)==true) listModelCards.removeElement(cardName);
			            	if (listModelLearningCards.contains(cardName)==true) listModelLearningCards.removeElement(cardName);
							System.out.println("Delete card: " + cardName);
							LearningWords.log.info("Delete card: " + cardName);
						}
					} 
	    			catch (SQLException e) {
						e.printStackTrace();
						LearningWords.log.log(Level.SEVERE, "SQL executeQuery Error", e);
			            System.exit(1);
					}
				}

			}
		});
		GridBagConstraints gbcBtnDelCard = new GridBagConstraints();
		gbcBtnDelCard.fill = GridBagConstraints.HORIZONTAL;
		gbcBtnDelCard.insets = new Insets(0, 0, 5, 5);
		gbcBtnDelCard.gridx = 0;
		gbcBtnDelCard.gridy = 3;
		this.add(btnDelCard, gbcBtnDelCard);
		

//		add JButton btnExportCard that Export card to XML file
		
		JButton btnExportCard = new JButton("Export Card");
		btnExportCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				CardImportExport card=new CardImportExport();
				String cardName=null;

				//	get selected cardName	
				if(listCards.isSelectionEmpty()==false){
					cardName=listCards.getSelectedValue();
				}
				else{
	            	JOptionPane.showMessageDialog(mFrame, "Select Card.", "Empty Selection", JOptionPane.WARNING_MESSAGE);						
				}
	        	//	choose file to export
			    JFileChooser fc = new JFileChooser();
	            int returnVal = fc.showOpenDialog(mFrame);
	            File file=null;

				try {
		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                file = fc.getSelectedFile();
		                String fileNameWithPath=file.getCanonicalPath();
		            	System.out.println("Opening: " + fileNameWithPath);
		            	
		            	//	export in separate thread
		            	class ExportThread extends Thread {
		            		String inner_fileNameWithPath, inner_cardName;
		            		ExportThread(String fileNameWithPath,String cardName){
			            		inner_fileNameWithPath=fileNameWithPath;
			            		inner_cardName=cardName;		            			
		            		}
	            	        public void run(){
	            	          card.Export(inner_fileNameWithPath, inner_cardName);
	            	        }
		            	}
		            	ExportThread export_thread = new ExportThread(fileNameWithPath,cardName);
		            	export_thread.start();
	
		            	export_thread.join();		            
						System.out.println("Card Export Complete: " + cardName);
						LearningWords.log.info("Card Export Complete: " + cardName);	
		            	JOptionPane.showMessageDialog(mFrame, "Card Export Complete.", "information", JOptionPane.INFORMATION_MESSAGE);	
		            }
		            	
				} 
    			catch (IOException e) {
					e.printStackTrace();
					LearningWords.log.log(Level.WARNING, "getCanonicalPath Error", e);
				}
    			catch (InterruptedException e) {
					e.printStackTrace();
					LearningWords.log.log(Level.WARNING, "export_thread.join Error", e);
				}
			}
		});
		GridBagConstraints gbcBtnExportCard = new GridBagConstraints();
		gbcBtnExportCard.fill = GridBagConstraints.HORIZONTAL;
		gbcBtnExportCard.insets = new Insets(0, 0, 5, 5);
		gbcBtnExportCard.gridx = 0;
		gbcBtnExportCard.gridy = 4;
		this.add(btnExportCard, gbcBtnExportCard);
		
//		add JButton btnImportCard that Import card from XML file
		
		JButton btnImportCard = new JButton("Import Card");
		btnImportCard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//	choose file for import
			    JFileChooser fc = new JFileChooser();
	            int returnVal = fc.showOpenDialog(mFrame);
	            File file=null;
	            
				CardImportExport card=new CardImportExport();
				try {
		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		                file = fc.getSelectedFile();
		                String fileNameWithPath=file.getCanonicalPath();
		            	System.out.println("Opening: " + fileNameWithPath);
		            	
		            	String cardName=null;
		            	//	get cardName from file name without extension 
		            	if(file.getName().indexOf(".")>-1){
		            		cardName=file.getName().substring(0,file.getName().lastIndexOf("."));
		            	}
		            	else{
		            		cardName=file.getName();
		            	}
		            	
		            	//	confirm cardName
		            	String dlgCaption="Card Name";
		            	do{
			            	String answ = (String)JOptionPane.showInputDialog(mFrame, "Import Card Name:",dlgCaption, JOptionPane.PLAIN_MESSAGE,null,null,cardName);
	
							if (answ == null) {
								return;
							}
							else if (answ.length() > 0) {
								cardName=answ;
							} 		
							
							if(listModelCards.contains(cardName)==true) dlgCaption=dlgCaption.concat(" ALLREADY EXIST !!!");
		            	} while(listModelCards.contains(cardName)==true);
		            	
		            	// 	import in separate thread
		            	class ImportThread extends Thread {
		            		File file;
		            		String cardName;
		            		boolean result=false;
		            		ImportThread(File arg_file,String arg_cardName){
			            		file=arg_file;
			            		cardName=arg_cardName;		            			
		            		}
	            	        public void run(){
	            	        	result=card.Import(file, cardName);
	            	        }
		            	}
		            	ImportThread import_thread = new ImportThread(file,cardName);
		            	import_thread.start();
		            	btnImportCard.setText("Wait...");
		            	btnImportCard.update(btnImportCard.getGraphics());
		            	import_thread.join();
		            	btnImportCard.setText("Import Card");
		            	
		            	if (import_thread.result==true) {
		            		listModelCards.addElement(cardName);
							System.out.println("Card Import Complete: " + cardName);
							LearningWords.log.info("Card Import Complete: " + cardName);
			            	JOptionPane.showMessageDialog(mFrame, "Card Import Complete.", "information", JOptionPane.INFORMATION_MESSAGE);
		            	}
		            	
		            } else {
		            	System.out.println("Import command cancelled by user.");
		            }
				}
    			catch (IOException e) {
					e.printStackTrace();
					LearningWords.log.log(Level.WARNING, "getCanonicalPath Error", e);
				}
    			catch (InterruptedException e) {
					e.printStackTrace();
					LearningWords.log.log(Level.WARNING, "import_thread.join Error", e);
				}
			}
		});
		GridBagConstraints gbcBtnImportCard = new GridBagConstraints();
		gbcBtnImportCard.fill = GridBagConstraints.HORIZONTAL;
		gbcBtnImportCard.insets = new Insets(0, 0, 5, 5);
		gbcBtnImportCard.gridx = 0;
		gbcBtnImportCard.gridy = 5;
		this.add(btnImportCard, gbcBtnImportCard);
	}
}
