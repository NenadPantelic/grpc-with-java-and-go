package main

import (
	"context"
	"log"
	"net"

	pb "github.com/NenadPantelic/grpc-go/blog/proto"

	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
	"google.golang.org/grpc"
)

var addr string = "0.0.0.0:50051"

func main() {
	// Initialize the gRPC server
	client, err := mongo.NewClient(options.Client().ApplyURI("mongodb://root:root@localhost:27017/"))
	if err != nil {
		log.Fatal(err)
	}

	err = client.Connect(context.Background())
	if err != nil {
		log.Fatal(err)
	}

	collection = client.Database("blogdb").Collection("blog")
	lis, err := net.Listen("tcp", addr)
	if err != nil {
		log.Fatal(err)
	}

	log.Println("Server is listening on port", addr)
	s := grpc.NewServer()
	pb.RegisterBlogServiceServer(s, &Server{})

	if err := s.Serve(lis); err != nil {
		log.Fatalf("Failed to serve: %v\n", err)
	}
}
