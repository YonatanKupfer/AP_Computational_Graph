package AP_project.graph;

import java.util.Date;
import java.nio.charset.StandardCharsets;

/**
 * The Message class represents a message with multiple representations:
 * as text, as a double, and as a byte array. It also records the time
 * the message was created.
 */
public class Message {
    /**
     * The message content as a byte array.
     */
    public final byte[] data;

    /**
     * The message content as text.
     */
    public final String asText;

    /**
     * The message content as a double.
     */
    public final double asDouble;

    /**
     * The date and time when the message was created.
     */
    public final Date date;

    /**
     * Constructs a Message from a text string.
     * @param asText The message content as text.
     */
    public Message(String asText) {
        double asDoubleValue;
        this.asText = asText;
        this.data = asText.getBytes(StandardCharsets.UTF_8);
        try {
            asDoubleValue = Double.parseDouble(asText);
        } catch (NumberFormatException e) {
            asDoubleValue = Double.NaN;
        }
        this.asDouble = asDoubleValue;
        this.date = new Date();
    }

    /**
     * Constructs a Message from a byte array.
     * @param data The message content as a byte array.
     */
    public Message(byte[] data) {
        this(new String(data, StandardCharsets.UTF_8));
    }

    /**
     * Constructs a Message from a double value.
     * @param asDouble The message content as a double.
     */
    public Message(Double asDouble) {
        this(Double.toString(asDouble));
    }

    /**
     * Constructs a Message from an integer value.
     * @param asInt The message content as an integer.
     */
    public Message(int asInt) {
        this(Integer.toString(asInt));
    }
}
