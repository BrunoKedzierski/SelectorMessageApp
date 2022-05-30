
import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Scanner;

public class MessageClient {
    private static  final Gson gson = new Gson();
    private String server;
    private int port;
    private SocketChannel channel;
    private final Charset charset  = Charset.forName("ISO-8859-2");
    private  final  int rozmiar_bufora = 1024;


    public MessageClient(String server, int port) {

        this.server = server;
        this.port = port;
    }

    public void connect(){

        try{
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(server,port));
            System.out.println("Connecting...");
            StringBuilder loadingBar = new StringBuilder(".");
            while(!channel.finishConnect()){
                System.out.println(loadingBar);
                loadingBar.append(".");
            }
        } catch (UnknownHostException unknownHostException){
            unknownHostException.printStackTrace();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        System.out.println("Connected");


    }

    public void fetchData() throws IOException {
        ByteBuffer inBuf = ByteBuffer.allocateDirect(rozmiar_bufora);
        CharBuffer cbuf = null;
        inBuf.clear();	// opróżnienie bufora wejściowego
        int readBytes = channel.read(inBuf); // czytanie nieblokujące
        // natychmiast zwraca liczbę
        // przeczytanych bajtów


        // System.out.println("readBytes =  " + readBytes);

        if(readBytes > 0) {		// dane dostępne w buforze
            //System.out.println("coś jest od serwera");
            inBuf.flip();	// przestawienie bufora

            // pobranie danych z bufora
            // ew. decyzje o tym czy mamy komplet danych - wtedy break
            // czy też mamy jeszcze coś do odebrania z serwera - kontynuacja
            cbuf = charset.decode(inBuf);

            String odSerwera = cbuf.toString();
            if(odSerwera.startsWith("[")) {
                Message[] messages = gson.fromJson(odSerwera.trim(), Message[].class);
                System.out.println("Klient: serwer właśnie odpisał ... " + Arrays.asList(messages));
            }
            else if(odSerwera.startsWith("{")){
                Message message = gson.fromJson(odSerwera.trim(), Message.class);
                System.out.println("Klient: serwer właśnie odpisał ... " + Arrays.asList(message));
            }

        }
        else
            System.out.println("Brak danych od serwera");


    }

    public void sendData(String message) throws IOException {

        CharBuffer cbuf = null;
        cbuf = CharBuffer.wrap(message + "\n");
        ByteBuffer outBuf = charset.encode(cbuf);
        channel.write(outBuf);

    }


    public static void main(String[] args) throws IOException {
        MessageClient client = new MessageClient("localhost",12555);
        client.connect();
        Scanner scanner = new Scanner(System.in);
        System.out.println("What to do next:\n" +
                "1 = fetch data\n" +
                "2 = write data\n" +
                "3 = quit");
        int flag = scanner.nextInt();
        scanner.nextLine();
        while (flag != 3){
            if (flag ==1)
                client.fetchData();
            else if(flag == 2) {
                System.out.println("Type your message:");
                client.sendData(scanner.nextLine());
            }
            System.out.println("What to do next:\n" +
                    "1 = fetch data\n" +
                    "2 = write data\n" +
                    "3 = quit");
            flag = scanner.nextInt();
            scanner.nextLine();
        }






    }
}