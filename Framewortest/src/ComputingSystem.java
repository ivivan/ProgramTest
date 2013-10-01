import java.util.Map;

/*
 * System interface, users need to implement all the four methods
 */


public interface ComputingSystem<INDEX,CRUDEDATA,INTERMEDIATEDATA,RESULTDATA> 
{
	Map<INDEX,CRUDEDATA> Reader();
    
    INTERMEDIATEDATA Preprocess(CRUDEDATA crudedata);

    RESULTDATA Compare(INTERMEDIATEDATA intermediatedataa,INTERMEDIATEDATA intermediatedatab);

    void Writer(Matrix<INDEX,RESULTDATA> matrix);
}



