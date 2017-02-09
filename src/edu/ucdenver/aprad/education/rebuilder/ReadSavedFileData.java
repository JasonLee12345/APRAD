package edu.ucdenver.aprad.education.rebuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

import org.apache.http.util.EncodingUtils;

import edu.ucdenver.aprad.education.rebuilder.fd.RebuilderFD;

/*****************************
 **
 ** @author Kun Li
 ** @created 19 Mar 2015
 ** 
 ** @modified_by Kun Li
 ** @modified_date 3 Apr 2015
 **
 *****************************/

public class ReadSavedFileData implements Serializable{
	
	public String headerAndData[];
	public static int theLen;
	public static double theFrequency;
	public static double theTimeSpan;
	public static short[] actualData;
	
	public ReadSavedFileData(Option option) throws IOException {
		
		String fileContent = readFileSdcardFile(option.getPath());
		headerAndData = getHeaderAndData(fileContent);
		
		theFrequency = getTheFrequency(headerAndData[0]);
		theTimeSpan = getTheTimeSpan(headerAndData[0]);
		actualData = translateMyData(headerAndData[1]);
	}
	
	
	public String readFileSdcardFile(String fileName) throws IOException{  
		  String res="";   
		  
		  try{   
		         FileInputStream fin = new FileInputStream(fileName);   
		  
		         int length = fin.available();   
		  
		         byte [] buffer = new byte[length];   
		         fin.read(buffer);       
		  
		         res = EncodingUtils.getString(buffer, "UTF-8");   
		  
		         fin.close();       
		        }
		    catch(Exception e){   
		    	e.printStackTrace();   
		    }   
		   	return res;   
		}  
	
	
	public String[] getHeaderAndData(String fileContent){
		String header[] = null;
		
		header = fileContent.split("!");
		
		return header;
	}
	
	
	/**
	 * To get the frequency value saved in the file.
	 * @param header "File Header Information" String.
	 * @return
	 */
	double getTheFrequency(String header) {
		int start = header.indexOf("Frequency:");
		int end = header.indexOf(" Hz");
		String frequency = header.substring(start + 11, end);
		double f = Double.parseDouble(frequency);
		return f;
	}
	
	
	/**
	 * To get the time duration value saved in the file.
	 * @param header "File Header Information" String.
	 * @return
	 */
	double getTheTimeSpan(String header) {
		int start = header.indexOf("Span:");
		int end = header.indexOf(" s'");
		String span = header.substring(start + 6, end);
		double f = Double.parseDouble(span);
		return f;
	}
	
	
	/**
	 * Converting String data to short ones.
	 * @param fileContent Saved data as a string.
	 * @return rawData short[]
	 */
	public short[] translateMyData(String fileContent){
		String[] rawDataInString = null;
		short[] rawData;
		
		rawDataInString = fileContent.split(",");
		theLen = rawDataInString.length;
		rawData = new short[theLen];
		for(int i=0; i<theLen; i++) {
			rawData[i] = Short.parseShort(rawDataInString[i]);
		}
		return rawData;
	}
}
