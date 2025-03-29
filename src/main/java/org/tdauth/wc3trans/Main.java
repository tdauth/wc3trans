package org.tdauth.wc3trans;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: wc3trans <source war3map.wts> <target war3map.wts files>");
            System.exit(1);
        }

        try {
            War3mapWts source = new War3mapWts(args[0]);

            for (int i = 1; i < args.length; i++) {
                War3mapWts target = new War3mapWts(args[i]);
                source.updateTarget(target);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}