package nom.brunokarpo.greeting.server;

import io.grpc.Context;
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

    @Override
    public void greetingManyTimes(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
        GreetingResponse response = GreetingResponse.newBuilder().setResult("Hello " + request.getFirstName()).build();
        for (int i = 0; i < 10; i++) {
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<GreetingRequest> longGreet(StreamObserver<GreetingResponse> responseObserver) {
        StringBuilder sb = new StringBuilder();

        return new StreamObserver<>() {
            @Override
            public void onNext(GreetingRequest greetingRequest) {
                sb.append("Hello ");
                sb.append(greetingRequest.getFirstName());
                sb.append("!\n");
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(GreetingResponse.newBuilder().setResult(sb.toString()).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<GreetingRequest> greetEveryone(StreamObserver<GreetingResponse> responseObserver) {
        return new StreamObserver<GreetingRequest>() {
            @Override
            public void onNext(GreetingRequest greetingRequest) {
                responseObserver.onNext(GreetingResponse.newBuilder().setResult("Hello " + greetingRequest.getFirstName()).build());
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void greetWithDeadline(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
        Context context = Context.current();

        try {
            for (int i = 0; i < 3; i++) {
                if (context.isCancelled()) {
                    return;
                }

                Thread.sleep(100);
            }

            responseObserver.onNext(GreetingResponse.newBuilder().setResult("Hello " + request.getFirstName()).build());
            responseObserver.onCompleted();
        } catch (InterruptedException e) {
            responseObserver.onError(e);
        }
    }
}
