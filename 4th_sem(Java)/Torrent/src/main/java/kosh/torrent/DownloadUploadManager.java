package kosh.torrent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class DownloadUploadManager implements Runnable {
    public DownloadUploadManager(MetainfoFile meta, boolean leecher) {
        this.meta = meta;
        String outputFileName = leecher ? meta.getName() + "test" : meta.getName();
        try {
            output = new RandomAccessFile(outputFileName, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (!tasks.isEmpty()) {
                doTask(tasks.poll());
            }
        }

        try {
            output.close();
        }
        catch (IOException e) {
            System.err.println("Couldn't close" + output);
            e.printStackTrace();
        }
        System.out.println("DU finished");
    }

    public void doTask(Task task) {
        switch (task.getType()) {
            case SAVE -> saveBlock(task);
            case SEND -> sendBlock(task);
            case CHECK_HASH -> checkHash(task);
            case STOP -> stop();
        }
    }

    public Map<Peer, Queue<Message>> getOutgoingMsg() {
        return outgoingMsg;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    private void saveBlock(Task task) {
        int idx = task.getBlock().getIdx();
        int begin = task.getBlock().getBegin();
        byte[] block = task.getBlock().getData();
        try {
            output.seek( meta.getPieceLen() * idx + begin);
            output.write(block);
        } catch (IOException e) {
            System.err.println("Caught an exception while saving block");
            e.printStackTrace();
        }
    }

    private void addToOutgoingMessages(Peer peer, Message msg) {
        synchronized (outgoingMsg) {
            if (outgoingMsg.containsKey(peer)) {
                outgoingMsg.get(peer).add(msg);
                return;
            }
            Queue<Message> queue = new LinkedList<>();
            queue.add(msg);
            outgoingMsg.put(peer, queue);
        }
    }

    private void sendBlock(Task task) {
        int idx = task.getBlock().getIdx();
        int begin = task.getBlock().getBegin();
        byte[] dataToSend = new byte[task.getBlock().getLen()];

        try {
            output.seek(meta.getPieceLen() * idx + begin);
            int read = output.read(dataToSend);
            if (task.getBlock().getLen() != read) {
                System.err.println("Count of read bytes and requested len are different");
                return;
            }
        } catch (IOException e) {
            System.err.println("Caught an exception while sending block");
            e.printStackTrace();
            return;
        }
        byte[] idxA = Util.convertToByteArr(idx);
        byte[] beginA = Util.convertToByteArr(begin);
        Message msgToSend = new ProtocolMessage(MessagesTypes.PIECE,
                                                Util.concatByteArrays(Util.concatByteArrays(idxA, beginA), dataToSend));
        addToOutgoingMessages(task.getWho(), msgToSend);
    }

    private void stop() {
        System.out.println("Stopped DU");
        Thread.currentThread().interrupt();
    }

    private byte[] getMetaHash(int pieceIdx) {
        int SHA1Len = 20;
        return Arrays.copyOfRange(meta.getInfoHash(), pieceIdx * SHA1Len, (pieceIdx + 1) * SHA1Len);
    }

    private void checkHash(Task task) {
        int idx = task.getIdx();
        int pieceLen = task.getPieceLen();
        byte[] metaHash = getMetaHash(idx);
        byte[] pieceData = new byte[pieceLen];
        try {
            output.seek(meta.getPieceLen() * idx);
            if (pieceLen != output.read(pieceData)) {
                System.err.println("Couldn't read enough bytes while checking hashes");
                return;
            }
        } catch (IOException e) {
            System.err.println("Caught an exception while checking hashes");
            e.printStackTrace();
            return;
        }

        if (Arrays.equals(metaHash, Util.generateHash(pieceData))) {
            successfulCheck.add(idx);
        } else {
            unsuccessfulCheck.add(idx);
        }
    }

    public Queue<Integer> getSuccessfulCheck() {
        return successfulCheck;
    }

    public Queue<Integer> getUnsuccessfulCheck() {
        return unsuccessfulCheck;
    }

    private final MetainfoFile meta;
    private final Queue<Task> tasks = new LinkedList<>();
    private final Map<Peer, Queue<Message>> outgoingMsg = new HashMap<>();
    private final Queue<Integer> successfulCheck = new LinkedList<>();
    private final Queue<Integer> unsuccessfulCheck = new LinkedList<>();
    private RandomAccessFile output;
}
