package kosh.torrent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

//server
public class Seeder {
    public Seeder(String hostname, int port) throws IOException {
        InetSocketAddress address = new InetSocketAddress(hostname, port);
        selector = Selector.open();

        server = ServerSocketChannel.open();
        server.bind(address);
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("initialized server");
    }

    public void start() throws IOException {
        System.out.println("entered start method");
        while (true) {
            int ready = selector.select();
            if (ready == 0) {
                continue;
            }
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    accept(key);
                }
                if (key.isReadable()) {
                    read(key);
                }
                if (key.isWritable()) {
                    write(key);
                }
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverSocketChannel.accept();
        channel.configureBlocking(false);
        Socket client = channel.socket();
        sockets.add(client);
        System.out.println("Connected to " + client.getRemoteSocketAddress());
        channel.register(selector, SelectionKey.OP_READ);
        channel.register(selector, SelectionKey.OP_WRITE);
    }

    private void read(SelectionKey key) throws IOException {
        int bytesToAllocate = 1024;
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(bytesToAllocate);
        int read = channel.read(buffer);

        if (read == - 1) {
            Socket client = channel.socket();
            System.out.println("Connection closed by " + client.getRemoteSocketAddress());
            channel.close();
            key.cancel();
            return;
        }

        byte[] data = new byte[read];
        System.arraycopy(buffer.array(), 0, data, 0, read);
        System.out.println("Got new message: " + new String(data));
    }

    //??????
    private void write(SelectionKey key) {

    }

    private final Set<Socket> sockets = new HashSet<>();
    private final ServerSocketChannel server;
    private final Selector selector;
}
