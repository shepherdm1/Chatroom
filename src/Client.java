import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static Scanner s;
    static DataOutputStream outToServer;

    public static void main(String[] args) throws Exception {
        @SuppressWarnings("resource")
		Socket connectionSocket = new Socket("localhost", 9999);

        DataOutputStream outToServer = new DataOutputStream(connectionSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        
        new Thread(new ClientRead(inFromServer)).start();
        new Thread(new ClientWrite(outToServer)).start();
    }
    
    private static class ClientRead implements Runnable {
        BufferedReader inFromServer;

        public ClientRead(BufferedReader inFromServer) {
            this.inFromServer = inFromServer;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println(inFromServer.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private static class ClientWrite implements Runnable {
        DataOutputStream outToServer;

        public ClientWrite(DataOutputStream outToServer) {
            this.outToServer = outToServer;
        }
        @Override
        public void run() {
            @SuppressWarnings("resource")
			Scanner s = new Scanner(System.in);
            while (true) {
                try {
                	String msg = s.nextLine();
                	outToServer.writeBytes(msg+"\r\n");
                    if (msg.equals("quit")) System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}

