package nom.brunokarpo.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalculatorServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50051;

        Server server = ServerBuilder
                .forPort(port)
                .addService(new CalculatorService())
                .build();

        server.start();

        System.out.println("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown requested to grpc server");
            server.shutdown();
            System.out.println("Server shut down");
        }));

        server.awaitTermination();
    }
}
