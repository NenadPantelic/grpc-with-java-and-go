package oneof;

import example.oneof.Oneof;

public class OneOfMain {

    public static void main(String[] args) {
        Oneof.Result message = Oneof.Result.newBuilder()
                .setMessage("Hello world")
                .build();

        Oneof.Result message2 = Oneof.Result.newBuilder()
                .setId(2)
                .build();

        Oneof.Result message3 = Oneof.Result.newBuilder()
                .setMessage("Hello world 3")
                .setId(3)
                .build();

        Oneof.Result message4 = Oneof.Result.newBuilder(message)
                .setId(4)
                .build();

        System.out.println("Message: " + message);
        System.out.println("Message 2: " + message2);
        System.out.println("Message 3: " + message3);
        System.out.println("Message 4: " + message4);

        System.out.println("Message 1 - is message set? " + message.hasMessage());
        System.out.println("Message 1 - is id set? " + message.hasId());

        System.out.println("Message 2 - is message set? " + message2.hasMessage());
        System.out.println("Message 2 - is id set? " + message2.hasId());
    }
}
