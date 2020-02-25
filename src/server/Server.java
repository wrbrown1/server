package server;

import java.net.*;
import java.io.*;

public class Server {
    
    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;
    
    public Server(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        System.out.println("Server started. Waiting for client.");
        socket = serverSocket.accept();
        System.out.println("Client connected.");
        input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        output = new DataOutputStream(socket.getOutputStream());
        
        String userInput = "";
        
        while(!userInput.equals("Disconnect")){
            userInput = input.readUTF();
            System.out.println(userInput);
            output.writeUTF("Server received " + userInput);
        }
        
        socket.close();
        input.close();
        System.out.println("Connection closed");
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(5000);
    }
    
}
