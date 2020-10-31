package network.tcp.multipleclient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApplication {
    public static void main(String[] args) throws IOException {

        JFrame frame = new JFrame("Chat Application");
        JLabel usernameLabel = new JLabel("Enter Username:");
        JTextField usernameField = new JTextField();
        JButton addUserBtn = new JButton("Add User");

        frame.setLayout(null);
        frame.setSize(300, 300);

        usernameLabel.setBounds(0, 0, 150, 25);
        usernameField.setBounds(0, 25, 150, 25);
        addUserBtn.setBounds(25, 50, 100, 25);

        frame.add(usernameLabel);
        frame.add(usernameField);
        frame.add(addUserBtn);
        frame.setVisible(true);


        addUserBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                Boolean checker;
                checker = true;
                DatabaseConnection conn = new DatabaseConnection();
                String sql = "select * from users";
                
                try {
                    PreparedStatement ps =  conn.getDatabaseConnection().prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();
                    
                    while(rs.next()){
                        String userName = rs.getString("username");
                        Boolean connected = rs.getBoolean("connected");
                        
                        if(userName.equals(usernameField.getText())){
                            checker = false;
                            if(connected == true){
                                ErrorFrame();
                                return;
                            }else{
                                break;
                            }
                            
                        }
                    }
                    
                } catch (ClassNotFoundException | SQLException ex) {
                    Logger.getLogger(MainApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (checker==false){
                String SQL_CHANGE="update users set connected=true where username='"+usernameField.getText()+"'";
                    try(PreparedStatement ps = conn.getDatabaseConnection().prepareStatement(SQL_CHANGE)) {
                        ps.executeUpdate();
                        System.out.println("Success");
                    } catch (SQLException ex) {
                        System.out.print(ex.getMessage());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                
                NetworkClient networkClient = new NetworkClient(usernameField.getText());
                usernameField.setText("");
                networkClient.start();
                }else{
                    String SQL_INSERT="insert into users (username,connected) values(?,?)";
                     try(PreparedStatement ps = conn.getDatabaseConnection().prepareStatement(SQL_INSERT)) {
                        ps.setString(1, usernameField.getText());
                        ps.setBoolean(2, true);
                        ps.executeUpdate();
                        System.out.println("Success");
                    } catch (SQLException ex) {
                    System.out.print(ex.getMessage());
                    } catch (Exception ex) {
                    ex.printStackTrace();
                    }
                    NetworkClient networkClient = new NetworkClient(usernameField.getText());
                    usernameField.setText("");
                    networkClient.start();
                }
            }

       public void ErrorFrame(){
                JFrame newFrame= new JFrame("Already connected");
                JLabel newLabel = new JLabel("This username is already connected in the chat.");
                newFrame.setLayout(null);
                newFrame.setSize(500,500);
                newLabel.setBounds(0,0,400,400);
                newFrame.add(newLabel);
                newFrame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
