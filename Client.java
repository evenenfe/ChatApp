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


public class Client implements Runnable{
    
    
    private static PrintStream ps = null;
    private static DataInputStream dis = null;
    private static Socket sock = null;
    private static BufferedReader bfr = null;
    private static boolean shut = false;
    
    
    public static void main(String[] args) {
         
        int portNum = 3308;
        String hostname = "localhost";
        
        if (args.length < 2) {
            System.out.println(">>>Client List<<<\nHostname : " + hostname + "\nPort number: " + portNum);
        } else {
            hostname = args[0];
            portNum = Integer.valueOf(args[1]).intValue();
        }
        
        try {
            
            sock = new Socket(hostname, portNum);
            bfr = new BufferedReader(new InputStreamReader(System.in));
            ps = new PrintStream(sock.getOutputStream());
            dis = new DataInputStream(sock.getInputStream());
            
            
        } catch (UnknownHostException ex) {
            System.out.println("Alienated Host!");
        } catch (IOException ex){
            System.out.println("Unable to fetch I/O from host: " + hostname);
        }
        
        if (sock != null && ps != null && dis != null) {
            
            try {
                
                
                new Thread(new Client()).start();
                
                while (!shut) {                    
                    
                    ps.println(bfr.readLine().trim());
                }
                
                ps.close();
                dis.close();
                sock.close();
            } catch (IOException ex) {
                
               ex.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        String res;
        
        
        try {
            while ((res = dis.readLine()) != null) {                
                System.out.println(res);
                
                if (res.indexOf("Session Ended!") != -1) {
                    break;
                }
            }
            shut = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
