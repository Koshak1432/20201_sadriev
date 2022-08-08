package kosh.torrent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

//класс, отсылающий и принимающий блоки
public class DownloadUploadManager implements Runnable {
    public DownloadUploadManager(MetainfoFile meta) {
        try {
            output = new RandomAccessFile(meta.getName(), "rw");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            if (!tasks.isEmpty()) {
                doTask(tasks.poll());
            }
        }
    }

    public void doTask(Task task) {
        switch (task.getType()) {
            case SAVE -> {
                saveBlock(task);
            }
            case SEND -> {
                sendBlock(task);
            }
            case STOP -> {
                stop();
            }
        }
    }

    private void saveBlock(Task task) {

    }

    private void sendBlock(Task task) {
        int idx = task.getBlock().getIdx();
        int begin = task.getBlock().getBegin();
        int len = task.getBlock().getLen();

        byte[] dataToSend = new byte[task.getBlock().getLen()];
        try {
            output.seek((long) Constants.PIECE_LENGTH * task.getBlock().getIdx() + task.getBlock().getBegin());
            if (task.getBlock().getLen() != output.read(dataToSend)) {
                System.err.println("Count of read bytes and requested len are different");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] idxA = Util.convertToNormalByteArr(idx);
        byte[] beginA = Util.convertToNormalByteArr(begin);
        Message msgToSend = new ProtocolMessage(MessagesTypes.PIECE,
                                                Util.concatByteArrays(idxA, Util.concatByteArrays(beginA, dataToSend)));
        Queue<Message> q;
        if (outgoingMsg.containsKey(task.getWho())) {
            q = outgoingMsg.get(task.getWho());
        } else {
            q = new LinkedList<>();
        }
        q.add(msgToSend);
    }

    private void stop() {

    }

    public Queue<Task> getTasks() {
        return tasks;
    }

    Queue<Task> tasks = new LinkedList<>();
    Map<PeerConnection, Queue<Message>> outgoingMsg = new HashMap<>();
    RandomAccessFile output = null;
}
