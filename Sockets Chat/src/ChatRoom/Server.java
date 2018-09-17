package ChatRoom;

import java.io.*;
import java.net.*;
import java.awt.Event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Server extends JFrame{
    
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;    
    
    //constructor
    public Server(){
        super("Chat de prueba");
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
        add(new JScrollPane(chatWindow));
        setSize(300,150);
        setVisible(true);
        }
    
        public void Start(){
            try{
                server = new ServerSocket(6789,100);
                while(true){
                    try{
                        waitForConnection();
                        setupStreams();
                        whileChatting();                        
                    }
                    catch(EOFException e){
                        showMessage("\nServer ended connection.");
                    }
                    finally{
                        closeServer();
                    }
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        //wait for connection, then display connection info
        private void waitForConnection() throws IOException{
            showMessage("Waiting for connections...\n");
            connection = server.accept();
            showMessage("Now connected to :"+connection.getInetAddress().getHostName());            
        }
        //get stream to send and recieve data
        private void setupStreams() throws IOException{
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            showMessage("\nStreams are now set up\n");
        }
        //during the chat conversation
        private void whileChatting() throws IOException{
            String message = "You are now connected";
            sendMessage(message);
            ableToType(true);
            do{
               try{
                   message = (String)input.readObject();
                   showMessage("\n"+message);
               }
               catch(ClassNotFoundException e)
               {
                   showMessage("WTF did he just type :v");
               }
            }
            while(!message.equals("CLIENT - END"));            
        }
        private void closeServer(){
            showMessage("\nClosing connections...\n");
            ableToType(false);
            try{
                output.close();
                input.close();
                connection.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }            
        }
        //send message to client
        private void sendMessage(String message){
            try{
                output.writeObject("SERVER - "+ message);
                output.flush();
                showMessage("\nSERVER - "+message);
            }
            catch(IOException e){
                chatWindow.append("\nERROR : Cant send message.");
            }
        }
        //chat window update
        private void showMessage(final String text){
            SwingUtilities.invokeLater(
                    new Runnable(){
                        public void run(){
                            chatWindow.append(text);
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

    void setDefaultOnCloseOperation(int EXIT_ON_CLOSE) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
         }
    }
    

