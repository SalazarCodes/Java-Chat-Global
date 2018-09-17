
package ClientChatRoom;

import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 *
 * @author serverssupport
 */
public class Client extends JFrame{
    
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;
    
    //constructor
    public Client(String host){
        super("Client server");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event){
                        sendMessage(event.getActionCommand());
                        userText.setText("");
                        
                    }
            }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300,150);
        setVisible(true);
       
    }
    public void Start(){
        try{
            connectToServer();
            setupStreams();
            whileChatting();
        }
        catch(EOFException e){
            showMessage("\n Client terminated connection.");
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally{
            closeServer();
        }
    }
    
    private void connectToServer() throws IOException{
        showMessage("Attempting connection...\n");
        connection = new Socket(InetAddress.getByName(serverIP),6789);
        showMessage("Connected to : "+ connection.getInetAddress().getHostName());
                
    }
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\nStreams good to go.\n");
    }
    
    private void whileChatting() throws IOException{
        ableToType(true);
        do{
            try{
                message = (String)input.readObject();
                showMessage("\n"+message);                
            }
            catch(ClassNotFoundException e){
                showMessage("\nI dont know wtf u did");
            }
        }
        while(!message.equals("SERVER - END"));
    }
    
    private void closeServer(){
        showMessage("\nClosing server...");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();            
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private void sendMessage(String message){
        try{
            output.writeObject("CLIENT - "+message);
            output.flush();
            showMessage("\nCLIENT - "+message);
            
        }
        catch(IOException e){
            chatWindow.append("You fucked up boi");
        }
    }
    
    private void showMessage(final String m){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        chatWindow.append(m);
                    }
                }
        );
    }
    
    private void ableToType(final boolean tof){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        userText.setEditable(tof);
                    }
                }
        );       
    }
}
