package com.example.Wallet.grpcClient;

import currencyconversion.ConvertRequest;
import currencyconversion.ConvertResponse;
import currencyconversion.ConverterGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CurrencyConverterClient {
    @Value("${helloGrpcServiceHost:localhost}")
    private String helloGrpcServiceHost;

    @Value("${helloGrpcServicePort:9090}")
    private int helloGrpcServicePort;



    private final static Logger log = LoggerFactory.getLogger(CurrencyConverterClient.class);


    public ConvertResponse convertCurrency(String baseCurrency, String targetCurrency, double amount) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(helloGrpcServiceHost, helloGrpcServicePort)
                .usePlaintext()
                .build();

        ConverterGrpc.ConverterBlockingStub stub = ConverterGrpc.newBlockingStub(channel);
        ConvertRequest request = ConvertRequest.newBuilder()
                .setBaseCurrency(baseCurrency)
                .setTargetCurrency(targetCurrency)
                .setAmount(amount)
                .build();

        ConvertResponse response = stub.convertCurrency(request);
        log.info("Server message: {}",response);
        channel.shutdown();
        return response;
    }

}
