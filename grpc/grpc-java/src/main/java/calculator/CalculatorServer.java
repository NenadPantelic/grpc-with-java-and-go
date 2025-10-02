package calculator;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalculatorServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 10001;

        Server server = ServerBuilder.forPort(port)
                .addService(new CalculatorServerImpl())
                .build();
        server.start();
        System.out.println("Server started");
        System.out.println("Listening on port: " + port);

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
