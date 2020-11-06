import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
    private static final ArrayList<ClientThread> clients = new ArrayList<>();

    private static class ClientThread implements Runnable{
        Socket socket;
        String name;
        BufferedReader readMessage;
        DataOutputStream sendMessage;

        public ClientThread(Socket socket) throws Exception {
            this.socket = socket;
            this.readMessage = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.sendMessage = new DataOutputStream(socket.getOutputStream());
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
            sendMessage.writeBytes("Welcome to the NP chatroom! Please type your name and press enter...\r\n");
            this.name = readMessage.readLine();
            sendMessage.writeBytes("Hello " + name + "! If you ever want to quit type \"{quit}\" to exit.\r\n");
            this.send(name + " has joined the chat!", this, false);
            System.out.println(name + " has joined the chat!");
            while(true){
                String msg = readMessage.readLine();
                if(msg!=null&&msg.equals("{quit}")){
                	this.send(name + " has left the chat.", this, false);
                	System.out.println(name + " has left the chat!");
                    socket.close();
                    break;
                }
                else {
                    send(msg, this, true);
                }
            }
            endClient();
        }
        public void send(String msg, ClientThread exclude, boolean includeUsername) throws Exception {
            for (ClientThread client : clients) {
                if (!client.equals(exclude)) {
                    client.toClient(this.name, msg, includeUsername);
                }
            }
        }
        public void toClient(String name, String msg, boolean includeUsername) throws Exception {
            if (includeUsername) sendMessage.writeBytes(name + ": " + msg + "\r\n");
            else sendMessage.writeBytes(msg + "\r\n");
        }
        void endClient(){
            clients.removeIf(client -> client.equals(this));
        }
    }


    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("Server Ready, Waiting for connection...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientThread newClient = new ClientThread(clientSocket);
            clients.add(newClient);
            Thread cthread = new Thread(newClient);
            Thread.sleep(100);
            cthread.start();
        }
    }
}
