package kosh.torrent;

import java.io.IOException;
import java.net.Socket;

//client
public class Peer {
    public Peer(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
    }

    private final Socket socket;
}
