
import java.io.*;
import java.net.*;
import java.util.Scanner;

class Client {
    static Scanner s;
    static DataOutputStream outToServer;

    private static class ClientThread implements Runnable{
        Socket socket;
        BufferedReader inFromServer;

        public ClientThread(Socket socket) throws Exception {
            this.socket = socket;
            this.inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            try{

                recieve();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        public void recieve() throws IOException {
            while(true){
                if(inFromServer.readLine().equals("Welcome to the NP chatroom! Please type your name and press enter...")) {
                    System.out.println("Welcome to the NP chatroom! Please type your name and press enter...");
                    outToServer.writeBytes(s.nextLine());
                }
                else if(inFromServer.readLine().equals("Goodbye")) {

                    System.out.println("Goodbye");
                    break;
                }
                else {
                    System.out.println(inFromServer.readLine());
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 9999);

        ClientThread reciever = new ClientThread(socket);
        Thread cthread = new Thread(reciever);
        cthread.start();

        outToServer = new DataOutputStream(socket.getOutputStream());
        s = new Scanner(System.in);

        while(!socket.isClosed()) {
            if(s.nextLine().equals("quit")) {
                outToServer.writeBytes("quit");
                break;
            }
            else {
                outToServer.writeBytes(s.nextLine());
            }
        }


    }
}
