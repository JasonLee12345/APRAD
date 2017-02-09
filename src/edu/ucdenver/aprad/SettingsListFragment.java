package edu.ucdenver.aprad;

import edu.ucdenver.aprad.spectrogram.SpectrogramView;
import edu.ucdenver.aprad.tools.FFT;
import android.app.Activity;
import android.app.ListFragment;
import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/*****************************
 **
 ** @author Kun Li
 ** @created 19 Jan 2015
 ** 
 ** @modified_by 
 ** @modified_date 
 **
 *****************************/

public class SettingsListFragment extends ListFragment  {
	
	OnArticleSelectedListener mListener;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate( savedInstanceState );
		
	}
	
	
	@Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment 
        return inflater.inflate(R.layout.my_side_menu, container, false);
    } 
	
	
	@Override
	public void onPause()
	{
		super.onPause();
		
	}
	
	
	public interface OnArticleSelectedListener {
        public void onArticleSelected(Uri articleUri);
    }
	
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnArticleSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }
	
	/*
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Append the clicked item's row ID with the content provider Uri 
       Uri noteUri = ContentUris.withAppendedId(ArticleColumns.CONTENT_URI, id);
        // Send the event and Uri to the host activity
       mListener.onArticleSelected(noteUri);
    }
*/
	
	
}
