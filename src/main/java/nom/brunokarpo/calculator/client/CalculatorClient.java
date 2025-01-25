package nom.brunokarpo.calculator.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import nom.brunokarpo.grpc.calculator.CalculatorRequest;
import nom.brunokarpo.grpc.calculator.CalculatorResponse;
import nom.brunokarpo.grpc.calculator.CalculatorServiceGrpc;
import nom.brunokarpo.grpc.calculator.PrimeRequest;

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

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50052)
                .usePlaintext()
                .build();

        switch (args[0]) {
            case "sum": doCalculation(channel); break;
            case "decompose": doPrimeDecomposition(channel); break;
            default:
                System.out.println("Invalid argument: " + args[0]);
        }

        System.out.println("Shutting down channel");
        channel.shutdown();
    }
}
