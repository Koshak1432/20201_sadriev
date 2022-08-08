package kosh.torrent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

//класс обработчик, который будет получать запросы(Message) и отправлять свои сообщения типа Message(куски и проч), а также сверять HS сервака и пира
//с id придумать что-то, мб всегда валидным сделать, ибо трекера нет, и что ожидать хз
public class MessagesManager {

    public MessagesManager() {}

    //мб оставить только этот метод, в котором будет проверятся, был ли хэндшейк с этим каналом, если не было то чек хэши
    //если было, то читаем len и id сообщения, определяем тип

    //todo подумать над этим
    //вопрос как теперь пиров отмечать, choke, unchoke, interested, notInterested и прочее
    //для этого есть класс Peer
    //здесь или в cm завести мапу, где ключи -- Peer, а проверять на наличие -- пробегом всей мапы в поисках remoteChannel?
    //
    public Message readMsg(PeerConnection connection) {
        return constructPeerMsg(connection.getChannel());

        //handle msg(construct response)
//        Message response = handleMsg(peerMsg, connection);
//        return response;
    }

    private Message handleMsg(ProtocolMessage msg, PeerConnection connection, Peer iam) {

    }

    private Message constructPeerMsg(SocketChannel channel) {
        int bytesToAllocate = 1024;
        ByteBuffer buffer = ByteBuffer.allocate(bytesToAllocate);
        int read = -1;
        try {
            read = channel.read(buffer);
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

    public boolean checkHS(SocketChannel remoteChannel, Message myHS) {
        boolean equals = false;
        Message remotePeerHS = getRemoteHS(remoteChannel);
        equals = checkHS(remotePeerHS, myHS);
        return equals;
    }

    private Message getRemoteHS(SocketChannel remoteChannel) {
        int infoHashIdx = 28;
        int peerIdIdx = infoHashIdx + 20;
        //прочитать и сохранить все данные?
        byte[] infoHash = new byte[20];
        byte[] peerId = new byte[20];
        ByteBuffer byteBuffer = ByteBuffer.allocate(68);
        try {
            remoteChannel.read(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byteBuffer.get(infoHashIdx, infoHash, 0, infoHash.length);
        byteBuffer.get(peerIdIdx, peerId, 0, peerId.length);
        //вот тут что-то с id придумать надо бы
//       todo peer.setId(peerId);
        return new Handshake(infoHash, peerId);
    }

    private boolean checkHS(Message HS1, Message HS2) {
        return Arrays.equals(HS1.getMessage(), HS2.getMessage());
    }
}
