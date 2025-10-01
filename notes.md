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

### Data evolution

- Why changing:
  - new business requirements
  - we do not want breaking changes
- Backward compatible:
  Write:

```proto
syntax = "proto3";

message Account {
  string first_name = 1;
  string last_name = 2;
}
```

Read:

```proto

syntax = "proto3";

message Account {
  string first_name = 1;
  string last_name = 2;
  string phone = 2;
}
```

- if the client doesn't send the phone field, it should be deserialized with the default value for string, i.e. ""

- Forward compatible
  Write

```proto

syntax = "proto3";

message Account {
  string first_name = 1;
  string last_name = 2;
  string phone = 2;
}
```

Read

```proto
syntax = "proto3";

message Account {
  string first_name = 1;
  string last_name = 2;
}
```

- The same thing here, the client will send 3 fields (phone number included) and the server will deserialize only two (first name and last name) and the phone no will be set to an empty string
- Rules when updating protobuffs

  1. do not change tags!
  2. add new fields (old code will ignore them)
  3. use reserved tags when removing fields (it makes the field tag unusable (useless is not the right term :-)) in the future)
  4. before changing type: check the doc for data compatiblity; or add a new field with the type that you want (preferred)

- Renaming fields:

  - why: business requirement or just to make it more descriptive
  - the name of a field only matters in your code, it doesn't matter for serde; for serde, it only looks at the field tag
  - Example:
  - From this proto

  ```proto
  syntax = "proto3";

  message Account {
    uint32 id = 1;
    string first_name = 2;
  }
  ```

  - we want to get this

  ```proto
  syntax = "proto3";

  message Account {
    uint32 id = 1;
    string alias = 2;
  }
  ```

  - it will work of out-of-the-box - if a client sends the `first_name`, but the server expects alias, it will deserialized into `alias` and vice-versa, if the server expects `first_name`, but the client sends `alias`, it will be deserialized into `first_name` - if the tag is the same, it works out-of-the-box

- Removing a field

  - we have this proto:

  ```proto
  syntax = "proto3";

  message Account {
    uint32 id = 1;
    string first_name = 2;
  }
  ```

  - we want to get this

  ```proto
  syntax = "proto3";

  message Account {
    reserved 2;
    reserved "first_name"; // optional
    uint32 id = 1;
  }
  ```

  - `reserved 2` - means that tag 2 is reserved and it cannot be used anymore, we do that to prevent if some other field in the future gets this tag. e.g. `phone_number`, we do not end up serializing/deserializing `first_name` as `phone_number` and the opposite
  - we can also reserve the field name, if `first_name` is reserved, it cannot be used anymore in proto (maybe for some other purpose), but this is optional
  - when some tag is reserved, and the client sends it, it will be just skipped

- Reserved fields

```proto
syntax = "proto3";

message Account  {
  reserved 2, 15, 9 to 11; // 2, 9, 10, 11 and 15
  reserved "first_name", "last_name"; // cannot reserve a field tag and a field name on the same line
  uint32 id = 1;
}
```

- Default values:
  - Good things
    - enables forward and backward compatibility
    - avoiding non-null values
  - Bad things
    - cannot differentiate missing or unset (empty string is an empty string or a missing value???)
- Do not give any business meaning to a defalt value
- check them with if or switch statements in your code (to return an error)

### Advanced protoc

- Decode bin and print tag:value pairs (works with stdin and stdout): `protoc --decode_raw` -> `cat simple.bin | protoc --decode_raw`
- Decode message of a given type; we use the binary input with the message type and a proto file: `cat simple.bin | protoc --decode=Simple simple.proto `
- NOTE: if the message type is defined in a package, we must use it, otherwise it won't work: `cat simple.bin | protoc --decode=simple.Simple simple.proto`; the same goes for encode
- Encode a txt input (name:value, one pair by line) and encodes it to a proto: `cat simple.txt | protoc --encode=Simple simple.proto`

```
cat simple.bin | protoc --decode=Simple simple.proto  >> simple.txt
cat simple.txt | protoc --encode=Simple simple.proto
```

### Advanced protobuf

#### Integer types deep dive

1. Range (32 or 64 bit)
   1. int32: -2^31:2^31-1
   2. int64: -2^63:2^63-1
2. Signed or unsigned
   1. uint32: 0 to 2^32-1
   2. uint64: 0 to 2^64-1
   - int32 and int64 accept negative values, but they are not efficient at serializing them (in a negative value, the encoded value is ten bytes long)
   - sint32, sint64: accept negative values (less efficient at serializing positive values)
3. Varint or not
   - the default serializing method for integers is taking the least space possible (variable size)
   - if we are using int32, the smaller the value of that value results in a smaller amount of bytes when it is serialized; so if we have number 4, it can be serialized with 1 byte, but if we have negative number, ten bytes are used for that which is less efficient)
   - fixed32, sfixed32 - always 4 bytes long; or fixed64, sfixed64 - 8 bytes long

- **oneof field cannot be repeated**
- also, evolving schema is complicated with oneof (if we remove that field)
- map field cannot be repeated; we cannot use float, double, enum and message as a key in a map field; key ordering is not guaranteed
- Other useful types:
  - `google/protobuf/timestamp.proto` -> `google.protobuf.Timestamp created = 1;`
  - `google/protobuf/duration.proto` -> `google.protobuf.Duration validity = 2;`
- In `descriptor.proto` you can find the metadata of proto files (`FileOptions`, `MessageOptions` and `Options` are useful)
- Naming conventions (by Google; linters obey it):

  - file name: lower snake case -> `my_file.proto`
  - license should stay at the very top
  - then `syntax`
  - then imports, they should be ordered alphabetically
  - then options
  - then message, services and RPC endpoints (these should be written in Pascal case)
  - enums should be in upper snake case
  - for repeated fields, use plurals

- Service: generic in protocol buffers and it's not designed for serialization/deserialization, but for communication

  - a set of endpoints that are defining an API (contract for RPC framework like gRPC)
  - example:

  ```proto
  service FooService {
    rpc GetSomething(GetSomethingRequest) return (GetSomethingResponse);
    rpc ListSomething(ListSomethingRequest) return (ListSomethingResponse);
  }
  ```

##### Protocol Buffer internals

- https://protobuf.dev/programming-guides/encoding/

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

- More efficient (faster, binary, less CPU intensive, schema & types support)
- Languages supported:
  - Java
  - Go
  - C#
  - C (the following are based on C)
    - C++
    - Python
    - Ruby
    - ...

### HTTP/2

- Good demo: `imagekit.io`

- HTTP/1

  - opens one TCP connection per request
  - doesn't compress HTTP headers (bigger payload, increased latency)
  - one request/one response (e.g. it wants to get the multipart response, it will send 3 request and get 3 responses)

- HTTP/2
  - one TCP connection (long lasting connection which used/shared by multiple requests/responses)
  - server push - server can push multiple messages from one request from the client; client doesn't have to ask for more data, it just sends a request and waits, the server does it on its own, sends multiple messages when the data is ready
  - multiplexing - client and server can push multiple messages in parallel over the same TCP connection
  - headers and the data are both compressed to binary
  - required from the browser by default
  - one request/multiple responses (e.g. it wants to get the multipart response, it will send 1 request and get 3 responses); uses less bandwidth

### Types of API in gRPC

1. Unary - the client sends one request and the server returns one response
2. Server streaming - the client sends one request and the server returns one or more responses (e.g. to get updates, get a list of nearest taxis)
3. Client streaming - the client sends one or multiple requests and the server responds with one response (e.g. uploading or updating the information)
4. Bidirectional streaming the client sends one or multiple requests and the server responds with one or multiple responses (e.g. uploading or updating the information)

```proto

service GreetService {

  // unary
  rpc Greet(GreetRequest) returns (GreetResponse) {};

  // server streaming
  rpc GreetManyTimes(GreetRequest) returns (stream GreetResponse) {};

  // client streaming
  rpc LongGreet(stream GreetRequest) returns (GreetResponse) {};

  // Bidirectional streaming
  rpc GreetEveryone(stream GreetRequest) returns (stream GreetResponse) {};
}

```

- Scalability in gRPC
  - server: async, the main thread is not blocked
  - client: async or blocking
  - Google example: 10B requests/sec
- Security

  - schema based serialization (binary data, not human-readable)
  - easy SSL certificates initialization
  - interceptors for auth

- gRPC vs REST
  | gRPC | REST |
  |---|---|
  | Protocol Buffers | JSON |
  | HTTP/2 | Usually HTTP/1 |
  | Streaming | Unary |
  | Bidirectional | Client -> Server |
  | Free design | GET/POST/PUT/DELETE |
