package main

import (
	"fmt"
	"log"
	"time"

	"github.com/NenadPantelic/grpc-go/greet/proto"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials"
	"google.golang.org/grpc/credentials/insecure"
)

var addr string = "localhost:50051"

func main() {
	tls := true
	opts := []grpc.DialOption{}
	// secure by default (TLS)
	// for now, we will do an insecure connection
	// use with grpc.WithTransportCredentials(insecure.NewCredentials())
	// in the future, we will add TLS
	if tls {
		log.Println("TLS enabled")
		// load the certificates from disk
		certFile := "ssl/ca.crt"
		creds, err := credentials.NewClientTLSFromFile(certFile, "")
		if err != nil {
			log.Fatalf("Error while loading CA trust certificate: %v", err)
		}

		opts = append(opts, grpc.WithTransportCredentials(creds))
	} else {
		creds := grpc.WithTransportCredentials(insecure.NewCredentials())
		opts = append(opts, creds)
	}

	conn, err := grpc.Dial(addr, opts...)
	if err != nil {
		log.Fatalf("could not connect: %v", err)
	}
	defer conn.Close()

	c := proto.NewGreetServiceClient(conn)
	fmt.Printf("Created client: %v\n", c)

	// doGreet(c)
	// doGreetManyTimes(c)
	// doLongGreet(c)
	// doGreetEveryone(c)
	// doGreetWithError(c, "Nenad")
	// doGreetWithError(c, "")
	doGreetWithDeadline(c, 5*time.Second) // should complete
	doGreetWithDeadline(c, 1*time.Second) // should timeout
}
