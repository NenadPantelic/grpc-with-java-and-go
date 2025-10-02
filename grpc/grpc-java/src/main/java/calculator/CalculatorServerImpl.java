package calculator;

import com.proto.calculator.*;
import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
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

    private PrimesResponse buildPrimeResult(long value) {
        return PrimesResponse.newBuilder()
                .setValue(value)
                .build();
    }
}
