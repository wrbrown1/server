package server;

import java.net.*;
import java.io.*;

public class Server {
    
    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream input = null;
    
    public Server(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        System.out.print("Server started. Waiting for client.");
        socket = serverSocket.accept();
        System.out.print("Client connected.");
        input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        
        String userInput = "";
        
        while(!userInput.equals("Disconnect")){
            userInput = input.readUTF();
            System.out.println(userInput);
        }
        
        socket.close();
        input.close();
        System.out.print("Connection closed");
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(5000);
    }
    
}
