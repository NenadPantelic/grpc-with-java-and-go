package greeting.server;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.stub.StreamObserver;

public class GreetingServerImpl extends GreetingServiceGrpc.GreetingServiceImplBase {

    @Override
    public void greet(GreetingRequest greetingRequest, StreamObserver<GreetingResponse> responseObserver) {
        responseObserver.onNext(
                GreetingResponse.newBuilder()
                        .setResult("Hello " + greetingRequest.getFirstName())
                        .build()
        ); // returns a response to the client
        responseObserver.onCompleted(); // complete the communication
    }

    @Override
    public void greetManyTimes(GreetingRequest greetingRequest, StreamObserver<GreetingResponse> responseObserver) {
        for (int i = 1; i <= 10; i++) {
            GreetingResponse greetingResponse = GreetingResponse.newBuilder()
                    .setResult(greetingRequest.getFirstName() + "_" + i)
                    .build();
            responseObserver.onNext(greetingResponse);
        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<GreetingRequest> longGreet(StreamObserver<GreetingResponse> responseObserver) {
        StringBuilder strBuilder = new StringBuilder();

        return new StreamObserver<>() {

            @Override
            public void onNext(GreetingRequest greetingRequest) {
                strBuilder.append("Hello ").append(greetingRequest.getFirstName()).append("\n");
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t); // just gives back the error to the client
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(
                        GreetingResponse.newBuilder()
                                .setResult(strBuilder.toString())
                                .build()
                );
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<GreetingRequest> greetEveryone(StreamObserver<GreetingResponse> responseObserver) {
        return new StreamObserver<GreetingRequest>() {

            @Override
            public void onNext(GreetingRequest value) {
                responseObserver.onNext(GreetingResponse.newBuilder()
                        .setResult("Hello " + value.getFirstName())
                        .build());
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t); // return an error to the client
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
