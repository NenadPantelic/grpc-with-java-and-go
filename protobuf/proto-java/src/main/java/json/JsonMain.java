package json;

import com.google.protobuf.InvalidProtocolBufferException;
import example.simple.SimpleOuterClass;

import com.google.protobuf.util.JsonFormat;

import java.util.List;

public class JsonMain {

    private static final JsonFormat.Printer PRINTER = JsonFormat.printer()
            // removes unneeded namespace
            .omittingInsignificantWhitespace()
            // sets the default value for all fields which value is not set explicitly
            .includingDefaultValueFields();
    private static final JsonFormat.Parser PARSER = JsonFormat.parser();

    private static String toJson(SimpleOuterClass.Simple message) throws InvalidProtocolBufferException {
        return PRINTER.print(message);
    }

    private static SimpleOuterClass.Simple fromJson(String json) throws InvalidProtocolBufferException {
        SimpleOuterClass.Simple.Builder builder = SimpleOuterClass.Simple.newBuilder();
        PARSER.merge(json, builder);
        return builder.build();
    }

    public static void main(String[] args) throws InvalidProtocolBufferException {
        SimpleOuterClass.Simple message = SimpleOuterClass.Simple.newBuilder()
                .setId(42)
                .setIsSimple(true)
                .setName("Nenad")
                .addSampleList(25)
                .addSampleList(35)
                .addSampleList(45)
                .addAllSampleList(List.of(1,2,3))
                .build();
        String json = toJson(message);
        System.out.println("Serialized message (JSON): " + json);

        SimpleOuterClass.Simple deserializedMessage = fromJson(json);
        System.out.println("Deserialized message: " + deserializedMessage);
    }
}
