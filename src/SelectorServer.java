import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple server implementation using Java NIO's Selector for handling multiple clients.
 * This server listens for incoming connections on a specified port and supports reading and writing data from/to clients.
 */
public class SelectorServer {
    /**
     * The main method of the SelectorServer class.
     * It initializes the server, binds it to a specified port, and starts listening for incoming connections.
     */
    public static void main(String[] args) {
        try(Selector selector = Selector.open();
        ServerSocketChannel server =ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(1234));
            server.configureBlocking(false);
            server.register(selector,SelectionKey.OP_ACCEPT);
            ByteBuffer buffer= ByteBuffer.allocate(1024);

            while (true){
                Thread.sleep(1000);
                selector.selectNow();
                Set<SelectionKey> events = selector.selectedKeys();
                Iterator<SelectionKey> selectionKeyIterator =events.iterator();
                System.out.println("\nEvents");
                while (selectionKeyIterator.hasNext()){
                    SelectionKey selectionKey = selectionKeyIterator.next();
                    if (selectionKey.isAcceptable()){
                        System.out.println("Incoming Connection");
                        SocketChannel serv = server.accept();
                        serv.configureBlocking(false);
                        serv.register(selector,SelectionKey.OP_READ);
                    }
                    else if(selectionKey.isReadable()){
                        System.out.println("Reading Event");
                        SocketChannel readingChannel = (SocketChannel) selectionKey.channel();

                        buffer.clear();
                       int bytes = readingChannel.read(buffer);
                       buffer.flip();
                       String  s = StandardCharsets.UTF_8.decode(buffer).toString();
                        System.out.println("read "+bytes+" bytes from client "+s);
                        if (s.equalsIgnoreCase("quit")){
                            readingChannel.close();
                        }
                        else readingChannel.register(selector,SelectionKey.OP_WRITE);
                    }
                    else if (selectionKey.isWritable()){
                        System.out.println("Writing Event");
                        SocketChannel writingChannel = (SocketChannel) selectionKey.channel();

                        buffer.clear();
                        buffer.put(StandardCharsets.UTF_8.encode("Hello client"));
                        buffer.flip();
                        int bytes = writingChannel.write(buffer);
                        System.out.println("sent "+bytes+" bytes to client");
                        writingChannel.register(selector,SelectionKey.OP_READ);
                    }
                    selectionKeyIterator.remove();
                }
            }
        }catch (IOException|InterruptedException e){
            System.out.println("connection end");
        }
    }
}
