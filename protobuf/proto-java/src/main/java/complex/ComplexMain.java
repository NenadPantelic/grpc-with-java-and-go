package complex;

import example.complex.ComplexOuterClass;

public class ComplexMain {

    private static ComplexOuterClass.Dummy newDummy(int id, String name) {
        return ComplexOuterClass.Dummy.newBuilder()
                .setId(id)
                .setName(name)
                .build();
    }

    public static void main(String[] args) {
        ComplexOuterClass.Complex message = ComplexOuterClass.Complex.newBuilder()
                .setOneDummy(newDummy(55, "One Dummy"))
                .addDummies(newDummy(66, "Two Dummy"))
                .addDummies(newDummy(77, "Three Dummy"))
                .addDummies(newDummy(88, "Four Dummy"))
                .addDummies(newDummy(99, "Five Dummy"))
                .build();

        System.out.println("Message: " + message);
    }
}
