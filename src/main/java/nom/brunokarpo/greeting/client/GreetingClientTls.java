package nom.brunokarpo.greeting.client;

import io.grpc.*;
import io.grpc.stub.StreamObserver;
import nom.brunokarpo.grpc.greeting.GreetingRequest;
import nom.brunokarpo.grpc.greeting.GreetingResponse;
import nom.brunokarpo.grpc.greeting.GreetingServiceGrpc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClientTls {

    private static void doGreet(ManagedChannel channel) {
        System.out.println("Enter doGreet");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingResponse response = stub.greet(GreetingRequest.newBuilder().setFirstName("Bruno").build());
        System.out.println("Response: " + response.getResult());
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Need one argument to work");
            return;
        }

        ChannelCredentials creds = TlsChannelCredentials.newBuilder()
                .trustManager(new File("ssl/ca.crt"))
                .build();

        // Create managed channel
        ManagedChannel channel = Grpc
                .newChannelBuilderForAddress("localhost", 50053, creds)
                .build();

        switch (args[0]) {
            case "greet": doGreet(channel); break;
            default:
                System.out.println("Keyword invalid: " + args[0]);
        }

        // Shutdown channel
        System.out.println("Shutting down channel");
        channel.shutdown();
    }
}
