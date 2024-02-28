package com.example.Wallet.grpcClient;

import com.example.Wallet.entities.Money;
import com.example.Wallet.enums.Currency;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import proto.ConvertRequest;
import proto.ConvertResponse;
import proto.ConverterServiceGrpc;


public class CurrencyConverterClient {

    private final String converterHost = "localhost";

    private final int port = 9090;



    public ConvertResponse convertMoney(Money money, Currency sourceCurrency, Currency targetCurrency){
        ManagedChannel channel = ManagedChannelBuilder.forAddress(converterHost, port)
                .usePlaintext().build();

        ConverterServiceGrpc.ConverterServiceBlockingStub stub = ConverterServiceGrpc.newBlockingStub(channel);
        ConvertRequest request = ConvertRequest.newBuilder().setMoney(proto.Money.newBuilder().setAmount((float) money.getAmount()).setCurrency(money.getCurrency().toString()).build())
                .setSourceCurrency(sourceCurrency.toString()).setTargetCurrency(targetCurrency.toString()).build();
        var response = stub.convertMoney(request);
        channel.shutdown();
        return response;
    }

}
