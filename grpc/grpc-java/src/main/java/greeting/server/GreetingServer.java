package greeting.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 5051;

        Server server = ServerBuilder.forPort(port).build();
        server.start();
        System.out.println("Server started");
        System.out.println("Listening port: " + port);

        // when the program gets killed
        // shutdown hook works better with IntelliJ IDE
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    System.out.println("Received a shutdown request");
                    server.shutdown();
                    System.out.println("Server stopped");
                })
        );

        server.awaitTermination();
    }
}
