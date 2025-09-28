import google.protobuf.json_format as json_format

import proto.simple_pb2 as simple_pb2
import proto.complex_pb2 as complex_pb2
import proto.enum_pb2 as enum_pb2
import proto.oneof_pb2 as oneof_pb2
import proto.map_pb2 as map_pb2


def simple():
    return simple_pb2.Simple(id=42, is_simple=True, name="Nenad", sample_list=[1, 2, 3])


def dummy(_id: int, name: str):
    return complex_pb2.Dummy(id=_id, name=name)


def complex():
    complex = complex_pb2.Complex()
    complex.one_dummy.id = 50
    ####  OR
    return complex_pb2.Complex(
        one_dummy=dummy(25, "Nenad"),
        multiple_dummies=[dummy(1, "N"), dummy(2, "P"), dummy(3, "NP")],
    )


def enum():
    enum = enum_pb2.Enumeration()
    enum.eye_color = enum_pb2.EYE_COLOR_BLUE
    return enum


def enum_with_ord():
    enumeration = enum_pb2.Enumeration()
    enumeration.eye_color = 1
    return enumeration
    # return enum_pb2.Enumeration(1)


def oneof(message=None, _id=None):
    return oneof_pb2.Result(message=message, id=_id)


def idwrapper(value):
    idwrapper = map_pb2.IdWrapper()
    idwrapper.id = value
    return idwrapper


def protomap():
    protomap = map_pb2.MapExample()
    protomap.ids["1"].id = 1
    protomap.ids["2"].id = 2
    protomap.ids["3"].id = 3
    return protomap


def write_to_file(message, path):
    print(f"Write to file {path}")

    with open(path, "wb") as fout:
        serialized_message = message.SerializeToString()
        fout.write(serialized_message)


def read_from_file(path, message):
    print(f"Read from file: {path}")

    with open(path, "rb") as fin:
        return type(message).FromString(fin.read())


def to_json(message):
    return json_format.MessageToJson(
        message, indent=None, preserving_proto_field_name=True
    )


def from_json(message, _type):
    return json_format.Parse(message, _type(), ignore_unknown_fields=True)


if __name__ == "__main__":
    # print(simple())
    # print(complex())
    # print(enum())
    # print(enum_with_ord())

    # oneof_with_id = oneof(_id=29)
    # print(oneof_with_id)

    # oneof_with_message = oneof(message="Hello World")
    # print(oneof_with_message)

    # one_of_with_both = oneof_pb2.Result()
    # one_of_with_both.message = "Nenad"
    # print(one_of_with_both)

    # one_of_with_both.id = 123
    # print(one_of_with_both)

    # print(protomap())

    # complex_message = complex()
    # file_path = "complex.bin"

    # write_to_file(complex_message, file_path)
    # deserialized_complex_message = read_from_file(file_path, complex_pb2.Complex())
    # print(deserialized_complex_message)

    serialized_complex = to_json(complex())
    print(serialized_complex)

    complex_as_json = from_json(serialized_complex, complex_pb2.Complex)
    print(complex_as_json)
