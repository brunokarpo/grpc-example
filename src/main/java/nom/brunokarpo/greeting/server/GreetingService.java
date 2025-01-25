package nom.brunokarpo.greeting.server;

import io.grpc.stub.StreamObserver;
import nom.brunokarpo.grpc.greeting.GreetingRequest;
import nom.brunokarpo.grpc.greeting.GreetingResponse;
import nom.brunokarpo.grpc.greeting.GreetingServiceGrpc;

public class GreetingService extends GreetingServiceGrpc.GreetingServiceImplBase {
    @Override
    public void greet(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
        responseObserver.onNext(GreetingResponse.newBuilder().setResult("Hello " + request.getFirstName()).build());
        responseObserver.onCompleted();
    }
}
