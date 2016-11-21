package csci446.project3.NB;

import csci446.project3.Util.Data;
import csci446.project3.Util.DataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TAN {
    DataSet dataSet;
    DataSet testSet;
    int classColumn;
    int columnCount;
    private String[] results;
    Map<String, Integer> classCount;
    public TAN(DataSet dataSet, DataSet testSet, int classColumn) {
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

    private String makePrediction(Data[] input, Map<String, Map<Integer, Map<String, Double>>> conditionalProbabilities, Map<String, Double> classProbabilities, Graph tree){
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
                        tmpProb *= -tree.getWeight(i);
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

        Map<String, Map<Integer, Map<Integer, Double>>> doubleConditionals = calcDoubleConditionals();
        Graph tree = createUndirectedGraph(doubleConditionals);
        // Check all classes
        // Go through each row of the dataset and make a prediction
        int correct = 0;
        for (int i = 0; i < dataSet.size(); i++) {
            predictions[i] = makePrediction(dataSet.get(i), conditionalProbabilities, classProbabilities, tree);
        }
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
    Graph createUndirectedGraph(Map<String, Map<Integer, Map<Integer, Double>>> doubleConditionals){
        int nodeCount = columnCount;
        Graph undirectedGraph = new Graph(nodeCount, doubleConditionals);
        undirectedGraph.generateCompleteGraph();
        undirectedGraph.generateMaxSpanTree();
        return undirectedGraph;
    }

    private Map<String, Map<Integer, Map<Integer, Double>>> calcDoubleConditionals(){
        Map<String, Map<Integer, Map<Integer, Double>>> classMap = new HashMap<>();
        for(String classKey : classCount.keySet()){
            Map<Integer, Map<Integer, Double>> xMap = new HashMap<>();
            classMap.put(classKey, xMap);
            for(int i = 0; i < columnCount; i++){
                if(i != classColumn) {
                    Map<Integer, Double> yMap = new HashMap<>();
                    xMap.put(i, yMap);
                    for (int j = 0; j < columnCount; j++) {
                        if (j != classColumn && i != j) {
                        Map<Integer, Integer> columnCount = new HashMap<>();
                            for (Data<?>[] data : testSet) {
                                if (data[classColumn].toString().equals(classKey) && data[i].value() == data[j].value()) {
                                    if (!columnCount.containsKey(j))
                                        columnCount.put(j, 0);
                                    int temp = columnCount.get(j);
                                    columnCount.put(j, temp + 1);
                                }
                            }
                            double mutualProbability = 0.0;
                            if (columnCount.containsKey(j))
                                mutualProbability = (double) columnCount.get(j) / (double) classCount.get(classKey);

                            yMap.put(j, mutualProbability);
                        }
                    }
                }
            }
        }
        return classMap;
    }
}

class Graph{
    private double[][] edges;
    private int nodeCount;
    private Map<String, Map<Integer, Map<Integer, Double>>> doubleConditionals;
    private int[] maxTree;

    Graph(int n, Map<String, Map<Integer, Map<Integer, Double>>> doubleConditionals){
        edges = new double[n][n];
        nodeCount = n;
        this.doubleConditionals = doubleConditionals;
    }

    void createEdge(int a, int b, double weight){
        edges[a][b] = weight;
    }

    double getWeight(int node){
        if(maxTree[node] > 0)
            return edges[maxTree[node]][node];
        return -1;
    }

    void generateCompleteGraph() {
        double[][] edgeWeights = calculateMutualConditionalInformation();
        for(int i = 0; i < nodeCount; i++){
            for(int j = 0; j < nodeCount; j++){
                if(i != j) {
                    double conditionalInformation = edgeWeights[i][j];
                    createEdge(i, j, conditionalInformation);
                    createEdge(j,i, conditionalInformation);
                }
            }
        }
    }
    private double[][] calculateMutualConditionalInformation(){
        double[][] finalValues = new double[nodeCount][nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            double temp = 0;
            if(i != 0) {
                for (int j = 0; j < nodeCount; j++) {
                    if(j != 0) {
                        for (String classKey : doubleConditionals.keySet()) {
                            if (doubleConditionals.get(classKey).containsKey(i))
                                if (doubleConditionals.get(classKey).get(i).containsKey(j) && doubleConditionals.get(classKey).get(i).get(j) > 0)
                                    temp += doubleConditionals.get(classKey).get(i).get(j) * Math.log(doubleConditionals.get(classKey).get(i).get(j));
                        }
                    }
                    if(i!=j)
                        finalValues[i][j] += temp;
                }
            }
        }

        return finalValues;
    }

    int[] generateMaxSpanTree(){
        int[] parent = new int[nodeCount];
        double[] key = new double[nodeCount];
        boolean[] visited = new boolean[nodeCount];
        for(int i = 0; i < nodeCount; i++){
            key[i] = 100.0;
            visited[i] = false;
        }
        key[1] = 0;
        parent[1] = -1;

        for(int count = 1; count < nodeCount - 1; count++){
            int u = minKey(key, visited);
            if(u < 0)
                break;
            visited[u] = true;
            for(int v = 0; v < nodeCount; v++){
                if(edges[u][v] != 0.0 && !visited[v] && edges[u][v] < key[v]){
                    parent[v] = u;
                    key[v] = edges[u][v];
                }
            }
        }
        this.maxTree = parent;
        return parent;
    }

    private int minKey(double[] key, boolean[] visited) {
        double min = 100.0;
        int minIndex = -1;

        for(int v = 1; v < nodeCount; v++){
            if(!visited[v] && key[v] < min){
                min = key[v];
                minIndex = v;
            }
        }
        return minIndex;
    }
}