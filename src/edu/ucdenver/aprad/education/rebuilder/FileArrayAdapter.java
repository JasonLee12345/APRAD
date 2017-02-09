package edu.ucdenver.aprad.education.rebuilder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.ucdenver.aprad.R;

/*****************************
 ** @author Nathan Vanderau
 ** @created 21 Nov 2014
 ** @modified_by
 ** @modified_date
 **
 ** From an online tutorial that can be found at:
 ** http://www.dreamincode.net/forums/topic/190013-creating-simple-file-chooser/
 **
 *****************************/


// Custom Array Adapter to handle files
public class FileArrayAdapter extends ArrayAdapter<Option>
{
    private Context c;
    private int id;
    private List<Option> items;
    
    //Constructor
    public FileArrayAdapter(Context context, int textViewResourceId, List<Option> objects)
    {
        super(context, textViewResourceId, objects);
        c       = context;
        id      = textViewResourceId;
        items   = objects;
    }
    
    
    //Allows us to get an item from a location in the list 
    public Option getItem(int i)
    {
        return items.get(i);
    }
    
    
    @Override
    public View getView( int position, View convertView, ViewGroup parent )
    {
        View v = convertView;
        if ( v == null )
        {
            LayoutInflater viewInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = viewInflater.inflate(id, null);
        }
        final Option option = items.get(position);
        if( option != null )
        {
            TextView t1 = (TextView) v.findViewById(R.id.TextView01);
            TextView t2 = (TextView) v.findViewById(R.id.TextView02);
            if( t1 != null )
                t1.setText(option.getName());
            if( t2 != null )
                t2.setText(option.getData());
        }
        return v;
    }
}
