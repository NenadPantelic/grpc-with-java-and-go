package main

import (
	"io"
	"log"

	pb "github.com/NenadPantelic/grpc-go/greet/proto"
)

func (*Server) LongGreet(stream pb.GreetService_LongGreetServer) error {
	log.Println("LongGreet function was invoked with a streaming request")

	result := ""

	for {
		req, err := stream.Recv()
		if err == io.EOF {
			// we have finished reading the client stream
			// Send the response back to the client
			return stream.SendAndClose(&pb.GreetResponse{
				Result: result,
			})
		}

		if err != nil {
			log.Fatalf("Error while reading client stream: %v", err)
		}

		log.Printf("Receiving req: %v\n", req)
		result += "Hello " + req.GetFirstName() + "! "
	}

}
