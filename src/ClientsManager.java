import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientsManager extends Thread{

    private List<MessageObject> messages = new ArrayList<>();
    private List<ClientHandler> clients = new ArrayList<>();
    private List<ClientHandler> clientsToRemove = new ArrayList<>();

    private volatile boolean running = true;

    public void run(){
        while(running){
            if(clientsToRemove.size()>0){
                removeClients();
            }

            if(messages.size()>0){
                MessageObject messageObject = messages.get(0);
                String message = messageObject.getId()+": "+messageObject.getMessage();
                for(ClientHandler clientHandler:clients){
                    if(messageObject.getId() != clientHandler.getID()){

                        clientHandler.write(message);
                    }
                }

                System.out.println(message);
                messages.remove(0);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeClients(){
        clients.removeAll(clientsToRemove);
        clientsToRemove.clear();
    }

    public void addMessage(String message, int id){
        messages.add(new MessageObject(message, id));
    }

    public void addClient(Socket socket){
        System.out.println("Client found");
        ClientHandler clientHandler;
        try {
            clientHandler = new ClientHandler(socket,this);
            clients.add(clientHandler);
            clientHandler.start();
            addMessage("Connected",clientHandler.getID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    public void addToRemoveClients(ClientHandler clientHandler){
        addMessage("Disconnected",clientHandler.getID());
        clientsToRemove.add(clientHandler);
    }

    public void close(){
        running = false;
        System.out.println("Closing clients");
        for(ClientHandler clientHandler:clients){
            clientHandler.write("q");
            clientHandler.close();
        }

    }

    private class MessageObject{
        private String message;
        private int id;
        MessageObject(String message, int id){
            this.message = message;
            this.id = id;
        }
        String getMessage(){
            return message;
        }
        int getId(){
            return id;
        }
    }
}