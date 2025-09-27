package simple;

import example.simple.SimpleOuterClass;

import java.util.List;

public class SimpleMain {

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

        System.out.println("Message: " + message);
    }
}
