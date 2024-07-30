package AP_project.view;

import AP_project.configs.Graph;
import AP_project.configs.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HtmlGraphWriter {

    /**
     * Generates HTML for visualizing the graph using D3.js.
     * @param graph The graph to be visualized.
     * @param topicToCurrentMessage Mapping of topics to their current messages.
     * @return A list of HTML lines.
     */
    public static List<String> getGraphHtml(Graph graph, Map<String, String> topicToCurrentMessage) {
        List<String> htmlLines = new ArrayList<>();

        // HTML head section
        htmlLines.add("<!DOCTYPE html>");
        htmlLines.add("<html lang=\"en\">");
        htmlLines.add("<head>");
        htmlLines.add("    <meta charset=\"UTF-8\">");
        htmlLines.add("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        htmlLines.add("    <title>Graph Visualization</title>");
        htmlLines.add("    <script src=\"https://d3js.org/d3.v6.min.js\"></script>");
        htmlLines.add("    <style>");
        htmlLines.add("        body {");
        htmlLines.add("            background-color: #f0f8ff; /* Light Blue */");
        htmlLines.add("        }");
        htmlLines.add("        .node rect {");
        htmlLines.add("            stroke: #000;");
        htmlLines.add("            stroke-width: 1px;");
        htmlLines.add("            fill: #98FB98; /* Light Green for Topics */");
        htmlLines.add("        }");
        htmlLines.add("        .node circle {");
        htmlLines.add("            stroke: #000;");
        htmlLines.add("            stroke-width: 1px;");
        htmlLines.add("            fill: #87CEFA; /* Light Blue for Agents */");
        htmlLines.add("        }");
        htmlLines.add("        .link {");
        htmlLines.add("            fill: none;");
        htmlLines.add("            stroke: #000;");
        htmlLines.add("            stroke-width: 2px;");
        htmlLines.add("            marker-end: url(#arrow); /* Add arrows to the links */");
        htmlLines.add("        }");
        htmlLines.add("        text {");
        htmlLines.add("            font: 10px sans-serif;");
        htmlLines.add("        }");
        htmlLines.add("        .value-text {");
        htmlLines.add("            font-size: 14px;");
        htmlLines.add("            font-weight: bold;");
        htmlLines.add("            fill: #333;");
        htmlLines.add("        }");
        htmlLines.add("    </style>");
        htmlLines.add("</head>");

        // HTML body section
        htmlLines.add("<body>");
        htmlLines.add("    <svg width=\"960\" height=\"600\"></svg>");
        htmlLines.add("    <script>");
        htmlLines.add("        const graphData = {");
        htmlLines.add("            nodes: [");

        // Add nodes to the graph data
        for (Node node : graph) {
            String nodeName = node.getName();
            String displayName = nodeName;
            if (nodeName.startsWith("T")) {
                displayName = nodeName.substring(1); // Remove 'T' prefix for topics
            } else if (nodeName.startsWith("A")) {
                displayName = nodeName.substring(1); // Remove 'A' prefix for agents
            }
            double nodeValue = 0.0;
            if (node.getName().startsWith("T")) {
                String topicName = node.getName().substring(1);
                String messageValue = topicToCurrentMessage.getOrDefault(topicName, "0.0");
                nodeValue = messageValue.isEmpty() ? 0.0 : Double.parseDouble(messageValue);
            } else {
                nodeValue = (node.getMsg() != null) ? node.getMsg().asDouble : 0.0;
            }
            htmlLines.add("{id: '" + nodeName + "', displayName: '" + displayName + "', value: " + nodeValue + ", type: '" + (nodeName.startsWith("T") ? "topic" : "agent") + "'},");
        }

        htmlLines.add("            ],");
        htmlLines.add("            links: [");

        // Add edges to the graph data
        for (Node node : graph) {
            for (Node edge : node.getEdges()) {
                htmlLines.add("{source: '" + node.getName() + "', target: '" + edge.getName() + "'},");
            }
        }

        htmlLines.add("            ]");
        htmlLines.add("        };");

        htmlLines.add("        const width = 960;");
        htmlLines.add("        const height = 600;");

        // Initialize SVG element
        htmlLines.add("        const svg = d3.select(\"svg\")");
        htmlLines.add("            .attr(\"width\", width)");
        htmlLines.add("            .attr(\"height\", height);");

        // Define marker for arrows
        htmlLines.add("        svg.append(\"defs\").append(\"marker\")");
        htmlLines.add("            .attr(\"id\", \"arrow\")");
        htmlLines.add("            .attr(\"viewBox\", \"0 -5 10 10\")");
        htmlLines.add("            .attr(\"refX\", 32)");  // Adjust this value as needed
        htmlLines.add("            .attr(\"refY\", 0)");
        htmlLines.add("            .attr(\"markerWidth\", 6)");
        htmlLines.add("            .attr(\"markerHeight\", 6)");
        htmlLines.add("            .attr(\"orient\", \"auto\")");
        htmlLines.add("            .append(\"path\")");
        htmlLines.add("            .attr(\"d\", \"M0,-5L10,0L0,5\")");
        htmlLines.add("            .attr(\"fill\", \"#000\");");

        // Initialize force simulation
        htmlLines.add("        const simulation = d3.forceSimulation(graphData.nodes)");
        htmlLines.add("            .force(\"link\", d3.forceLink(graphData.links).id(d => d.id).distance(100))");
        htmlLines.add("            .force(\"charge\", d3.forceManyBody().strength(-100))");
        htmlLines.add("            .force(\"center\", d3.forceCenter(width / 2, height / 2));");

        // Draw links
        htmlLines.add("        const link = svg.append(\"g\")");
        htmlLines.add("            .attr(\"class\", \"links\")");
        htmlLines.add("            .selectAll(\"line\")");
        htmlLines.add("            .data(graphData.links)");
        htmlLines.add("            .enter().append(\"line\")");
        htmlLines.add("            .attr(\"class\", \"link\")");
        htmlLines.add("            .attr(\"marker-end\", \"url(#arrow)\");");  // Ensure marker-end is correctly set

        // Draw nodes
        htmlLines.add("        const node = svg.append(\"g\")");
        htmlLines.add("            .attr(\"class\", \"nodes\")");
        htmlLines.add("            .selectAll(\"g\")");
        htmlLines.add("            .data(graphData.nodes)");
        htmlLines.add("            .enter().append(\"g\")");
        htmlLines.add("            .call(d3.drag()");
        htmlLines.add("                .on(\"start\", dragstarted)");
        htmlLines.add("                .on(\"drag\", dragged)");
        htmlLines.add("                .on(\"end\", dragended));");

        // Draw circles for agent nodes
        htmlLines.add("        node.filter(function(d) { return d.type === \"agent\"; })");
        htmlLines.add("            .append(\"circle\")");
        htmlLines.add("            .attr(\"r\", 30)");  // Adjust this value to make circles bigger
        htmlLines.add("            .attr(\"fill\", \"#87CEFA\");");

        // Draw rectangles for topic nodes
        htmlLines.add("        node.filter(function(d) { return d.type === \"topic\"; })");
        htmlLines.add("            .append(\"rect\")");
        htmlLines.add("            .attr(\"width\", 80)");
        htmlLines.add("            .attr(\"height\", 40)");
        htmlLines.add("            .attr(\"x\", -40)");
        htmlLines.add("            .attr(\"y\", -20)");
        htmlLines.add("            .attr(\"fill\", \"#98FB98\");");

        // Add value text above topic nodes
        htmlLines.add("        node.filter(function(d) { return d.type === \"topic\"; })");
        htmlLines.add("            .append(\"text\")");
        htmlLines.add("            .attr(\"class\", \"value-text\")");
        htmlLines.add("            .attr(\"data-id\", function(d) { return d.id; })");
        htmlLines.add("            .attr(\"x\", 0)");
        htmlLines.add("            .attr(\"y\", -30)");  // Position above the rectangle
        htmlLines.add("            .attr(\"text-anchor\", \"middle\")");
        htmlLines.add("            .style(\"font-size\", \"14px\")");  // Ensure font size is set
        htmlLines.add("            .style(\"font-weight\", \"bold\")");  // Ensure font weight is set
        htmlLines.add("            .text(function(d) { return d.value.toFixed(1); });");  // Initialize value to node's message value

        // Add display name to nodes
        htmlLines.add("        node.append(\"text\")");
        htmlLines.add("            .attr(\"dy\", 3)");
        htmlLines.add("            .attr(\"x\", 0)");
        htmlLines.add("            .style(\"text-anchor\", \"middle\")");
        htmlLines.add("            .text(function(d) { return d.displayName; });");

        // Update simulation positions on tick
        htmlLines.add("        simulation.on(\"tick\", function() {");
        htmlLines.add("            link.attr(\"x1\", function(d) { return d.source.x; })");
        htmlLines.add("                .attr(\"y1\", function(d) { return d.source.y; })");
        htmlLines.add("                .attr(\"x2\", function(d) { return d.target.x; })");
        htmlLines.add("                .attr(\"y2\", function(d) { return d.target.y; });");

        htmlLines.add("            node.attr(\"transform\", function(d) { return 'translate(' + d.x + ',' + d.y + ')'; });");
        htmlLines.add("        });");

        // Drag event handlers
        htmlLines.add("        function dragstarted(event, d) {");
        htmlLines.add("            if (!event.active) simulation.alphaTarget(0.3).restart();");
        htmlLines.add("            d.fx = d.x;");
        htmlLines.add("            d.fy = d.y;");
        htmlLines.add("        }");

        htmlLines.add("        function dragged(event, d) {");
        htmlLines.add("            d.fx = event.x;");
        htmlLines.add("            d.fy = event.y;");
        htmlLines.add("        }");

        htmlLines.add("        function dragended(event, d) {");
        htmlLines.add("            if (!event.active) simulation.alphaTarget(0);");
        htmlLines.add("            d.fx = null;");
        htmlLines.add("            d.fy = null;");
        htmlLines.add("        }");

        // Update node values on receiving messages
        htmlLines.add("        window.addEventListener('message', function(event) {");
        htmlLines.add("            if (event.data.type === 'updateNodes') {");
        htmlLines.add("                const updatedNodes = Object.entries(event.data.data).map(([id, value]) => ({");
        htmlLines.add("                    id: 'T' + id,");
        htmlLines.add("                    value: parseFloat(value) || 0");
        htmlLines.add("                }));");
        htmlLines.add("                updatedNodes.forEach(node => {");
        htmlLines.add("                    const textElement = document.querySelector(`text.value-text[data-id='${node.id}']`);");
        htmlLines.add("                    if (textElement) {");
        htmlLines.add("                        textElement.textContent = node.value.toFixed(1);");
        htmlLines.add("                    }");
        htmlLines.add("                });");
        htmlLines.add("            }");
        htmlLines.add("        });");

        htmlLines.add("    </script>");
        htmlLines.add("</body>");
        htmlLines.add("</html>");

        return htmlLines;
    }

    /**
     * Generates an HTML table displaying the values of nodes in the graph.
     * @param graph The graph containing the nodes.
     * @param topicToCurrentMessage Mapping of topics to their current messages.
     * @param description Description of the configuration.
     * @return A list of HTML lines for the table.
     */
    public static List<String> getTableHtml(Graph graph, Map<String, String> topicToCurrentMessage, String description) {
        List<String> htmlLines = new ArrayList<>();

        // HTML head section
        htmlLines.add("<!DOCTYPE html>");
        htmlLines.add("<html lang=\"en\">");
        htmlLines.add("<head>");
        htmlLines.add("    <meta charset=\"UTF-8\">");
        htmlLines.add("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        htmlLines.add("    <title>Node Values Table</title>");
        htmlLines.add("    <style>");
        htmlLines.add("        body {");
        htmlLines.add("            font-family: Arial, sans-serif;");
        htmlLines.add("            background-color: #f0f8ff; /* Light Blue */");
        htmlLines.add("            color: #333;");
        htmlLines.add("            margin: 0;");
        htmlLines.add("            padding: 20px;");
        htmlLines.add("        }");
        htmlLines.add("        table {");
        htmlLines.add("            width: 100%;");
        htmlLines.add("            border-collapse: collapse;");
        htmlLines.add("            margin-bottom: 20px;");
        htmlLines.add("            background-color: #fff;");
        htmlLines.add("            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);");
        htmlLines.add("        }");
        htmlLines.add("        th, td {");
        htmlLines.add("            padding: 12px;");
        htmlLines.add("            text-align: left;");
        htmlLines.add("            border-bottom: 1px solid #ddd;");
        htmlLines.add("        }");
        htmlLines.add("        th {");
        htmlLines.add("            background-color: #4CAF50;");
        htmlLines.add("            color: white;");
        htmlLines.add("        }");
        htmlLines.add("        tr:nth-child(even) {");
        htmlLines.add("            background-color: #f2f2f2;");
        htmlLines.add("        }");
        htmlLines.add("        tr:hover {");
        htmlLines.add("            background-color: #ddd;");
        htmlLines.add("        }");
        htmlLines.add("        h1 {");
        htmlLines.add("            color: #2c3e50;");
        htmlLines.add("            text-align: center;");
        htmlLines.add("        }");
        htmlLines.add("    </style>");
        htmlLines.add("</head>");

        // HTML body section
        htmlLines.add("<body>");
        htmlLines.add("    <h1>Node Values</h1>");
        htmlLines.add("    <table>");
        htmlLines.add("        <tr>");
        htmlLines.add("            <th>Node</th>");
        htmlLines.add("            <th>Value</th>");
        htmlLines.add("        </tr>");

        // Add rows to the table for each topic node
        for (Node node : graph) {
            if (node.getName().startsWith("T")) {
                String nodeName = node.getName().substring(1); // Remove 'T' prefix
                String messageValue = topicToCurrentMessage.getOrDefault(nodeName, "0.0");
                double nodeValue = messageValue.isEmpty() ? 0.0 : Double.parseDouble(messageValue);
                htmlLines.add("<tr>");
                htmlLines.add("<td>" + nodeName + "</td>");
                htmlLines.add("<td>" + nodeValue + "</td>");
                htmlLines.add("</tr>");
            }
        }

        htmlLines.add("    </table>");
        htmlLines.add("    <h2>Configuration Description</h2>");
        htmlLines.add("    <p>" + description + "</p>");

        htmlLines.add("</body>");
        htmlLines.add("</html>");

        return htmlLines;
    }
}
