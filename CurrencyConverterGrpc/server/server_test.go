package main

import (
	pb "CurrencyConverterGrpc/proto"
	"context"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestConvertUSDToINR(t *testing.T) {
	srv := &server{}
	req := &pb.ConvertRequest{
		Money: &pb.Money{
			Currency: "USD",
			Amount:   100,
		},
		SourceCurrency: "USD",
		TargetCurrency: "INR",
	}
	ctx := context.Background()
	res, err := srv.ConvertMoney(ctx, req)
	if err != nil {
		t.Errorf("Error converting currency: %v", err)
	}
	if res == nil {
		t.Error("Convert response is nil")
	}
	expected := &pb.ConvertResponse{
		Money: &pb.Money{
			Currency: "INR",
			Amount:   8310,
		},
		ServiceCharge: &pb.Money{
			Currency: "INR",
			Amount:   10,
		},
	}

	assert.Equal(t, expected, res)
	assert.NoError(t, err)
}

func TestConvertINRToUSD(t *testing.T) {
	srv := &server{}
	req := &pb.ConvertRequest{
		Money: &pb.Money{
			Currency: "INR",
			Amount:   831,
		},
		SourceCurrency: "INR",
		TargetCurrency: "USD",
	}
	ctx := context.Background()

	res, err := srv.ConvertMoney(ctx, req)
	if err != nil {
		t.Errorf("Error converting currency: %v", err)
	}
	if res == nil {
		t.Error("Conver response is nil")
	}
	expected := &pb.ConvertResponse{
		Money: &pb.Money{
			Currency: "USD",
			Amount:   10,
		},
		ServiceCharge: &pb.Money{
			Currency: "INR",
			Amount:   10,
		},
	}

	assert.Equal(t, expected, res)
	assert.NoError(t, err)
}
