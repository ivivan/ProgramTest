import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.lang.Math;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/*
 * This Class provide by the System, 
 */

public class SystemRun 
{
	
	private static Class getClass(Type type, int i) {     
        if (type instanceof ParameterizedType) { // generic
            return getGenericClass((ParameterizedType) type, i);     
        } else if (type instanceof TypeVariable) {     
            return (Class) getClass(((TypeVariable) type).getBounds()[0], 0); // generic erase
        } else {   
            return (Class) type;     
        }     
    }     
    
    private static Class getGenericClass(ParameterizedType parameterizedType, int i) {     
        Object genericClass = parameterizedType.getActualTypeArguments()[i];     
        if (genericClass instanceof ParameterizedType) { 
            return (Class) ((ParameterizedType) genericClass).getRawType();     
        } else if (genericClass instanceof GenericArrayType) {   
            return (Class) ((GenericArrayType) genericClass).getGenericComponentType();     
        } else if (genericClass instanceof TypeVariable) {      
            return (Class) getClass(((TypeVariable) genericClass).getBounds()[0], 0);     
        } else {     
            return (Class) genericClass;     
        }     
    } 
	
	public static Class getMethodGenericReturnType(Method method, int index) { 
	Type returnType = method.getGenericReturnType(); 
	if (returnType instanceof ParameterizedType) { 
	ParameterizedType type = (ParameterizedType) returnType; 
	Type[] typeArguments = type.getActualTypeArguments(); 
	if (index >= typeArguments.length || index < 0) { 
	throw new RuntimeException("the index" 
	+ (index < 0 ? "index can not < 0" : "index too big")); 
	} 
	return (Class) typeArguments[index]; 
	} 
	return Object.class; 
	} 

	

	
	public static void main(String[] args) 
	{
		Map crudepairs;
		Map intermediatepairs;
		Map resultpairs;
		Object resultdata;
		List listindex;
		
	    try
	    { 
	    	/*
	    	 * Use Reflection get User Class, by inputing Class name;
	    	 * Get all the four methods;
	    	 */
			Class cvtreeprogram = Class.forName("Cvtreesystem"); 
			Cvtreesystem cvtree = new Cvtreesystem();
//		    Constructor localConstructor = cvtreeprogram.getConstructor(new Class[0]);
//			Cvtreesystem cvtree = (Cvtreesystem)localConstructor.newInstance(new Object[0]);
			
			Type[] gis = cvtree.getClass().getGenericInterfaces(); // interface generic information
					

			Method Reader = cvtreeprogram.getMethod("Reader");    
		    Class indexclass = getMethodGenericReturnType(Reader,0);
		    Class crudedataclass = getMethodGenericReturnType(Reader,1);    
		    Method Preprocess = cvtreeprogram.getMethod("Preprocess",new Class[]{crudedataclass});	
		    Class intermediatedataclass = getClass(gis[0], 2);	
	        Method Compare = cvtreeprogram.getMethod("Compare",new Class[]{intermediatedataclass,intermediatedataclass});   
	        Class resultdataclass = getClass(gis[0], 3);    
		    Method Writer = cvtreeprogram.getMethod("Writer",new Class[]{Matrix.class});
		
	        
	        
	        /*
	         * Running the Reader method providing by Users
	         */
		    Object objcrudepairs = Reader.invoke(cvtree);
		    crudepairs = (Map) objcrudepairs;

	        /*
	         * Running the Preprocess method providing by Users
	         */
	        Iterator<Entry> crudeiterator = crudepairs.entrySet().iterator();
	        intermediatepairs = new HashMap();
	            
	        while(crudeiterator.hasNext())
	        {
	            Map.Entry entry = crudeiterator.next(); 
	            intermediatepairs.put(entry.getKey(),Preprocess.invoke(cvtree,entry.getValue()));
	        
	        }
	        crudepairs=null;
	        /*
	         * Running Compare method providing by Users;
	         */
	        Iterator<Entry> intermediateiterator = intermediatepairs.entrySet().iterator();
	        listindex = new ArrayList();   
	        resultpairs = new HashMap();
	        
	        while(intermediateiterator.hasNext())
	        {
	            Entry entry = intermediateiterator.next();
	            listindex.add(entry.getKey());
	        }
	            
	        Object[] comparearglist = new Object[2];
	        
	        for(int i=0;i<listindex.size();i++)
	            for(int j=i+1;j<listindex.size();j++)
	            {
	             	comparearglist[0] = intermediatepairs.get(listindex.get(i));
	            	comparearglist[1] = intermediatepairs.get(listindex.get(j));       	
	            	
	        	try {
	        		
	        		List list = new ArrayList();
					resultdata = Compare.invoke(cvtree,comparearglist);
					list.add(listindex.get(i));
	                list.add(listindex.get(j));
	                resultpairs.put(list,resultdata);
					
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
	        }
	  
	        intermediatepairs=null;
	        /*
	         * Running Writer Method providing by Users;
	         */
	        Matrix resultmatrix = new Matrix(resultpairs,listindex);
	        Writer.invoke(cvtree,resultmatrix);
	        
  	} catch (Throwable e)
    { 
        System.err.println(e); 
    }  
}
}