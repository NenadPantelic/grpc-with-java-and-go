package main

import (
	"log"

	pb "github.com/NenadPantelic/grpc-go/blog/proto"

	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

var addr string = "localhost:50051"

func main() {
	// Set up a connection to the gRPC server
	conn, err := grpc.Dial(addr, grpc.WithTransportCredentials(insecure.NewCredentials()))

	if err != nil {
		log.Fatalf("Could not connect: %v", err)
	}

	defer conn.Close()

	c := pb.NewBlogServiceClient(conn)

	// Use the client 'c' to call RPC methods
	id := createBlog(c)
	log.Printf("Blog was created with ID: %s\n", id)

	blog := readBlog(c, id)
	log.Printf("Blog was read: %v\n", blog)

	readBlog(c, "non-existing-id")

	updateBlog(c, id)

	blog = readBlog(c, id)
	log.Printf("Blog after update: %v\n", blog)

	listBlog(c)
	deleteBlog(c, id)
}
