package nom.brunokarpo.blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class BlogServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50054;

        MongoClient client = MongoClients.create("mongodb://root:root@localhost:27017/");

        // Create a new server to listen on port
        Server server = ServerBuilder
                .forPort(port)
                .addService(new BlogService(client))
                .build();

        // Starting the server
        server.start();

        System.out.println("Server started, listening on " + port);

        // Adding shutdown hook gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            client.close();
            System.out.println("Server shut down");
        }));

        server.awaitTermination();
    }
}
