package kosh.torrent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

//server
public class ConnectionManager implements Runnable {
    public ConnectionManager(String hostname, int port, Message myHS) {
        this.myHS = myHS;
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

    //todo мб вообще не принимать тут, а изначально подключиться к пирам, список которых в аргументах прилетает
    private void accept(SelectionKey key) {
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel channel = serverSocketChannel.accept();
            channel.configureBlocking(false);
            PeerConnection connection = new PeerConnection(channel);
            if (!connection.checkHS(myHS)) {
                connection.closeConnection();
                System.out.println("info hashes are different, closed connection with " + connection);
            }
            System.out.println("Connect from " + connection);
            connections.add(connection);
            messagesToPeer.put(connection, new LinkedList<>());
            messagesToPeer.get(connection).add(myHS);
            channel.register(selector, SelectionKey.OP_READ); //to read from remote socket
            channel.register(selector, SelectionKey.OP_WRITE); //to write to remote socket
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PeerConnection findConnection(SocketChannel remoteChannel) {
        for (PeerConnection connection : connections) {
            if (connection.getChannel().equals(remoteChannel)) {
                return connection;
            }
        }
        return null; //never
    }

    //прочитать сообщение от remotePeer и обработать, потом закинуть в какую-нибудь очедерь сообщений для этого remotePeer, чтобы потом отправить
    private void readFromPeer(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        PeerConnection connection = findConnection(channel);
        assert connection != null;
        Message response = connection.readMsg();
        if (response == null) {
            try {
                System.out.println("Connection closed by " + connection);
                channel.close();
                //мб как-то ещё обработать, закинуть его в неактивные коннекты, чтобы потом рестартнуть если что
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (messagesToPeer.containsKey(connection)) {
            messagesToPeer.get(connection).add(response);
        } else {
            Queue<Message> messages = new LinkedList<>();
            messages.add(response);
            messagesToPeer.put(connection, messages);
        }
    }


    //тут отправить сообщение из очереди
    private void sendToPeer(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        PeerConnection connection = findConnection(channel);
        Queue<Message> messages = messagesToPeer.get(connection);
        if (!messages.isEmpty()) {
            Message msgToSend = messages.poll();
            assert connection != null;
            connection.sendMsg(msgToSend);
            System.out.println("Wrote to " + connection + ", type of msg(int): " + msgToSend.getType());
            return;
        }
        System.out.println("No more messages to " + connection);
    }

    private final Map<PeerConnection, Queue<Message>> messagesToPeer = new HashMap<>();
    private final List<PeerConnection> connections = new ArrayList<>();
    private final Peer iam = new Peer();
    private Selector selector;
    //мб куда-нибудь перенести
    private final Message myHS;
}
