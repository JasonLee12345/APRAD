package edu.ucdenver.aprad.spectrogram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.ucdenver.aprad.R.string;
import edu.ucdenver.aprad.preferences.Preferences;
import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Display;
import android.widget.Toast;

/*****************************
 **
 ** @author Kun Li
 ** @created 20 Feb 2015
 ** 
 ** @modified_by 
 ** @modified_date 
 **
 *****************************/

public class NewSaveDialog extends DialogFragment{

	public final static int THREAD_NUMBER	= 50;
	
	public static boolean SAVE_PRESSED = false;
	public static String FILE_PATH = null;
	public static String[] data = new String[THREAD_NUMBER];
	public static int[] dataLength = new int[THREAD_NUMBER];
	
	public ExecutorService excutor = Executors.newCachedThreadPool();
	public CountDownLatch latch = new CountDownLatch(THREAD_NUMBER);
	public CountDownLatch small_latch = new CountDownLatch(1);
	
	public ExecutorService write_excutor = Executors.newCachedThreadPool();
	public CountDownLatch write_latch = new CountDownLatch(THREAD_NUMBER);
	public CountDownLatch write_small_latch = new CountDownLatch(1);
	
	//Handler handler = new Handler();
	
	@Override
	  public Dialog onCreateDialog(Bundle savedInstanceState)
	  {
		  //final Dialog prepareProcessingDialog = buildProcessDialog(getActivity(), "Transforming data...");
		  final Dialog saveProcessingDialog = buildProcessDialog(getActivity(), "Saving in progress...");
	      // Use the Builder class for convenient dialog construction
	      final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    		  
	      builder.setTitle("Save to the file?").setMessage("This should take just a few minutes, please be patient")
	      		.setPositiveButton(string.save, new DialogInterface.OnClickListener() {
	                 public void onClick(DialogInterface dialog, int id)
	                 {
	                	 //Looper.prepare();
	                	 
	                	 SAVE_PRESSED = true;
	                	 
	                	 String status = Environment.getExternalStorageState();
	             		 if (status.equals(Environment.MEDIA_MOUNTED)){
	             			 
	             			 	saveProcessingDialog.show();
	             			 	//prepareProcessingDialog.show();
	             			 	//This alert dialog cannot be shown in the child thread(anonymous thread 1)
	             			 	//where I explanatorily note it in the Anonymous Thread 1 below.
	             			 	//Thus you need the knowledge of communication between threads if you want it, after that it can be shown.
	             			 	//And some structure should be changed after that, including one code sentence above.
	             			 	
	             			 	transData();
	             			 	
	                	  }else{
	                   	  		
	                   	  		AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
	                   	  		builder1.setTitle("Reminder").setMessage("SD card not found!")
	                	  				.setPositiveButton("OK", new DialogInterface.OnClickListener(){
	                												public void onClick(DialogInterface dialog, int id){  
	                											
	                												}
	                											}).show();
	                								
	                   	 }
	             		 
	                 }//onClick() ends     
	                 
	      		}//onClickListener() ends
	      		).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	                 								public void onClick(DialogInterface dialog, int id) {
	                	
	                 								}
	             								}
	      );
	      
	      
	      /**
	       * Anonymous Thread 1
	       */
	      new Thread(new Runnable() {
	    	  
	    	  //public Handler mHandler;
	    	  
	 			@Override  
	 			public void run() {
	 				
	 				//Looper.prepare();
	 				
	 				if(Spectrogram.signal_countForFiles <= THREAD_NUMBER){
	 					try {
							small_latch.await();
							//prepareProcessingDialog.dismiss();
							saveData();
							//saveProcessingDialog.show(); //the explanatory note described above
							write_small_latch.await();
							//handler.sendEmptyMessage(0);
							saveProcessingDialog.dismiss();
							
						} catch (InterruptedException e) {
							
							e.printStackTrace();
						}
	 				}
	 				else{
	 					try {
							latch.await();
							//prepareProcessingDialog.dismiss();
							saveData();
							//saveProcessingDialog.show(); //the explanatory note
							write_latch.await();
							//handler.sendEmptyMessage(0);
							saveProcessingDialog.dismiss();
							
						} catch (InterruptedException e) {
							
							e.printStackTrace();
						}
	 				}
	 			}
	      }).start();
	      
	      return builder.create();
	  }
	
	
	
	
	
	public void showFinalDialog()
	{
		AlertDialog.Builder doneBuilder = new AlertDialog.Builder(getActivity());  
		doneBuilder.setTitle("Important Message").setMessage("Your data has been SAVED!").setPositiveButton("Got it", null).show();
	}
	
	
	public Dialog buildProcessDialog(Context context, String str)
	{
		ProgressDialog pDialog = new ProgressDialog(context);
		pDialog.setTitle(str);
		pDialog.setMessage("Please wait untill this dialog disappears...");
		
		return pDialog;
	}
	
	
	/**
	 * Return system time
	 * Default format: yyyy-mm-dd hh:mm:ss
	 */
	public static String getCurrentTime()
	{ 
		String returnStr = null;
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		returnStr = f.format(date);
		return returnStr;
	}
	
	
	public void transData()
	{
		
			String folderPath = Environment.getExternalStorageDirectory().getPath() + "/ApradData";
		  	File myFolder = new File(folderPath);
		  	myFolder.mkdirs();
		  	//Spectrogram.absolute_start_time = Spectrogram.absolute_start_time.replace(" ", "");
		  	//Spectrogram.absolute_start_time = Spectrogram.absolute_start_time.replace("-", "");
		  	//Spectrogram.absolute_start_time = Spectrogram.absolute_start_time.replace(":", "");
		  	FILE_PATH = folderPath + "/" + Spectrogram.absolute_start_time + ".txt";
		  	
			//try{
					//RandomAccessFile raf = new RandomAccessFile(FILE_PATH, "rw");
					//raf.setLength(fileContent.getBytes().length); // allocate 3M space for the file
					//raf.close();
				
					if(Spectrogram.signal_countForFiles <= THREAD_NUMBER){
						excutor.execute(new FileWriteBufferedThread(0, 0, Spectrogram.signal_countForFiles, small_latch));
						excutor.shutdown();
					}
					else{
						
						int N = Spectrogram.signal_countForFiles / THREAD_NUMBER;
						for(int i=0; i<THREAD_NUMBER; i++){
							
							if( i!=(THREAD_NUMBER-1) )
								excutor.execute(new FileWriteBufferedThread(i, N * i, N* (i+1), latch));
							else
								excutor.execute(new FileWriteBufferedThread(i, N * i, Spectrogram.signal_countForFiles, latch));
						}
						excutor.shutdown();
						/*
						excutor.execute(new FileWriteBufferedThread(0, 0, N, latch));
						excutor.execute(new FileWriteBufferedThread(1, N, N*2, latch));
						...
						excutor.execute(new FileWriteBufferedThread(18, N*18, N*19, latch));
						excutor.execute(new FileWriteBufferedThread(19, N*19, Spectrogram.signal_countForFiles, latch));
						*/
					}
		        
				//} catch(Exception e){ 
				//}
		SAVE_PRESSED = false;
	}
	
	
	static class FileWriteBufferedThread extends Thread{
		
		private int number;
        private int start;
        private int end;
        private CountDownLatch latch;
          
        public FileWriteBufferedThread(int number, int start, int end, CountDownLatch latch){  
        	
        	this.number = number;
            this.start = start;
            this.end = end;
            this.latch = latch;
            NewSaveDialog.data[number] = new String("");
        } 
        
		@Override 
		public void run(){
			
			if(number == 0){
				
				NewSaveDialog.data[number] += "Start Recording Time: " + Spectrogram.absolute_start_time + "\r\n";
				NewSaveDialog.data[number] += "End Recording Time: " + Spectrogram.absolute_end_time + "\r\n";
				NewSaveDialog.data[number] += "Time Span: " + String.format("%.2f", Spectrogram.absolute_time_span) + " s'\r\n\r\n";
				NewSaveDialog.data[number] += "File Saving Time: " + getCurrentTime() + "\r\n\r\n";
				NewSaveDialog.data[number] += "Sample Frequency: " + Spectrogram.absolute_frequency + " Hz\r\n";
				NewSaveDialog.data[number] += "Sample Number (Block Number * 512): " + Spectrogram.signal_countForFiles*512 + "\r\n\r\n";	
			}
			
			for(int i=start; i<end; i++){
				
				//NewSaveDialog.data[number] += "[" + i + "]";
				
				if(i == 0)
					NewSaveDialog.data[number] += "!"; //It is the divider between the header and the real data
						
	  			for(int j=0; j<512; j++){
	  			
	  				NewSaveDialog.data[number] += String.valueOf(Spectrogram.rawSignalsForFiles[i][j]);
	  				
	  				if( ( i!=(Spectrogram.signal_countForFiles-1) ) || (j!=511) ){
	  						NewSaveDialog.data[number] += ",";
	  				}
	  			}
	  			
	  			//NewSaveDialog.data[number] += "[" + i + "]\r\n";
			}
			
			NewSaveDialog.dataLength[number] = NewSaveDialog.data[number].length();
			
			this.latch.countDown();
		}
	}


	public void saveData()
	{
		if(Spectrogram.signal_countForFiles <= THREAD_NUMBER){
			write_excutor.execute(new FileWriteThread(0, 0, write_small_latch));
			write_excutor.shutdown();
		}
		else{
			for(int i=0; i<THREAD_NUMBER; i++){
				
				write_excutor.execute(new FileWriteThread(addressPlus(i), i, write_latch));
			}
			write_excutor.shutdown();
			/*
			write_excutor.execute(new FileWriteThread(0, 0, write_latch));
			write_excutor.execute(new FileWriteThread(addressPlus(1), 1, write_latch));
			write_excutor.execute(new FileWriteThread(addressPlus(2), 2, write_latch));
			...
			write_excutor.execute(new FileWriteThread(addressPlus(19), 19, write_latch));
			*/
			
		}
		
	}
	
	
	static class FileWriteThread extends Thread{  
		
        private int skip;  
        private int number;
        private CountDownLatch latch;
          
        public FileWriteThread(int skip, int number, CountDownLatch latch){  
            this.skip = skip;  
            this.number = number;
            this.latch = latch;
        }  
          
        @Override 
        public void run(){  
            RandomAccessFile raf = null;  
            try {  
                	raf = new RandomAccessFile(FILE_PATH, "rw");
                	raf.seek(skip);
		  			raf.write(data[number].getBytes());
                	this.latch.countDown();
            } catch (FileNotFoundException e) {  
                e.printStackTrace();  
            } catch (IOException e) {
                e.printStackTrace();  
            } finally {  
                try {  
                    raf.close();
                } catch (Exception e) {  
                }  
            }  
        }  
    } 
	
	
	public int addressPlus(int number)
	{
		int sum = 0;
		for(int i=0; i<number; i++){
			sum += dataLength[i];
		}
		return sum;
	}
	
}
