package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
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
import org.example.enums.CsvColumnEnum;
import org.example.enums.EntryTypeEnum;
import org.example.parsers.CsvParser;
import org.example.parsers.DatasetParser;
import org.example.parsers.FrequencyParser;
import org.example.utils.SetupOutput;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HadoopMain {
    public static void main(String[] args) throws Exception {
        Configuration c = new Configuration();
        FileSystem fs = FileSystem.get(c);
        String[] files = new GenericOptionsParser(c, args).getRemainingArgs();

        Path input = new Path(files[0]);
        Path output = new Path(files[1]);

        c.set("csv_path", files[0]);
        new SetupOutput().delete(fs, output);

        Job j = Job.getInstance(c, "csv");
        j.setJarByClass(HadoopMain.class);
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
        Text text = new Text();

        /** Teve que fazer no setup porque tem um linkbreak no meio do arquivo que
         * o hadoop não consegue entender quando o processamento é feito linha por linha. */
        @Override protected void setup(Mapper<LongWritable, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException {
            Path path = new Path(context.getConfiguration().get("csv_path"));
            org.apache.hadoop.fs.FileSystem fs = path.getFileSystem(context.getConfiguration());
            CSVParser parser = CSVFormat.DEFAULT.parse(
                    new InputStreamReader(
                            fs.open(path),
                            StandardCharsets.UTF_8
                    )
            );
            CsvParser csvParser = new CsvParser().read(parser);
            FrequencyParser frequencyParser = new FrequencyParser().findMostFrequent(csvParser);
            DatasetParser datasetParser = new DatasetParser().first(frequencyParser);


//            List<Year> years = DatasetParser
//                    .results
//                    .stream()
//                    .map(v -> v.year)
//                    .collect(Collectors.toList());
//            for (CsvEntry csvEntry : csvParser.entries) {
//                if (years.contains(csvEntry.release_year)) {
//                    writeCsvEntryIntoContext(context, csvEntry);
//                }
//            }
        }

        private void writeCsvEntryIntoContext(Mapper<LongWritable, Text, Text, IntWritable>.Context context, CsvEntry csvEntry) throws IOException, InterruptedException {
            write(context, csvEntry, CsvColumnEnum.RATING, csvEntry.rating);
        }
        private void write(
                Context context,
                CsvEntry entry,
                CsvColumnEnum csvColumn,
                String str
        ) throws IOException, InterruptedException {
            String base = entry.type.name() + " " + entry.release_year + " " + csvColumn.name();
            text.set(base + " " + str);
            context.write(text, one);
        }

        public void map(LongWritable key, Text value, Context context) {}
    }
    public static class ReduceForCsv extends Reducer<Text, IntWritable, Text, IntWritable> {
        Text text = new Text();

        public void reduce(Text word, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable value : values) {
                sum += value.get();
            }
            context.write(word, new IntWritable(sum));
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            printTable(context,EntryTypeEnum.MOVIE);
            write(context, "");
            printTable(context, EntryTypeEnum.TV_SHOW);
        }
        private void write(Context context, String str) {
            try {
                text.set(str);
                context.write(text, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private void printTable(Context context, EntryTypeEnum type) throws IOException, InterruptedException {
            write(context, "============" + type.name() + "============");
            List<DatasetEntry> stream = DatasetParser
                    .results
                    .stream()
                    .filter(v -> v.type == type)
                    .collect(Collectors.toList());
            CsvColumnEnum[] columns = {
                    CsvColumnEnum.TITLE,
                    CsvColumnEnum.TITLE_LENGTH,
                    CsvColumnEnum.RATING,
                    CsvColumnEnum.DURATION,
                    CsvColumnEnum.CATEGORIES,
                    CsvColumnEnum.DESCRIPTION,
                    CsvColumnEnum.DESCRIPTION_LENGTH
            };
            for (CsvColumnEnum column : columns) {
                write(context, "======" + column.name() + "======");
                writeHeader(context);
                stream.stream().filter(v -> v.column == column).forEach(v -> {
                    if (column == CsvColumnEnum.TITLE_LENGTH || column == CsvColumnEnum.DESCRIPTION_LENGTH) {
                        writeWordQtdLine(context, v);
                    } else {
                        writeLine(context, v);
                    }
                });
            }

            context.write(text, null);
        }
        private void writeHeader(Context context) {
            String str = "| YEAR | ";
            str += padEnd("TOP 1");
            str += " | ";
            str += padEnd("TOP 2");
            str += " | ";
            str += padEnd("TOP 3");
            str += " | ";
            str += padEnd("TOP 4");
            str += " | ";
            str += padEnd("TOP 5");
            str += " |";
            write(context, str);
        }
        private void writeLine(Context context, DatasetEntry entry) {
            String str = "| " + entry.year + " | ";
            str += entry.top1 != null
                    ? padEnd(entry.top1.getKey() + ": " + entry.top1.getValue())
                    : padEnd("");
            str += " | ";
            str += entry.top2 != null
                    ? padEnd(entry.top2.getKey() + ": " + entry.top2.getValue())
                    : padEnd("");
            str += " | ";
            str += entry.top3 != null
                    ? padEnd(entry.top3.getKey() + ": " + entry.top3.getValue())
                    : padEnd("");
            str += " | ";
            str += entry.top4 != null
                    ? padEnd(entry.top4.getKey() + ": " + entry.top4.getValue())
                    : padEnd("");
            str += " | ";
            str += entry.top5 != null
                    ? padEnd(entry.top5.getKey() + ": " + entry.top5.getValue())
                    : padEnd("");
            str += " | ";
            write(context,  str);
        }
        private void writeWordQtdLine(Context context, DatasetEntry entry) {
            String str = "| " + entry.year + " | ";
            str += entry.top1 != null
                    ? padEnd(entry.top1.getKey() + " palavras: " + entry.top1.getValue())
                    : padEnd("");
            str += " | ";
            str += entry.top2 != null
                    ? padEnd(entry.top2.getKey() + " palavras: " + entry.top2.getValue())
                    : padEnd("");
            str += " | ";
            str += entry.top3 != null
                    ? padEnd(entry.top3.getKey() + " palavras: " + entry.top3.getValue())
                    : padEnd("");
            str += " | ";
            str += entry.top4 != null
                    ? padEnd(entry.top4.getKey() + " palavras: " + entry.top4.getValue())
                    : padEnd("");
            str += " | ";
            str += entry.top5 != null
                    ? padEnd(entry.top5.getKey() + " palavras: " + entry.top5.getValue())
                    : padEnd("");
            str += " | ";
            write(context,  str);
        }
        public static String padEnd(String s) {
            int length = 32;
            char padChar = ' ';
            int needed = length - s.length();
            if (needed <= 0) return s;

            StringBuilder sb = new StringBuilder(length);
            sb.append(s);
            for (int i = 0; i < needed; i++) {
                sb.append(padChar);
            }
            return sb.toString();
        }
    }
}