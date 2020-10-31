package network.tcp.multipleclient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkClient extends Thread {
    Socket s1 = null;
    String line = null;
    BufferedReader br = null;
    BufferedReader in = null;
    PrintWriter os = null;
    static JTextArea conversationView = new JTextArea();
    JTextField messageField = new JTextField();
    JButton sendMessageBtn = new JButton("Send Message");
    JButton logoutBtn = new JButton("Logout");
    private String username;
    private boolean isConnectionCreated;
//    separate  each lines in the input
    public static final String newLine = System.getProperty("line.separator");


    public NetworkClient(String username) {
        this.username = username;
    }
    
    public void createInnerView() throws SQLException, ClassNotFoundException{
        String innerViewSQL = "select * from messages";
        DatabaseConnection conn = new DatabaseConnection();
        PreparedStatement ps = conn.getDatabaseConnection().prepareStatement(innerViewSQL);
        ResultSet rs = ps.executeQuery();
    
        
        while(rs.next()){
            conversationView.append(rs.getString(2) + "=>");
            conversationView.append(rs.getString(3));
            conversationView.append(newLine);
        }
//        conversationView.append(line+"\n");

}

    @Override
    public void run() {

        JFrame userFrame = new JFrame(this.username + " chat window");
        userFrame.setLayout(null);
        userFrame.setSize(500, 500);

        conversationView.setEditable(false);
//        conversationView.setLineWrap(true);
//        conversationView.setWrapStyleWord(true);
        conversationView.setBounds(0, 0, 500, 350);
        userFrame.add(conversationView);

        messageField.setBounds(0, 355, 350, 65);
        userFrame.add(messageField);

        sendMessageBtn.setBounds(355, 355, 145, 65);
        userFrame.add(sendMessageBtn);

        logoutBtn.setBounds(0,422,100,40);
        userFrame.add(logoutBtn);

        Runnable runnableView = new Runnable(){
            @Override
            public void run(){
                conversationView.setText("");
                
                try {
                    createInnerView();
                    
                } catch (SQLException | ClassNotFoundException ex) {
                    Logger.getLogger(NetworkClient.class.getName()).log(Level.SEVERE, null, ex);
                }
//                conversationView.append(line+"\n");
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(runnableView, 0, 2, TimeUnit.SECONDS);
        
        
        sendMessageBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {

                InetAddress address = null;
                try {
                    address = InetAddress.getLocalHost();
                } catch (UnknownHostException ex) {

                }
                if (!isConnectionCreated) {
                    try {
                        s1 = new Socket(address, 4445); // You can use static final constant PORT_NUM
                        in = new BufferedReader(new InputStreamReader(s1.getInputStream()));
                        os = new PrintWriter(s1.getOutputStream());
                        isConnectionCreated = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.print("IO Exception");
                    }

                    System.out.println("Client Address : " + address);
                    System.out.println("Enter Data to echo Server ( Enter QUIT to end):");
                }

                String response = null;
                line = messageField.getText();
                
                
                
                String sql = "INSERT INTO messages(username, messages) VALUES (?,?)";
                DatabaseConnection conn = new DatabaseConnection();
                try {

                    PreparedStatement preparedStatement = conn.getDatabaseConnection().prepareStatement(sql);
                    preparedStatement.setString(1,username);
                    preparedStatement.setString(2,messageField.getText());
                    preparedStatement.executeUpdate();
                } catch (SQLException throwables) {
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(NetworkClient.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (!line.equalsIgnoreCase("QUIT")) {
                    os.println(username + ": " + line);
                    os.flush();
                    response = messageField.getText();
                    System.out.println("Server Response : " + response);
                    line = messageField.getText();
                    messageField.setText("");
                } else {
                    try {
                        in.close();
                        os.close();
                        s1.close();
                        System.out.println("Connection Closed");
                    } catch (IOException ex) {
                    }
                }

            }
        });

        userFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        userFrame.setVisible(true);
        
        logoutBtn.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               String sql="UPDATE users SET connected = ? WHERE username=?";
          DatabaseConnection conn = new DatabaseConnection();
               try {
                   PreparedStatement preparedStatement = conn.getDatabaseConnection().prepareStatement(sql);
                   preparedStatement.setBoolean(1,false);
                   preparedStatement.setString(2,username);
                   preparedStatement.executeUpdate();
        //           dbConnection.close();

               } catch (SQLException throwables) {
               } catch (ClassNotFoundException ex) {
                   Logger.getLogger(NetworkClient.class.getName()).log(Level.SEVERE, null, ex);
               }
               userFrame.dispose();
           }
       });
    }
}
class MyWindowListener extends JFrame implements WindowListener{
String userName;
    public MyWindowListener(String userName){
        this.userName = userName;
    this.addWindowListener(this);
    }
    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

       DatabaseConnection connection = new DatabaseConnection();

       String sql="UPDATE users SET connected=? WHERE username = ? ";
        DatabaseConnection dbConnection = new DatabaseConnection();

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = dbConnection.getDatabaseConnection().prepareStatement(sql);
            preparedStatement.setBoolean(1,false);
            preparedStatement.setString(2,userName);
            preparedStatement.executeUpdate();
         //   connection.close();
        } catch (SQLException | ClassNotFoundException throwables) {
        }


    }

    @Override
    public void windowClosed(WindowEvent e) {


    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}