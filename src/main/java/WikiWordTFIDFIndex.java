import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

public class WikiWordTFIDFIndex {
  public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, WikiWordInfo> {
    private WikiWordInfo map_val;
    private Text map_key;

    @Override
    public void configure(JobConf job) {
      Integer NUMBER_OF_DOCS = job.getInt("NUMBER_OF_DOCS", 1);
      Integer MAX_NUMBER_OF_RESULTS = job.getInt("MAX_NUMBER_OF_RESULTS", 1);
      map_val = new WikiWordInfo(NUMBER_OF_DOCS, MAX_NUMBER_OF_RESULTS);
      map_key = new Text();
      super.configure(job);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, WikiWordInfo> output, Reporter reporter)
            throws IOException {
      String line = value.toString();
      String[] doc_id_and_content = line.split("\t");

      if (doc_id_and_content.length == 2) {
        String _id = doc_id_and_content[0];
        Enumeration<String> t = new WikiWordTokenizer(doc_id_and_content[1]);
        HashMap<String, Integer> counter = new HashMap<String, Integer>();
        int count_words = 0;
        int max_count_of_word = 0;

        while (t.hasMoreElements()) {
          String next_word = t.nextElement();
          count_words++;

          Integer count = (Integer) counter.get(next_word);
          if (count != null) {
            count += 1;
          } else {
            count = 1;
          }

          counter.put(next_word, count);

          if (count > max_count_of_word) max_count_of_word = count;
        }

        for (java.util.Map.Entry<String, Integer> kv: counter.entrySet()) {
          String _word = kv.getKey();
          int _freq = kv.getValue();
          float _tf = 0.5f + 0.5f * _freq / max_count_of_word;  /* ! TF */
          map_val.set(_id, _tf, _freq);
          map_key.set(_word);
          output.collect(map_key, map_val);
        }
      }
    }
  }

  public static class Reduce extends MapReduceBase implements Reducer<Text, WikiWordInfo, Text, WikiWordInfo> {
    private WikiWordInfo map_val;
    private WikiWordInfoUpdater updater;

    @Override
    public void configure(JobConf job) {
      Integer NUMBER_OF_DOCS = job.getInt("NUMBER_OF_DOCS", 1);
      Integer MAX_NUMBER_OF_RESULTS = job.getInt("MAX_NUMBER_OF_RESULTS", 1);
      map_val = new WikiWordInfo(NUMBER_OF_DOCS, MAX_NUMBER_OF_RESULTS);
      updater = new WikiWordInfoUpdater();
      super.configure(job);
    }

    public void reduce(Text key, Iterator<WikiWordInfo> values, OutputCollector<Text, WikiWordInfo> output, Reporter reporter) throws IOException {
      updater.reset();

      while (values.hasNext()) {
        WikiWordInfo k = values.next();
        updater.add(k);
      }

      updater.update(map_val);
      output.collect(key, map_val);
    }
  }

  public static void main(String[] args) throws Exception {
    JobConf conf = new JobConf(WikiWordTFIDFIndex.class);
    FileSystem fs = FileSystem.get(conf);

    conf.setJobName("WikiWordTFIDFIndex");

    conf.setOutputKeyClass(Text.class);
    conf.setOutputValueClass(WikiWordInfo.class);

    conf.setMapperClass(Map.class);
    //conf.setCombinerClass(Reduce.class);
    conf.setReducerClass(Reduce.class);

    conf.setInputFormat(TextInputFormat.class);
    conf.setOutputFormat(TextOutputFormat.class);

    FileInputFormat.setInputPaths(conf, new Path(args[0]));
    FileOutputFormat.setOutputPath(conf, new Path(args[2]));

    // set NUMBER_OF_DOCS from args[1]
    FSDataInputStream in = fs.open(new Path(args[1]));
    String line = in.readLine();
    String[] textAndCount = line.split("\t");
    int NUMBER_OF_DOCS = Integer.parseInt(textAndCount[1]);
    conf.setInt("NUMBER_OF_DOCS", NUMBER_OF_DOCS);

    // set MAX_NUMBER_OF_RESULTS
    conf.setInt("MAX_NUMBER_OF_RESULTS", 20);

    JobClient.runJob(conf);
  }
}
