package AP_project.configs;

import AP_project.graph.*;
import java.util.function.BinaryOperator;

/**
 * The BinOpAgent class implements the Agent interface and performs binary operations on inputs from two topics.
 * It publishes the result to an output topic.
 */
public class BinOpAgent implements Agent {
    private final String name;
    private final Topic inputTopic1;
    private final Topic inputTopic2;
    private final Topic outputTopic;
    private final BinaryOperator<Double> operation;
    private Double input1Value = null; // Initialize as null
    private Double input2Value = null; // Initialize as null
    private final TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();

    /**
     * Constructs a BinOpAgent with the specified parameters.
     * @param name The name of the agent.
     * @param inputTopicName1 The name of the first input topic.
     * @param inputTopicName2 The name of the second input topic.
     * @param outputTopicName The name of the output topic.
     * @param operation The binary operation to perform on the inputs.
     */
    public BinOpAgent(String name, String inputTopicName1, String inputTopicName2, String outputTopicName, BinaryOperator<Double> operation) {
        this.name = name;
        this.operation = operation;

        this.inputTopic1 = topicManager.getTopic(inputTopicName1);
        this.inputTopic2 = topicManager.getTopic(inputTopicName2);
        this.outputTopic = topicManager.getTopic(outputTopicName);

        this.inputTopic1.subscribe(this);
        this.inputTopic2.subscribe(this);
        this.outputTopic.addPublisher(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        input1Value = 0.0;
        input2Value = 0.0;
    }

    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(inputTopic1.getName())) {
            input1Value = msg.asDouble;
        } else if (topic.equals(inputTopic2.getName())) {
            input2Value = msg.asDouble;
        }

        if (input1Value != null && input2Value != null) {
            Double result = operation.apply(input1Value, input2Value);
            outputTopic.publish(new Message(result));
            //reset(); // Reset after publishing if needed
        }
    }

    @Override
    public void close() {
        inputTopic1.unsubscribe(this);
        inputTopic2.unsubscribe(this);
    }
}
