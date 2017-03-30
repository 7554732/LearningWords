
import java.net.*;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.io.*;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.*;

public class GoogleTranslator {
	private String langFrom="en", langTo="ru";

//		initialize language pair 
	
	public GoogleTranslator(String arglangFrom,String arglangTo){
		langFrom=arglangFrom;
		langTo=arglangTo;
	}
	
//	get connection to Google speech mp3 stream for word
	
	public HttpURLConnection getConnToSpeech(String word)  { 
		try {
			//	Configure url
			String url = "https://translate.googleapis.com/translate_tts?ie=UTF-8"+
			"&q=" + URLEncoder.encode(word, "UTF-8")+
			"&tl=" + langFrom + 
			"&total=1&idx=0&textlen="+word.length()+
			"&client=gtx";
			System.out.println(url);
			LearningWords.log.info(url);
			  
			//	set connection
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection(); 
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
		 
		    // get content length 
		    long len = con.getContentLengthLong(); 
		    if(len == -1) 
		      System.out.println("Content length unavailable."); 
		    else 
		      System.out.println("Content-Length: " + len); 
		 
		    if(len != 0) { 
			    return con;
		    } 
		    else { 
		      System.out.println("No content available.");		      
		    } 
		} 
		catch (Exception e) {
			e.printStackTrace();
			LearningWords.log.log(Level.WARNING, "getConnToSpeech Error", e);
		}
	    return null;
	}
	
//	play mp3 stream located at connection
	
	public void playURL(HttpURLConnection con){
	    try {
			InputStream input;
			input = con.getInputStream(); 			    
	        Player playMP3 = new Player(input);
	        playMP3.play();
	        input.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
			LearningWords.log.log(Level.WARNING, "getInputStream or close  Error", e);
		}		
		catch (JavaLayerException e) {
			e.printStackTrace();
			LearningWords.log.log(Level.WARNING, "mp3 Player Error", e);
		}	
	}
	
//	translate word from langFrom to langTo 
	
	public String translate(String word)  { 
		try {
			//	Configure url
			String url = "https://translate.googleapis.com/translate_a/single?"+
			"client=gtx&"+
			"sl=" + langFrom + 
			"&tl=" + langTo + 
			"&dt=t&q=" + URLEncoder.encode(word, "UTF-8");    
			System.out.println(url);
			LearningWords.log.info(url);

			//	set connection
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection(); 
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
		 
		    // get content length 
		    long len = con.getContentLengthLong(); 
		    if(len == -1) 
		      System.out.println("Content length unavailable."); 
		    else 
		      System.out.println("Content-Length: " + len); 
		    
		    //	get String from Stream and parse Translation 
		    if(len != 0) { 
			      InputStream input = con.getInputStream(); 
			      byte[] bytesArray = new byte[128];
			      int i=0,c;
			      while (((c = input.read()) != -1)) { 
			    	  bytesArray[i++]=(byte)c;		
			    	  if(i>127) break;
			      }  
			      String output = new String(bytesArray, Charset.forName("UTF-8"));
			      int beginIndex=output.indexOf("\"");
			      int endIndex=output.indexOf("\"",beginIndex+1);
			      input.close();
		    	  return output.substring(beginIndex+1, endIndex);
		    } 
		    else { 
		      System.out.println("No content available.");		      
		    } 
		}
		catch (Exception e) {
			e.printStackTrace();
			LearningWords.log.log(Level.WARNING, "translate Error", e);
		}	
		return null;
	}
	

}