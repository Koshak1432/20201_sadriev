package kosh.torrent;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.*;

//client
public class Peer {
    public Peer() {
        this.id = Util.generateId();
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }


    public int getDownloaded() {
        return downloaded;
    }

    public int getUploaded() {
        return uploaded;
    }

    public BitSet getHas() {
        return has;
    }

    public void setHas(byte[] bitfield) {
        boolean[] bits = Util.convertToBits(bitfield);
        for (int i = 0; i < bits.length; ++i) {
            has.set(i, bits[i]);
        }
    }

    public Map<Integer, List<Block>> getHasMap() {
        return hasMap;
    }

    public BitSet getRequested() {
        return requested;
    }

    public void setPiece(int pieceIdx, boolean has) {
        this.has.set(pieceIdx, has);
    }

    public void setDownloaded(int downloaded) {
        this.downloaded = downloaded;
    }

    public void setUploaded(int uploaded) {
        this.uploaded = uploaded;
    }

    public int getIdxLastRequested() {
        return idxLastRequested;
    }

    private byte[] id;
    private int downloaded = 0;
    private int uploaded = 0;

    private int idxLastRequested = 0;

    //схема:
    //либо этот класс переделать, либо новый, который будет представлять соединение п1-п2
    //в этом классе будет лежать битфилд доступных у того, к кому подключаемся

    //а тут -- те, что есть у пира вообще, channel будет у коннекшена, статусные туда можно скопировать, но тут оставить(наверное???)
    //надо ещё добавить битсет с запрошенными

    //итого: Peer -- представление пира собсна, статусные поля 1-1 с другим пиром, поэтому лучше в коннекшн убрать
    //взаимодействия в cm и mm будут не с пирами, а с коннекшенами, а коннекшн будет пересылать готовые куски пиру?
    //либо mm будет обрабатывать сообщение от пира, и присылать в cm ответ, типа чокнуть пира или добавить кусок
    //тогда можно ведь всё это делать в mm самом, то есть пир будет хранится в mm?
    //пир будет в cm(не совсем, строчка вверх)
    private final BitSet has = new BitSet();
    private final BitSet requested = new BitSet();
    private final Map<Integer, List<Block>> hasMap = new HashMap<>();

}
