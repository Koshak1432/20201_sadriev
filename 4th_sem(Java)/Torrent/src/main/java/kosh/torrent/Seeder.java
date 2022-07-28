package kosh.torrent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;

//server
public class Seeder {
    public Seeder(int port) throws IOException {
        Selector selector = Selector.open();
        server = ServerSocketChannel.open();
        server.configureBlocking(false);
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", port);
        server.bind(hostAddress);
        server.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int ready = selector.select();
            if (ready == 0) {
                continue;
            }

        }
    }

    public void cycle() throws IOException {
        int i = 0;
        while (true) {

        }
    }

    private final ArrayList<Socket> sockets = new ArrayList<>();
    private final ServerSocketChannel server;
}
