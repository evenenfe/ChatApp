/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChatApp;
/**
 *
 * @author Eva Fe T. Delima
 */
import java.net.*;
import java.io.*;

public class Threads extends Thread {

    private final Threads[] chatThreads;
    private int maxClient;
    private String name = null;
    private DataInputStream dis = null;
    private Socket clientSock = null;
    private PrintStream ps = null;
    

    public Threads(Socket clientSocket, Threads[] chatThreads){
        maxClient = chatThreads.length;
        clientSock = clientSocket;
        this.chatThreads = chatThreads;
    }

        //Method used by the Client to enter its name and 
        //will be added to the clientList
    public void run(){
        int maxClient = this.maxClient;
        Threads[] chatThreads = this.chatThreads;
        
        try {
            ps = new PrintStream(clientSock.getOutputStream());
            dis = new DataInputStream(clientSock.getInputStream());
            
            String username;
            
            while (true) {                
                System.out.println(">>>Client List<<<");
                ps.println("Enter Client's name: ");
                username = dis.readLine().trim();
                
                if (username.indexOf('@') == -1) {
                    break;
                } else {
                    ps.println("Invalid Key has been Inputed!");
                }
            }
            
            ps.println("Greetings! " + username + "\nQuit the Conversation?\n"
                    + "Just type '?Quit'.");
            
            synchronized (this){
                for (int i = 0; i < maxClient; i++) {
                    if (chatThreads[i] != null && chatThreads[i] == this) {
                        name = "@" + username;
                        break;
                    }
                }
                
            //This will evaluate the client and will be
            //included on the chat room
                for (int i = 0; i < maxClient; i++) {
                    if (chatThreads[i] != null && chatThreads[i] != this) {
                        chatThreads[i].ps.println(username + " just JOINED the conversation!");
                    }
                }
            }
            
            /*
            The codes below will allow the users to converse
            */
            
            while (true) {                
                String res = dis.readLine();
                if (res.startsWith("?Quit")) {
                    break;
                }
                if (res.startsWith("@")) {
                    String[] words = res.split("\\s", 2);
                    if (words.length > 1 && words[1] != null) {
                        words[1] = words[1].trim();
                        if (!words[1].isEmpty()) {
                            synchronized (this){
                                for (int i = 0; i < maxClient; i++) {
                                    if (chatThreads[i] != null && chatThreads[i] != this && chatThreads[i].name != null &&
                                            chatThreads[i].name.equals(words[0])) {
                                        chatThreads[i].ps.println(username + " ->> " + words[1]);
                                        this.ps.println(username + " ->> " + words[1]);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else{
                    synchronized (this) {
                        for (int i = 0; i < maxClient; i++) {
                            if (chatThreads[i] != null && chatThreads[i].name != null) {
                                chatThreads[i].ps.println(username + " ->> " + res);
                            }
                        }
                    }
                }
            }

            //This is the instructions if the Client types ?Quit
                synchronized (this){
                    for (int i = 0; i < maxClient; i++) {
                        if (chatThreads[i] != null && chatThreads[i] != this && chatThreads[i].name != null) {
                            chatThreads[i].ps.println(username + " has left the conversation.");
                        }
                        
                    }
                }
                
                ps.println("Session Ended!");
                synchronized (this) {
                    for (int i = 0; i < maxClient; i++) {
                        if (chatThreads[i] == this) {
                            chatThreads[i] = null;
                        }
                    }
                }
                
                dis.close();
                ps.close();
                clientSock.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
