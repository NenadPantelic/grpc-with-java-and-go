package main

import (
	"context"
	"io"
	"log"

	pb "github.com/NenadPantelic/grpc-go/greet/proto"
)

func doGreetManyTimes(client pb.GreetServiceClient) {
	log.Println("doGreetManyTimes was invoked")

	req := &pb.GreetRequest{
		FirstName: "Nenad",
	}

	resStream, err := client.GreetManyTimes(context.Background(), req)
	if err != nil {
		log.Fatalf("Error while calling GreetManyTimes RPC: %v", err)
	}

	for {
		msg, err := resStream.Recv()
		if err != nil {
			log.Fatalf("Error while receiving GreetManyTimes response: %v", err)
		}

		if err == io.EOF {
			// we've reached the end of the stream
			break
		}

		log.Printf("Received message from GreetManyTimes: %v", msg)

	}
}
