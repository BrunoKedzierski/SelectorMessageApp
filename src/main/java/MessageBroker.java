
import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.*;


public class MessageBroker {
    private static final Gson gson = new Gson();
    private static final Charset charset  = Charset.forName("ISO-8859-2");
    private static final int BSIZE = 1024;
    private static List<Subscription> subscriptions = new ArrayList<>();
    private String hostName;
    private int portNum;
    private  ServerSocketChannel serverChannel;
    private Selector selector;
    private static HashMap<Message,List<String>> messagesAquired = new HashMap<>()
    {{
        put(new Message("Politics","Admin", "test1"),new ArrayList<>());
        put(new Message("Politics","Admin", "test2"),new ArrayList<>());
        put(new Message("Politics","Admin", "test3"),new ArrayList<>());
        put(new Message("Culture","Admin", "test1123"),new ArrayList<>());

    }};
    // Bufor bajtowy - do niego są wczytywane dane z kanału
    private ByteBuffer bbuf = ByteBuffer.allocate(BSIZE);

    // Tu będzie zlecenie do pezetworzenia
    private StringBuffer reqString = new StringBuffer();


    public MessageBroker(String hostName, int portNum) throws IOException {
        this.hostName = hostName;
        this.portNum = portNum;
    }


    public void listen() throws IOException {

        serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(hostName, portNum));
        serverChannel.configureBlocking(false);

        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Listening... ");
        while (true){

            selector.select();
            handleRequest(selector.selectedKeys());

        }

    }


    public void handleRequest(Set<SelectionKey> keys) throws IOException {
        Iterator<SelectionKey> iter = keys.iterator();
        while(iter.hasNext()) {

            // pobranie klucza
            SelectionKey key = iter.next();

            // musi być usunięty ze zbioru (nie ma autonatycznego usuwania)
            // w przeciwnym razie w kolejnym kroku pętli "obsłużony" klucz
            // dostalibyśmy do ponownej obsługi
            iter.remove();

            // Wykonanie operacji opisywanej przez klucz
            if (key.isAcceptable()) { // połaczenie klienta gotowe do akceptacji

                System.out.println("Serwer: ktoś się połączył ..., akceptuję go ... ");
                // Uzyskanie kanału do komunikacji z klientem
                // accept jest nieblokujące, bo już klient czeka
                SocketChannel cc = serverChannel.accept();

                // Kanał nieblokujący, bo będzie rejestrowany u selektora
                cc.configureBlocking(false);

                // rejestrujemy kanał komunikacji z klientem
                // do monitorowania przez ten sam selektor
                cc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, new Subscription());

                continue;
            }

            if (key.isReadable()) {  // któryś z kanałów gotowy do czytania

                System.out.println("is readable");
                // Uzyskanie kanału na którym czekają dane do odczytania
                SelectableChannel channelKey = key.channel();
                SocketChannel cc = (SocketChannel) channelKey;

                serviceRequest(cc, key);

                // obsługa zleceń klienta
                // ...
                continue;
            }
            if (key.isWritable()) {  // któryś z kanałów gotowy do pisania


                SocketChannel cc = (SocketChannel) key.channel();

                Subscription subscription = (Subscription) key.attachment();
                if (subscription == null)
                    break;
                Set<String> subscribedTopics = subscription.getSubscribedTo();
                List<Message> messagesToSend = new ArrayList<>();
                for (Map.Entry<Message,List<String> > e: messagesAquired.entrySet() ) {
                    if(subscribedTopics.contains(e.getKey().getTopic()) && !e.getValue().contains(subscription.getSubscriptionId()) ){

                        e.getValue().add(subscription.getSubscriptionId());
                        messagesToSend.add(e.getKey());
                    }
                }

                if(!messagesToSend.isEmpty())
                    cc.write(charset.encode(CharBuffer.wrap(gson.toJson(messagesToSend))));



                continue;
            }

        }


    }

    public static void main(String[] args) throws IOException, InterruptedException {
        MessageBroker messageBroker =  new MessageBroker("localhost",12455);
        messageBroker.listen();
    }






    private void serviceRequest(SocketChannel sc, SelectionKey key) {
        if (!sc.isOpen()) return; // jeżeli kanał zamknięty

        System.out.print("Serwer: czytam komunikat od klienta ... \n");
        // Odczytanie zlecenia
        reqString.setLength(0);
        bbuf.clear();

        try {
            readLoop:                    // Czytanie jest nieblokujące
            while (true) {               // kontynujemy je dopóki
                int n = sc.read(bbuf);   // nie natrafimy na koniec wiersza
                if (n > 0) {
                    bbuf.flip();
                    CharBuffer cbuf = charset.decode(bbuf);
                    while(cbuf.hasRemaining()) {
                        char c = cbuf.get();
                        //System.out.println(c);
                        if (c == '\r' || c == '\n') break readLoop;
                        else {
                            //System.out.println(c);
                            reqString.append(c);
                        }
                    }
                }
            }

            String cmd = reqString.toString();
            System.out.println(reqString);


            if (cmd.equals("Bye")) {           // koniec komunikacji

                sc.close();                      // - zamknięcie kanału
                sc.socket().close();			 // i gniazda

            }
            else if(cmd.equals("Passphrase:admin")) {
                Subscription subscription = (Subscription) key.attachment();
                subscription.authorized = true;

            }
            else if(((Subscription) key.attachment()).authorized){

                try {
                    Message message = (Message) gson.fromJson(cmd.trim(), Message.class);
                    messagesAquired.put(message,new ArrayList<>());
                } catch (Exception e){
                    e.printStackTrace();
                    System.out.println( );


                }


            }

            else {
                Subscription subscription = (Subscription) key.attachment();
                subscription.addSubscribedTo(cmd);
            }


        } catch (Exception exc) { // przerwane polączenie?
            exc.printStackTrace();
            try { sc.close();
                sc.socket().close();
            } catch (Exception e) {}
        }

    }

}
