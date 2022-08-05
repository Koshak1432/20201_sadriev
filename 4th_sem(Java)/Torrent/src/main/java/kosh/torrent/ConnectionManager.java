package kosh.torrent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

//server
public class ConnectionManager implements Runnable {
    public ConnectionManager(String hostname, int port, Message myHS) {
        messagesManager = new MessagesManager(myHS);
        InetSocketAddress address = new InetSocketAddress(hostname, port);
        try {
            selector = Selector.open();
            ServerSocketChannel server = ServerSocketChannel.open();
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
                if (key.isReadable()) {
                    readFromPeer(key); //readManager
                }
                if (key.isWritable()) {
                    sendToPeer(key);
                }
            }
        }
    }

    //принимаю подключение, завожу очередь для пира, куда сразу же кладу HS сервера для отправки этому пиру, тот уже смотрит его, и если расходятся, то отключается
    //либо тут сверяю HS(через MessagesManager), если расходятся, то сервак отклоняет соединение
    //если всё ок, то кидаю в очередь свой HS для отправки в будущем и регистрирую канал для чтения и записи

    //оповещать messageManager-а о новом подключении, передавая сокет, чтобы тот уже хранил мапу очередей
    //когда отправить захочу, то смотрю на очередь соответствующего сокета и отсылаю сообщение
    //или
    //в этом классе будет мапа сокет-очередь строк с названиями команд    бред
    //пусть тут будет мапа очередей, засовывать туда буду то, что вернёт команда из messageManager
    private void accept(SelectionKey key) {
        try {
            //завести ещё и свой Peer и его обновлять? ага 100 проц нужно
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel channel = serverSocketChannel.accept();
            channel.configureBlocking(false);
            Peer peer = new Peer(channel);
            if (!messagesManager.checkHS(peer)) {
                closeConnection(channel);
                System.out.println("info hashes are different, closed connection with " + peer);
            }
            System.out.println("Connect from " + peer);
            peers.add(peer);
            messagesToPeer.put(peer, new LinkedList<>());
            messagesToPeer.get(peer).add(messagesManager.getMyHS());
            channel.register(selector, SelectionKey.OP_READ); //to read from remote socket
            channel.register(selector, SelectionKey.OP_WRITE); //to write to remote socket
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Peer findPeer(SocketChannel peerChannel) {
        for (Peer peer : peers) {
            if (peer.getChannel().equals(peerChannel)) {
                return peer;
            }
        }
        return null;
    }

    //прочитать сообщение от remotePeer и обработать, потом закинуть в какую-нибудь очедерь сообщений для этого remotePeer, чтобы потом отправить
    private void readFromPeer(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        Peer peer = findPeer(channel);
        if (peer == null) {
            closeConnection(channel);
        }
        Message response = messagesManager.readMsg(peer);
        if (response == null) {
            try {
                System.out.println("Connection closed by " + peer);
                channel.close();
                //мб как-то ещё обработать, закинуть его в неактивные коннекты, чтобы потом рестартнуть если что
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (messagesToPeer.containsKey(peer)) {
            messagesToPeer.get(peer).add(response);
        } else {
            Queue<Message> messages = new LinkedList<>();
            messages.add(response);
            messagesToPeer.put(peer, messages);
        }
    }

    private void closeConnection(SocketChannel channel) {
        try {
            channel.socket().close();
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //тут отправить сообщение из очереди
    private void sendToPeer(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        Peer peer = findPeer(channel);
        if (peer == null) {
            closeConnection(channel);
        }
        Queue<Message> messages = messagesToPeer.get(peer);
        if (!messages.isEmpty()) {
            Message msgToSend = messages.poll();
            ByteBuffer buffer = ByteBuffer.wrap(msgToSend.getMessage());
            System.out.println("Wrote to " + peer + ", type of msg(int): " + msgToSend.getType());
            try {
                channel.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        System.out.println("No more messages to " + peer);
    }

    private final Map<Peer, Queue<Message>> messagesToPeer = new HashMap<>();

    //он не должен тут лежать, потом перенести куда-нибудь, отдельный класс мб
    private final List<Peer> peers = new ArrayList<>();
    private Selector selector;
    private final MessagesManager messagesManager;
}
