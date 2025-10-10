package main

import (
	"context"
	"log"

	pb "github.com/NenadPantelic/grpc-go/greet/proto"
)

func doGreet(c pb.GreetServiceClient) {
	log.Println("Invoking doGreet...")

	req := &pb.GreetRequest{
		FirstName: "Nenad",
	}

	res, err := c.Greet(context.Background(), req)
	if err != nil {
		log.Fatalf("Error while calling Greet: %v", err)
	}

	log.Printf("Response from Greet: %v", res)
}
