package calculator;

import com.proto.calculator.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class CalculatorServerImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(SumRequest sumRequest, StreamObserver<SumResponse> responseObserver) {
        responseObserver.onNext(
                SumResponse.newBuilder()
                        .setResult(sumRequest.getOperandOne() + sumRequest.getOperandTwo())
                        .build()
        ); // returns a response to the client
        responseObserver.onCompleted(); // complete the communication
    }

    @Override
    public void primes(PrimesRequest request, StreamObserver<PrimesResponse> responseObserver) {
        long value = 2;
        long num = request.getNum();

        while (num > 1) {
            if (num % value == 0) {
                responseObserver.onNext(buildPrimeResult(value));
                num /= value;
            } else {
                value++;
            }

        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<AvgRequest> avg(StreamObserver<AvgResponse> responseObserver) {
        return new StreamObserver<>() {
            int count = 0;
            float total = 0;

            @Override
            public void onNext(AvgRequest greetingRequest) {
                count++;
                total += greetingRequest.getNum();
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t); // just gives back the error to the client
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(
                        AvgResponse.newBuilder()
                                .setValue(total / count)
                                .build()
                );
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<MaxRequest> max(StreamObserver<MaxResponse> responseObserver) {
        return new StreamObserver<>() {
            long max = 0L;

            @Override
            public void onNext(MaxRequest maxRequest) {
                long num = maxRequest.getNum();
                if (num > max) {
                    max = num;
                    // whenever a new max is computed, return it
                    responseObserver.onNext(buildMaxResponse(max));
                }
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t); // just gives back the error to the client
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void sqrt(SqrtRequest request, StreamObserver<SqrtResponse> responseObserver) {
        int number = request.getNum();
        if (number < 0) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("The number that was sent cannot be negative")
                    .augmentDescription("Number: " + number)
                    .asRuntimeException());
            return;
        }

        responseObserver.onNext(
                SqrtResponse.newBuilder()
                        .setResult(Math.sqrt(request.getNum()))
                        .build()
        );
        responseObserver.onCompleted();
    }

    private PrimesResponse buildPrimeResult(long value) {
        return PrimesResponse.newBuilder()
                .setValue(value)
                .build();
    }

    private MaxResponse buildMaxResponse(long value) {
        return MaxResponse.newBuilder()
                .setValue(value)
                .build();
    }
}
