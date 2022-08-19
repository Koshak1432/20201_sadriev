package kosh.torrent;


/*
* Class represents a portion of data that a client may request from a peer
 */
public record Block(int idx, int begin, int len, byte[] data) {}
