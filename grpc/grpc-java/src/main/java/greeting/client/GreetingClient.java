package greeting.client;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0) {
            System.out.println("Need one argument to work");
            return;
        }

        // Channels will create the TCP connection between the client and the server
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        switch (args[0]) {
            case "greet" -> doGreet(channel);
            case "greet-many-times" -> doGreetManyTimes(channel);
            case "long-greet" -> doLongGreet(channel);
            case "greet-everyone" -> doGreetEveryone(channel);
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

    private static void doGreetManyTimes(ManagedChannel channel) {
        System.out.println("Entered doGreetManyTimes");
        // blocking stub (blocking calls)
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        stub.greetManyTimes(
                GreetingRequest.newBuilder()
                        .setFirstName("Nenad")
                        .build()
        ).forEachRemaining(greetingResponse -> System.out.println("Greeting: " + greetingResponse.getResult()));
    }

    // sends one long string with the newline separator
    private static void doLongGreet(ManagedChannel channel) throws InterruptedException {
        System.out.println("Entered doLongGreet");

        // asynchronous stub
        GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1); // to wait response from the server

        StreamObserver<GreetingRequest> stream = stub.longGreet(new StreamObserver<>() {
            // the next response the client received - only one at a time
            @Override
            public void onNext(GreetingResponse greetingResponse) {
                System.out.println(greetingResponse.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        List<String> names = List.of("Nenad", "John", "Jane");
        for (String name : names) {
            // sending requests to the server
            stream.onNext(GreetingRequest.newBuilder()
                    .setFirstName(name)
                    .build()
            );
        }

        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    // sends N requests
    private static void doGreetEveryone(ManagedChannel channel) throws InterruptedException {
        System.out.println("Entered doGreetEveryone");

        // asynchronous stub
        GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1); // to wait response from the server

        StreamObserver<GreetingRequest> stream = stub.longGreet(new StreamObserver<>() {
            // the next response the client received - only one at a time
            @Override
            public void onNext(GreetingResponse greetingResponse) {
                System.out.println(greetingResponse.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        List<String> names = List.of("Nenad", "John", "Jane");
        for (String name : names) {
            // sending requests to the server
            stream.onNext(GreetingRequest.newBuilder()
                    .setFirstName(name)
                    .build()
            );
        }

        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

}
