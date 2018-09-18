import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread{

    private static final int port = 5000;
    private volatile static boolean running = true;

    private static Socket socket;
    private static BufferedReader clientInput;
    private static BufferedReader serverInput;
    private static PrintWriter clientToServer;

    public static void main(String[] args) {
        if(args.length == 0||args[0].equals("-h")){
            System.out.println("Please pass IP address as arg");
            System.out.println("EX: java -jar Client.jar xxx.xxx.xxx.xxx");
            System.exit(0);
        }
        String specialChars = "["+"1234567890."+"]+";
        if(!args[0].matches(specialChars)) {
            System.out.println("Please enter a valid IP address");
            System.out.println("EX: 192.168.1.100");
            System.exit(0);
        }

        String host = args[0];
        try {
            socket = new Socket(host, port);
            serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientToServer = new PrintWriter(socket.getOutputStream(), true);

        }catch (IOException e){
            System.out.println("Server closed or connection refused");
            System.exit(0);
        }
        System.out.println("Server connected");

        (new Client()).start();

        clientInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Listening for user input");
        String message = "";
        while (running){
            try {
                message = clientInput.readLine();
            } catch (IOException e){
                System.out.println("user input closed");
                running = false;
            }
            if(message.equals("q")){
                clientToServer.println("q");
                running = false;
            } else {
                clientToServer.println(message);
            }
        }
        System.out.println("Client closed");
        close();
    }

    public void run(){
        String message;
        System.out.println("Listening for server messages");
        while (running){
            try {
                if((message = serverInput.readLine())!=null){
                    if(message.equals("q")){
                        running = false;
                    } else {
                        System.out.println(message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Server messages closed");
                running = false;
            }
        }
        System.out.println("Server closed");
        close();
    }

    private static void close(){
        running = false;
        try {
            socket.close();
            clientInput.close();
            serverInput.close();
            clientToServer.close();
        } catch (IOException e){
            System.out.println("Failed to close connection");
            e.printStackTrace();
        }

    }
}
