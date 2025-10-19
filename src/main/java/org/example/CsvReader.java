package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

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
import org.example.utils.SetupOutput;
import org.example.utils.StringUtils;

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

        @Override
        protected void setup(Mapper<LongWritable, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException {
            Path path = new Path(context.getConfiguration().get("csv_path"));
            org.apache.hadoop.fs.FileSystem fs = path.getFileSystem(context.getConfiguration());
            Text textWord = new Text();
            boolean skippedHeader = false;
            Movie longestDescriptionMovie = null;
            Movie shortestDescriptionMovie = null;
            WordCounter wordCounter = new WordCounter();

            Reader reader = new InputStreamReader(fs.open(path), StandardCharsets.UTF_8);
            CSVParser parser = CSVFormat.DEFAULT.parse(reader);
            for (CSVRecord record : parser) {
                if (!skippedHeader) {
                    skippedHeader = true;
                    continue;
                }
                if (record.size() < 12) continue;
                Movie movie = new Movie(record.get(2), record.get(11));
                wordCounter.add(movie);

                if (longestDescriptionMovie == null) {
                    longestDescriptionMovie = movie;
                }
                if (shortestDescriptionMovie == null) {
                    shortestDescriptionMovie = movie;
                }
                if (longestDescriptionMovie.compare(movie) > 0) {
                    longestDescriptionMovie = movie;
                }
                if (shortestDescriptionMovie.compare(movie) < 0) {
                    shortestDescriptionMovie = movie;
                }
                for (String word : movie.getDescriptionWords()) {
                    if (word.isEmpty()) { continue; }
                    // if (StringUtils.isDigit(word)) { continue; }
                    textWord.set(word);
                    context.write(textWord, one);
                }
            }
            assert longestDescriptionMovie != null;
            context.write(longestDescriptionMovie.asText("Descrição Mais Longa"), one);
            context.write(shortestDescriptionMovie.asText("Descrição Mais Curta"), one);
            context.write(wordCounter.asText(), one);
        }
        public void map(LongWritable key, Text value, Context context) {}
    }
    public static class ReduceForCsv extends Reducer<Text, IntWritable, Text, IntWritable> {
        private final Map<Integer, ArrayList<String>> frequencyMap = new HashMap<>();

        public void reduce(Text word, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            if (word.toString().startsWith("[Descrição") || word.toString().startsWith("[Total de Palavras")) {
                context.write(word, null);
                return;
            }

            int sum = 0;
            for (IntWritable value : values) {
                sum += value.get();
            }

            frequencyMap
                    .computeIfAbsent(sum, k -> new ArrayList<>())
                    .add(word.toString());
        }
        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            FrequencyManager manager = new FrequencyManager(this.frequencyMap);

            context.write(new Text("[Palavras menos frequentes (desconsiderando dígitos)]:"), null);
            context.write(manager.leastFrequent(StringUtils::isDigit), null);

            context.write(new Text("[Palavras menos frequentes (desconsiderando palavras com dígitos)]:"), null);
            context.write(manager.leastFrequent(StringUtils::hasDigit), null);

            context.write(new Text("[Palavras mais frequentes (desconsiderando stopwords)]:"), null);
            context.write(manager.mostFrequent(), null);
        }
    }
}