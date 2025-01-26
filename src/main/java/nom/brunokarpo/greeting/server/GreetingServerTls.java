package nom.brunokarpo.greeting.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class GreetingServerTls {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50053;

        // Create a new server to listen on port
        Server server = ServerBuilder
                .forPort(port)
                .useTransportSecurity(
                        new File("ssl/server.crt"),
                        new File("ssl/server.pem")
                )
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
