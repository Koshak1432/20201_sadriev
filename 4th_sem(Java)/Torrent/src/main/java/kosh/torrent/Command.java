package kosh.torrent;

public class Command {
    public Command(CommandType type) {
        this.type = type;
    }

    public Command(CommandType type, Block block) {

    }



    private CommandType type;
    private Block block;
}
