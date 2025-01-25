package nom.brunokarpo.calculator.server;

import io.grpc.stub.StreamObserver;
import nom.brunokarpo.grpc.calculator.CalculatorRequest;
import nom.brunokarpo.grpc.calculator.CalculatorResponse;
import nom.brunokarpo.grpc.calculator.CalculatorServiceGrpc;

public class CalculatorService extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void sum(CalculatorRequest request, StreamObserver<CalculatorResponse> responseObserver) {
        System.out.println("Received a calculation request");
        int firstNumber = request.getFirstNumber();
        int secondNumber = request.getSecondNumber();
        System.out.println("firstNumber: " + firstNumber + ", secondNumber: " + secondNumber);
        responseObserver.onNext(CalculatorResponse.newBuilder().setResult(firstNumber + secondNumber).build());
        responseObserver.onCompleted();
        System.out.println("response sent");
    }
}
