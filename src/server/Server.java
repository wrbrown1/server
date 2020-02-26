package server;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.JFrame;

public class Server {
    
    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;
    private ArrayList<User> users;
    private ArrayList<Patient> patients;
    
    public Server(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        System.out.println("Server started. Waiting for client.");
        socket = serverSocket.accept();
        System.out.println("Client connected.");
        input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        output = new DataOutputStream(socket.getOutputStream());
        users = new ArrayList<>();
        
        String userInput = "";
        
        while(!userInput.equals("Disconnect")){
            userInput = input.readUTF();
            if(SearchForUsername(userInput)){
                output.writeUTF("Username found.");
            }else{
                output.writeUTF("Username NOT found, registering new username.");
                RegisterUser(userInput);
            }
        }
        
        socket.close();
        input.close();
        System.out.println("Connection closed");
    }
    
    private boolean SearchForUsername(String userInput){
        boolean found = false;
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getUserName().equals(userInput)){
                found = true;
            }
        }
        return found;
    }
    
    private void RegisterUser(String userInput){
        User user = new User();
        user.setUserName(userInput);
        users.add(user);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(5000);
    }
    
}
