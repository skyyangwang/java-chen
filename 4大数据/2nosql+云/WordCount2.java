/*    */ 
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ import java.util.StringTokenizer;
/*    */ import org.apache.hadoop.conf.Configuration;
/*    */ import org.apache.hadoop.fs.Path;
/*    */ import org.apache.hadoop.io.IntWritable;
/*    */ import org.apache.hadoop.io.Text;
/*    */ import org.apache.hadoop.mapreduce.Job;
/*    */ import org.apache.hadoop.mapreduce.Mapper;
/*    */ import org.apache.hadoop.mapreduce.Reducer;
/*    */ import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
/*    */ import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
/*    */ import org.apache.hadoop.util.GenericOptionsParser;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WordCount2
/*    */ {
/*    */   public WordCount2() {}
/*    */   
/*    */   public static class TokenizerMapper
/*    */     extends Mapper<Object, Text, Text, IntWritable>
/*    */   {
/* 39 */     private static final IntWritable one = new IntWritable(1);
/* 40 */     private Text word = new Text();
/*    */     
/*    */     public TokenizerMapper() {}
/*    */     
/* 44 */     public void map(Object key, Text value, Mapper<Object, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException { StringTokenizer itr = new StringTokenizer(value.toString());
/* 45 */       while (itr.hasMoreTokens()) {
/* 46 */         this.word.set(itr.nextToken());
/* 47 */         context.write(this.word, one);
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable>
/*    */   {
/* 54 */     private IntWritable result = new IntWritable();
/*    */     
/*    */     public IntSumReducer() {}
/*    */     
/*    */     public void reduce(Text key, Iterable<IntWritable> values, Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
/* 59 */       int sum = 0;
/* 60 */       for (IntWritable val : values) {
/* 61 */         sum += val.get();
/*    */       }
/* 63 */       this.result.set(sum);
/* 64 */       context.write(key, this.result);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void main(String[] args) throws Exception {
/* 69 */     Configuration conf = new Configuration();
/* 70 */     String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
/* 71 */     if (otherArgs.length != 2) {
/* 72 */       System.err.println("Usage: wordcount <in> <out>");
/* 73 */       System.exit(2);
/*    */     }
/* 75 */     Job job = new Job(conf, "word count");
/* 76 */     job.setJarByClass(WordCount2.class);
/* 77 */     job.setMapperClass(TokenizerMapper.class);
/* 78 */     job.setCombinerClass(IntSumReducer.class);
/* 79 */     job.setReducerClass(IntSumReducer.class);
/* 80 */     job.setOutputKeyClass(Text.class);
/* 81 */     job.setOutputValueClass(IntWritable.class);
/* 82 */     FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
/* 83 */     FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
/* 84 */     System.exit(job.waitForCompletion(true) ? 0 : 1);
/*    */   }
/*    */ }

/* Location:           F:\shsWeb\hadoop-mapreduce-examples-2.2.0\ImportedClasses
 * Qualified Name:     org.apache.hadoop.examples.WordCount
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.0.1
 */