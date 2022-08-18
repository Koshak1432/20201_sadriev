package kosh.torrent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;


//если сделать DU паблишером, а км и прочие, кому нужен он, будут подписываться, тогда не понятно, как добавлять таски в него
//подпишутся друг на друга?
public class DownloadUploadManager implements Runnable, IDownloadUploadManager {
    public DownloadUploadManager(MetainfoFile meta, boolean seeder) {
        this.meta = meta;
        Random random = new Random();

        byte id = meta.getPieces()[random.nextInt(meta.getPieces().length)];
        String outputFileName = seeder ? meta.getName() : meta.getName() + id;
        System.out.println(outputFileName);
        try {
            output = new RandomAccessFile(outputFileName, "rw");
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't open: " + outputFileName);
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    //use wait(sleep) and notify
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (tasks) {
                if (!tasks.isEmpty()) {
                    doTask(tasks.poll());
                }
                else {
                    try {
//                        System.out.println("WAITING");
                        tasks.wait();
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        try {
            output.close();
        }
        catch (IOException e) {
            System.err.println("Couldn't close: " + output);
            e.printStackTrace();
        }
        System.out.println("DU finished");
    }

    private void doTask(Task task) {
        System.out.println("doing task " + task);
        switch (task.getType()) {
            case SAVE -> saveBlock(task);
            case EXTRACT_BLOCK -> sendBlock(task);
            case CHECK_HASH -> checkHash(task);
            case STOP -> stop();
        }
    }

    @Override
    public IMessage getOutgoingMsg(Peer peer) {
        synchronized (outgoingMsg) {
            if (outgoingMsg.containsKey(peer)) {
                synchronized (outgoingMsg.get(peer)) {
                    IMessage msg = outgoingMsg.get(peer).poll();
                    return msg;
                }
            }
        }
        return null;
    }

    @Override
    public void addTask(Task task) {
        synchronized (tasks) {
            tasks.add(task);
            tasks.notifyAll();
        }
    }

    private void saveBlock(Task task) {
        int idx = task.getBlock().idx();
        int begin = task.getBlock().begin();
        byte[] block = task.getBlock().data();
        try {
            output.seek( meta.getPieceLen() * idx + begin);
            output.write(block);
        } catch (IOException e) {
            System.err.println("Couldn't save block");
            e.printStackTrace();
        }
    }

    private void addToOutgoingMessages(Peer peer, IMessage msg) {
        synchronized (outgoingMsg) {
            if (outgoingMsg.containsKey(peer)) {
                outgoingMsg.get(peer).add(msg);
                return;
            }
            Queue<IMessage> queue = new LinkedList<>();
            queue.add(msg);
            outgoingMsg.put(peer, queue);
        }
    }

    private void sendBlock(Task task) {
        int idx = task.getBlock().idx();
        int begin = task.getBlock().begin();
        byte[] dataToSend = new byte[task.getBlock().len()];

        try {
            output.seek(meta.getPieceLen() * idx + begin);
            int read = output.read(dataToSend);
            if (dataToSend.length != read) {
                System.err.println("Count of read bytes and requested len are different");
                return;
            }
        } catch (IOException e) {
            System.err.println("Couldn't send block");
            e.printStackTrace();
            return;
        }
        byte[] idxA = Util.convertToByteArr(idx);
        byte[] beginA = Util.convertToByteArr(begin);
        IMessage msgToSend = new ProtocolMessage(MessagesTypes.PIECE,
                                                 Util.concatByteArrays(Util.concatByteArrays(idxA, beginA), dataToSend));
        addToOutgoingMessages(task.getWho(), msgToSend);
    }

    private void stop() {
        System.out.println("Stopped DU");
        Thread.currentThread().interrupt();
    }

    private byte[] getMetaHash(int pieceIdx) {
        int SHA1Len = 20;
        return Arrays.copyOfRange(meta.getPieces(), pieceIdx * SHA1Len, (pieceIdx + 1) * SHA1Len);
    }

    private void checkHash(Task task) {
        int idx = task.getIdx();
        int pieceLen = task.getPieceLen();
        byte[] metaHash = getMetaHash(idx);
        byte[] pieceData = new byte[pieceLen];
        System.out.println("checking hash task, idx: " + idx + ", piece len: " + pieceLen + " , piece data len: " + pieceData.length);
        try {
            output.seek(meta.getPieceLen() * idx);
            if (pieceLen != output.read(pieceData)) {
                System.err.println("Couldn't read enough bytes while checking hashes");
                return;
            }
        } catch (IOException e) {
            System.err.println("Couldn't check hashes");
            e.printStackTrace();
            return;
        }

        if (Arrays.equals(metaHash, Util.generateHash(pieceData))) {
            System.out.println("successful check");
            successfulCheck.add(idx);
        } else {
            unsuccessfulCheck.add(idx);
            System.out.println("unsuccessful check");
        }
    }

    @Override
    public Integer getSuccessfulCheck() {
        synchronized (successfulCheck) {
            return successfulCheck.poll();
        }
    }

    @Override
    public Integer getUnsuccessfulCheck() {
        synchronized (unsuccessfulCheck) {
            return unsuccessfulCheck.poll();
        }
    }

    private final MetainfoFile meta;
    private final Queue<Task> tasks = new LinkedList<>();
    private final Map<Peer, Queue<IMessage>> outgoingMsg = new HashMap<>();
    private final Queue<Integer> successfulCheck = new LinkedList<>();
    private final Queue<Integer> unsuccessfulCheck = new LinkedList<>();
    private RandomAccessFile output;
}
