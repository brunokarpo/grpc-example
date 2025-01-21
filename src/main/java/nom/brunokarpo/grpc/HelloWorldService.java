package nom.brunokarpo.grpc;

import io.grpc.stub.StreamObserver;

public class HelloWorldService extends GreeterGrpc.GreeterImplBase {

        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            String message = "Hello " + request.getName();
            HelloReply reply = HelloReply.newBuilder().setMessage(message).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
}
