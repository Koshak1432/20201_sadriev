import java.util.ArrayList;
import java.util.List;

public class Pointer implements IPointer {

    @Override
    public void movePointer(int idx) {
        if (0 == idx) {
            idx_ = MAX_SIZE - 1;
        }
        else if (MAX_SIZE - 1 == idx) {
            idx_ = 0;
        }
        else {
            idx_ = idx;
        }
    }

    @Override
    public void setPointer(byte value) {
        tape_.set(idx_, value);
    }

    @Override
    public byte getValue() {
        return tape_.get(idx_);
    }

    @Override
    public int getPointer() {
        return idx_;
    }

    private final List<Byte> tape_ = new ArrayList<>(MAX_SIZE);
    private int idx_ = 0;
    private static final int MAX_SIZE = 30000;
}
