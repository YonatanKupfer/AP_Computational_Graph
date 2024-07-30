# Computational Graph
## Background

This project is a distributed system designed to perform a variety of mathematical operations through agent-based processing and visualize the results through a web interface. The system is built using Java for the backend processing and HTML, CSS, and JavaScript for the frontend interface. Users can interact with the system by uploading configuration files, inputting data, and viewing the results in real-time through dynamic tables and graph visualizations.

### Key Components

- **Agents**: Perform mathematical operations (addition, subtraction, multiplication, division, etc.)
- **Configuration Management**: Load and manage configuration settings
- **Core Logic**: Handle message passing and graph representation
- **HTTP Server**: Manage HTTP requests and responses
- **Frontend**: Provide user interface for input and visualization using HTML, CSS, and JavaScript

## Installation

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- An Integrated Development Environment (IDE) like IntelliJ IDEA, Eclipse, or NetBeans
- A web browser

### Steps

1. **Clone the repository:**

    ```bash
    git clone https://github.com/YonatanKupfer/AP_Computational_Graph.git
    cd AP_Computational_Graph
    ```

2. **Open the project in your IDE:**
    - Open your preferred IDE.
    - Import the project as a Java project.

3. **Compile and run the backend server:**
    - Locate the `Main` class in your IDE.
    - Right-click the `Main` class and select `Run` or use the IDE's run configuration to start the application.

4. **Open the frontend:**
    - Open `http://localhost:8080/app/index.html` in your web browser to interact with the system.

## Usage

1. **Upload Configuration File:**
    - Open the form on the left side of the interface.
    - Select the configuration file and click "Deploy".

2. **Input Data and Send Messages:**
    - Use the form to input the topic name and message.
    - Click "Send" to process the data.

3. **View Results:**
    - The table and graph frames will display the processed results and visualizations.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.
