import java.io.IOException;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;



public class HadoopIO {
	
	  public HashMap<String,String> Reader(String dir)
		{
			Configuration conf = new Configuration();
			HashMap<String,String> pairsforpreprocess = new HashMap<String,String>();
			try {
			
			FileSystem fs = FileSystem.get(conf);
			FSDataInputStream hdfsInStream;
			FileStatus[] stats = fs.listStatus(new Path(dir));
			String[] filename = new String[stats.length];
			
			for(int i = 0; i < stats.length; ++i)
			{
				filename[i]=stats[i].getPath().getName();
				
					
					hdfsInStream = fs.open(stats[i].getPath());
				    byte[] buffer = new byte[Integer.parseInt(String.valueOf(stats[i].getLen()))];
		            hdfsInStream.readFully(0, buffer);
		    		String wholeContent = new String(buffer);
				    pairsforpreprocess.put(filename[i],wholeContent);
				    fs.close();
			    
			}
			
		    	} catch (IOException e) {
					e.printStackTrace();
				}
			return pairsforpreprocess;
		}	  
}
