package kosh.torrent;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

//server
public class ConnectionManager implements Runnable {
    public ConnectionManager(String hostname, int port, MetainfoFile meta, DownloadUploadManager DU) {
        this.meta = meta;
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
            int ready;
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
                    readFromPeer(key);
                }
                if (key.isWritable()) {
                    sendToPeer(key);
                }
            }
            if (connections.isEmpty()) {
                return;
            }
            while (!DU.getSuccessfulCheck().isEmpty()) {
                Integer idxHave = DU.getSuccessfulCheck().poll();
                assert idxHave != null;
                for (Peer peer : connections) {
                    messagesToPeer.get(peer).add(new ProtocolMessage(MessagesTypes.HAVE, Util.convertToNormalByteArr(idxHave)));
                }
            }
            while (!DU.getUnsuccessfulCheck().isEmpty()) {
                Integer idxToClear = DU.getUnsuccessfulCheck().poll();
                assert idxToClear != null;
                clearPiece(idxToClear);
            }
        }
    }

    private void clearPiece(Integer idxToClear) {
        iam.getPiecesHas().set(idxToClear, false);
        int blocksNumber = iam.getHasMap().get(idxToClear).size();
        iam.getHasMap().get(idxToClear).set(0, blocksNumber, false);
        int fromIdx = idxToClear * (int) meta.getPieceLen() / Constants.BLOCK_SIZE;
        int toIdx = idxToClear * (int) meta.getPieceLen() / Constants.BLOCK_SIZE + blocksNumber;
        iam.getRequestedBlocks().set(fromIdx, toIdx, false);
    }

    private Peer findPeer(SocketChannel remoteChannel) {
        for (Peer peer : connections) {
            if (peer.getChannel().equals(remoteChannel)) {
                return peer;
            }
        }
        return null; //never
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
    //принимаю подключение, чекаю HS, добавляю подключение в лист,
    // добавляю сообщения: свой HS и Bitfield в очередь для пира
    //регистрирую канал для записи и чтения
    private void accept(SelectionKey key) {
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel channel = serverSocketChannel.accept();
            channel.configureBlocking(false);
            Peer peer = new Peer(channel);
            Message myHS = new Handshake(meta.getInfoHash(), peer.getId());
            if (!peer.checkHS(myHS)) {
                peer.closeConnection();
                System.out.println("info hashes are different, closed connection with " + peer);
            }
            System.out.println("Connect from " + peer);
            peer.setChoked(false);
            connections.add(peer);
            messagesToPeer.put(peer, new LinkedList<>());
            messagesToPeer.get(peer).add(myHS);
            messagesToPeer.get(peer).add(new ProtocolMessage(MessagesTypes.UNCHOKE));
            channel.register(selector, SelectionKey.OP_READ); //to read from remote socket
            channel.register(selector, SelectionKey.OP_WRITE); //to write to remote socket
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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
        Peer peer = findPeer(channel);
        assert peer != null;
        Message peerMsg = peer.constructPeerMsg(peer.getChannel());
        if (peerMsg == null) {
            try {
                //или не совсем закрыто??
                System.out.println("Connection closed by " + peer);
                channel.close();
                //мб как-то ещё обработать, закинуть его в неактивные коннекты, чтобы потом рестартнуть если что
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        handleMsg(peerMsg, peer);
    }

    //тут отправить сообщение из очереди
    //ищу пира, беру сообщение из очереди для него, отправляю
    //подумать, через кого отправлять
    private void sendToPeer(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        Peer peer = findPeer(channel);
        assert peer != null;
        Queue<Message> messages = messagesToPeer.get(peer);
        synchronized (DU.getOutgoingMsg().get(peer)) {
            messages.addAll(DU.getOutgoingMsg().get(peer));
        }
        if (!messages.isEmpty()) {
            Message msgToSend = messages.poll();
            peer.sendMsg(msgToSend);
            System.out.println("Wrote to " + peer + ", type of msg(int): " + msgToSend.getType());
            return;
        }
        System.out.println("No more messages to " + peer);
    }

    //возомжно, нормал инт и дурацкая конвертация в байтовый массив -- бред, и следует просто буффером класть туда инт
    private void handleMsg(Message msg, Peer peer) {
        switch (msg.getType()) {
            case MessagesTypes.CHOKE -> peer.setChoked(true);
            case MessagesTypes.UNCHOKE -> {
                peer.setChoked(false);
                //мб ещё припилить метод, который будет говорить, заинтересован ли я в пире, а то мб у него нет ничего
                messagesToPeer.get(peer).add(new ProtocolMessage(MessagesTypes.INTERESTED));
            }
            case MessagesTypes.INTERESTED -> {
                peer.setInterested(true);
                messagesToPeer.get(peer).add(new ProtocolMessage(MessagesTypes.BITFIELD, iam.getPiecesHas().toByteArray()));
            }
            case MessagesTypes.NOT_INTERESTED -> peer.setInterested(false);
            case MessagesTypes.HAVE -> {
                peer.setPiece(Util.convertToNormalInt(msg.getPayload()), true);
                requestPiece(peer, iam);
            }
            case MessagesTypes.BITFIELD -> {
                peer.setPiecesHas(msg.getPayload());
                peer.initBlocks(meta.getPieceLen(), meta.getFileLen());
                requestPiece(peer, iam);
            }
            //means remote peer is requesting a piece
            case MessagesTypes.REQUEST -> {
                byte[] payload = msg.getPayload();
                assert payload.length == 12;
                int idx = Util.convertToNormalInt(Arrays.copyOfRange(payload, 0, 4));
                int begin = Util.convertToNormalInt(Arrays.copyOfRange(payload, 4, 8));
                int len = Util.convertToNormalInt(Arrays.copyOfRange(payload, 8, payload.length));
                //добавить в очерень тасок DU
                DU.addTask(new Task(TaskType.SEND, idx, begin, len, peer));
            }
            //means remote peer send us a block of data
            case MessagesTypes.PIECE -> {
                byte[] payload = msg.getPayload();
                int idx = Util.convertToNormalInt(Arrays.copyOfRange(payload, 0, 4));
                int begin = Util.convertToNormalInt(Arrays.copyOfRange(payload, 4, 8));
                byte[] blockData = Arrays.copyOfRange(payload, 8, payload.length);
                DU.addTask(new Task(TaskType.SAVE, idx, begin, blockData));
                int blockIdx = begin / Constants.BLOCK_SIZE;
                iam.getHasMap().get(idx).set(blockIdx);
                requestPiece(peer, iam);
                BitSet bs = iam.getHasMap().get(idx);
                if (bs.cardinality() == bs.size()) {
                    int pieceLen = Constants.BLOCK_SIZE * (bs.size() - 1) + peer.getLastBlockSize();
                    DU.addTask(new Task(TaskType.CHECK_HASH, idx, pieceLen));
                }
            }
            //means remote peer want us to cancel last request from him
            case MessagesTypes.CANCEL -> {
                //todo
                //как отменять? где очередь сообщений должна быть?
                //нужен интерфейс, чтобы ещё и have всем отправлять
            }
        }
    }

    //мб в пира засунуть или в mm
    private int getBlockIdxToRequest(Peer to, Peer from) {
        BitSet piecesToRequest = (BitSet) from.getPiecesHas().clone();
        piecesToRequest.flip(0, piecesToRequest.size());
        piecesToRequest.and(to.getPiecesHas());
        //в piecesToRequest те куски, которых нет у меня, есть у пира
        if (piecesToRequest.cardinality() == 0) {
            return - 1;
        }
        Random random = new Random();
        int pieceIdx = piecesToRequest.nextSetBit(random.nextInt(piecesToRequest.size()));
        if (pieceIdx == -1) {
            pieceIdx = piecesToRequest.nextSetBit(0);
        }

        BitSet blocksToRequest = (BitSet) from.getHasMap().get(pieceIdx).clone(); //кокие у меня есть
        blocksToRequest.or(from.getRequestedBlocks()); // какие есть у меня и запрошенные
        blocksToRequest.flip(0, blocksToRequest.size()); //каких у меня нет и не запрошенные
        blocksToRequest.and(to.getHasMap().get(pieceIdx)); //нет у меня, не запрошенные и есть у пира
        if (blocksToRequest.cardinality() == 0) {
            return -1;
        }

        int blockIdx = blocksToRequest.nextSetBit(random.nextInt(blocksToRequest.size()));
        if (blockIdx == -1) {
            blockIdx = blocksToRequest.nextSetBit(0);
        }
        return blockIdx;
    }
    private void requestPiece(Peer to, Peer from) {
        int blockToRequest = getBlockIdxToRequest(to, from);
        if (blockToRequest == -1) {
            System.out.println("Don't have blocks to send request to " + to);
            return;
        }
        from.getRequestedBlocks().set(blockToRequest);
        messagesToPeer.get(to).add(new ProtocolMessage(MessagesTypes.REQUEST, Util.convertToNormalByteArr(blockToRequest)));
    }


    private final Map<Peer, Queue<Message>> messagesToPeer = new HashMap<>();
    private final List<Peer> connections = new ArrayList<>();
    private final Peer iam = new Peer(null);
    private Selector selector;
    //мб куда-нибудь перенести
    private final MetainfoFile meta;
    private final DownloadUploadManager DU;
}
