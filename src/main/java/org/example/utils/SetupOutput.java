package org.example.utils;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class SetupOutput {
    public void main(FileSystem fs, Path output) throws IOException {
        if (fs.exists(output)) {
            fs.delete(output, true);
        }
    }
}
