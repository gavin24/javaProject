/*
 * 
 */
import java.io.*;
public class DVD implements Serializable
{
    private String title;
    private String category;
    private boolean newRelease;
    private boolean availableForRent;
    
    //empty constructor
    public DVD()
    {
        
    }
    
    //constructor that takes 4 arguments to initialize the instance variables
    public DVD(String title, int category, boolean newRelease, boolean avail)
    {
        setTitle(title);
        setCategory(category);
        setRelease(newRelease);
        setAvailable(avail);
    }
    
   /* public DVD(String title,String category, boolean newRelease, boolean avail)
    {
        setTitle(title);
       // getCategory();
        setRelease(newRelease);
        setAvailable(avail);
    } */
    // set methods
    public void setTitle(String sTitle)
    {
        title = sTitle;
    }
    
    public void setCategory(int sCategory)
    {
        switch(sCategory)
        {
            case 1:
                category = "Horror";
                break;
            case 2:
                category = "Sci-fi";
                break;
            case 3:
                category = "Drama";
                break;
            case 4:
                category = "Romance";
                break;
            case 5:
                category = "Comedy";
                break;
            case 6:
                category = "Action";
                break;
            case 7:
                category = "Cartoon";
                break;
        }
    }
    
    public void setRelease(boolean sRelease)
    {
        newRelease = sRelease;
    }
    
    public void setAvailable(boolean sAvailable)
    {
        availableForRent = sAvailable;
    }
    
    //get methods
    public String getTitle()
    {
        return title;
    }
    
    public String getCategory()
    {
        return category;
    }
    
    //checks if the movie is a new release
    public boolean isNewRelease()
    {
        return newRelease;
    }
    
    //checks if the movie is available
    public boolean isAvailable()
    {
        return availableForRent;
    }
    
    //overrides the object method
    @Override 
    public String toString()
    {
        return String.format("title: %s ",title );
    }
}
