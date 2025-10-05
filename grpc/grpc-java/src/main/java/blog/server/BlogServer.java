package blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;

public class BlogServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 20002;

        MongoClient client = MongoClients.create("mongodb://root:root@localhost:27017/");

        Server server = ServerBuilder.forPort(port)
                .addService(new BlogServiceImpl(client))
                .addService(ProtoReflectionService.newInstance())
                .build();

        server.start();
        System.out.println("Server started");
        System.out.println("Listening on port: " + port);

        // when the program gets killed
        // shutdown hook works better with IntelliJ IDE
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    System.out.println("Received a shutdown request");
                    server.shutdown();
                    client.close();
                    System.out.println("Server stopped");
                })
        );

        server.awaitTermination();
    }
}

