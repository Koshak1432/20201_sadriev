package kosh.torrent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/*
* Class which works with file system by doing tasks:
* Saves blocks to output file, extracts blocks to send PIECE message and checks SHA-1 full pieces hashes
 */
public class DownloadUploadManager implements Runnable, IDownloadUploadManager {

    /*
    * @param meta the .torrent file
    * @param seeder indicates whether this client is seeder or not
     */
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

    /*
    * Main method in which tasks are executed
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (tasks) {
                if (!tasks.isEmpty()) {
                    doTask(tasks.poll());
                }
                else {
                    try {
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
            System.err.println("Couldn't properly close: " + output);
            e.printStackTrace();
        }
        System.out.println("DU finished");
    }

    /*
     * Adds a task to queue
     * @param task task to add
     */
    @Override
    public void addTask(Task task) {
        synchronized (tasks) {
            tasks.add(task);
            tasks.notifyAll();
        }
    }

    /*
     * Writes block of data to output file
     * @param task all the needed info
     */
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

    /*
     * Extracts block of data from file and adds to outgoing messages
     * @param task all the needed info
     */
    private void extractBlock(Task task) {
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

    /*
     * Checks whether SHA-1 hash of downloaded piece is equals with hash in .torrent
     * @param task all the needed info
     */
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
            System.err.println("Couldn't check hashes");
            e.printStackTrace();
            return;
        }

        if (Arrays.equals(metaHash, Util.generateHash(pieceData))) {
            successfulCheck.add(idx);
        } else {
            unsuccessfulCheck.add(idx);
        }
    }

    /*
     * Gets a produced message (PIECE) to the peer
     * @param peer recipient
     * @return IMessage if there is a message or null if not
     */
    @Override
    public IMessage getOutgoingMsg(Peer peer) {
        if (outgoingMsg.containsKey(peer)) {
            synchronized (outgoingMsg.get(peer)) {
                return outgoingMsg.get(peer).poll();
            }
        }
        return null;
    }

    /*
     * Gets idx of a successfully downloaded and verified via the hash piece
     * @return piece idx
     */
    @Override
    public Integer getSuccessfulCheck() {
        synchronized (successfulCheck) {
            return successfulCheck.poll();
        }
    }

    /*
     * Gets idx of a not verified via the hash piece
     * @return piece idx
     */
    @Override
    public Integer getUnsuccessfulCheck() {
        synchronized (unsuccessfulCheck) {
            return unsuccessfulCheck.poll();
        }
    }

    private void doTask(Task task) {
        switch (task.getType()) {
            case SAVE -> saveBlock(task);
            case EXTRACT_BLOCK -> extractBlock(task);
            case CHECK_HASH -> checkHash(task);
            case STOP -> stop();
        }
    }

    private void addToOutgoingMessages(Peer peer, IMessage msg) {
        if (outgoingMsg.containsKey(peer)) {
            synchronized (outgoingMsg.get(peer)) {
                outgoingMsg.get(peer).add(msg);
                return;
            }
        }
        Queue<IMessage> queue = new LinkedList<>();
        queue.add(msg);
        synchronized (outgoingMsg) {
            outgoingMsg.put(peer, queue);
        }
    }

    private void stop() {
        System.out.println("Stopped DU");
        Thread.currentThread().interrupt();
    }

    private byte[] getMetaHash(int pieceIdx) {
        int SHA1Len = 20;
        return Arrays.copyOfRange(meta.getPieces(), pieceIdx * SHA1Len, (pieceIdx + 1) * SHA1Len);
    }

    private final MetainfoFile meta;
    private final Queue<Task> tasks = new LinkedList<>();
    private final Map<Peer, Queue<IMessage>> outgoingMsg = new HashMap<>();
    private final Queue<Integer> successfulCheck = new LinkedList<>();
    private final Queue<Integer> unsuccessfulCheck = new LinkedList<>();
    private RandomAccessFile output;
}
