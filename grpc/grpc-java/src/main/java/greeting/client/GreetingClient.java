package greeting.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    public static void main(String[] args) {
        // Channels will create the TCP connection between the client and the server
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        // do something

        System.out.println("Shutting down");
        channel.shutdown();
    }
}
