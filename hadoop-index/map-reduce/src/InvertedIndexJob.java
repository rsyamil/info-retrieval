import java.io.IOException;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.*;

public class InvertedIndexJob {

	public static class TokenizerMapper extends Mapper<LongWritable, Text, Text, Text>{
		
		private Text docId = new Text();
		private Text word = new Text();

		public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException 
		{
			// handle string first before tokenizing???
			
			StringTokenizer tokens = new StringTokenizer(value.toString());
			
			//the first input key is the document ID
			String docIdStr = tokens.nextToken();
			docId = new Text(docIdStr);

			//get all the words with the document ID as the identifier
			while (tokens.hasMoreTokens()) {
				
				//replace special tokens \n \r s \t numerals by space
				//convert all words to lowercase
				//5722018101	"The DeLorme PN-20 represents a new breed of GPS devices. ...a fantastic device, and it leads the way in a new breed of GPS de
				
				word.set(tokens.nextToken());
				context.write(word, docId);
			}

		}
	}

	public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException 
		{
			Hashmap<String, Integer> hashmp = new HashMap<String, Integer>();
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
		Job job = Job.getInstance(conf, "Inverted Index");
		
		job.setJarByClass(InvertedIndexJob.class);
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







