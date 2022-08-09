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
        //handle msg(construct response)
//        Message response = handleMsg(peerMsg, connection);
//        return response;
    }



    public boolean checkHS(SocketChannel remoteChannel, Message myHS) {
        Message remotePeerHS = getRemoteHS(remoteChannel);
        return checkHS(remotePeerHS, myHS);
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
