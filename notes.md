# gRPC with Java and Go

## Protocol Buffers

- An evolution of data:

1. CSV
   - advantages: easy to read, easy to parse, contextual
   - disadvantages: type inferred, data with comma (what if string contains comma), column names are missing
2. Database tables
   - advantages: typed data, focus on the schema (data structure and integrity)
   - disadvantages: no list, array,... (some of them support that + JSON etc), schema sharing (across different databases), ORM (extra overhead, more resources, clutters our code)
3. JSON
   - advantages: easily shareable, arrays, high support (widely accepted by languages), dynamic (flexible schema)
   - disadvantages: dynamic schema (backward and forward compatibilty) can break JSON deserialization, redundancy, indentation hell (bigger payloads), no comments (cannot be documented)
4. Protocol buffers
   - advantages: typed data, generates code for us, schema evolution (backward and forward compatibility), comments (as doc), binary format (smaller, less CPU intensive to parse - closer to machine)
   - disadvantages: binary (not readable by default), less support
   - numbers:
     - 3-10x smaller, 20-100x faster than XML
     - 34% smaller, 21% less time to be available (JS - uncompressed JSON)
     - 9% smaller, 4% less time to be available (JS - compressed JSON)
     - 48000 messages in 12000 proto files (Google)

- used by gRPC

- An example of a protobuf

```proto

syntax = "proto3"

message Account {
  uint32 id = 1;
  string name = 2;
  bool is_verified = 3;
}
```

- when you do not set a value to a field, this field will not be serialized (empty data is overhead)
- we can populate with default value

#### Scalar types

1. Numbers

- int32, int64, sint32, sint64 - signed
- uint32, uint64 - unsigned
- fixed32, fixed64, sfixed32, sfixed64 - fixed (it will take a fixed number of bytes when serialized)
- float, double
- default value: 0

2. Boolean

- keyword: bool
- value: true or false
- default value: false

3. String

- keyword: string
- value: arbitrary length text
- default: empty string
- UTF-8 encoded or 7-bit ASCII

4. Byte

- keyword: bytes
- value: arbitrary length sequence
- default value: empty bytes

- Tags

  - field names are not important for serialization, but tags are
  - smallest tag: 1
  - largest tag: 536 870 911
  - reserved tags: 19000 to 19999
  - 1 - 15 -> 1 byte (for the most populated field in schema)
  - 16 - 2047 -> 2 bytes
  - ...

- Repeated fields
  - keyword: `repeated <type><name>=<tag>;`
  - value: any number of elements (0 or more)
  - default value: empty
- Enum

  - keyword: enum
  - default value: first value of enum
  - **the first tag is 0**

- Comments

  - `//` - single line comment
  - `/**/` - multiline comments

- protoc use: `protoc --java_out=java --python_out=python proto/simple.proto`

## gRPC introduction

- Microservices have to agree on:

  - API to exchange data
  - data format
  - error patterns

- API troubles:
  - payload size
  - latency
  - scalability
  - load balancing
  - auth
  - monitoring
  - logging
- gRPC is a free open-source framework developed by Google and now is part of the Cloud Native Computation Foundation
- built on top of HTTP/2, low latency and supports data streaming
- RPC - Remote Procedure Call - it seems like calling a function on the remote server

### Protocol Buffers & Language Interoperability

```proto

syntax = "proto3";

message Greeting {
  string first_name = 1;
}

message GreetRequest {
  Greeting greeting = 1;
}

message GreetResponse {
  string result = 1;
}

service GreetService {
  rpc Greet(GreetRequest) returns (GreetResponse) {}
}

```
