package com.example;


import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        XMLExecutiveMotions motions = new XMLExecutiveMotions("/firma.csv");
        EPUSender epuSender = new EPUSender(motions);
        motions.prettyPrint();
        try {
            epuSender.send();
        } catch (IOException e) {
             e.printStackTrace();
        }
    }
}
