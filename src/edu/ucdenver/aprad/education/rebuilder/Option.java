package edu.ucdenver.aprad.education.rebuilder;

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

public class Option implements Comparable<Option>
{
    private String name;
    private String data;
    private String path;

    public Option(String n, String d, String p)
    {
        name = n;
        data = d;
        path = p;
    }

    public String getName()
    {
        return name;
    }
    public String getData()
    {
        return data;
    }
    public String getPath()
    {
        return path;
    }

    @Override
    public int compareTo(Option option)
    {
        if (this.name != null)
            return this.name.toLowerCase().compareTo(option.getName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
}
