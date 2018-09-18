import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.SocketException;

public class Server extends Thread {

    private static final int port = 5000;
    private static volatile boolean running = true;
    private static ClientsManager clientsManager;
    private static ServerSocket serverSocket;

    public void run(){
        BufferedReader serverInput = new BufferedReader(new InputStreamReader(System.in));
        while(running){
            try{
                String message = serverInput.readLine();
                if(message!=null){
                    if(message.equals("q")){
                        running = false;
                    }
                    clientsManager.addMessage(message,0);
                }
            } catch(IOException e){
                System.out.println("Failed to read message from server input");
                clientsManager.addMessage("q",0);
                running = false;
            }
        }
        System.out.println("Server closing");
        clientsManager.close();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        try {
            serverSocket = new ServerSocket(port);
        }catch (BindException e){
            System.out.println("Port "+port+" already in use");
            System.exit(0);
        }

        clientsManager = new ClientsManager();
        clientsManager.start();
        (new Server()).start();

        System.out.println("Socket opened on port: "+port);
        System.out.println("Listening for clients");
        while(running){
            try {
                clientsManager.addClient(serverSocket.accept());
            } catch (SocketException e){
                running = false;
            }
        }

        System.out.println("Server closed");

    }
}
