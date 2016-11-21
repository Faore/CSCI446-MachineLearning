package csci446.project3.NB;

import csci446.project3.Util.Data;
import csci446.project3.Util.DataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NaiveBayes {
    private DataSet dataSet;
    private DataSet testSet;
    private int classColumn;
    private int columnCount;
    private String[] results;
    private Map<String, Integer> classCount;
    public NaiveBayes(DataSet dataSet, DataSet testSet, int classColumn){
        this.dataSet = testSet;
        this.testSet = dataSet;
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
            double tmpProb = classProbabilities.get(classKey);
            List<Double> probabilities = new ArrayList<>();

            for (int i = 0; i < input.length; i++) {
                if (!(i == classColumn)) {
                    if (conditionalProbabilities.get(classKey).get(i).containsKey(input[i].toString())) {
                        double conditionalProbability = conditionalProbabilities.get(classKey).get(i).get(input[i].toString());
                        probabilities.add(conditionalProbability);
                        tmpProb *= conditionalProbability;
                    } else {
                        tmpProb = 0;
                        break;
                    }
                }
            }
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
        // Go through each row of the dataset and make a prediction
        int correct = 0;
        for (int i = 0; i < dataSet.size(); i++) {
            predictions[i] = makePrediction(dataSet.get(i), conditionalProbabilities, classProbabilities);
            if(predictions[i].equals(dataSet.get(i)[classColumn].toString()))
                correct++;
        }
//        System.out.println(correct);
        return predictions;
    }

    // Prior probabilities
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

    // Likelihood probabilities
    private Map<String, Map<Integer, Map<String, Double>>> getConditionalProbabilities(Map<String, Double> classProbabilities){
        // Get count for each variable with class
        // Go through class variables
        // Determine how often each value of each variable exists
        Map<String, Map<Integer, Map<String, Double>>> conditionalProbabilities = new HashMap<>();
        for(String classKey : classProbabilities.keySet()){
            //columns
            Map<Integer, Map<String, Double>> probabilityMap = new HashMap<>();
            for(int i = 0; i < columnCount; i++) {
                if(!(i == classColumn)) {
                    Map<String, Double> columnProbability = new HashMap<>();
                    Map<String, Integer> columnCount = new HashMap<>();
                    //rows
                    for (Data<?>[] data : testSet) {
                        if (data[classColumn].toString().equals(classKey)) {
                            if (!columnCount.containsKey(data[i].toString()))
                                columnCount.put(data[i].toString(), 0);
                            int temp = columnCount.get(data[i].toString());
                            columnCount.put(data[i].toString(), temp + 1);
                        }
                    }
                    for (String key : columnCount.keySet()) {
                        columnProbability.put(key, (double) columnCount.get(key) / (double) classCount.get(classKey));
                    }
                    probabilityMap.put(i, columnProbability);
                }
            }
            conditionalProbabilities.put(classKey, probabilityMap);
        }

        return conditionalProbabilities;
    }

    // Evidence probabilities (similar to conditional except just count probability of each value of each column of data)
    private Map<Integer, Map<String, Double>> getEvidence(){
        Map<Integer, Map<String, Double>> evidence = new HashMap<>();

        //columns
        for(int i = 0; i < columnCount; i++) {
            if (!(i == classColumn)) {
                Map<String, Double> columnProbability = new HashMap<>();
                Map<String, Double> test = new HashMap<>();
                int count = 0;
                //rows
                for (Data<?>[] data : testSet) {
                    count++;
                    if (!test.containsKey(data[i].toString()))
                        test.put(data[i].toString(), 0.0);
                    double temp = test.get(data[i].toString());
                    test.put(data[i].toString(), temp + 1.0);
                }
                for (String key : test.keySet()) {
                    columnProbability.put(key, test.get(key) / (double) count);
                }
                evidence.put(i, columnProbability);
            }
        }
        return evidence;
    }

    //replace true and false with 1 and 0
    private DataSet replace(DataSet temp){
        for(int i = 0; i< temp.size(); i++){
            for(int j =0; j<temp.get(i).length; j++){
                if(temp.get(i)[j].value().equals(true)){
                    temp.get(i)[j] = new Data<>(1);
                }
                if(temp.get(i)[j].value().equals(false)){
                    temp.get(i)[j] = new Data<>(0);
                }
            }
        }
        return temp;
    }
}
