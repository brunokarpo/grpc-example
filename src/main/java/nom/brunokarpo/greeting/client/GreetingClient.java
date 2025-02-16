package nom.brunokarpo.greeting.client;

import io.grpc.*;
import io.grpc.stub.StreamObserver;
import nom.brunokarpo.grpc.greeting.GreetingRequest;
import nom.brunokarpo.grpc.greeting.GreetingResponse;
import nom.brunokarpo.grpc.greeting.GreetingServiceGrpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    private static void doGreet(ManagedChannel channel) {
        System.out.println("Enter doGreet");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingResponse response = stub.greet(GreetingRequest.newBuilder().setFirstName("Bruno").build());
        System.out.println("Response: " + response.getResult());
    }

    private static void doGreetManyTimes(ManagedChannel channel) {
        System.out.println("Enter doGreetManyTimes");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);

        stub.greetingManyTimes(GreetingRequest.newBuilder().setFirstName("Bruno").build()).forEachRemaining( response ->  {
            System.out.println(response.getResult());
        });
    }

    private static void doLongGreet(ManagedChannel channel) throws InterruptedException {
        System.out.println("Enter doLongGreet");
        GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);

        List<String> names = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        Collections.addAll(names, "Bruno", "Dayane", "Eliana", "Aldemiro", "Ademir");

        StreamObserver<GreetingRequest> stream = stub.longGreet(new StreamObserver<GreetingResponse>() {
            @Override
            public void onNext(GreetingResponse greetingResponse) {
                System.out.println(greetingResponse.getResult());
            }

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        for (String name : names) {
            stream.onNext(GreetingRequest.newBuilder().setFirstName(name).build());
        }

        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doGreetEveryone(ManagedChannel channel) throws InterruptedException {
        System.out.println("Enter doGreetEveryone");
        GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetingRequest> stream = stub.greetEveryone(new StreamObserver<GreetingResponse>() {
            @Override
            public void onNext(GreetingResponse greetingResponse) {
                System.out.println(greetingResponse.getResult());
            }

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        Arrays.asList("Bruno", "Dayane", "Aldemiro", "Eliana").forEach(name -> {
            stream.onNext(GreetingRequest.newBuilder().setFirstName(name).build());
        });

        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doGreetWithDeadline(ManagedChannel channel) {
        System.out.println("Entered doGreetWithDeadline");
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);

        GreetingResponse response = stub
                .withDeadline(Deadline.after(3, TimeUnit.SECONDS))
                .greetWithDeadline(GreetingRequest.newBuilder().setFirstName("Bruno").build());

        System.out.println("Greeting within deadline: " + response.getResult());

        try {
            response = stub
                    .withDeadline(Deadline.after(100, TimeUnit.MICROSECONDS))
                    .greetWithDeadline(GreetingRequest.newBuilder().setFirstName("Bruno").build());

            System.out.println("Greeting deadline exceeded: " + response.getResult());
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                System.out.println("Deadline has been exceeded");
            } else {
                System.out.println("Got an exception in greetWithDeadline");
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0) {
            System.out.println("Need one argument to work");
            return;
        }

        // Create managed channel
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        switch (args[0]) {
            case "greet": doGreet(channel); break;
            case "greet-many-times": doGreetManyTimes(channel); break;
            case "long-greet": doLongGreet(channel); break;
            case "greet-everyone": doGreetEveryone(channel); break;
            case "greet-with-deadline": doGreetWithDeadline(channel); break;
            default:
                System.out.println("Keyword invalid: " + args[0]);
        }

        // Shutdown channel
        System.out.println("Shutting down channel");
        channel.shutdown();
    }
}
