package AP_project.configs;

import AP_project.graph.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * The GenericConfig class implements the Config interface and reads agent configurations from a file.
 * It creates and manages instances of Agent based on the configuration.
 */
public class GenericConfig implements Config {
    private String conFile; // Configuration file path
    private final List<Agent> agents = new ArrayList<>(); // List to hold created agents

    /**
     * Reads configuration from a file and creates agents based on the file content.
     */
    @Override
    public void create() {
        System.out.println("Creating GenericConfig from file: " + conFile);

        try (BufferedReader reader = new BufferedReader(new FileReader(conFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    System.out.println("Skipping empty line");
                    continue;
                }

                // Skip lines starting with "Content-Type:"
                if (line.trim().startsWith("Content-Type:")) {
                    System.out.println("Skipping Content-Type line");
                    continue;
                }

                // Process the agent class name
                System.out.println("Reading agent class name: " + line);
                String agentClassName = line.trim();

                // Read the subscriptions and publications lines
                String subsLine = reader.readLine();
                String pubsLine = reader.readLine();

                // Check if the lines are properly formatted
                if (subsLine == null || pubsLine == null || subsLine.trim().isEmpty() || pubsLine.trim().isEmpty()) {
                    throw new IOException("Configuration file is not properly formatted.");
                }

                System.out.println("Subscriptions: " + subsLine);
                System.out.println("Publications: " + pubsLine);

                // Split the subscription and publication topics
                String[] subs = subsLine.split(",");
                String[] pubs = pubsLine.split(",");

                // Create an instance of the agent class using reflection
                Class<?> clazz = Class.forName(agentClassName);
                Constructor<?> constructor = clazz.getConstructor(String[].class, String[].class);
                Agent agent = (Agent) constructor.newInstance((Object) subs, (Object) pubs);

                System.out.println("Created agent: " + agent.getName());
                for (String sub : subs) {
                    System.out.println("Agent " + agent.getName() + " subscribes to: " + sub);
                }
                for (String pub : pubs) {
                    System.out.println("Agent " + agent.getName() + " publishes to: " + pub);
                }

                // Wrap the agent in a ParallelAgent and add to the list
                ParallelAgent parallelAgent = new ParallelAgent(agent, 10);
                agents.add(parallelAgent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error processing configuration file: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "GenericConfig";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void close() {
        for (Agent agent : agents) {
            agent.close();
        }
    }

    /**
     * Sets the path to the configuration file.
     * @param conFile The configuration file path.
     */
    public void setConfFile(String conFile) {
        this.conFile = conFile;
    }
}
