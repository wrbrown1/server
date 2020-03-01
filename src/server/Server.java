package server;

import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    
    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream input = null;
    private DataOutputStream output = null;
    private ArrayList<User> users;
    private ArrayList<Patient> patients;
    private User currentUser;
    String userInput = "";
    Scanner in = new Scanner(System.in);
    int steps = 0;
    int IDcount = 0;
    int loginAttempts;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    
    public Server(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        System.out.println("Server started. Waiting for client.");
        socket = serverSocket.accept();
        System.out.println("Client connected.");
        input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        output = new DataOutputStream(socket.getOutputStream());
        users = new ArrayList<>();
        patients = new ArrayList<>();
        PopulateDatabase();
        boolean connected = true;
        
        while(connected){
            if(steps == 0){
                output.writeUTF("Enter username: ");//write
                userInput = input.readUTF();//read
            }  
            switch (steps){
                //case 0 checks if the username the user entered is actually in the database
                case 0:
                    if (!SearchForUsername(userInput)){
                        System.out.print("Username not found");
                    }else{
                        System.out.print("Username found.");
                        UpdateCurrentUser(userInput);
                        steps++;
                    }                   
                    break;
                //case 1 checks for the correct password and jumps to case 6 if it is incorrect, jumps to case 2 if it is
                case 1:
                    output.writeUTF("Enter your password: ");//write
                    userInput = input.readUTF();//read
                    if(!AuthenticatePassword(userInput)){
                        steps = 6;
                    }else{
                        steps++;
                    }
                    break;
                //case 2 checks the current user's privilege. If it is d/n it jumps to step 3. If it isn't it just displays the balance because the user is a patient
                case 2:
                    if(currentUser.getPrivilege().equals("d") || currentUser.getPrivilege().equals("n")){
                        output.writeUTF(dtf.format(now) + "\n-Employee Access Menu-\nView Patient Database(v)\nAdd/Change Patient Data(c)\nLogout(o)");//write
                        userInput = input.readUTF();//read
                        steps++;
                    }else{
                        ShowBalance();
                    }
                    break;
                //case 3: if the input from case 2 is "v" display the database
                //        if the input from case 2 is "o" it jumps to case 0;
                //        if the input from case 2 is "c"(or anything else) it jumps to stwep 4
                case 3:
                switch (userInput) {
                    case "v":
                        DisplayPatientDatabase();//write
                        userInput = input.readUTF();//read
                        break;
                    case "o":
                        steps = 0;
                        break;
                    default:
                        steps++;
                        break;
                }
                    break;
                //case 4 displays a menu that lets the user add a patient or edit an existing patient's info
                case 4:
                    output.writeUTF("-Patient Database Editor Menu-\nAdd Patient(a)\nEdit Patient Data(e)\nBack(b)\nLogout(o)");//write
                    userInput = input.readUTF();//read
                    steps++;
                    if(userInput.equals("b")){
                        steps = 2;
                    }
                    if(userInput.equals("o")){
                        steps = 0;
                    }
                    break;  
                case 5:
                    if(userInput.equals("a")){
                        AddPatient();
                    }else if(userInput.equals("e")){
                        EditPatient();
                    }
                    steps--;
                    break;
                //case 6 checks for the correct password until the correct pw is entered or the user tries too many times
                case 6:
                    output.writeUTF("Invalid password, try again: ");//write
                    userInput = input.readUTF();//read
                    if(!AuthenticatePassword(userInput)){
                        if(loginAttempts > 5) connected = false;
                        loginAttempts++;
                    }else{
                        loginAttempts = 0;
                        steps = 2;
                    }
                    break;
            }
        }
        
        socket.close();
        input.close();
        System.out.println("Connection closed");
    }
    
    private void UpdateCurrentUser(String name){
        if(users.isEmpty()) return;
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getUsername().equals(name)){
                currentUser = users.get(i);
            }
        }
    }
    
    private boolean SearchForUsername(String name){
        boolean found = false;
        if (users.isEmpty()) return false;
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getUsername().equals(name)){
                found = true;
            }
        }
        return found;
    }
    
    private void RegisterUser(String name){
        User user = new User();
        user.setUsername(name);
        users.add(user);
        currentUser = user;
    }
    
    private boolean AuthenticatePassword(String password){
        return currentUser.getPassword().equals(password);
    }
    
    private void PopulateDatabase() {
        User drStrange = new User("BestAvenger123", "Cumberb@tch", "d", 0001);
        User drSmith = new User("SmithRulez", "password123", "d", 0002);
        User nurseJoy = new User("Poke-Nurse", "pikachu", "n", 0003);
        User nurseJane = new User("JaneRulez", "password123", "n", 0004);
        Patient Will = new Patient("Will", "Brown", "1433 Country Lake Dr, Greensboro NC", "336-707-0369", 23.45, "wrbrown", "0429", "p", 1005);
        Patient Rachel = new Patient("Rachel", "Somerville", "1435 Country Lake Dr, Greensboro NC", "336-707-0000", 12.76, "rcSomerville", "1031", "p", 1006);
        Patient Sue = new Patient("Sue", "Brown", "1433 Country Lake Dr, Greensboro NC", "336-707-1234", 41.98, "smBrown", "Oct1990", "p", 1007);
        Patient Bill = new Patient("Bill", "Brown", "1433 Country Lake Dr, Greensboro NC", "336-707-4321", 102.30, "brBrown", "Oct1990", "p", 1008);
        Patient Debby = new Patient("Debby", "Somerville", "1234 Union Cross St, Rutherfordton NC", "555-555-1234", 2082.34, "dSomerville", "Apr1995", "p", 1009);
        Patient Ed = new Patient("Ed", "Somerville", "1234 Union Cross St, Rutherfordton NC", "555-555-4321", 0.00, "eSomerville", "Apr1995", "p", 1010);
        users.add(drStrange);
        users.add(drSmith);
        users.add(nurseJoy);
        users.add(nurseJane);
        users.add(Will);
        users.add(Rachel);
        users.add(Sue);
        users.add(Bill);
        users.add(Debby);
        users.add(Ed);
        patients.add(Will);
        patients.add(Rachel);
        patients.add(Sue);
        patients.add(Bill);
        patients.add(Debby);
        patients.add(Ed);
    }
    
    private void DisplayPatientDatabase() throws IOException{
        String out = "";
        for(int i = 0; i < patients.size(); i++){
            out += patients.get(i).ToString() + "\n";
        }
        output.writeUTF(out + "\n-Employee Access Menu-\nView Patient Database(v)\nAdd/Change Patient Data(c)\nLogout(o)");
    }
 
    private void HospitalEmployeeInterface() throws IOException {
        if(userInput.equals("v")){
            DisplayPatientDatabase();
        }else if(userInput.equals("c")){
            steps++;
        }
    }
    
    private void PatientDatabaseEditor() throws IOException{
        output.writeUTF("-Patient Database Editor Menu-\nAdd Patient(a)\nEdit Patient Data(e)\nBack(b)\nLogout(o)");
        userInput = input.readUTF();
        if(userInput.equals("a")){
            AddPatient();            
        }else if(userInput.equals("e")){
            //EditPatient();
            steps++;
        }
    }
    
    private void AddPatient() throws IOException{
        Patient patient = new Patient();
        output.writeUTF("Enter the patient's first name: ");
        patient.setFirstName(input.readUTF());
        output.writeUTF("Enter the patient's last name: ");
        patient.setLastName(input.readUTF());
        output.writeUTF("Enter the patient's address: ");
        patient.setAddress(input.readUTF());
        output.writeUTF("Enter the patient's phone number: ");
        patient.setPhoneNumber(input.readUTF());
        output.writeUTF("Enter the patient's balance: ");
        patient.setBalance(Double.parseDouble(input.readUTF()));
        output.writeUTF("Enter the patient's username: ");
        String name = input.readUTF();
        boolean available = false;
        while(!available){
            for(int i = 0; i < users.size(); i++){
                if(name.equals(users.get(i).getUsername())){
                    output.writeUTF("Username is taken, try a different username: ");
                    name = input.readUTF();
                    i = 0;
                }else{
                    available = true;
                }
            }   
        }
        output.writeUTF("Enter the patient's password: ");
        patient.setPassword(input.readUTF());
        patient.setID(1010 + IDcount);
        patients.add(patient);
        users.add(patient);
        IDcount++;
    }
    
    private void EditPatient() throws IOException{
        output.writeUTF("Enter the patient's ID: ");
        userInput = input.readUTF();
        Patient patient = null;
        int index = 0;
        for(int i = 0; i < patients.size(); i++){
            if(userInput.equals(Integer.toString(patients.get(i).getID()))){
                patient = patients.get(i);
                index = i;
            }
        }
        if(patient != null){
            output.writeUTF("Patient's current data:\n" + patient.ToString() + "\n-Patient Database Editor Menu-\nChange address(x)\nChange phone(y)\nChange balance(z)");
            userInput = input.readUTF();
            switch (userInput) {
                case "x":
                    output.writeUTF("Enter new address: ");
                    userInput = input.readUTF();
                    patients.get(index).setAddress(userInput);
                    break;
                case "y":
                    output.writeUTF("Enter new phone number: ");
                    userInput = input.readUTF();
                    patients.get(index).setPhoneNumber(userInput);
                    break;
                case "z":
                    output.writeUTF("Enter new balance: ");
                    userInput = input.readUTF();
                    patients.get(index).setBalance(Double.parseDouble(userInput));
                    break;
                default:
                    break;
            }
        }else{
            System.out.println("No matching ID");
        }
    }
    
    private void ShowBalance() throws IOException{
        for(int i = 0; i < patients.size(); i++){
            if(patients.get(i).getUsername().equals(currentUser.getUsername())){
                output.writeUTF(dtf.format(now) + "\nCurrent balance: " + patients.get(i).getBalance());
                steps = 0;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(5000);
    }   
}
