package calculator;

import com.proto.calculator.*;
import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class CalculatorClient {

    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0) {
            System.out.println("Need one argument to work");
            return;
        }

        // Channels will create the TCP connection between the client and the server
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 10001)
                .usePlaintext()
                .build();

        switch (args[0]) {
            case "sum" -> doSum(channel);
            case "primes" -> doPrimes(channel);
            case "avg" -> doAvg(channel);
            default -> System.out.println("Invalid keyword: " + args[0]);
        }

        System.out.println("Shutting down");
        channel.shutdown();
    }

    private static void doSum(ManagedChannel channel) {
        System.out.println("Entered doSum");
        // blocking call
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        SumResponse sumResponse = stub.sum(
                SumRequest.newBuilder()
                        .setOperandOne(3)
                        .setOperandTwo(10)
                        .build()
        );

        System.out.println("Sum: " + sumResponse.getResult());
    }


    private static void doPrimes(ManagedChannel channel) {
        System.out.println("Entered doPrimes");
        // blocking call
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        stub.primes(
                PrimesRequest.newBuilder().setNum(120).build()
        ).forEachRemaining(primesResponse -> System.out.println("Next prime: " + primesResponse.getValue()));

    }

    private static void doAvg(ManagedChannel channel) throws InterruptedException {
        System.out.println("Entered doAvg");

        // asynchronous stub
        CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(channel);

        List<Integer> nums = List.of(1, 2, 3, 4);
        CountDownLatch latch = new CountDownLatch(1); // to wait response from the server

        StreamObserver<AvgRequest> stream = stub.avg(new StreamObserver<>() {
            // the next response the client received - only one at a time
            @Override
            public void onNext(AvgResponse avgResponse) {
                System.out.println(avgResponse.getValue());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        IntStream.rangeClosed(1, 10).forEach(num -> stream.onNext(AvgRequest.newBuilder()
                        .setNum(num)
                        .build()
                )
        );

        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }
}