import java.io.IOException;

import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class InvertedIndexBigrams {

	public static class TokenizerMapper extends Mapper<LongWritable, Text, Text, Text>{
		
		private Text docId = new Text();
		private Text bigram = new Text();

		public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException 
		{
			//convert all words to lowercase
			String valueLowerCase = value.toString().toLowerCase();
			//tokenizer - for docId only
			StringTokenizer tokens = new StringTokenizer(valueLowerCase);
			//the first input key is the document ID
			String docIdStr = tokens.nextToken();
			docId = new Text(docIdStr);
			
			//convert all words to lowercase and replace special tokens \n \r s \t numerals by space
			String valueLowerCaseClean = value.toString().toLowerCase().replaceAll("[^a-z]", " "); 
			//tokenizer - for all parsing
			StringTokenizer tokensClean = new StringTokenizer(valueLowerCaseClean);
			
			String previous = tokensClean.nextToken();
			
			//get all the words with the document ID as the identifier
			while (tokensClean.hasMoreTokens()) {			
				String current = tokensClean.nextToken();
				bigram.set(previous + " " + current);
				context.write(bigram, docId);
				previous = current;
			}
		}
	}

	public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException 
		{
			HashMap<String, Integer> hashmp = new HashMap<String, Integer>();
			Iterator<Text> itr = values.iterator();
			
			String val;
			int freq = 0;
			
			while(itr.hasNext()) {
				val = itr.next().toString();
				
				if (hashmp.containsKey(val)) {
					freq = hashmp.get(val);
					freq = freq + 1;
					hashmp.put(val, freq);
				} else {
					hashmp.put(val, 1);
				}
			}
				
			StringBuffer strBuffer = new StringBuffer("");
			
			for(Map.Entry<String, Integer> map: hashmp.entrySet()) {
				strBuffer.append(map.getKey() + ":" + map.getValue() + "\t");
			}
			context.write(key, new Text(strBuffer.toString()));
		}
	}

	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Inverted Index Bigrams");
		
		job.setJarByClass(InvertedIndexBigrams.class);
		job.setMapperClass(TokenizerMapper.class);
		
		//job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
}







