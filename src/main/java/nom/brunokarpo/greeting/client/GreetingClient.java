package nom.brunokarpo.greeting.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import nom.brunokarpo.grpc.greeting.GreetingRequest;
import nom.brunokarpo.grpc.greeting.GreetingResponse;
import nom.brunokarpo.grpc.greeting.GreetingServiceGrpc;

public class GreetingClient {

    private static void doGreet(ManagedChannel channel) {
        System.out.println("Enter doGreet");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingResponse response = stub.greet(GreetingRequest.newBuilder().setFirstName("Bruno").build());
        System.out.println("Response: " + response.getResult());
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Need one argument to work");
            return;
        }

        // Create managed channel
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        if (args[0].equals("greet")) {
            doGreet(channel);
        } else {
            System.out.println("Keyword invalid: " + args[0]);
        }

        // Shutdown channel
        System.out.println("Shutting down channel");
        channel.shutdown();
    }
}
