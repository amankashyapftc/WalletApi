package main

import (
	pb "CurrencyConverterGrpc/proto"
	"context"
	"flag"
	"fmt"
	"log"
	"net"

	"google.golang.org/grpc"
)

type Currency string

const (
	INR Currency = "INR"
	USD Currency = "USD"
)

var ConversionFactorMap = map[Currency]float32{
	INR: 1.0,
	USD: 83.10,
}

func (c Currency) GetConversionFactor() float32 {
	return ConversionFactorMap[c]
}

type server struct {
	pb.UnimplementedConverterServiceServer
}

func (s *server) ConvertMoney(ctx context.Context, req *pb.ConvertRequest) (*pb.ConvertResponse, error) {
	log.Printf("Inside convert function.")
	currency := req.Money.Currency
	amount := req.Money.Amount
	targetCurrency := req.TargetCurrency
	sourceCurrency := req.SourceCurrency

	convertedAmount := amount
	serviceCharge := &pb.Money{Currency: "INR", Amount: 0.0}
	if currency != targetCurrency || currency != sourceCurrency {
		convertedAmount = amount / Currency(targetCurrency).GetConversionFactor() * Currency(currency).GetConversionFactor()
		serviceCharge = &pb.Money{Currency: targetCurrency, Amount: 10.0 / Currency(targetCurrency).GetConversionFactor()}
	}

	res := &pb.ConvertResponse{
		Money: &pb.Money{
			Currency: targetCurrency,
			Amount:   convertedAmount,
		},
		ServiceCharge: serviceCharge,
	}
	return res, nil
}

var (
	port = flag.Int("port", 9090, "gRPC server port")
)

func main() {
	fmt.Println("gRPC server started")

	lis, err := net.Listen("tcp", fmt.Sprintf(":%d", *port))

	if err != nil {
		log.Fatalf("Failed to listen: %v", err)
	}

	s := grpc.NewServer()
	pb.RegisterConverterServiceServer(s, &server{})

	log.Printf("Server listening at %v", lis.Addr())

	if err := s.Serve(lis); err != nil {
		log.Fatalf("failed to serve : %v", err)
	}
}
