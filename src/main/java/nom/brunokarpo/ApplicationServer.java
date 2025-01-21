package nom.brunokarpo;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import nom.brunokarpo.grpc.HelloWorldService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class ApplicationServer {
    private Server server;

    private void start() throws IOException {
        server = Grpc.newServerBuilderForPort(50051, InsecureServerCredentials.create())
                .addService(new HelloWorldService())
                .build()
                .start();
        System.out.println("Server started, listening on 50051");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server since JVM is shutting down");
            try {
                ApplicationServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.out.println("Server shut down");
        }));
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final ApplicationServer server = new ApplicationServer();
        server.start();
        server.blockUntilShutdown();
    }
}
