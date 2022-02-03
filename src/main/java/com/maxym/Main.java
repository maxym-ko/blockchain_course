package com.maxym;

import com.maxym.sha256.Sha256;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        demoSha256();
    }

    private static void demoSha256() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the message to be hashed: ");

        String message = scanner.nextLine();
        String digest = Sha256.hash(message);

        System.out.println("The hash value (digest) of your message is: " + digest);
    }
}
