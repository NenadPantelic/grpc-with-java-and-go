package io;

import example.simple.SimpleOuterClass;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class IOMain {

    public static void writeTo(SimpleOuterClass.Simple message, String path) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            message.writeTo(fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SimpleOuterClass.Simple readFrom(String path) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            SimpleOuterClass.Simple message = SimpleOuterClass.Simple.parseFrom(fileInputStream);
            System.out.println("Parsed message: " + message);
            return message;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SimpleOuterClass.Simple message = SimpleOuterClass.Simple.newBuilder()
                .setId(42)
                .setIsSimple(true)
                .setName("Nenad")
                .addSampleList(25)
                .addSampleList(35)
                .addSampleList(45)
                .addAllSampleList(List.of(1,2,3))
                .build();
        String path = "simple.bin";
        writeTo(message, path);

        readFrom(path);
    }
}
