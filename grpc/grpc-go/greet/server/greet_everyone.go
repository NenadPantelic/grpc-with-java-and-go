package main

import (
	"io"
	"log"

	pb "github.com/NenadPantelic/grpc-go/greet/proto"
)

func (*Server) GreetEveryone(stream pb.GreetService_GreetEveryoneServer) error {
	log.Println("GreetEveryone function was invoked with a streaming request")

	for {
		req, err := stream.Recv()
		if err == io.EOF {
			break
		}

		if err != nil {
			log.Fatalf("Error while reading client stream: %v\n", err)
		}

		log.Printf("Received message from client: %v\n", req)
		res := &pb.GreetResponse{
			Result: "Hello " + req.FirstName,
		}
		if err := stream.Send(res); err != nil {
			log.Fatalf("Error while sending data to client: %v\n", err)
		}
	}

	return nil
}
