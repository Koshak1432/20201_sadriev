package kosh.torrent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

//server
public class ConnectionManager implements Runnable {
    public ConnectionManager(String hostname, int port) {
        InetSocketAddress address = new InetSocketAddress(hostname, port);
        try {
            selector = Selector.open();
            server = ServerSocketChannel.open();
            server.bind(address);
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT);
        }
        catch (IOException e) {
            System.err.println("can't init connection manager");
            e.printStackTrace();
            return;
        }
        System.out.println("initialized connection manager");
    }

    @Override
    public void run() {
        System.out.println("connection manager is running");
        while (true) {
            int ready = 0;
            try {
                ready = selector.select();
            }
            catch (IOException e) {
                System.err.println("Can't select");
                e.printStackTrace();
                return;
            }
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
//                if (key.isReadable()) {
//                    read(key); //readManager
//                }
            }
        }
    }

    private void accept(SelectionKey key) {
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel channel = serverSocketChannel.accept();
            channel.configureBlocking(false);
            Socket client = channel.socket();
            sockets.add(client);
            System.out.println("Connect from " + client.getRemoteSocketAddress());
            channel.register(selector, SelectionKey.OP_READ); //to read from remote socket
            channel.register(selector, SelectionKey.OP_WRITE); //to write to remote socket
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(SelectionKey key) throws IOException {
        int bytesToAllocate = 1024;
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(bytesToAllocate);
        int read = client.read(buffer);
        if (read == -1) {
            System.out.println("Connection closed by " + client.socket().getRemoteSocketAddress());
            client.close();
            key.cancel();
            return;
        }

        byte[] data = new byte[read];
        System.arraycopy(buffer.array(), 0, data, 0, read);
        System.out.println("Got new message: " + new String(data));
    }

    private final Set<Socket> sockets = new HashSet<>();
    private final Set<Socket> handshakedSockets = new HashSet<>();
    private ServerSocketChannel server;
    private Selector selector;
}
