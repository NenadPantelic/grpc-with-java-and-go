package main

import (
	"fmt"
	"io/ioutil"
	"log"

	"google.golang.org/protobuf/proto"
)

func writeToFile(fname string, pb proto.Message) error {
	out, err := proto.Marshal(pb)
	if err != nil {
		log.Fatalf("Could not serialize %v. Error: %v", pb, err)
		return err
	}

	if err = ioutil.WriteFile(fname, out, 0644); err != nil {
		log.Fatalf("Could not write to a file. Error: %v", err)
		return err
	}

	fmt.Println("Proto message has been written")
	return nil
}

func readFromFile(fname string, pb proto.Message) (proto.Message, error) {
	in, err := ioutil.ReadFile(fname)

	if err != nil {
		log.Fatalln("Could not read from file %s. Error: %v", fname, err)
		return nil, err
	}

	if err = proto.Unmarshal(in, pb); err != nil {
		log.Fatalln("Could not deserialize %v to a valid proto message. Error: %v", in, err)
		return nil, err
	}

	return pb, nil

}
