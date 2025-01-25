package nom.brunokarpo.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50051;

        // Create a new server to listen on port
        Server server = ServerBuilder
                .forPort(port)
                .addService(new GreetingService())
                .build();

        // Starting the server
        server.start();

        System.out.println("Server started, listening on " + port);

        // Adding shutdown hook gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            System.out.println("Server shut down");
        }));

        server.awaitTermination();
    }
}
