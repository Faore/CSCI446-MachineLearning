package csci446.project3.NB;

import csci446.project3.Util.Data;
import csci446.project3.Util.DataSet;

import java.util.HashMap;
import java.util.Map;

public class NaiveBayes {
    private DataSet dataSet;
    private DataSet testSet;
    private int classColumn;
    private int columnCount;
    private String[] results;
    private Map<String, Integer> classCount;
    public NaiveBayes(DataSet dataSet, DataSet testSet, int classColumn){
        this.dataSet = dataSet;
        this.testSet = testSet;
        replace(dataSet);
        replace(testSet);
        this.classColumn = classColumn;
        this.columnCount = dataSet.get(0).length;
        begin();
    }

    private void begin() {
        results = makeAllPredictions(dataSet);
    }

    public String[] getResults(){
        return results;
    }

    private String makePrediction(Data[] input, Map<String, Map<Integer, Map<String, Double>>> conditionalProbabilities, Map<String, Double> classProbabilities){
        String predictClass = "";
        double bestProbability = -1;
        // Compare all classes and find the best probability
        for(String classKey : conditionalProbabilities.keySet()) {
            double tmpProb = 1;
            for (int i = 0; i < input.length; i++) {
                if(!(i == classColumn)) {
                    if (conditionalProbabilities.get(classKey).get(i).containsKey(input[i].toString()))
                        tmpProb *= conditionalProbabilities.get(classKey).get(i).get(input[i].toString());
                }
            }

            tmpProb =tmpProb * classProbabilities.get(classKey);

            if (predictClass.isEmpty() || tmpProb > bestProbability) {
                predictClass = classKey;
                bestProbability = tmpProb;
            }
        }

        return predictClass;
    }

    private String[] makeAllPredictions(DataSet dataSet){
        String[] predictions = new String[dataSet.size()];
        Map<String, Double> classProbabilities = getClassProbabilities();
        Map<String, Map<Integer, Map<String, Double>>> conditionalProbabilities = getConditionalProbabilities(classProbabilities);
        // Check all classes
        int i = 0;
        // Go through each row of the dataset and make a prediction
        for (Data<?>[] data : dataSet) {
            predictions[i] = makePrediction(data, conditionalProbabilities, classProbabilities);
            i++;
        }

        return predictions;
    }
    private Map<String, Double> getClassProbabilities(){
        classCount = new HashMap<>();

        for(Data<?>[] data : testSet){
            if(!classCount.containsKey(data[classColumn].toString())){
                classCount.put(data[classColumn].value().toString(), 0);
            }
            int tmp = classCount.get(data[classColumn].toString());
            classCount.put(data[classColumn].value().toString(), tmp+1);
        }
        Map<String, Double> classProbabilities = new HashMap<>();
        for(Map.Entry<String, Integer> entry : classCount.entrySet()) {
            int count = entry.getValue();
            double classProb = (double)count / (double) testSet.size();
            classProbabilities.put(entry.getKey(), classProb);
        }
        return classProbabilities;
    }
    private Map<String, Map<Integer, Map<String, Double>>> getConditionalProbabilities(Map<String, Double> classProbabilities){
        // Get count for each variable with class
        // Go through class variables
        // Determine how often each value of each variable exists
        Map<String, Map<Integer, Map<String, Double>>> conditionalProbabilities = new HashMap<>();
        for(Map.Entry<String, Double> entry : classProbabilities.entrySet()){
            //columns
            Map<Integer, Map<String, Double>> probabilityMap = new HashMap<>();
            for(int i = 0; i < columnCount; i++) {
                if(!(i == classColumn)) {
                    Map<String, Double> columnProbability = new HashMap<>();
                    Map<String, Double> test = new HashMap<>();
                    int count = 0;
                    //rows
                    for (Data<?>[] data : testSet) {
                        if (data[classColumn].toString().equals(entry.getKey())) {
                            count++;
                            if (!test.containsKey(data[i].toString()))
                                test.put(data[i].toString(), 0.0);
                            double temp = test.get(data[i].toString());
                            test.put(data[i].toString(), temp + 1.0);
                        }
                    }
                    for (String key : test.keySet()) {
                        columnProbability.put(key, test.get(key) / (double) count);
                    }
                    probabilityMap.put(i, columnProbability);
                }
            }
            conditionalProbabilities.put(entry.getKey(), probabilityMap);
        }

        return conditionalProbabilities;
    }


    //replace true and false with 1 and 0
    private DataSet replace(DataSet temp){
        for(int i = 0; i< temp.size(); i++){
            for(int j =0; j<temp.get(i).length; j++){
                if(temp.get(i)[j].value().equals(true)){
                    temp.get(i)[j] = new Data<Integer>(1);
                }
                if(temp.get(i)[j].value().equals(false)){
                    temp.get(i)[j] = new Data<Integer>(0);
                }
            }
        }
        return temp;
    }
}
