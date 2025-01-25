package nom.brunokarpo.calculator.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import nom.brunokarpo.grpc.calculator.CalculatorRequest;
import nom.brunokarpo.grpc.calculator.CalculatorResponse;
import nom.brunokarpo.grpc.calculator.CalculatorServiceGrpc;

public class CalculatorClient {

    private static void doCalculation(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        CalculatorRequest request = CalculatorRequest.newBuilder().setFirstNumber(10).setSecondNumber(3).build();
        CalculatorResponse response = stub.sum(request);
        System.out.println("10 + 3 = " + response.getResult());
    }

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        doCalculation(channel);

        System.out.println("Shutting down channel");
        channel.shutdown();
    }
}
