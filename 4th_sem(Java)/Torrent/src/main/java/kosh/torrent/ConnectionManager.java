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
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel channel = serverSocketChannel.accept();
            channel.configureBlocking(false);
            Socket client = channel.socket();
            if (!messagesManager.checkHS(channel)) {
                channel.close();
                client.close();
                System.out.println("info hashes are different, closed connection with " + client.getRemoteSocketAddress());
            }
            System.out.println("Connect from " + client.getRemoteSocketAddress());
            messagesToPeer.put(client, new LinkedList<>());
            messagesToPeer.get(client).add(messagesManager.getMyHS());
            channel.register(selector, SelectionKey.OP_READ); //to read from remote socket
            channel.register(selector, SelectionKey.OP_WRITE); //to write to remote socket
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //прочитать сообщение от remotePeer и обработать, потом закинуть в какую-нибудь очедерь сообщений для этого remotePeer, чтобы потом отправить
    private void readFromPeer(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        Message respond = messagesManager.readMsg(client);
        if (respond == null) {
            try {
                System.out.println("Connection closed by " + client.socket().getRemoteSocketAddress());
                client.close();
                //мб как-то ещё обработать, закинуть его в неактивные коннекты, чтобы потом рестартнуть если что
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (messagesToPeer.containsKey(client.socket())) {
            messagesToPeer.get(client.socket()).add(respond);
        } else {
            Queue<Message> messages = new LinkedList<>();
            messages.add(respond);
            messagesToPeer.put(client.socket(), messages);
        }
    }

    //тут отправить сообщение из очереди
    private void sendToPeer(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        Queue<Message> messages = messagesToPeer.get(client.socket());
        if (!messages.isEmpty()) {
            Message msgToSend = messages.poll();
            ByteBuffer buffer = ByteBuffer.wrap(msgToSend.getMessage());
            System.out.println("Wrote to " + client.socket().getRemoteSocketAddress() + ", type of msg(int): " + msgToSend.getType());
            try {
                client.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        System.out.println("No more messages to " + client.socket().getRemoteSocketAddress());
    }

    //ключи -- сокеты или Peer???
    //если Peer, то как?
    //todo как и где юзать Peer
    private final Map<Socket, Queue<Message>> messagesToPeer = new HashMap<>();
    private Selector selector;
    private final MessagesManager messagesManager;
}
