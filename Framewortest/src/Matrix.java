import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * The system define this Matrix Class
 * Users can use the Methods in this Class to get the final result and deal by their own ways 
 */
public class Matrix<INDEX,RESULTDATA> 
{
	Matrix(Map<List<INDEX>,RESULTDATA> resultmap,List<INDEX> index)
	{
		finalindex = index;
		finalresult = resultmap;
	}
	
	public Map<List<INDEX>,RESULTDATA> Getallresult()
	{
		return finalresult;
	}

	
	public RESULTDATA Getresult(int axisxindexnumber,int axisyindexnumber)
	{
		List<INDEX> list= new ArrayList<INDEX>();
		list.add(finalindex.get(axisxindexnumber));
		list.add(finalindex.get(axisyindexnumber));
		return finalresult.get(list);
	}
	
	
	public RESULTDATA Getresult(INDEX axisxindex,INDEX axisyindex)
	{
		List<INDEX> list= new ArrayList<INDEX>();
		list.add(axisxindex);
		list.add(axisyindex);
		return finalresult.get(list);
	}
	
	public List<INDEX> GetindexList()
	{
		return finalindex;
	}
	
	public INDEX Getindex(int number)
	{
		return finalindex.get(number);
	}
	
	
	INDEX axisxindex;
	INDEX axisyindex;
	RESULTDATA resultdata;
	List<INDEX> finalindex;
	Map<List<INDEX>,RESULTDATA> finalresult;
	   
}