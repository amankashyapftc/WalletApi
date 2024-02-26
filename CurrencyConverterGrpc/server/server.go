package main

import (
	currency_conversion "CurrencyConverterGrpc/proto"
	"log"
	"net"
	"os"
	"os/signal"
	"syscall"

	"github.com/gin-gonic/gin"
	"google.golang.org/grpc"
)

func main() {
	// Start gRPC server
	go func() {
		if err := createGrpcServer(); err != nil {
			log.Fatalf("Failed to start gRPC server: %v", err)
		}
	}()

	// Start HTTP server
	router := gin.Default()
	router.GET("/health", func(c *gin.Context) {
		c.String(200, "health")
	})

	go func() {
		if err := router.Run(":8081"); err != nil {
			log.Fatalf("Failed to start HTTP server: %v", err)
		}
	}()

	// Wait for termination signal
	sigCh := make(chan os.Signal, 1)
	signal.Notify(sigCh, syscall.SIGINT, syscall.SIGTERM)
	<-sigCh
	log.Println("Received termination signal. Shutting down servers...")
}

func createGrpcServer() error {
	lis, err := net.Listen("tcp", ":9090")
	if err != nil {
		return err
	}
	defer lis.Close()

	grpcServer := grpc.NewServer()
	converterServer := currency_conversion.Server{}
	currency_conversion.RegisterConverterServer(grpcServer, &converterServer)

	log.Println("gRPC server started on port 9090")
	return grpcServer.Serve(lis)
}
