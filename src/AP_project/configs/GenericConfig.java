//package AP_project.test;
package AP_project.configs;

import AP_project.graph.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class GenericConfig implements Config {
    private String conFile;
    private final List<Agent> agents = new ArrayList<>();

    @Override
    public void create() {
        System.out.println("Creating GenericConfig from file: " + conFile);
        try (BufferedReader reader = new BufferedReader(new FileReader(conFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    System.out.println("Skipping empty line");
                    continue;
                }

                // Skip the Content-Type line
                if (line.trim().startsWith("Content-Type:")) {
                    System.out.println("Skipping Content-Type line");
                    continue;
                }

                System.out.println("Reading agent class name: " + line);
                String agentClassName = line.trim();
                String subsLine = reader.readLine();
                String pubsLine = reader.readLine();

                if (subsLine == null || pubsLine == null || subsLine.trim().isEmpty() || pubsLine.trim().isEmpty()) {
                    throw new IOException("Configuration file is not properly formatted.");
                }

                System.out.println("Subscriptions: " + subsLine);
                System.out.println("Publications: " + pubsLine);

                String[] subs = subsLine.split(",");
                String[] pubs = pubsLine.split(",");

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

    public void setConfFile(String conFile) {
        this.conFile = conFile;
    }
}
