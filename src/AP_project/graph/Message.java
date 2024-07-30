//package AP_project.test;
package AP_project.graph;

import java.util.Date;
import java.nio.charset.StandardCharsets;

public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

    public Message(String asText) {
        double asDouble1;
        this.asText = asText;
        this.data = asText.getBytes(StandardCharsets.UTF_8);
        try{
            asDouble1 = Double.parseDouble(asText);
        } catch(NumberFormatException e) {
            asDouble1 = Double.NaN;
        }

        this.asDouble = asDouble1;
        this.date = new Date();

    }

    public Message(byte[] data) {
        this(new String(data, StandardCharsets.UTF_8));
    }

    public Message(Double asDouble) {
        this(Double.toString(asDouble));
    }

    public Message(int asInt){
        this(Integer.toString(asInt));
    }

}

