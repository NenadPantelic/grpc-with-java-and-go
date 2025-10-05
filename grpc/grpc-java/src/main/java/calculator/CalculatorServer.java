package calculator;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;

public class CalculatorServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 10001;

        Server server = ServerBuilder.forPort(port)
                .addService(new CalculatorServiceImpl())
                .addService(ProtoReflectionService.newInstance()) // with this the server is able to client some client
                // which services and messages it has
                // install https://github.com/ktr0731/evans and use it as a client to check how server exposes it
                // ./evans --host localhost --port 10001 --reflection repl
                // > show package
                // > show service
                // > show message

                // call sum
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
