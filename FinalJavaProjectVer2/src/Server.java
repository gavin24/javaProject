
import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.util.StringTokenizer;

public class Server {
    private Connection con;
    private Statement s;
    private ServerSocket listener;
    private Socket client;
    DVD receivedDVD = new DVD();
    Customer receivedCustomer = new Customer();
    final int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);
    int priceOfDVD;
    private ArrayList<DVD> DVDTable = new ArrayList();
    private ArrayList<Customer> CTable = new ArrayList();
    ArrayList<DVD> DVDList = new ArrayList();
    DVD temp;
    ArrayList<Customer> CustList = new ArrayList();
    ArrayList<String> mcat = new ArrayList();
    Customer tempCust;
    private String avaiString;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    
    public Server(){
        try {
            listener = new ServerSocket(12345, 10);
        } catch (IOException ex) {
            System.out.println("IOException: Setting Up Server Failed.");
        }
    }
    
    public void openConn(){
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");

            String filename = "Database2.mdb";
            String database = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
            database += filename.trim() + ";DriverID=22;READONLY=true}";
            // now we can get the connection from the DriverManager
            con = DriverManager.getConnection( database ,"","");
            s = con.createStatement();
            //----------------------------------------------------------------------------- 
            s.execute("create table DVD ( Title varchar,Category varchar,NewRelease bit,Availability bit)"); 
            s.execute("create table Customer ( CustomerName varchar,CustomerSurname varchar,PhoneNum varchar,Credit float)"); 

            this.LoadCustomers();
            this.LoadDvds();
            

           
        }
         catch (Exception err){
        System.out.println("ERROR: " + err);
        } 
    } 
    public void LoadDvds(){
       try{
               InputStream file = new FileInputStream("Movies.ser");
               InputStream buffer = new BufferedInputStream(file);
               ObjectInput input = new ObjectInputStream(buffer);
               PreparedStatement pstmt = null;
               

 
         
              while(input != null){
                  temp = (DVD)input.readObject();
                   String SQL = "INSERT INTO DVD" +  
                          "(Title,Category,NewRelease,Availability) VALUES" +
                          "(?,?,?,?)";
                     pstmt = con.prepareStatement(SQL);
                     pstmt.setString(1, temp.getTitle());
                     pstmt.setString(2, temp.getCategory());
                     pstmt.setBoolean(3, temp.isNewRelease());
                     pstmt.setBoolean(4, temp.isAvailable());
                     pstmt.executeUpdate();
               
      
               }
            }
               
            catch(IOException e)
           {
              System.out.println("Movies File not Found");
           }
           catch(Exception e)
           {
               System.out.println("Load Unsuccessful");
           } 
    }
    public void LoadCustomers() throws SQLException{
         try{

               InputStream file = new FileInputStream("Customers.ser");
               InputStream buffer = new BufferedInputStream(file);
               ObjectInput input = new ObjectInputStream(buffer);
              // tempCust = (Customer)input.readObject();

               
               while((tempCust = (Customer) input.readObject()) != null){
                 s.execute("INSERT INTO Customer (CustomerName,CustomerSurname,PhoneNum,Credit) VALUES ('"+tempCust.getName()+"','"+tempCust.getSurname()+"','"+tempCust.getPhoneNum()+"',"+tempCust.getCredit()+")");        
                // tempCust = (Customer)input.readObject();

               }  

           }
           catch(IOException e){
              System.out.println("Customer File not Found");
            //  s.execute("drop table DVD");
           //   s.execute("drop table CUSTOMER");
           }
           catch(Exception e){
               System.out.println("Cust Load Unsuccessful");
            //  s.execute("drop table DVD");
           //    s.execute("drop table CUSTOMER");
           } 
        }
        
    
    
    public void Listen() throws SQLException{
        try {
            client = listener.accept();
            processClient();
        } catch (IOException ex) {
            System.out.println("IOException: Connection to Client Failed.");
        }
    }
    
    public void processClient() throws SQLException {
        try {
            //COMMUNICATE WITH CLIENT
            
            //Step 1: Initiate Channels
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
          
            
            //Step 2: Communicate With Client
            String option = (String) in.readObject();
            System.out.println("Read string");
            
            while (!option.equals("exit")){
                
                if (option.equalsIgnoreCase("rent")){
                    receivedDVD = (DVD) in.readObject();
                    receivedCustomer = (Customer) in.readObject();
                    Double price = 10.0;
                        //DATABASE CONNECTION TO SELECT MOVIE WITH CORRECT TITLE
                    s.execute("SELECT * FROM Customer WHERE PhoneNum = '"+ receivedCustomer.getPhoneNum() +"'");
                            //------------------------------------------------------
                    Customer dbCust = new Customer();
                   
                    ResultSet rs = s.getResultSet();
                   
                    while(rs.next()){
                       dbCust.setName(rs.getString("CustomerName"));
                       dbCust.setSurname(rs.getString("CustomerSurname"));
                       dbCust.setPhoneNum(rs.getString("PhoneNum"));
                       dbCust.setCredit(rs.getFloat("Credit"));
                        
                       s.execute("SELECT * FROM DVD WHERE Title = '"+ receivedDVD.getTitle() +"'"); 
                       
                       DVD dbMovie = new DVD();
                      // rs = s.getResultSet();
                       ResultSet r = s.getResultSet();
                        while(r.next()){
                            dbMovie.setTitle(r.getString("Title"));
                            r.getString("Category");
                            dbMovie.setRelease(r.getBoolean("NewRelease")); 
                            dbMovie.setAvailable(r.getBoolean("Availability"));
                            
                            if (dbMovie.isNewRelease())
                                price = 15.0;
                            boolean f = false;
                            String t = "No";
                            int tr = -1;
                            int fl = 0;
                            if ((dbCust.getCredit() > price) && (dbMovie.isAvailable())){
                                double newCredit;
                                dbMovie.setAvailable(false);
                                newCredit = dbCust.getCredit() - price;
                                boolean e = dbMovie.isAvailable();
                                s.execute("UPDATE Customer SET Credit = '" + newCredit + "' WHERE PhoneNum = '"+ dbCust.getPhoneNum()+"'");
                              // s.execute("UPDATE DVD SET Availability = "+dbMovie.isAvailable()+" WHERE Title = '"+ dbMovie.getTitle() + "'");
                                 s.execute("UPDATE DVD SET Availability = "+fl+" WHERE Title = '"+ dbMovie.getTitle() + "'");     
                                       /* String sql = "update DVD set Availability=?  where Title=?";
                               PreparedStatement preparedStatement =con.prepareStatement(sql);
                                preparedStatement.setBoolean(4, false);
                                preparedStatement.setString(1, dbMovie.getTitle());
                               preparedStatement.executeUpdate();
                               */
                               
                                out.writeObject("successful");
                                break;
                            }
                            else{
                                out.writeObject("usuccessful");
                                break;
                            } 
                        }
                    } 
                    out.flush();
                }
                
                if (option.equalsIgnoreCase("return")){
                    receivedDVD = (DVD) in.readObject();
                        
                        //DATABASE CONNECTION TO SELECT MOVIE WITH CORRECT TITLE
                   s.execute("SELECT * FROM DVD WHERE Title = '"+receivedDVD.getTitle()+"'");
                            //--------------
                    DVD movie = receivedDVD; //MOVIE FROM DATABASE
   
                    DVD dbMovie = new DVD();
                    ResultSet rs = s.getResultSet();
                    String title;
                    //System.out.println("dfds");
                   // System.out.println(rs.getString("Title").toString());
                    while(rs.next()){
                       // dbMovie.setTitle(rs.getString("title"));
                        dbMovie.setTitle(rs.getString("Title"));
                        rs.getString("Category");
                        rs.getBoolean("NewRelease"); 
                        dbMovie.setAvailable(rs.getBoolean("Availability"));
                        
                        if (dbMovie.isAvailable()){
                            out.writeObject("unsuccessful");
                            break;
                        }
                        else{
                           // s.execute("UPDATE DVD SET Availability = true WHERE Title = '"+movie.getTitle()+"'");
                            s.execute("UPDATE DVD SET Availability = '"+-1+"' WHERE Title = '"+movie.getTitle()+"'");
                            out.writeObject("successful");
                            break;
                        }    
                    } 

                    out.flush();               

                }
                
                if (option.equalsIgnoreCase("delete")){
                    receivedDVD = (DVD) in.readObject();
                   
                     //DATABASE CONNECTION TO SELECT MOVIE WITH CORRECT TITLE
                      
                    DVD movie = receivedDVD;
    
                    try{
                        s.execute("DELETE * FROM DVD WHERE Title = '"+movie.getTitle()+"'");
                        //s.execute("DELETE * FROM DVD WHERE Title = '"+receivedDVD.getTitle()+"'");
                        out.writeObject("successful");
                    }
                    catch(Exception e){ 
                        out.writeObject("unsuccessful"); 
                    }
                    out.flush();
                }
                ///adds movie
                if(option.equalsIgnoreCase("addmovie")){
                    receivedDVD = (DVD) in.readObject();
                    System.out.println("Read object");
                    DVD movie = new DVD();
                    movie = receivedDVD;
                    System.out.println("DVD received by server");
                    
                    try{
                        s.execute("INSERT INTO DVD (Title,Category) VALUES ('"+movie.getTitle()+"','"+movie.getCategory()+"')");//,'"+temp.isAvailable()+"'//)");
                        if (movie.isNewRelease()){
                            s.execute("UPDATE DVD SET NewRelease = true WHERE Title = '"+movie.getTitle()+"'");
                        }
                        else{
                            s.execute("UPDATE DVD SET NewRelease = false WHERE Title = '"+movie.getTitle()+"'");
                        }
                        s.execute("UPDATE DVD SET Availability = true WHERE Title = '"+movie.getTitle()+"'");
                        out.writeObject("successful");
                    }
                    catch (Exception e){
                        out.writeObject("unsuccessful");
                        System.out.println(e);
                    }
                    
                    System.out.println("DVD added to database");
                    out.flush();
                } 
                
                if(option.equalsIgnoreCase("addcustomer")){
                        receivedCustomer = (Customer) in.readObject();
                        Customer cust = receivedCustomer;
                        try{
                        s.execute("INSERT INTO Customer (CustomerName,CustomerSurname,PhoneNum,Credit) VALUES ('"+cust.getName()+"','"+cust.getSurname()+"','"+cust.getPhoneNum()+"',"+cust.getCredit()+")");
                        out.writeObject("successful");
                        }
                        catch(Exception e){
                        out.writeObject("unsuccessful");
                        }
                        
                        out.flush();
                 }
                
                 if(option.equalsIgnoreCase("displayallmovies"))
                 {
                          s.execute("SELECT * FROM DVD");
                    ResultSet rs = s.getResultSet();


                    String title,category;
                    boolean newRelease,avail;

                    while(rs.next()){
                        title = rs.getString(1);
                        category = rs.getString(2);
                        newRelease = rs.getBoolean(3);
                        avail = rs.getBoolean(4);
                        int cat = 0;
                        
                         
                          mcat.add(category);
                        temp = new DVD(title,cat,newRelease,avail);
                        DVDList.add(temp);
                        
                    }
                    out.writeObject(DVDList);
                    out.flush();
                    DVDList.clear();
                    out.writeObject(mcat);
                    out.flush();
                    mcat.clear();
                 }
                  if(option.equalsIgnoreCase("displaycustomer")){
                        s.execute("SELECT * FROM CUSTOMER");
                        
                        ResultSet rs = s.getResultSet();


                        String FName,LName,PhoneNumber;
                        float CreditAmount;

                        while(rs.next()){
                            FName = rs.getString("CustomerName");
                            LName = rs.getString("CustomerSurname");
                            PhoneNumber = rs.getString("PhoneNum");
                            CreditAmount = rs.getFloat("Credit");

                            tempCust = new Customer(FName,LName,PhoneNumber,CreditAmount);
                             
                            CustList.add(tempCust);
                        }
                        out.writeObject(CustList);
                        out.flush();
                        
                      }
                  
                   if(option.equalsIgnoreCase("displayavailablemovies")){
                              s.execute("SELECT * FROM DVD");
                            //  s.execute("DELETE * FROM DVD WHERE Title = '"+movie.getTitle()+"'");
                    ResultSet rs = s.getResultSet();


                    String title,category;
                    boolean newRelease,avail;

                    while(rs.next()){
                        title = rs.getString(1);
                        category = rs.getString(2);
                        newRelease = rs.getBoolean(3);
                        avail = rs.getBoolean(4);
                         int cat = 0;
                        temp = new DVD(title,cat,newRelease,avail);
                        if(newRelease == true)
                        {
                             
                            DVDList.add(temp);
                            mcat.add(category);
                          
                        }
                        }
                    
                      out.writeObject(DVDList);
                      out.flush();
                      DVDList.clear();
                      out.writeObject(mcat);
                      out.flush();
                   
                    mcat.clear();
                    }
                   
                if (option.equalsIgnoreCase("exit")){  
                    s.execute("drop table DVD");
                    s.execute("drop table CUSTOMER");
                    in.close();
                    out.close();
                    client.close();
                    System.out.println("Server Closed");
                }
                if(option.equalsIgnoreCase("deletecustomer")){
                    receivedCustomer = (Customer) in.readObject();
                        Customer cust = new Customer();
                        s.execute("DELETE Customer WHERE PhoneNum = '"+cust.getPhoneNum()+"'");
                }
                
                 if(option.equalsIgnoreCase("displaycustomers")){
                        s.execute("SELECT * FROM CUSTOMER");
                        ResultSet rs = s.getResultSet();


                        String CustomerName = " ";
                        String  CustomerSurname = " ";
                        String PhoneNum = " ";
                        float Credit = 0;

                        while(rs.next()){
                        
                        CustomerName = rs.getString("CustomerName");
                        CustomerSurname = rs.getString("CustomerSurname");
                        PhoneNum = rs.getString("PhoneNum");
                        Credit = rs.getFloat("Credit");

                        tempCust = new Customer(CustomerName,CustomerSurname,PhoneNum,Credit);
                            
                        CustList.add(tempCust);
                             
                      /*  out.writeObject(CustomerName);
                        out.flush();
                        out.writeObject(CustomerSurname);
                        out.flush();
                        out.writeObject(PhoneNum);
                        out.flush();
                        out.writeObject(Credit);
                        out.flush(); 
                        
                            */
                        }
                        out.writeObject(CustList);
                      //  out.writeObject(CustList);
			out.flush();
                        CustList.clear();
                      }
              
                out = new ObjectOutputStream(client.getOutputStream());
                out.flush();
                in = new ObjectInputStream(client.getInputStream());
                
                
                option = (String) in.readObject();
                
            }
            
       
        } catch (IOException ex) {
            System.out.println("IOException: Stream failed.");
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException: Failed to read Object");
        }
    }
    
    public static void main(String[] args) throws SQLException{
        
        Server s = new Server();
        s.openConn();
        s.Listen();
    }
 
}
 

