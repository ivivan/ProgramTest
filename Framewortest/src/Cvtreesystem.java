import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.lang.Math;


/*
 * Users write this class
 */

public class Cvtreesystem implements ComputingSystem<String,String,List<Object>,Double>
{
	/*
	 * Read all the files in folder c:\try  and generate a Map saving all the data;
	 * the keys are file name, the values are the whole file content
	 * @see ComputingSystem#Reader()
	 */
	public HashMap<String,String> Reader()
    {
        String path="c:\\try";
		int i;
        HashMap<String,String> pairsforpreprocess = new HashMap<String,String>();
        File inputfiles = new File(path);
        String[] filename = inputfiles.list();
        File[] files= inputfiles.listFiles();
        
        
        for(i=0;i<files.length;i++)
        {
            StringBuffer wholeContent = new StringBuffer();
        	try
            { 
               FileReader freader = new FileReader(files[i]);
               BufferedReader buffreader = new BufferedReader(freader);
        		
        		while((buffreader.ready()))
               {
                   wholeContent.append((char)buffreader.read());
               }	
        	   freader.close();
               buffreader.close();
            }
               catch(Exception e)
               {
                   e.printStackTrace();
               }
            pairsforpreprocess.put(filename[i],wholeContent.toString());
         
            
        }
        
        return pairsforpreprocess;
    } 
	
	
    /*
     * the following methods are belonging to the Preprocess part, including the Preprocess method and other methods
     * other methods are used to do the preprocess;
     */
    
	void Init()
	{
		M2 = 1;
		for (int i=0; i<LEN-2; i++)	// M2 = AA_NUMBER ^ (LEN-2);
			M2 *= AA_NUMBER; 
		M1 = M2 * AA_NUMBER;		// M1 = AA_NUMBER ^ (LEN-1);
		M  = M1 *AA_NUMBER;			// M  = AA_NUMBER ^ (LEN);
	}
	
    void InitVectors()
	{
		vector = new long[(int) M];
		second = new long [(int) M1];
	//	vector=null;
	//	second=null;
		one_l= new long[AA_NUMBER];
		total = 0;
		total_l = 0;
		complement = 0;
	}
    
    void init_buffer(char[] buffer)
	{
		complement++;
		indexs = 0;
		for (int i=0; i<LEN-1; i++)
		{
			int enc = encode(buffer[i]);
			one_l[enc]++;
			total_l++;
			indexs = indexs * AA_NUMBER + enc;
		}
		second[(int) indexs]++;
	}
    
    void cont_buffer(char ch)
	{
		int enc = encode(ch);
		one_l[enc]++;
		total_l++;
		long index = indexs * AA_NUMBER + enc;
		vector[(int) index]++;
		total++;
		indexs = (indexs % M2) * AA_NUMBER + enc;
		second[(int) indexs]++;
	} 
    
    int encode(char ch)
    {
    	return code[ch-'A'];
    }
    
    /*
     * Preprocess method, generate a list including all the values, values can be different type;
     * @see ComputingSystem#Preprocess(java.lang.Object)
     */
    public List<Object> Preprocess(String content)
    {
    	Init();
        InitVectors();
        StringReader in = new StringReader(content);
		
		
		try
		{
        int ch=0;
        
        
		while ((ch = in.read())!=-1)
		{
			if (ch == '>')
			{
				while ((in.read()) != '\n'); // skip rest of line
				char[] buffer = new char[LEN-1];
				in.read(buffer,0,LEN-1);
				init_buffer(buffer);
			}
			else if (ch != '\r' && ch != '\n')
				cont_buffer((char)ch);
		}
		in.close();
		}
		catch(IOException e)
		{
			e.printStackTrace(); 
		}
		
        
        long total_plus_complement = total + complement;
		double total_div_2 = total * 0.5;
		int i_mod_aa_number = 0;
		int i_div_aa_number = 0;
		long i_mod_M1 = 0;
		long i_div_M1 = 0;

		double[] one_l_div_total = new double[AA_NUMBER];
		for (int i=0; i<AA_NUMBER; i++)
			one_l_div_total[i] = (double)one_l[i] / total_l;
		
		double[] second_div_total = new double[(int) M1];
		for (int i=0; i<M1; i++)
			second_div_total[i] = (double)second[i] / total_plus_complement;

		
		second = null;
		count = 0;
		double[] t = new double[(int) M];

		for(int i=0; i<M; i++)
		{
			double p1 = second_div_total[i_div_aa_number];
			double p2 = one_l_div_total[i_mod_aa_number];
			double p3 = second_div_total[(int) i_mod_M1];
			double p4 = one_l_div_total[(int) i_div_M1];
			double stochastic =  (p1 * p2 + p3 * p4) * total_div_2;

			if (i_mod_aa_number == AA_NUMBER-1)
			{
				i_mod_aa_number = 0;
				i_div_aa_number++;
			}
			else
				i_mod_aa_number++;

			if (i_mod_M1 == M1-1)
			{
				i_mod_M1 = 0;
				i_div_M1++;
			}
			else
				i_mod_M1++;

			if (stochastic > EPSILON) 
			{
				t[i] = (vector[i] - stochastic) / stochastic;
				count++;
			}
			else
				t[i] = 0;
		}
		

		second_div_total=null;
		vector=null;
		
		tv = new double[(int) count];
		ti = new long[(int) count];

		int pos = 0;
		for (int i=0; i<M; i++)
		{
			if (t[i] != 0)
			{
				tv[pos] = t[i];
				ti[pos] = i;
				pos++;
			}
		}
		
		t=null;
        
        List<Object> intermediateData = new ArrayList<Object>();
        
        intermediateData.add(count);
        for(int m=0;m<count;m++)
            intermediateData.add(tv[m]);
        for(int n=0;n<count;n++)
            intermediateData.add(ti[n]);
    
        return intermediateData;  
	}
    
    
    /*
     * the following methods are belonging to the Compare part; 
     * first few methods define how to deal the List and get separate values;
     */

    long getcount(List<Object> list)
    {
        long thecount;
        thecount = (long)list.get(0);
        return thecount;
    }

    double[] gettv(List<Object> list)
    {
        long thecount;
        thecount = getcount(list);
        double[] tv = new double[(int) thecount];
    
        for(int i=0;i<thecount;i++)
            tv[i]=(double)list.get(i+1);
        return tv;
    }

    long[] getti(List<Object> list)
    {
        long thecount;
        thecount = getcount(list);
        long[] ti = new long[(int) thecount];
    
        for(int i=0;i<thecount;i++)
            ti[i]=(long)list.get(i+(int)thecount+1);
        return ti;
    }

    /*
     * Compare method, compare two different intermediate value and get the result
     * @see ComputingSystem#Compare(java.lang.Object, java.lang.Object)
     */
    public Double Compare(List<Object> intermediatedataa,List<Object> intermediatedatab)
    {
        counta=getcount(intermediatedataa);
        countb=getcount(intermediatedatab);
        tva=gettv(intermediatedataa);
        tvb=gettv(intermediatedatab);
        tia=getti(intermediatedataa);
        tib=getti(intermediatedatab);
        
        double correlation = 0;
	    double vector_len1=0;
	    double vector_len2=0;
	    long p1 = 0;
	    long p2 = 0;
        
	while (p1 < counta && p2 < countb)
	{
		long n1 = tia[(int) p1];
		long n2 = tib[(int) p2];
		if (n1 < n2)
		    {
			    double t1 = tva[(int) p1];
		    	vector_len1 += (t1 * t1);
		    	p1++;
		    }
		else if (n2 < n1)
		{
			double t2 = tvb[(int) p2];
			p2++;
			vector_len2 += (t2 * t2);
		}
		else
		{
			double t1 = tva[(int) p1++];
			double t2 = tvb[(int) p2++];
			vector_len1 += (t1 * t1);
			vector_len2 += (t2 * t2);
			correlation += t1 * t2;
		}
	}
	while (p1 < counta)
	{
		double t1 = tva[(int) p1++];
		vector_len1 += (t1 * t1);
	}
	while (p2 < countb)
	{
		double t2 = tvb[(int) p2++];
		vector_len2 += (t2 * t2);
	}

	return correlation / (Math.sqrt(vector_len1) * Math.sqrt(vector_len2));
    }

    /*
     * Users use Writer function to operate the final result
     * here just print all the index and result;
     * @see ComputingSystem#Writer(Matrix)
     */
    
    public void Writer(Matrix<String,Double> matrix)
    {
       
    	Map<List<String>,Double> resultpairs = matrix.Getallresult();
    	
    	Iterator<Entry<List<String>, Double>> resultiterator = resultpairs.entrySet().iterator();
 	    
 	    while(resultiterator.hasNext())
 	    {
 	        Map.Entry<List<String>, Double> entry = resultiterator.next();
 	        
 	        System.out.println("Key:"+entry.getKey().toString());
 	        System.out.println("Value:"+entry.getValue().toString());
 	        
 	    }
     
    }
    
    int number_bacteria;
    char[][] bacteria_name;
    long M, M1, M2;
    static int code[] = new int[] {0, 2, 1, 2, 3, 4, 5, 6, 7, -1, 8, 9, 10, 11, -1, 12, 13, 14, 15, 16, 1, 17, 18, 5, 19, 3};
    private static final int LEN=6;
    private static final int AA_NUMBER=20;
    private static final double EPSILON=1e-010;
    long counta;
    double[] tva;
    long[] tia;
    long countb;
    double[] tvb;
    long[] tib;  
    long[] vector;
	long[] second;
	long[] one_l;
	long indexs;
	long total;
	long total_l;
	long complement;
    long count;
	double[] tv;
	long[] ti;
    List<Object> intermediateData;
}
