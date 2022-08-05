package kosh.torrent;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

//класс обработчик, который будет получать запросы(Message) и отправлять свои сообщения типа Message(куски и проч), а также сверять HS сервака и пира
//с id придумать что-то, мб всегда валидным сделать, ибо трекера нет, и что ожидать хз
public class MessagesManager {

    public MessagesManager(Message myHs) {
        this.myHS = myHs;
    }

    public Message getMyHS() {
        return myHS;
    }

    public boolean checkHS(Peer peer) {
        boolean equals = false;
        Message remotePeerHS = getRemoteHS(peer);
        //мб сделать мапу для каждого пира с его хэндшейком? зачем?
        equals = checkHS(remotePeerHS, this.myHS);
        return equals;
    }

    //мб оставить только этот метод, в котором будет проверятся, был ли хэндшейк с этим каналом, если не было то чек хэши
    //если было, то читаем len и id сообщения, определяем тип

    //todo подумать над этим
    //вопрос как теперь пиров отмечать, choke, unchoke, interested, notInterested и прочее
    //для этого есть класс Peer
    //здесь или в cm завести мапу, где ключи -- Peer, а проверять на наличие -- пробегом всей мапы в поисках remoteChannel?
    //
    public Message readMsg(Peer peer) {
        Message peerMsg = constructPeerMsg(peer);
        //handle msg(construct response)
        Message response = handleMsg(peerMsg);


    }

    //нужно завести мапу? кусков(мб блоков), которые доступны пирам и мапу блоков, которые уже запросил
    private Message handleMsg(Message msg) {
        switch (msg.getType()) {
            case MessagesTypes.CHOKE -> //choke my peer
            case MessagesTypes.UNCHOKE -> //unchoke
            case MessagesTypes.INTERESTED -> //set interested
            case MessagesTypes.NOT_INTERESTED -> //set not interested
            case MessagesTypes.HAVE -> //this means remote peer has a full piece
            case MessagesTypes.BITFIELD -> //means remote peer send us a bitfield
            case MessagesTypes.REQUEST -> //means remote peer is requesting a piece
            case MessagesTypes.PIECE -> //means remote peer send us a block of data
            case MessagesTypes.CANCEL -> //means remote peer want us to cancel last request from him
        }
    }

    private Message constructPeerMsg(Peer peer) {
        int bytesToAllocate = 1024;
        ByteBuffer buffer = ByteBuffer.allocate(bytesToAllocate);
        int read = -1;
        try {
            read = peer.getChannel().read(buffer);
            if (read == -1) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] length = new byte[4];
        byte[] id = new byte[1];
        byte[] payload = new byte[read - id.length - length.length];
        int len = Util.convertToInt(length);
        int idInt;
        buffer.get(0, length, 0, length.length);
        if (read > length.length) {
            buffer.get(length.length, id, 0, id.length);
            buffer.get(length.length + id.length, payload, 0, payload.length);
            idInt = Util.convertToInt(id);
        } else {
            idInt = MessagesTypes.KEEP_ALIVE;
        }
        return (payload.length > 0) ? new ProtocolMessage(idInt, payload) :
                new ProtocolMessage(idInt);
    }

    private Message getRemoteHS(Peer peer) {
        int infoHashIdx = 28;
        int peerIdIdx = infoHashIdx + 20;
        //прочитать и сохранить все данные?
        byte[] infoHash = new byte[20];
        byte[] peerId = new byte[20];
        ByteBuffer byteBuffer = ByteBuffer.allocate(68);
        try {
            peer.getChannel().read(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byteBuffer.get(infoHashIdx, infoHash, 0, infoHash.length);
        byteBuffer.get(peerIdIdx, peerId, 0, peerId.length);
        //вот тут что-то с id придумать надо бы
        peer.setId(peerId);
        return new Handshake(infoHash, peerId);
    }

    private boolean checkHS(Message remoteHS, Message myHS) {
        return Arrays.equals(remoteHS.getMessage(), myHS.getMessage());
    }
    

    private final Message myHS;
}
