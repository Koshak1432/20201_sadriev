package kosh.torrent;


public record Block(int idx, int begin, int len, byte[] data) {}
