
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private static final ArrayList<ClientThread> clients = new ArrayList<ClientThread>();

    private static class ClientThread implements Runnable{
        Socket socket;
        String name;
        BufferedReader readMessage;
        DataOutputStream sendMessage;

        public ClientThread(Socket socket) throws Exception {
            this.socket = socket;
            this.readMessage = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.sendMessage = new DataOutputStream(socket.getOutputStream());
            sendMessage.writeBytes("Welcome to the NP chatroom! Please type your name and press enter...");
            this.name = readMessage.readLine();
            sendMessage.writeBytes("Hello" + name + "! If you ever want to quit type \"quit\" to exit.");
            this.send(name + " has joined the chat!", this);
        }
        @Override
        public void run() {
            try {
                fromClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void fromClient() throws Exception {
            while(true){
                String msg = readMessage.readLine();
                System.out.println("loop");
                if(msg!=null&&msg.equals("quit")){
                    socket.close();
                    break;
                }
                else {
                    send(msg, this);
                }
            }
            endClient();
        }
        public void send(String msg, ClientThread exclude) throws Exception {
            System.out.println(msg);
            for (ClientThread client : clients) {
                if (!client.equals(exclude)) {
                    client.toClient(msg);
                }
            }
        }
        public void toClient(String msg) throws Exception {
            sendMessage.writeBytes(this.name + ": " + msg);
        }
        void endClient(){
            clients.removeIf(client -> client.equals(this));
        }
    }


    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("Server Ready\n");

        while (true) {
            Socket clientSocket = serverSocket.accept();;
            ClientThread newClient = new ClientThread(clientSocket);
            clients.add(newClient);
            Thread cthread = new Thread(newClient);
            Thread.sleep(100);
            cthread.start();
        }
    }
}
