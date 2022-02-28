package brainfuck;

public class Pointer implements IPointer {

    @Override
    public void movePointer(int idx) {
        if (idx < 0) {
            idx_ = MAX_SIZE - 1;
        }
        else if (idx >= MAX_SIZE - 1) {
            idx_ = 0;
        }
        else {
            idx_ = idx;
        }
    }

    @Override
    public void setPointer(byte value) {
        assert (idx_ < MAX_SIZE);
        tape_[idx_] = value;
    }

    @Override
    public byte getValue() {
        assert (idx_ < MAX_SIZE);
        return tape_[idx_];
    }

    @Override
    public int getIdx() {
        return idx_;
    }

    private final byte[] tape_ = new byte[MAX_SIZE];
    private int idx_ = 0;
    private static final int MAX_SIZE = 30000;
}
