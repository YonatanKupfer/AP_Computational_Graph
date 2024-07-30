//package AP_project.test;
package AP_project.configs;


public interface Config {
    void create();
    String getName();
    int getVersion();
    void close();
}
