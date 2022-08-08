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
    public ConnectionManager(String hostname, int port, Message myHS, DownloadUploadManager DU) {
        this.myHS = myHS;
        this.DU = DU;
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
            messagesToPeer.get(connection).add(new ProtocolMessage(MessagesTypes.BITFIELD, iam.getHas()));
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

    //прочитать инфу от пира, вернуть сообщение с этой инфой, тут в свиче посмотреть на тип сообщения, если что-то отметить надо, то отметить тут
    //если реквест, пис или кансел, то кинуть таску в очередь DU
    //можно будет паттерн наблюдателя имплементнуть, DU подпишется на cm, а cm будет оповещать своих подписчиков, с методом, который
    //будет добавлять в очередь таску или что-то такое
    //как бродкастить have? кто-то должен смотреть на количество блоков скачанных и чекать хэши
    //можно завести мапу, где ключи -- индекс куска, а значения -- лист блоков | где?
    //где выбирать какие куски запрашивать?
    private void readFromPeer(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        PeerConnection connection = findConnection(channel);
        assert connection != null;
        //что если прочитать то, что прислал пир, обернуть это в сообщение, сюда он и вернётся, а потом оповестить DU манагера
        //который подпишется на cm, туда так же подавать коннекшн, чтобы кусок отправлять на ревкест
        //и вообще все сообщения будет обрабатывать DU манагер, для чоканья и интереса передавать конекшн?
        //
        //создаётся DU манагер в одном потоке, создаётся cm в другом потоке и ему передаётся ссылка на DU
        //потом брать из мапы результатов DU сообщения уже в байтовой форме и на isWritable() отправлять его
        Message peerMsg = connection.readMsg();

//        Message response = connection.readMsg();
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

    //если убрать всё из конекшна в пира, и завести мапу socketChannel-Peer????
    private void handleMsg(ProtocolMessage msg, PeerConnection connection) {
        switch (msg.getType()) {
            case MessagesTypes.CHOKE -> connection.setPeerChoking(true);
            case MessagesTypes.UNCHOKE -> connection.setPeerChoking(false);
            case MessagesTypes.INTERESTED -> connection.setPeerInterested(true);
            case MessagesTypes.NOT_INTERESTED -> connection.setPeerInterested(false);
            case MessagesTypes.HAVE -> {
                int pieceIdx = Util.convertToNormalInt(msg.getPayload());
                connection.setPiece(pieceIdx, true);
            }
            case MessagesTypes.BITFIELD -> {
                connection.setPeerHas(msg.getPayload());
            }
            //means remote peer is requesting a piece
            case MessagesTypes.REQUEST -> {
                byte[] payload = msg.getPayload();
                assert payload.length == 12;
                int idx = Util.convertToNormalInt(Arrays.copyOfRange(payload, 0, 4));
                int begin = Util.convertToNormalInt(Arrays.copyOfRange(payload, 4, 8));
                int len = Util.convertToNormalInt(Arrays.copyOfRange(payload, 8, payload.length));
                //добавить в очерень тасок DU
                Queue<Task> q = DU.getTasks();
                if (q.isEmpty()) {
                    q = new LinkedList<>();
                }
                q.add(new Task(TaskType.SEND, idx, begin, len, connection));
            }
            //means remote peer send us a block of data
            case MessagesTypes.PIECE -> {
                byte[] payload = msg.getPayload();
                int idx = Util.convertToNormalInt(Arrays.copyOfRange(payload, 0, 4));
                int begin = Util.convertToNormalInt(Arrays.copyOfRange(payload, 4, 8));
                byte[] blockData = Arrays.copyOfRange(payload, 8, payload.length);
                Queue<Task> q = DU.getTasks();
                if (q.isEmpty()) {
                    q = new LinkedList<>();
                }
                q.add(new Task(TaskType.SAVE, idx, begin, blockData));
                List<Block> piece;
                if (iam.getHasMap().containsKey(idx)) {
                    piece = iam.getHasMap().get(idx);
                } else {
                     piece = new ArrayList<>(Constants.PIECE_LENGTH / Constants.BLOCK_SIZE);
                }
                piece.set(begin / Constants.BLOCK_SIZE, new Block(idx, begin, blockData.length, blockData)); // todo трабл с блоком в том, что begin -- оффсет внутри блока, переделать Block, и креатора, чтобы заходил не глобавльный оффсет, а в куске
                //если полный кусок, то чек хэшей
            }
            //means remote peer want us to cancel last request from him
            case MessagesTypes.CANCEL -> {
                //todo
                iam.getRequested().clear(iam.getIdxLastRequested());
                //notifyCancel
                //как отменять? где очередь сообщений должна быть?
                //сделать этот класс паблишером, а конекшн подписчиком?
                //или cm подписчик, и ему говорить пришло сообщение отменить запрос на кусок с индексом idx у такого-то коннекшна
                //отправить кусок такой-то такому-то пиру
                //сохранить блок такой-то
                //нужен интерфейс, чтобы ещё и have всем отправлять
            }
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
    private final DownloadUploadManager DU;
}
