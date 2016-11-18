package csci446.project3;

import csci446.project3.Util.Data;
import csci446.project3.Util.DataSet;
import csci446.project3.Util.DataType;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Lukasrama
 */

//K Nearest Neighbor
public class KNN {
    public DataSet fullDataSet; //the fullDataSet is the training set. The test points have all been removed.
    public DataSet testSet;
    public int classColumn;
    public int k;

    public String classes[];
    public Double distance[];
    
public KNN(DataSet dataSet,  DataSet testSet, int classColumn, int k) throws Exception {
        this.classColumn = classColumn;
        this.fullDataSet = dataSet;
        this.testSet = testSet;
        this.k = k;
        
        ArrayList<String> classes = new ArrayList<String>();
        distance = new Double[fullDataSet.size()];
        
        //Create a unique list of all the possible classes.
        for (Data<?>[] data : dataSet) {
            if(!classes.contains((String) data[classColumn].value())) {
                classes.add((String) data[classColumn].value());
            }
        }
        this.classes = new String[classes.size()];
        classes.toArray(this.classes);
        replace(dataSet);   //replace trues and falses with 1 and 0
        replace(testSet);   //replace trues and falses with 1 and 0     
}

    String classify(Data<?>[] get) {
        //get the distance from test point to each point dataset
        //set every value in doulbe to zero
        for(int i = 0; i< distance.length; i++){
            distance[i] = 0.0;
        }
        //every value in double is now the differences in the point variables squared
        for(int j=0; j<get.length; j++){
            for(int i = 0; i< distance.length; i++){
                if(j != classColumn){
                    distance[i] = distance[i] + Math.pow((Double.parseDouble(get[j].value().toString()) - Double.parseDouble(fullDataSet.get(i)[j].value().toString())), 2);
                } 
            }
        }
        //every valuse in doulbe is now the sqaure root of the difference in variables squared
        for(int i = 0; i< distance.length; i++){
            distance[i] = sqrt(distance[i]);
            //System.out.println(distance[i]);
        }
        Arrays.sort(distance);
        //test print off distance
        //for(int i = 0; i< distance.length; i++){
        //    System.out.println(distance[i]);
        //}
        
        return classes[1];  //returns the first of the listed classes
    }
    
    //replace true and false with 1 and 0
    public DataSet replace(DataSet temp){
        for(int i = 0; i< temp.size(); i++){
            for(int j =0; j<temp.get(i).length; j++){
                if(temp.get(i)[j].value().equals(true)){
                    //System.out.println("TRUE");
                    temp.get(i)[j] = new Data<Integer>(1);
                    //System.out.println(temp.get(i)[j]);
                }
                if(temp.get(i)[j].value().equals(false)){
                    //System.out.println("FALSE");
                    temp.get(i)[j] = new Data<Integer>(0);
                    //System.out.println(temp.get(i)[j]);
                }
            }
        }
        return temp;
    }  
    
}
