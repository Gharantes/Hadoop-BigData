package org.example.utils;

public class SetupLogs {
    public void main () {
        org.apache.log4j.BasicConfigurator.configure();
        System.setProperty("hadoop.root.logger", "INFO,console");
        System.setProperty("hadoop.log.dir", "./hadoop-logs");
        System.setProperty("hadoop.log.file", "job.log");
    }
}
