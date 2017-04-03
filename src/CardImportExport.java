
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CardImportExport {	
	
//	Import from XML file to card in connected DataBase 
	
	public boolean Import(File fileName,String cardName){
		String questionLang="en",answerLang="ru";
    	System.out.println(cardName+" Import <<< from "+fileName);
    	LearningWords.log.info(cardName+" Import <<< from "+fileName);
        try {

			//	Create Document Builder
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    // Create Tree of XML fileName
            Document document = documentBuilder.parse(fileName);
            //	get Root element
            Node root = document.getDocumentElement();
            
			//	get language pair from attribute subroot node cards
            if(root.getNodeName()=="cards" & root.hasAttributes()){
            	if(root.getAttributes().getNamedItem("questionLang")!=null){
            		questionLang=root.getAttributes().getNamedItem("questionLang").getNodeValue();
            		System.out.println("questionLang "+questionLang);
            	}
            	if(root.getAttributes().getNamedItem("answerLang")!=null){
            		answerLang=root.getAttributes().getNamedItem("answerLang").getNodeValue();
            		System.out.println("answerLang "+answerLang);                		
            	}
            }
            
            //	add card cardName if not exist in DataBase 
			try {
				SqlConnection.resSet = SqlConnection.stmt.executeQuery("SELECT * FROM cards WHERE card='"+cardName+"';");
				if(SqlConnection.resSet.next()==false){
	    				SqlConnection.stmt.execute("INSERT INTO cards ('card','learning','questionLang','answerLang') VALUES ('"+cardName+"',0,'"+questionLang+"','"+answerLang+"');");
				}
			} 
			catch (SQLException e) {
				e.printStackTrace();
				LearningWords.log.log(Level.SEVERE, "SQL executeQuery Error", e);
	            System.exit(1);
			}
			
            // get all subroot card nodes
            NodeList cardsNodeList = root.getChildNodes();
            for (int i = 0; i < cardsNodeList.getLength(); i++) {
                Node cardNode = cardsNodeList.item(i);
                if (cardNode.getNodeType() != Node.TEXT_NODE) 
                {
                	String question=null;
                	String answer=null;
                    NodeList cardProps = cardNode.getChildNodes();
                    for(int j = 0; j < cardProps.getLength(); j++) {
                        Node cardProp = cardProps.item(j);
                        //	get question and answer from subelements of each cardNode 
                        if ((cardProp.getNodeType() != Node.TEXT_NODE) & (cardProp.getNodeName()!="comment")) {
                            if(cardProp.getNodeName()=="question")question=cardProp.getChildNodes().item(0).getTextContent();
                            if(cardProp.getNodeName()=="answer")answer=cardProp.getChildNodes().item(0).getTextContent();
                        }
                    }
                    //	insert question, answer and cardName to 'words' table of current DataBase
                    if(question!=null & answer!=null){
                    	
	        			try {
	        				SqlConnection.stmt.execute("INSERT INTO words ('card','question','answer') VALUES ('"+cardName+"','"+question+"','"+answer+"');");
	        				System.out.println(question + ":" + answer);
	        			}
	        			catch (SQLException e) {
	        				e.printStackTrace();
	        				LearningWords.log.log(Level.SEVERE, "SQL execute Error", e);
	        	            System.exit(1);
	        			}
	        		}
                }
            }
            return true;
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
	}
	
//	Export card from  connected DataBase to  XML file
	
	public void Export(String fileName,String cardName){
		String questionLang="en",answerLang="ru";		
    	System.out.println(cardName+" Export >>> from "+fileName);		
    	LearningWords.log.info(cardName+" Export >>> from "+fileName);
    	//	get language pair from current DataBase
        try {
			SqlConnection.resSet = SqlConnection.stmt.executeQuery("SELECT * FROM cards WHERE card='"+cardName+"';");
			if(SqlConnection.resSet.next()==true){
				questionLang = SqlConnection.resSet.getString("questionLang");
				answerLang = SqlConnection.resSet.getString("answerLang");
	    		System.out.println("answerLang "+answerLang); 
	    		System.out.println("questionLang "+questionLang);
			}
              		
			//	Create Document Builder
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    // Create Tree of XML document
            Document document = documentBuilder.newDocument();
            
            
            // create root node and set language attributes
            Element cardsElement = document.createElement("cards");
            cardsElement.setAttribute("questionLang", questionLang);
            cardsElement.setAttribute("answerLang", answerLang);
            
            //	add every pair question and answer of cardName from 'words' table connected DataBase to created subnode 'card'.
            //	then add it to root node
			SqlConnection.resSet = SqlConnection.stmt.executeQuery("SELECT * FROM words WHERE card='"+cardName+"';");
			while(SqlConnection.resSet.next())
			{
				String question = SqlConnection.resSet.getString("question");
				String  answer = SqlConnection.resSet.getString("answer");
            	System.out.println(question + ":" + answer);
		         
	            Element questionElement = document.createElement("question");
	            questionElement.setTextContent(question);
	            Element answerElement = document.createElement("answer");
	            answerElement.setTextContent(answer);
        
	            Element cardElement = document.createElement("card");
	            cardElement.appendChild(questionElement);
	            cardElement.appendChild(answerElement);
	            cardsElement.appendChild(cardElement);
			}	
			//	add created root node to document
            document.appendChild(cardsElement);
            
            // Write result DOM to fileName
            writeDocument(fileName,document);
 
        }
		catch (SQLException e) {
			e.printStackTrace();
			LearningWords.log.log(Level.SEVERE, "SQL executeQuery Error", e);
            System.exit(1);
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			LearningWords.log.log(Level.WARNING, "newDocumentBuilder Error", e);
		}
    }

//		 Write XML DOM to file
	
    private void writeDocument(String fileName,Document document) throws TransformerFactoryConfigurationError {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "html");
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
