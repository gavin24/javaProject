/*
 * 
 */
import java.io.*;
public class Customer implements Serializable
{
    private String firstName;
    private String surname;
    private String phoneNum;
    private double credit;
    
    public Customer()
    {
        
    }
    
    public Customer(String fName, String lName, String phone, double credAmt)
    {
        setName(fName);
        setSurname(lName);
        setPhoneNum(phone);
        setCredit(credAmt);
    }
    
    public void setName(String sFName)
    {
        firstName = sFName;
    }
    
    public void setSurname(String sSName)
    {
        surname = sSName;
    }
    
    public void setPhoneNum(String sPhone)
    {
        phoneNum = sPhone;
    }
    
    public void setCredit(double sCredAmt)
    {
        credit = sCredAmt;
    }
    
    public String getName()
    {
        return firstName;
    }
    
    public String getSurname()
    {
        return surname;
    }
    
    public String getPhoneNum()
    {
        return phoneNum;
    }
    
    public double getCredit()
    {
        return credit;
    }
    
    //this method must be edited to display strings properly(in order)
    @Override
    public String toString()
    {
        return String.format("%-15s%-10s%-10s     %.2f", getName(), getSurname(),
                getPhoneNum(), getCredit());
    }      
}
