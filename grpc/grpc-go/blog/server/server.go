package main

import pb "github.com/NenadPantelic/grpc-go/blog/proto"

type Server struct {
	pb.BlogServiceServer
}
