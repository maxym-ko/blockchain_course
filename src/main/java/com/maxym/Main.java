package com.maxym;

import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Main {

    private static final int H_0 = 0x6a09e667;
    private static final int H_1 = 0xbb67ae85;
    private static final int H_2 = 0x3c6ef372;
    private static final int H_3 = 0xa54ff53a;
    private static final int H_4 = 0x510e527f;
    private static final int H_5 = 0x9b05688c;
    private static final int H_6 = 0x1f83d9ab;
    private static final int H_7 = 0x5be0cd19;

    private static final int[] k = new int[]{
        0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
        0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
        0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
        0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
        0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
        0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
        0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
        0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    public static void main(String[] args) {
        String s = "asd";

        byte[] message = new Message(s).getMessage();

        int a = H_0;
        int b = H_1;
        int c = H_2;
        int d = H_3;
        int e = H_4;
        int f = H_5;
        int g = H_6;
        int h = H_7;

        int h0 = H_0;
        int h1 = H_1;
        int h2 = H_2;
        int h3 = H_3;
        int h4 = H_4;
        int h5 = H_5;
        int h6 = H_6;
        int h7 = H_7;

        for (int ss = 0; ss < message.length / 64; ss++) {
            int[] w = new int[64];
            int j = 0;
            for (int i = 0; i < 64; i += 4) {
                int i1 = Byte.toUnsignedInt(message[ss * 64 + i]) << 24;
                int i2 = Byte.toUnsignedInt(message[ss * 64 + i + 1]) << 16;
                int i3 = Byte.toUnsignedInt(message[ss * 64 + i + 2]) << 8;
                int i4 = Byte.toUnsignedInt(message[ss * 64 + i + 3]);
                w[j++] = i1 | i2 | i3 | i4;
            }

            for (int i = 16; i < 64; i++) {
                int s0 = Integer.rotateRight(w[i - 15],  7) ^ Integer.rotateRight(w[i - 15], 18) ^ (w[i - 15] >>> 3);
                int s1 = Integer.rotateRight(w[i - 2],  17) ^ Integer.rotateRight(w[i - 2], 19) ^ (w[i - 2] >>> 10);
                w[i] = w[i - 16] + s0 + w[i - 7] + s1;
            }

            a = h0;
            b = h1;
            c = h2;
            d = h3;
            e = h4;
            f = h5;
            g = h6;
            h = h7;

            for (int i = 0; i < 64; i++) {
                int s1 = Integer.rotateRight(e, 6) ^ Integer.rotateRight(e, 11) ^ Integer.rotateRight(e, 25);
                int ch = (e & f) ^ (~e & g);
                int temp1 = h + s1 + ch + k[i] + w[i];
                int s0 = Integer.rotateRight(a, 2) ^ Integer.rotateRight(a, 13) ^ Integer.rotateRight(a, 22);
                int maj = (a & b) ^ (a & c) ^ (b & c);
                int temp2 = s0 + maj;

                h = g;
                g = f;
                f = e;
                e = d + temp1;
                d = c;
                c = b;
                b = a;
                a = temp1 + temp2;
            }

            h0 += a;
            h1 += b;
            h2 +=+ c;
            h3 += d;
            h4 += e;
            h5 += f;
            h6 += g;
            h7 += h;
        }

        ByteBuffer buf = ByteBuffer.allocate(8 * Integer.BYTES);
        buf.putInt(h0);
        buf.putInt(h1);
        buf.putInt(h2);
        buf.putInt(h3);
        buf.putInt(h4);
        buf.putInt(h5);
        buf.putInt(h6);
        buf.putInt(h7);

        toHex(buf.array());
    }

    private static void toHex( byte[] bytes) {
        for (byte b : bytes) {
            System.out.print(Integer.toHexString((b & 0xFF) + 0x100).substring(1));
        }
        System.out.println();
    }

    private static void toBinary( byte[] bytes) {
        for (byte b : bytes) {
            System.out.print(Integer.toBinaryString((b & 0xFF) + 0x100).substring(1));
        }
        System.out.println();
    }
}
