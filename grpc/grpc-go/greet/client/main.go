package main

import (
	"fmt"
	"log"

	"github.com/NenadPantelic/grpc-go/greet/proto"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

var addr string = "localhost:50051"

func main() {
	// secure by default (TLS)
	// for now, we will do an insecure connection
	// use with grpc.WithTransportCredentials(insecure.NewCredentials())
	// in the future, we will add TLS
	conn, err := grpc.Dial(addr, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf("could not connect: %v", err)
	}
	defer conn.Close()

	c := proto.NewGreetServiceClient(conn)
	fmt.Printf("Created client: %v\n", c)

	// doGreet(c)
	// doGreetManyTimes(c)
	// doLongGreet(c)
	doGreetEveryone(c)
}
