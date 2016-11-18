package csci446.project3.NB;

import csci446.project3.Util.Data;
import csci446.project3.Util.DataSet;

import java.util.HashMap;
import java.util.Map;

public class NaiveBayes {
    private DataSet dataSet;
    private DataSet testSet;
//    private Map<String, Double> classProbabilities;
    private int classColumn;
    private String[] results;
    public NaiveBayes(DataSet dataSet, DataSet testSet, int classColumn){
        this.dataSet = dataSet;
        this.testSet = testSet;
        this.classColumn = classColumn;
        begin();
    }

    private void begin() {
//        this.classProbabilities = classProbabilities();
        Map<String, SummarizedData[]> summaries = summarizeDataByClass();
        results = makeAllPredictions(summaries, dataSet);
    }
    public String[] getResults(){
        return results;
    }
    private String makePrediction(Map<String, SummarizedData[]> summaries, Data[] input){
        Map<String, Double> classProbabilities = getClassProbabilities(summaries, input);
        String predictClass = "";
        double bestProbability = -1;
        for(Map.Entry<String, Double> entry : classProbabilities.entrySet()){
            double curValue = entry.getValue();
            if(predictClass.isEmpty() ||  bestProbability < curValue){
                predictClass = entry.getKey();
                bestProbability = curValue;
            }
        }

        return predictClass;
    }

    private String[] makeAllPredictions(Map<String, SummarizedData[]> summaries, DataSet dataSet){
        String[] predictions = new String[dataSet.size()];
        for(int i = 0; i < predictions.length; i++){
            predictions[i] = makePrediction(summaries, dataSet.get(i));
        }

        return predictions;
    }
    private Map<String, Double> getClassProbabilities(Map<String, SummarizedData[]> summaries, Data[] input){
        Map<String, Double> classProbabilities = new HashMap<>();
        for(Map.Entry<String, SummarizedData[]> entry : summaries.entrySet()) {
            SummarizedData[] summarizedData = entry.getValue();
            double tmpProbability = 1;
            for(int i = 0; i < summarizedData.length; i++){
                SummarizedData summary = summarizedData[i];
                tmpProbability *= probability(input[i], summary.mean, summary.deviation);
            }
            classProbabilities.put(entry.getKey(), tmpProbability);
        }
        return classProbabilities;
    }
    private double probability(Data value, double average, double deviation){
        double convertedValue = Double.parseDouble(value.value().toString());
        double exp = Math.exp(-(Math.pow(convertedValue - average, 2)/(2*Math.pow(deviation, 2))));
        return (1.0 / (Math.sqrt(2 * Math.PI)) * deviation) * exp;
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
            String classValue = (String)data[classColumn].value();
            if(!seperatedData.containsKey(classValue)){
                seperatedData.put(classValue, new DataSet());
            }

            DataSet dataSet = seperatedData.get(classValue);
            dataSet.add(data);
        }
        return seperatedData;
    }

//    private Map<String, Double> classProbabilities(){
//        HashMap<String, Integer> classTotals = new HashMap<>();
//        HashMap<String, Double> trainedModel = new HashMap<>();
//        //Iterate through rows of training set
//        // Add up values when class = 4 and add up values when class = 2
//        // Class probabilities
//        for(Data[] row : this.testSet){
//            String classValue = (String)row[this.classColumn].value();
//            if(!classTotals.containsKey(classValue)) {
//                classTotals.put(classValue, 0);
//            }
//            int curCount = classTotals.get(classValue);
//            curCount++;
//            classTotals.put(classValue, curCount);
//        }
//        for(Map.Entry<String, Integer> entry : classTotals.entrySet()){
//            if(!trainedModel.containsKey(entry.getKey())){
//                trainedModel.put(entry.getKey(), (double)entry.getValue()/(double)this.testSet.size());
//            }
//        }
//        return trainedModel;
//    }
    
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
