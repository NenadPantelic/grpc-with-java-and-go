package greeting.client;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.ChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import io.grpc.Grpc;

import java.io.File;
import java.io.IOException;

public class GreetingClientTLS {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Need one argument to work");
            return;
        }

        ChannelCredentials credentials = TlsChannelCredentials.newBuilder().trustManager(
                new File("ssl/ca.crt")
        ).build();
        // Creating a channel that will use TLS
        ManagedChannel channel = Grpc.newChannelBuilderForAddress("localhost", 50051, credentials)
                .build();

        switch (args[0]) {
            case "greet" -> doGreet(channel);
            default -> System.out.println("Invalid keyword: " + args[0]);
        }

        System.out.println("Shutting down");
        channel.shutdown();
    }

    private static void doGreet(ManagedChannel channel) {
        System.out.println("Entered doGreet");
        // blocking stub (blocking calls)
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingResponse greetingResponse = stub.greet(
                GreetingRequest.newBuilder()
                        .setFirstName("Nenad")
                        .build()
        );

        System.out.println("Greeting: " + greetingResponse.getResult());
    }
}
