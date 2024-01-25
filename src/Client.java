import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * A simple asynchronous client implementation using Java NIO's AsynchronousSocketChannel.
 * This client connects to a server on a specified host and port, sends messages, and receives responses from the server.
 */
public class Client {
    /**
     * The main method of the Client class.
     * It initializes the client, establishes a connection to the server, sends messages, and receives responses.
     */
    public static void main(String[] args) {
        try(AsynchronousSocketChannel client =AsynchronousSocketChannel.open()) {
            // Connect to the server
            Future<Void> check = client.connect(new InetSocketAddress(InetAddress.getLocalHost(),1234));
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            check.get(); // Wait for the connection to be established

            // Send the first message to the server
            buffer.put(StandardCharsets.UTF_8.encode("first message"));
            buffer.flip();
            Future<Integer> futureRes = client.write(buffer); // Send the message asynchronously
            int bytes = futureRes.get(); // Wait for the write operation to complete
            System.out.println("sent  "+bytes+" to server.");

            // Receive a response from the server
            buffer.clear();
            futureRes = client.read(buffer); // Receive the response asynchronously
            bytes = futureRes.get(); // Wait for the read operation to complete
            buffer.flip();
            String s = StandardCharsets.UTF_8.decode(buffer).toString();
            System.out.println("read "+bytes+" from server "+s);

            // Send the second message to the server
            buffer.clear();
            buffer.put(StandardCharsets.UTF_8.encode("second message"));
            buffer.flip();
            futureRes =client.write(buffer); // Send the message asynchronously
            bytes =futureRes.get(); // Wait for the write operation to complete
            System.out.println("sent "+bytes+" to server");

        }catch (IOException| ExecutionException|InterruptedException e){
            e.printStackTrace();
        }
    }
}
