package map;

import example.maps.Maps;

import java.util.Map;

public class MapMain {

    private static Maps.IdWrapper newIdWrapper(int id) {
        return Maps.IdWrapper.newBuilder().setId(id).build();
    }

    public static void main(String[] args) {
        Maps.MapExample message = Maps.MapExample.newBuilder()
                .putIds("id-11", newIdWrapper(11))
                .putIds("id-21", newIdWrapper(21))
                .putIds("id-31", newIdWrapper(31))
                .putAllIds(Map.of("id-41", newIdWrapper(41), "id-51", newIdWrapper(51)))
                .build();

        System.out.println("Message: " + message);
    }
}
