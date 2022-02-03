package com.maxym.sha256;

import com.maxym.utils.BytesUtils;

import java.nio.ByteBuffer;

import static java.lang.Byte.toUnsignedInt;
import static java.lang.Integer.rotateRight;

public final class Sha256 {

    // initial hash values
    private static final int[] HASH_VALUES_INITIAL = new int[]{
        0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
    };

    // round constants
    private static final int[] K = new int[]{
        0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
        0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
        0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
        0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
        0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
        0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
        0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
        0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    private Sha256() {
        // this class cannot be instantiated
    }

    /**
     *
     * @param message {@link String} to be hashed
     * @return a hexadecimal representation of the given message
     */
    public static String hash(String message) {
        return hash(message.getBytes());
    }

    /**
     *
     * @param message {@code byte} array to be hashed
     * @return a hexadecimal representation of the given message
     */
    public static String hash(byte[] message) {
        // pre-processing (padding):
        message = pad(message);

        // initialize hash values
        int[] hashValues = new int[8];
        System.arraycopy(HASH_VALUES_INITIAL, 0, hashValues, 0, hashValues.length);

        int[] workingVariables = new int[8];

        // process the message in successive 512-bit chunks
        for (int chunkIndex = 0; chunkIndex < message.length / 64; chunkIndex++) {
            int[] words = generateWords(message, chunkIndex);

            // initialize working variables to current hash value
            System.arraycopy(hashValues, 0, workingVariables, 0, workingVariables.length);

            // compression function main loop
            for (int i = 0; i < 64; i++) {
                int s1 = rotateRight(workingVariables[4], 6) ^ rotateRight(workingVariables[4], 11) ^ rotateRight(workingVariables[4], 25);
                int ch = (workingVariables[4] & workingVariables[5]) ^ (~workingVariables[4] & workingVariables[6]);
                int temp1 = workingVariables[7] + s1 + ch + K[i] + words[i];
                int s0 = rotateRight(workingVariables[0], 2) ^ rotateRight(workingVariables[0], 13) ^ rotateRight(workingVariables[0], 22);
                int maj = (workingVariables[0] & workingVariables[1]) ^ (workingVariables[0] & workingVariables[2]) ^ (workingVariables[1] & workingVariables[2]);
                int temp2 = s0 + maj;

                // reorder working variables
                workingVariables[3] += temp1;
                System.arraycopy(workingVariables, 0, workingVariables, 1, 7);
                workingVariables[0] = temp1 + temp2;
            }

            // add the compressed chunk to the current hash value
            for (int i = 0; i < hashValues.length; i++) {
                hashValues[i] += workingVariables[i];
            }
        }

        byte[] hash = concatIntsToArray(hashValues);

        return BytesUtils.toHex(hash);
    }

    private static byte[] pad(byte[] message) {
        int k = 512 - (message.length * 8 + 65) % 512;   // minimum positive number such that L + 1 + K + 64 is a multiple of 512

        return ByteBuffer.allocate((message.length * 8 + 1 + k + 64) / 8)
                         .put(message)                   // append original message of length L bits
                         .put(Byte.MIN_VALUE)            // append a single '1' bit
                         .put(new byte[k / 8])           // append K '0' bits
                         .putLong(message.length * 8L)   // append L bits (the length of the message as a 64-bit number)
                         .array();
    }

    private static int[] generateWords(byte[] message, int chunkIndex) {
        int[] words = new int[64];
        int j = 0;

        // copy chunk into first 16 words w[0..15] of the message schedule array
        for (int i = 0; i < 16; i++) {
            words[i] = concatBytesToInt(message[chunkIndex * 64 + j],
                                        message[chunkIndex * 64 + j + 1],
                                        message[chunkIndex * 64 + j + 2],
                                        message[chunkIndex * 64 + j + 3]);
            j += 4;
        }

        // extend the first 16 words into the remaining 48 words w[16..63] of the message schedule array:
        for (int i = 16; i < 64; i++) {
            int s0 = rotateRight(words[i - 15],  7) ^ rotateRight(words[i - 15], 18) ^ (words[i - 15] >>> 3);
            int s1 = rotateRight(words[i - 2],  17) ^ rotateRight(words[i - 2], 19) ^ (words[i - 2] >>> 10);
            words[i] = words[i - 16] + s0 + words[i - 7] + s1;
        }

        return words;
    }

    private static int concatBytesToInt(byte i1, byte i2, byte i3, byte i4) {
        return (toUnsignedInt(i1) << 24)
               | (toUnsignedInt(i2) << 16)
               | (toUnsignedInt(i3) << 8)
               | (toUnsignedInt(i4));
    }

    private static byte[] concatIntsToArray(int[] values) {
        ByteBuffer buf = ByteBuffer.allocate(values.length * Integer.BYTES);
        for (int value : values) {
            buf.putInt(value);
        }

        return buf.array();
    }
}
