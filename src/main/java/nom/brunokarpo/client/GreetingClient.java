package nom.brunokarpo.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    public static void main(String[] args) {
        // Create managed channel
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        // do something

        // Shutdown channel
        System.out.println("Shutting down channel");
        channel.shutdown();
    }
}
