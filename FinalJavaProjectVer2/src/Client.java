
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import static javax.swing.JFrame.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.table.DefaultTableModel;


public class Client {
    
    private Socket server;
    ObjectOutputStream out;
    ObjectInputStream in;
    
    private String optionToServer;
    private Customer cust = new Customer();
    private DVD movie = new DVD();
    private int btnChoice;
    
    private JFrame frame = new JFrame("VIDEO STORE");
    private GridBagConstraints gbc = new GridBagConstraints();
    
    final int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);
    String currentYear = Integer.toString(CURRENT_YEAR);
    
    private final String[] columnNamesMovies = {"Title", "Category", "New Release", "Availibitly"};
    private final String[] columnNamesCustomers = {"Name", "Surname", "Phone Number", "Credit"};
    
    ArrayList<Customer> custList = new ArrayList();
    ArrayList<DVD> movieList = new ArrayList();
    ArrayList<String> dv = new ArrayList();
    
    public Client(){
        try {
            server = new Socket("127.0.0.1", 12345);
        } catch (IOException ex) {
           System.out.println("IOException: Could Establish Connection To Server");
        }
        
       /* try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            System.out.println("Exception: UI Manager");
        } catch (InstantiationException ex) {
            System.out.println("Exception: UI Manager");
        } catch (IllegalAccessException ex) {
            System.out.println("Exception: UI Manager");
        } catch (UnsupportedLookAndFeelException ex) {
            System.out.println("Exception: UI Manager");
        } */
        try{
            for (LookAndFeelInfo info: UIManager.getInstalledLookAndFeels()){
                if("Nimbus".equals(info.getName())){
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                }
            } 
        } catch (Exception e){
                try{
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex){   
            }
        }
        
        frame.setSize(750,700);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);
        
        JLabel title = new JLabel("VIDEO STORE");
        title.setFont(new Font("Calisto MT", Font.BOLD, 42));
        
        JButton btnRent = new JButton("RENT MOVIE");
        btnRent.setPreferredSize(new Dimension(500, 50));
        btnRent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RentMovie();
            }
        });
        
        JButton btnReturn = new JButton("RETURN MOVIE");
        btnReturn.setPreferredSize(new Dimension(500, 50));
        btnReturn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                ReturnMovie();
            }
        });
        
        JButton btnAddMovie = new JButton("ADD MOVIE");
        btnAddMovie.setPreferredSize(new Dimension(500, 50));
        btnAddMovie.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                AddMovie();
            }
        });
        
        JButton btnDeleteMovie = new JButton("DELETE MOVIE");
        btnDeleteMovie.setPreferredSize(new Dimension(500, 50));
        btnDeleteMovie.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent e){
              DeleteMovie();
          }  
        });
        
        JButton btnDisplayAllMovies = new JButton("DISPLAY ALL MOVIES");
        btnDisplayAllMovies.setPreferredSize(new Dimension(500, 50));
        btnDisplayAllMovies.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                DisplayAllMovies();
            }
        });
        
        JButton btnDisplayAvailableMovies = new JButton("DISPLAY AVAILABLE MOVIES");
        btnDisplayAvailableMovies.setPreferredSize(new Dimension(500,50));
        btnDisplayAvailableMovies.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                DisplayAvailableMovies();
            }
        });
        
        JButton btnAddCustomer = new JButton("ADD NEW CUSTOMER");
        btnAddCustomer.setPreferredSize(new Dimension(500, 50));
        btnAddCustomer.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                AddCustomer();
            }
        });
        
        JButton btnDisplayCustomers = new JButton("DISPLAY CUSTOMERS");
        btnDisplayCustomers.setPreferredSize(new Dimension(500, 50));
        btnDisplayCustomers.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                DisplayCustomers();
            }
        });
        
        JButton btnExit = new JButton("EXIT");
        btnExit.setPreferredSize(new Dimension(500, 50));  
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Communicate(9);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(0);
            }
        });
        
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        frame.add(title, gbc);
        gbc.gridwidth = 2;
        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(btnRent, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(btnReturn, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        frame.add(btnAddMovie, gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        frame.add(btnDeleteMovie, gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        frame.add(btnDisplayAllMovies, gbc);
        gbc.gridx = 1;
        gbc.gridy = 6;
        frame.add(btnDisplayAvailableMovies, gbc);
        gbc.gridx = 1;
        gbc.gridy = 7;
        frame.add(btnAddCustomer, gbc);
        gbc.gridx = 1;
        gbc.gridy = 8;
        frame.add(btnDisplayCustomers, gbc);
        gbc.gridx = 1;
        gbc.gridy = 9;
        frame.add(btnExit, gbc);
        
        frame.setVisible(true);
    }
    
    public void RentMovie(){
        
        final JDialog rDialog = new JDialog();
        rDialog.setTitle("RENT VIDEO");
        rDialog.setSize(400, 300);
        rDialog.setLocationRelativeTo(frame);
        rDialog.setLayout(new GridBagLayout());
        
        JLabel title = new JLabel("RENT A VIDEO");
        title.setFont(new Font("Calisto MT", Font.BOLD, 26));
        
        JLabel IDlabel = new JLabel("Customer Phone Number:");
        final JTextField IDfield = new JTextField(10);
        
        JLabel movieLabel = new JLabel("Movie Title:");
        final JTextField movieField = new JTextField(10);
        
        JButton btnConfirm = new JButton("Confirm");
        btnConfirm.setPreferredSize(new Dimension(100, 30));
        btnConfirm.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                movie.setTitle(movieField.getText());
                cust.setPhoneNum(IDfield.getText());
                try {
                    Communicate(1);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                rDialog.dispose();
            }
        });
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(110, 30));
        btnCancel.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                rDialog.dispose();
                
            }
        });
        
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        rDialog.add(title, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        rDialog.add(IDlabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        rDialog.add(IDfield, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        rDialog.add(movieLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        rDialog.add(movieField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        rDialog.add(btnConfirm, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        rDialog.add(btnCancel, gbc);
        rDialog.setVisible(true);
        
    }
    
    public void ReturnMovie(){
        final JDialog rDialog = new JDialog();
        rDialog.setTitle("Return Video");
        rDialog.setSize(400, 300);
        rDialog.setLayout(new GridBagLayout());
        rDialog.setLocationRelativeTo(frame);
        
        JLabel title = new JLabel("RETURN VIDEO");
        title.setFont(new Font("Calisto MT", Font.BOLD, 26));
        
        JLabel movieLabel = new JLabel("Movie Title:");
        final JTextField movieField = new JTextField(10);
        
        JButton btnConfirm = new JButton("Confirm");
        btnConfirm.setPreferredSize(new Dimension(110, 30));
        btnConfirm.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                movie.setTitle(movieField.getText());
                try {
                    movie.setTitle(movieField.getText());
                    Communicate(2);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                rDialog.dispose();
            }
        });
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(110, 30));
        btnCancel.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                rDialog.dispose();
            }
        });
        
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        rDialog.add(title, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        rDialog.add(movieLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        rDialog.add(movieField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        rDialog.add(btnConfirm, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        rDialog.add(btnCancel, gbc);
        
        rDialog.setVisible(true);
    }
    
    public void AddMovie(){
        final JDialog aDialog = new JDialog();
        aDialog.setTitle("Add Movie");
        aDialog.setSize(400,500);
        aDialog.setLayout(new GridBagLayout());
        aDialog.setLocationRelativeTo(frame);
        
        String[] years = {"1999", "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014"};
        String[] categories = {"Horror", "Sci-fi", "Drama", "Romance", "Comedy", "Action", "Cartoon"};
        
        JLabel title = new JLabel("ADD MOVIE");
        title.setFont(new Font("Calisto MT", Font.BOLD, 26));
        
        JLabel movieLabel = new JLabel("Movie Title:");
        final JTextField movieField = new JTextField(10);
        JLabel catLabel = new JLabel("Movie Category:");
        final JComboBox catBox = new JComboBox(categories);
        JLabel yearLabel = new JLabel("Movie Release Year:");
        final JComboBox yearBox = new JComboBox(years);
        
        JButton btnConfirm = new JButton("Confirm");
        btnConfirm.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                movie.setTitle(movieField.getText());
                movie.setCategory(catBox.getSelectedIndex() + 1);
                System.out.println(yearBox.getSelectedItem().toString());
                if (yearBox.getSelectedItem().toString().equalsIgnoreCase(currentYear)){
                    movie.setRelease(true);
                }
                else{
                    movie.setRelease(false);
                }
                movie.setAvailable(true);
                try {
                    System.out.println("Getting ready to communicate");
                    Communicate(3);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                aDialog.dispose();
            }
        });
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                aDialog.dispose();
            }
        });
        
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        aDialog.add(title, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        aDialog.add(movieLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        aDialog.add(movieField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        aDialog.add(catLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        aDialog.add(catBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        aDialog.add(yearLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        aDialog.add(yearBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        aDialog.add(btnConfirm, gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        aDialog.add(btnCancel, gbc);
        
        aDialog.setVisible(true);
    }
    
    public void DeleteMovie(){
        final JDialog rDialog = new JDialog();
        rDialog.setTitle("Delete Video");
        rDialog.setSize(400, 300);
        rDialog.setLayout(new GridBagLayout());
        rDialog.setLocationRelativeTo(frame);
        
        
        JLabel title = new JLabel("DELETE VIDEO");
        title.setFont(new Font("Calisto MT", Font.BOLD, 26));
        
        JLabel movieLabel = new JLabel("Movie Title:");
        final JTextField movieField = new JTextField(10);
        
        JButton btnConfirm = new JButton("Confirm");
        btnConfirm.setPreferredSize(new Dimension(110, 30));
        
        btnConfirm.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                movie.setTitle(movieField.getText());
                try {
                    Communicate(4);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                rDialog.dispose();
            }
        });
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(110, 30));
        btnCancel.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                rDialog.dispose();
            }
        });
        
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        rDialog.add(title, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        rDialog.add(movieLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        rDialog.add(movieField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        rDialog.add(btnConfirm, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        rDialog.add(btnCancel, gbc);
        
        rDialog.setVisible(true);
    }
    
    public void DisplayAllMovies(){
      
        try {
            Communicate(5);
            JDialog dDialog = new JDialog();
            dDialog.setTitle("All Movies");
            dDialog.setLayout(new GridBagLayout());
            dDialog.setSize(600, 500);
            dDialog.setLocationRelativeTo(frame);
            
            JLabel title = new JLabel("ALL MOVIES");
            title.setFont(new Font("Calisto MT", Font.BOLD, 26));

            DefaultTableModel tableModel = new DefaultTableModel(columnNamesMovies, 0);
           
            JTable table = new JTable(tableModel);
        
            String m =" ";
            for (int i = 0; i < movieList.size(); i++){
                String movTitle = movieList.get(i).getTitle();
                String cat = dv.get(i);
                boolean avai = movieList.get(i).isAvailable();
                boolean newRelease = movieList.get(i).isNewRelease();
                
               
                if(!m.equalsIgnoreCase(movTitle))
                {
                   Object[] data = {movTitle, cat, avai, newRelease};
                    tableModel.addRow(data); 
                } 
                    
                
                 m = movTitle;
            }

            table.setPreferredSize(new Dimension(500, 350));
            
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            //.getColumnModel().getColumn(1).setHeaderValue("Customers");
            table.setModel(tableModel);
            JPanel panel = new JPanel(new FlowLayout());
            panel.add(new JScrollPane(table));
            
            gbc.insets = new Insets(5,5,5,5);
            gbc.gridx = 0;
            gbc.gridy = 0;
            dDialog.add(title, gbc);
            gbc.gridx = 0;
            gbc.gridx = 1;
            dDialog.add(table.getTableHeader(), gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            dDialog.add(panel, gbc);
            dDialog.pack();
            dDialog.setVisible(true);
           
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
         movieList.clear();
         dv.clear();
    }
  /*
    private void removeRow()
{
            //model = (DefaultTableModel) this.table.getModel();
            int size = this.table.getRowCount();
            if(size != 0)
            {
            for(int i=0;  i < size ;i++)
            {
                //model.removeRow(i);
                this.table.remove(i);
            }
          }
}
    
    
    
    /*
    */  
    public void DisplayAvailableMovies(){
        
        try {
            Communicate(6);
            JDialog dDialog = new JDialog();
            dDialog.setTitle("Available Movies");
            dDialog.setLayout(new GridBagLayout());
            dDialog.setSize(600, 500);
            dDialog.setLocationRelativeTo(frame);
            
            JLabel title = new JLabel("AVAILABLE MOVIES");
            title.setFont(new Font("Calisto MT", Font.BOLD, 26));
            
            DefaultTableModel tableModel = new DefaultTableModel(columnNamesMovies, 0);
            String m = " ";
            JTable table = new JTable(tableModel);
            //String category = " ";
            for (int i = 0; i < movieList.size(); i++){
                String movTitle = movieList.get(i).getTitle();
                String cat = dv.get(i);
                boolean avai = movieList.get(i).isAvailable();
                boolean newRelease = movieList.get(i).isNewRelease();
                
              
                
             
                if(!m.equalsIgnoreCase(movTitle))
                {
                   Object[] data = {movTitle, cat, avai, newRelease};               
                    tableModel.addRow(data); 
                } 
                    
                
                 m = movTitle;
                
                
            }

            table.setPreferredSize(new Dimension(500, 350));
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setModel(tableModel);
            JPanel panel = new JPanel(new FlowLayout());
            panel.add(new JScrollPane(table));
            
            gbc.insets = new Insets(5,5,5,5);
            gbc.gridx = 0;
            gbc.gridy = 0;
            dDialog.add(title, gbc);
            gbc.gridx = 0;
            gbc.gridx = 1;
            dDialog.add(table.getTableHeader(), gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            dDialog.add(panel, gbc);
            dDialog.pack();
            dDialog.setVisible(true);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        movieList.clear();
         dv.clear();
    }
    
    public void AddCustomer(){
        final JDialog aDialog = new JDialog();
        aDialog.setTitle("Add Customer");
        aDialog.setSize(300,400);
        aDialog.setLayout(new GridBagLayout());
        aDialog.setLocationRelativeTo(frame);
        
        JLabel title = new JLabel("ADD CUSTOMER");
        title.setFont(new Font("Calisto MT", Font.BOLD, 26));
        
        JLabel nameLabel = new JLabel("First Name:");
        final JTextField nameField = new JTextField(10);
        JLabel surnameLabel = new JLabel("Last Name:");
        final JTextField surnameField = new JTextField(10);
        JLabel phoneLabel = new JLabel("Phone Number:");
        final JTextField phoneField = new JTextField(10);
        
        JButton btnConfirm = new JButton("Confirm");
        btnConfirm.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                
                try {
                    cust.setName(nameField.getText());
                    cust.setSurname(surnameField.getText());
                    cust.setPhoneNum(phoneField.getText());
                    cust.setCredit(100.0);
                    
                    Communicate(7);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                aDialog.dispose();
            }
        });
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                aDialog.dispose();
            }
        });
        
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        aDialog.add(title, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        aDialog.add(nameLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        aDialog.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        aDialog.add(surnameLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        aDialog.add(surnameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        aDialog.add(phoneLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        aDialog.add(phoneField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        aDialog.add(btnConfirm, gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        aDialog.add(btnCancel, gbc);
        
        aDialog.setVisible(true);
    }

    public void DisplayCustomers(){
      
       try {
            Communicate(8);
            JDialog dDialog = new JDialog();
            dDialog.setTitle("Customers");
            dDialog.setLayout(new GridBagLayout());
            dDialog.setSize(600, 500);
            dDialog.setLocationRelativeTo(frame);
            
            JLabel title = new JLabel("CUSTOMERS");
            title.setFont(new Font("Calisto MT", Font.BOLD, 26));
            
            DefaultTableModel tableModel = new DefaultTableModel(columnNamesCustomers, 0);
         
           
        
            JTable table = new JTable(tableModel);
          
         
            for (int i = 0; i < custList.size(); i++){
                String name = custList.get(i).getName();
                String surname = custList.get(i).getSurname();
                String phone = custList.get(i).getPhoneNum();
                String credit = Double.toString(custList.get(i).getCredit());
               
                Object[] data = {name, surname, phone, credit};     
                tableModel.addRow(data);
                  
              
                
            }
            table.setPreferredSize(new Dimension(500, 350));
            
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            //.getColumnModel().getColumn(1).setHeaderValue("Customers");
            table.setModel(tableModel);
            JPanel panel = new JPanel(new FlowLayout());
            panel.add(new JScrollPane(table));
            
            gbc.insets = new Insets(5,5,5,5);
            gbc.gridx = 0;
            gbc.gridy = 0;
            dDialog.add(title, gbc);
            gbc.gridwidth = 3;
            gbc.gridx = 0;
            gbc.gridy = 2;
            dDialog.add(table.getTableHeader(), gbc);
            gbc.gridx = 0;
            gbc.gridy = 3;
            dDialog.add(panel, gbc);
            dDialog.pack();
            dDialog.setVisible(true);
            
          } catch (ClassNotFoundException ex) {
           
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        custList.clear();
    }
 
    public void Communicate(int opt) throws ClassNotFoundException{
        try {
            /*CONNECTION HAS BEEN ESTABLISHED */
            //CREATE CHANNELS
            out = new ObjectOutputStream(server.getOutputStream());
            out.flush();
            in = new ObjectInputStream(server.getInputStream());
            String exists;
            
            //COMMUNICATE
            switch(opt){
                case 1: out.writeObject("rent");
                        out.flush();
                        out.writeObject(movie);
                        out.flush();
                        out.writeObject(cust);
                        out.flush();
                        exists = (String) in.readObject();
                        if (exists.equalsIgnoreCase("successful"))
                            JOptionPane.showMessageDialog(null, "Movie rented successfully"); 
                        else
                            JOptionPane.showMessageDialog(null, "Movie renting failed"); 
                        break;
                case 2: out.writeObject("return");
                        out.flush();
                        out.writeObject(movie);
                        out.flush();
                        exists = (String) in.readObject();
                        if(exists.equalsIgnoreCase("successful"))
                            JOptionPane.showMessageDialog(null, "Movie has been returned");
                        else
                            JOptionPane.showMessageDialog(null, "Movie is already returned or does not exist");
                        break;
                case 3: out.writeObject("addmovie");
                        out.flush();
                        System.out.println("Sent string");
                        out.writeObject(movie);
                        out.flush();
                        System.out.println("Sent object");
                        exists = (String) in.readObject();
                        System.out.println("DVD sent to server");
                        if(exists.equalsIgnoreCase("successful"))
                            JOptionPane.showMessageDialog(null, "Movie has been added to the database");
                        else if (exists.equalsIgnoreCase("unsuccessful"))
                            JOptionPane.showMessageDialog(null, "Movie has failed to be added to the database");
                        break;
                case 4: out.writeObject("delete");
                        out.flush();
                        out.writeObject(movie);
                        out.flush();
                        exists = (String) in.readObject();
                        if(exists.equalsIgnoreCase("successful"))
                                JOptionPane.showMessageDialog(null, "Movie has been deleted");
                        else
                                JOptionPane.showMessageDialog(null, "Movie does not exist in database");
                        break;
                case 5: out.writeObject("displayallmovies");
                        out.flush();
                        movieList = (ArrayList)in.readObject();
                        dv = (ArrayList)in.readObject();
                        break;
                case 6: out.writeObject("displayavailablemovies");
                        out.flush();
                        movieList = (ArrayList)in.readObject();
                        dv = (ArrayList)in.readObject();
                        break;
                case 7: out.writeObject("addcustomer");
                        out.flush();
                        out.writeObject(cust);
                        out.flush();
                        exists = (String) in.readObject();
                        System.out.println("Customer Sent to server");
                        if(exists.equalsIgnoreCase("successful"))
                            JOptionPane.showMessageDialog(null, "Customer has been added to the database");
                        else if (exists.equalsIgnoreCase("unsuccessful"))
                            JOptionPane.showMessageDialog(null, "Customer has failed to be added to the database");
                        break;
                case 8: out.writeObject("displaycustomers");
                        out.flush();
                        custList = (ArrayList)in.readObject();
                        
                        break;
                case 9: out.writeObject("exit");
                        out.flush();
                        break;
            }
            
        } catch (IOException ex) {
            System.out.println("IOExceptionError:  Error With Streams");
       //} catch (ClassNotFoundException ex) {
            System.out.println("CNF Error: Error Cannot Read Object");
        }
    }
    
    
    
    public static void main(String[] args){
        Client client = new Client();
    }
}

