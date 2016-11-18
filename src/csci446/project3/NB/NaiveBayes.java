package csci446.project3.NB;

import csci446.project3.Util.Data;
import csci446.project3.Util.DataSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NaiveBayes {
    private DataSet dataSet;
    private DataSet testSet;
    private int classColumn;
    private String[] results;
    private Map<String, Integer> classCount;
    public NaiveBayes(DataSet dataSet, DataSet testSet, int classColumn){
        this.dataSet = dataSet;
        this.testSet = testSet;
        replace(dataSet);
        replace(testSet);
        this.classColumn = classColumn;
        begin();
    }

    private void begin() {
        Map<String, SummarizedData[]> summaries = summarizeDataByClass();
        results = makeAllPredictions(dataSet);
    }

    public String[] getResults(){
        return results;
    }

    private String makePrediction(Data[] input, Map<String, double[]> conditionalProbabilities){
        String predictClass = "";
        double bestProbability = -1;
        // Compare all classes and find the best probability
        for(Map.Entry<String, double[]> entry : conditionalProbabilities.entrySet()){
            double[] values = entry.getValue();
            double tmp = 1;
            for(int i = 0; i < values.length; i++){
                tmp *= values[i];
            }
            if(predictClass.isEmpty() ||  bestProbability < tmp){
                predictClass = entry.getKey();
                bestProbability = tmp;
            }
        }

        return predictClass;
    }

    private String[] makeAllPredictions(DataSet dataSet){
        String[] predictions = new String[dataSet.size()];
        Map<String, Double> classProbabilities = getClassProbabilities();
        Map<String, double[]> conditionalProbabilities = getConditionalProbabilities(classProbabilities);
        // Check all classes
        int i = 0;
        // Go through each row of the dataset and make a prediction
        for (Data<?>[] data : dataSet) {
            predictions[i] = makePrediction(data, conditionalProbabilities);
            i++;
        }
//        for(int i = 0; i < predictions.length; i++){
//            predictions[i] = makePrediction(summaries, dataSet.get(i));
//        }

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
    private Map<String, double[]> getConditionalProbabilities(Map<String, Double> classProbabilities){
        // Get count for each variable with class
        Map<String, double[]> conditionalProbabilities = new HashMap<>();
        for(Map.Entry<String, Double> entry : classProbabilities.entrySet()){
            // TODO: Replace classColumn
            double[] probabilities = new double[classColumn];
            for(int i = 0; i < classColumn; i++) {
                double tmp = 0;
                for (Data<?>[] data : testSet){
                    if(Objects.equals(data[classColumn].toString(), entry.getKey())){
                        tmp += Double.parseDouble(data[i].value().toString());
                    }
                }
                probabilities[i] = (tmp / classCount.get(entry.getKey()).doubleValue());
            }
            conditionalProbabilities.put(entry.getKey(), probabilities);
        }

        return conditionalProbabilities;
    }

    private SummarizedData[] summarizeData(DataSet aDataSet) {
        // Go through each column of each row and sum them together
        int columnCount = aDataSet.get(0).length;
        double[] columnSum = new double[columnCount - 1];
        for(int i = 0; i < columnCount - 1; i++) {
            for (Data<?>[] data : aDataSet) {
                columnSum[i] += Double.parseDouble(data[i].value().toString());
            }
        }

        SummarizedData[] summarizedData = new SummarizedData[columnCount - 1];
        //Averages
        double[] averages = new double[columnCount - 1];
        for(int i = 0; i < columnSum.length; i ++){
            averages[i] = (double)columnSum[i] / (double)aDataSet.size();
        }
        //deviations
        double[] deviations = deviations(aDataSet, averages);
        for(int i = 0; i < averages.length; i++){
            summarizedData[i] = new SummarizedData(averages[i], deviations[i]);
        }
        return summarizedData;
    }

    private double[] deviations(DataSet dataSet, double[] averages){
        double[] deviations = new double[averages.length];
        for(int i = 0; i < averages.length; i++) {
            double sum = 0.0;
            for (Data<?>[] data : dataSet) {
                double curVal = Double.parseDouble(data[i].value().toString());
                sum += Math.pow((double)curVal - averages[i], 2);
            }
            deviations[i] = Math.sqrt(sum/(double)dataSet.size());
        }
        return deviations;
    }

    private Map<String, SummarizedData[]> summarizeDataByClass() {
        Map<String, DataSet> seperatedData = seperateDataByClass();
        Map<String, SummarizedData[]> classSummaries = new HashMap<>();
        for(Map.Entry<String, DataSet> entry : seperatedData.entrySet()){
            SummarizedData[] summarizedData = summarizeData(entry.getValue());
            if(!classSummaries.containsKey(entry.getKey()))
                classSummaries.put(entry.getKey(), summarizedData);
        }

        return classSummaries;
    }

    private Map<String, DataSet> seperateDataByClass(){
        HashMap<String, DataSet> seperatedData = new HashMap<>();
        // Loop through data
        for(Data[] data : testSet){
            String classValue = data[classColumn].value().toString();
            if(!seperatedData.containsKey(classValue)){
                seperatedData.put(classValue, new DataSet());
            }

            DataSet dataSet = seperatedData.get(classValue);
            dataSet.add(data);
        }
        return seperatedData;
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

    private void swapClassColumn(DataSet dataSet){
        if(this.classColumn == 1) {

        }
    }
}

class SummarizedData
{
    double mean;
    double deviation;
    SummarizedData(double mean, double deviation){
        this.mean = mean;
        this.deviation = deviation;
    }
}
