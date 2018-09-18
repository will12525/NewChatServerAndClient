import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private ClientsManager clientsManager;
    private Socket socket;
    private static int clientCount = 1;
    private int id;
    private volatile boolean running = true;

    ClientHandler(final Socket socket, final ClientsManager clientsManager) throws Exception{

        id = clientCount;
        clientCount++;

        this.socket = socket;
        this.clientsManager = clientsManager;

        try{
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e){
            e.printStackTrace();
            throw new Exception("Failed to start client, rejecting");
        }
    }

    public int getID() {
        return id;
    }

    public void run() {
        String message;

        while(running) {
            try {
                if((message=bufferedReader.readLine())!=null) {
                    if(message.equals("q")) {
                        clientsManager.addToRemoveClients(this);
                        running = false;
                    } else {
                        clientsManager.addMessage(message, id);
                    }
                }
            }
            catch(IOException e) {
                System.out.println("Client thread closed: "+ id);
            }
        }
    }

    public void write(String message){
        printWriter.println(message);
    }

    public void close(){
        running = false;
        try {
            socket.close();
            bufferedReader.close();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}