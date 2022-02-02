package com.maxym;

import java.util.List;

public class Message {

    private final byte[] message;

    public byte[] getMessage() {
        return message;
    }

    public Message(String message) {
        byte[] messageBytes = message.getBytes();

        int k = 512 - (messageBytes.length * 8 + 65) % 512;

        this.message = new byte[(messageBytes.length * 8 + 1 + k + 64) / 8];

        for (int i = 0; i < messageBytes.length; i++) {
            this.message[i] = messageBytes[i];
        }

        this.message[messageBytes.length] = (byte) 0b1000_0000;

        byte[] messageLength = longToBytes(messageBytes.length * 8L);

        int j = messageLength.length - 1;
        for (int i = this.message.length - 1; i > this.message.length - 1 - messageLength.length; i--) {
            this.message[i] = messageLength[j--];
        }
    }

    private static byte[] longToBytes(long value) {
        byte[] result = new byte[8];

        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(value & 0xFF);
            value >>= 8;
        }

        return result;
    }
}
