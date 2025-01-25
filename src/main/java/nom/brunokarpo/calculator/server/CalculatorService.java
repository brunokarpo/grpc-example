package nom.brunokarpo.calculator.server;

import io.grpc.stub.StreamObserver;
import nom.brunokarpo.grpc.calculator.*;

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

    @Override
    public void primes(PrimeRequest request, StreamObserver<PrimeResponse> responseObserver) {
        long base = request.getNumber();
        int k = 2;
        while (base > 1) {
            if (base % k == 0) {
                responseObserver.onNext(PrimeResponse.newBuilder().setPrimeFactor(k).build());
                base /= k;
            } else {
                k++;
            }
        }
        responseObserver.onCompleted();
    }
}
