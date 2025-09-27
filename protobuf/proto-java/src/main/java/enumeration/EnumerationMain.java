package enumeration;

import example.enumerations.Enumerations;

public class EnumerationMain {

    public static void main(String[] args) {
        Enumerations.Enumeration message = Enumerations.Enumeration.newBuilder()
                .setEyeColor(Enumerations.EyeColor.EYE_COLOR_BROWN)
                .setEyeColorValue(2) // the int value of an enum; overwrite to BLUE
                .build();

        System.out.println("Message: " + message);
    }
}
