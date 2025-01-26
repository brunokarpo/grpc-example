package nom.brunokarpo.calculator.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import nom.brunokarpo.grpc.calculator.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {

    private static void doCalculation(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        CalculatorRequest request = CalculatorRequest.newBuilder().setFirstNumber(10).setSecondNumber(3).build();
        CalculatorResponse response = stub.sum(request);
        System.out.println("10 + 3 = " + response.getResult());
    }

    private static void doPrimeDecomposition(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        PrimeRequest request = PrimeRequest.newBuilder().setNumber(120).build();

        stub.primes(request).forEachRemaining(response -> {
            System.out.println(response.getPrimeFactor());
        });
    }

    private static void doAverage(ManagedChannel channel) throws InterruptedException {
        CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<AverageRequest> stream = stub.average(new StreamObserver<AverageResponse>() {
            @Override
            public void onNext(AverageResponse averageResponse) {
                System.out.println(averageResponse.getAverage());
            }

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        List<Integer> numbers = new ArrayList<>();
        Collections.addAll(numbers, 1, 2, 3, 4);

        for (Integer number : numbers) {
            stream.onNext(AverageRequest.newBuilder().setNumber(number).build());
        }

        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doMaximum(ManagedChannel channel) throws InterruptedException {
        CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<MaximumRequest> stream = stub.maximum(new StreamObserver<MaximumResponse>() {
            @Override
            public void onNext(MaximumResponse maximumResponse) {
                System.out.println(maximumResponse.getValue());
            }

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        Arrays.asList(1, 5, 3, 6, 2, 20).forEach(number ->
                stream.onNext(MaximumRequest.newBuilder().setNumber(number).build())
        );

        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doSqrt(ManagedChannel channel) {
        System.out.println("Entered sqrt");

        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        SqrtResponse response = stub.sqrt(SqrtRequest.newBuilder().setNumber(25).build());

        System.out.println("Sqrt 25 = " + response.getResult());

        try {
            response = stub.sqrt(SqrtRequest.newBuilder().setNumber(-25).buildPartial());
            System.out.println("Sqrt -25 = " + response.getResult());
        } catch (RuntimeException ex) {
            System.out.println("Got an Exception for sqrt");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50052)
                .usePlaintext()
                .build();

        switch (args[0]) {
            case "sum": doCalculation(channel); break;
            case "decompose": doPrimeDecomposition(channel); break;
            case "average": doAverage(channel); break;
            case "maximum": doMaximum(channel); break;
            case "sqrt": doSqrt(channel); break;
            default:
                System.out.println("Invalid argument: " + args[0]);
        }

        System.out.println("Shutting down channel");
        channel.shutdown();
    }
}
