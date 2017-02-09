package edu.ucdenver.aprad.education.rebuilder;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import edu.ucdenver.aprad.Aprad;
import edu.ucdenver.aprad.R;
import edu.ucdenver.aprad.education.rebuilder.fd.RebuilderFD;
import edu.ucdenver.aprad.education.rebuilder.s.RebuilderS;
import edu.ucdenver.aprad.education.rebuilder.td.RebuilderTD;
import edu.ucdenver.aprad.info.About;
import edu.ucdenver.aprad.opengl.OpenGLforFD;
import edu.ucdenver.aprad.opengl.OpenGLforS;
import edu.ucdenver.aprad.opengl.OpenGLforTD;

/*****************************
 **
 ** @author Kun Li
 ** @author Nathan Vanderau
 ** @created 21 Nov 2014
 ** 
 ** @modified_by Kun Li
 ** @modified_date 19 Mar 2015
 **
 ** @modified_by Kun Li
 ** @modified_date 9 Apr 2015
 **
 *****************************/

public class FileChooser extends ListActivity
{
    private File currentDir;
    private FileArrayAdapter adapter;
    private int selfCheck = 0;

	public CountDownLatch latch = new CountDownLatch(1);
	public String temp = null;
	Dialog readProcessingDialog = null;
	Dialog anotherReadProcessingDialog = null;
	public boolean bigGuy = false;
	public float howMuchBig = 0;
	
	View myView = null;
	View inflateView = null;
	
	boolean isOpenGLMode = false;
	private Switch modeSwitch;
	
	Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg) {
    		// TODO Auto-generated method stub
    		switch(msg.what){
    		case 0:
    			readProcessingDialog.dismiss();
    			break;
    		case 1:
    			createMyDialog(temp);
    			break;
    		case 2:
    			anotherReadProcessingDialog.dismiss();
    		}
    	}
    };
    
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        
        modeSwitch = (Switch) findViewById(R.id.mode_switch);
        
        if(isOpenGLMode)
        	modeSwitch.setChecked(true);
        else
        	modeSwitch.setChecked(false);
        
        modeSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
            @Override  
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {  
            	
                if (isChecked) {  
                	isOpenGLMode = true;
                	Toast.makeText(FileChooser.this, "<USE> functions are changed\n OpenGL Mode Engaged", 1500).show();
                } else {  
                	isOpenGLMode = false;
                	Toast.makeText(FileChooser.this, "<USE> functions are changed\n OpenGL Mode Disabled", 1500).show();
                }  
            }  
        });
        
        String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)){
			 
			String folderPath = Environment.getExternalStorageDirectory().getPath() + "/ApradData";
			currentDir = new File(folderPath);
			if(!currentDir.isDirectory())
				currentDir.mkdirs();
	        fill(currentDir);
			 	
   	  	}else{
      	  		
      	  	AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
      	  	builder1.setTitle("Reminder").setMessage("SD card not found!")
   	  				.setPositiveButton("OK", new DialogInterface.OnClickListener(){
   	  					
   												public void onClick(DialogInterface dialog, int id){  
   											
   												}
   											 }).show();
      	}
    }
    
    
    //Fils in File info
    private void fill(File f)
    {
        File[]dirs = f.listFiles();
        //this.setTitle(f.getName());
        this.setTitle("APRAD DATA");
        List<Option>dir = new ArrayList<Option>();
        List<Option>fls = new ArrayList<Option>();
        try
        {
            for(File ff: dirs) //an easy way of traversing an array
            {
                if(ff.isDirectory())
                    dir.add(new Option(ff.getName(),"Folder",ff.getAbsolutePath()));
                else
                {
                    fls.add(new Option(ff.getName(),
                    		"File Size: "+ String.format("%.2f", (float)ff.length() / 1024) + " KB",
                    		ff.getAbsolutePath()));
                }
            }
        }
        catch(Exception e)
        {
        	
        }
        
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        
        //if(!f.getName().equalsIgnoreCase("sdcard"))
        
        /**
         * Turn on the code below if you want the file explorer function
         */
        /*
        if(dirs.length != 0)
            dir.add(0,new Option("..","Parent Directory",f.getParent()));
        */
        
        adapter = new FileArrayAdapter(FileChooser.this, R.layout.open_file, dir);
        this.setListAdapter(adapter);
    }

    
    //Handles when users click on files and folders.

    //Implementing a ".. Parent Directory" option in the file navigation
    //as when the user clicks on the default Back button, it takes them
    //back to the education.xml view.

    //Stack<File> dirStack = new Stack<File>();

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        Option option = adapter.getItem(position);
        if( option.getData().equalsIgnoreCase("folder") || option.getData().equalsIgnoreCase("Parent Directory") )
        {
            currentDir = new File(option.getPath());
            fill(currentDir);
            
        }
        else 
        	if(option.getData().equalsIgnoreCase("Parent Directory"))
        	{
        		//currentDir = dirStack.pop();
        		//fill(currentDir);
        	}
        	else
        	{
        		try {
					onFileClick(option, l, v);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
    }
    
    
    private void onFileClick(final Option option, ListView l, View v) throws IOException {
    	if(myView != null)
    		((ViewGroup) myView).removeView(inflateView);
    	
		final ReadSavedFileData gotFile = new ReadSavedFileData(option);
		String fileData = option.getData();
		fileData = fileData.substring(fileData.lastIndexOf(":")+1, fileData.lastIndexOf("K"));
		final String fileSize = fileData;
		//final String fileSize[];
		//String regex = "(?<=one)(?=123)";
		//fileSize = option.getData().split(regex);
		
    	Context mContext = getApplicationContext();
    	LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
    	//View layout = inflater.inflate(R.layout.choice_dialog, (ViewGroup) v);
    	View layout = inflater.inflate(R.layout.choice_dialog, null);
    	((ViewGroup) v).addView(layout);
    	myView = v;
    	inflateView = layout;
    	
    	final TextView text = (TextView)layout.findViewById(R.id.textView3);
    	text.setText(gotFile.headerAndData[0]);
    	
    	
    	//PREVIEW button
        Button button1 = (Button)layout.findViewById(R.id.choiceButton1);
        button1.setOnClickListener( new View.OnClickListener() {
        	
        	@Override
            public void onClick( View v ) {
        		final float i = Float.parseFloat(fileSize);
        		if(i >= 350) {
        			temp = gotFile.headerAndData[0] + gotFile.headerAndData[1];
        			final Dialog dialog = new Dialog(FileChooser.this, R.style.dialog_style);  
        			dialog.setContentView(R.layout.filesize_notice);
        			dialog.show();
        			
        			Button someButton = (Button) dialog.findViewById(R.id.id_some_preview);
        			someButton.setOnClickListener( new View.OnClickListener() {
                    	@Override
                        public void onClick( View v )
                        {
                    		Dialog dialog1 = new Dialog(FileChooser.this, R.style.dialog_style);  
                    		dialog1.setContentView(R.layout.previewer);
                    		dialog1.show();
                    		TextView text = (TextView) dialog1.findViewById(R.id.textView3);
                    		text.setText(gotFile.headerAndData[0] + gotFile.headerAndData[1].substring(0, 100000) + "...");
                        }
        	        });
        			
        			Button stillButton = (Button) dialog.findViewById(R.id.id_still_preview);
        			stillButton.setOnClickListener( new View.OnClickListener() {
                    	@Override
                        public void onClick( View v )
                        {	
                    		if(i > 840)
                    		{
                    			/**
                    			 * record string size for preparing to dismiss the second processing dialog
                    			 */
                    			bigGuy = true;
                    			howMuchBig = i;
                    		}
                    		
                    		readProcessingDialog = buildProcessDialog(FileChooser.this, "Please be patient and wait...");
                			readProcessingDialog.show();
                			
                			Timer timer = new Timer();
         			        TimerTask task = new TimerTask() {
         			            @Override
         			            public void run() {
         			            	mHandler.sendEmptyMessage(1);
         			            }
         			        };
         			        timer.schedule(task, 1);
                        }
        	        });
        			
        			Button cancelButton = (Button) dialog.findViewById(R.id.id_cancel_preview);
        			cancelButton.setOnClickListener( new View.OnClickListener() {
                    	@Override
                        public void onClick( View v )
                        {
                    		dialog.dismiss();
                        }
        	        });
        		}
        		else
        		{
        			Dialog dialog1 = new Dialog(FileChooser.this, R.style.dialog_style);  
        			dialog1.setContentView(R.layout.previewer);
        			dialog1.show();
        			TextView text = (TextView) dialog1.findViewById(R.id.textView3);
        			text.setText(gotFile.headerAndData[0] + gotFile.headerAndData[1]);
        		}
            }
        });
        
        
        //USE button
        Button button2 = (Button)layout.findViewById(R.id.choiceButton2);
        button2.setOnClickListener( new View.OnClickListener() {
        	
        	@Override
            public void onClick( View v )
            {
        		Dialog dialog2 = new Dialog(FileChooser.this, R.style.dialog_style);  
                dialog2.setContentView(R.layout.choose_rebuilder);
                dialog2.show();
                
                Button reBTDButton = (Button) dialog2.findViewById(R.id.id_td_button);
                reBTDButton.setOnClickListener( new View.OnClickListener() {
                	@Override
                    public void onClick( View v )
                    {
                		if(isOpenGLMode) {
                			Intent intent = new Intent(v.getContext(), OpenGLforTD.class);
                	        //Bundle bundle = new Bundle();
                	        //bundle.putSerializable("aa", gotFile);
                	        //intent.putExtras(bundle);
                	        v.getContext().startActivity(intent);
                		}else {
                			Intent intent = new Intent( v.getContext(), RebuilderTD.class);
                            v.getContext().startActivity(intent);
                		}
                    }
    	        });
                
                Button reBFDButton = (Button) dialog2.findViewById(R.id.id_fd_button);
                reBFDButton.setOnClickListener( new View.OnClickListener() {
                	@Override
                    public void onClick( View v )
                    {
                		if(isOpenGLMode) {
                			Intent intent = new Intent( v.getContext(), OpenGLforFD.class);
                            v.getContext().startActivity(intent);
                		}else {
                			Intent intent = new Intent( v.getContext(), RebuilderFD.class);
                            v.getContext().startActivity(intent);
                		}
                    }
    	        });
                
                Button reBSButton = (Button) dialog2.findViewById(R.id.id_s_button);
                reBSButton.setOnClickListener( new View.OnClickListener() {
                	@Override
                    public void onClick( View v )
                    {
                		if(isOpenGLMode) {
                			Intent intent = new Intent( v.getContext(), OpenGLforS.class);
                            v.getContext().startActivity(intent);
                		}else {
                			Intent intent = new Intent( v.getContext(), RebuilderS.class);
                            v.getContext().startActivity(intent);
                		}
                    }
    	        });
            }
        });
        
        
        //DELETE button
        Button button3 = (Button)layout.findViewById(R.id.choiceButton3);
        button3.setOnClickListener( new View.OnClickListener() {
        	
        	@Override
            public void onClick( View v )
            {
        		final Dialog dialog = new Dialog(FileChooser.this, R.style.dialog_style);  
                dialog.setContentView(R.layout.default_dialog);
                dialog.show();
                
                Button sureButton = (Button)dialog.findViewById(R.id.button1);
                sureButton.setOnClickListener( new View.OnClickListener() {
                	@Override
                    public void onClick( View v )
                    {
                		deleteFile(option.getPath());
                		dialog.dismiss();
                		String folderPath = Environment.getExternalStorageDirectory().getPath() + "/ApradData";
            			currentDir = new File(folderPath);
            	        fill(currentDir);
                    }
                });
                
                Button noButton = (Button)dialog.findViewById(R.id.button2);
                noButton.setOnClickListener( new View.OnClickListener() {
                	@Override
                    public void onClick( View v )
                    {
                		dialog.dismiss();
                    }
                });
                    
            }
        });
        
    }
    
    
    public boolean deleteFile(String sPath) {  
    	
	    Boolean flag = false;  
	    File file = new File(sPath);  
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	        flag = true;  
	    }  
	    return flag;  
	}
    
    
    public Dialog buildProcessDialog(Context context, String str) {
    	
		ProgressDialog pDialog = new ProgressDialog(context);
		pDialog.setTitle(str);
		pDialog.setMessage("Data is around the corner...");
		return pDialog;
	}
    
    
    public void createMyDialog(final String temp) {
    	
	 	final Dialog dialog1 = new Dialog(FileChooser.this, R.style.dialog_style);  
	 	dialog1.setContentView(R.layout.previewer);
	 	dialog1.show();
	 	TextView text = (TextView) dialog1.findViewById(R.id.textView3);
	 	text.setText(temp);
	 	
	 	Timer timer = new Timer();
	    TimerTask task = new TimerTask() {
	    	@Override
	        public void run() {
	    		mHandler.sendEmptyMessage(0);
	        }
	    };
	    timer.schedule(task, 10000);
	    
	    if(bigGuy)
	    {
	    	anotherReadProcessingDialog = buildProcessDialog(FileChooser.this, "Your data is coming...");
	    	anotherReadProcessingDialog.show();
	    	Timer timer1 = new Timer();
		    TimerTask task1 = new TimerTask() {
		    	@Override
		        public void run() {
		    		mHandler.sendEmptyMessage(2);
		        }
		    };
		    timer1.schedule(task1, (long)howMuchBig * 37);
	    }
		
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.opengl_menu, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected( MenuItem item ){
      // Handle item selection
      switch ( item.getItemId() )
      {
      	case R.id.isOpenGL:
      		isOpenGLMode = true;
      		Toast.makeText(this, "<USE> functions are changed\n OpenGL Mode Engaged", 1000).show();
      		return true;
      	case R.id.notOpenGL:
      		isOpenGLMode = false;
      		Toast.makeText(this, "<USE> functions are changed\n OpenGL Mode Disabled", 1000).show();
      		return true;
      	default:
      		return super.onOptionsItemSelected(item);
      }
    }
}