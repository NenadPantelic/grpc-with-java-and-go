package main

import (
	"fmt"
	"log"

	pb "github.com/NenadPantelic/grpc-go/greet/proto"
)

func (*Server) GreetManyTimes(req *pb.GreetRequest, stream pb.GreetService_GreetManyTimesServer) error {
	log.Printf("GreetManyTimes function was invoked with %v", req)

	for i := 0; i < 10; i++ {
		res := fmt.Sprintf("Hello %s number %d", req.GetFirstName(), i)

		stream.Send(&pb.GreetResponse{
			Result: res,
		})
	}

	return nil
}
