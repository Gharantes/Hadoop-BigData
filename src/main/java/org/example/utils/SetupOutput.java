package org.example.utils;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class SetupOutput {
    /** Serve para deletar a pasta de output caso já exista.
     * Sem isso, tem que deletar manualmente ou dá erro **/
    public void delete(FileSystem fs, Path output) throws IOException {
        if (fs.exists(output)) {
            fs.delete(output, true);
        }
    }
}
