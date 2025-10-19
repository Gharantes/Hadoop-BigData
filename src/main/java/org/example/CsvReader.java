package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.example.setup.SetupOutput;

public class CsvReader {
    public static void main(String[] args) throws Exception {
        Configuration c = new Configuration();
        FileSystem fs = FileSystem.get(c);

        String[] files = new GenericOptionsParser(c, args).getRemainingArgs();

        Path input = new Path(files[0]);
        Path output = new Path(files[1]);

        c.set("csv_path", files[0]);
        new SetupOutput().main(fs, output);

        Job j = Job.getInstance(c, "csv");
        j.setJarByClass(CsvReader.class);
        j.setMapperClass(MapForCsv.class);
        j.setReducerClass(ReduceForCsv.class);
        j.setOutputKeyClass(Text.class);
        j.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(j, input);
        FileOutputFormat.setOutputPath(j, output);

        System.exit(j.waitForCompletion(true) ? 0 : 1);
    }
    public static class MapForCsv extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private final Text wordText = new Text();

        @Override
        protected void setup(Mapper<LongWritable, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException {
            Path path = new Path(context.getConfiguration().get("csv_path"));
            org.apache.hadoop.fs.FileSystem fs = path.getFileSystem(context.getConfiguration());

            Reader reader = new InputStreamReader(fs.open(path), StandardCharsets.UTF_8);
            CSVParser parser = CSVFormat.DEFAULT.parse(reader);

            for (CSVRecord record : parser) {
                if (record.size() < 12) continue;
                iterateWords(context, record.get(2)); // Title
                iterateWords(context, record.get(11)); // Description
            }
        }
        private void iterateWords(Context context, String col) throws IOException, InterruptedException {
            col = fixCol(col);
            String[] words = col.split("\\s+");
            for (String word : words) {
                word = fixWord(word);
                if (!word.isEmpty()) {
                    wordText.set(word);
                    context.write(wordText, one);
                }
            }
        }
        private String fixCol(String originalCol) {
            if (originalCol == null) { return null; }
            String newCol = originalCol;
            newCol = newCol.toLowerCase();
            newCol = Normalizer.normalize(newCol, Normalizer.Form.NFD);
            newCol = newCol.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");            newCol = newCol.trim();
            newCol = newCol.replaceAll("[^a-z0-9]", " ");
            newCol = newCol.trim();
            return newCol;
        }
        private String fixWord(String originalWord) {
            String newWord = originalWord;
            newWord = newWord.trim();
            if (newWord.matches("\\d+")) { return ""; }
            return newWord;
        }
        public void map(LongWritable key, Text value, Context context) {}
    }
    public static class ReduceForCsv extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text word, Iterable<IntWritable> values, Context con) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable value : values) {
                sum += value.get();
            }
            con.write(word, new IntWritable(sum));
        }
    }
}