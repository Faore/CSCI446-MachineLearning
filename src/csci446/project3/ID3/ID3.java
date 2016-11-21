package csci446.project3.ID3;

import com.sun.org.apache.xpath.internal.operations.Bool;
import csci446.project3.Util.Data;
import csci446.project3.Util.DataSet;
import csci446.project3.Util.DataType;

import java.util.ArrayList;
import java.util.LinkedList;

import static java.lang.Double.NaN;

/**
 * Created by cetho on 11/12/2016.
 */
public class ID3 {

    private DataSet fullDataSet;
    private DataSet testSet;

    private boolean[] usedFeatures;
    private int classColumn;

    private String classes[];

    private Node root;
    private LinkedList<Node> unfinishedNodes;

    public ID3(DataSet dataSet, DataSet testSet, int classColumn) throws Exception {
        //Create a boolean array to keep track of whether or not data has been split by the algorithm already.
        this.usedFeatures = new boolean[dataSet.columnCount()];
        //Mark the class column so it isn't used. Afterall, we're trying to derive that.
        this.classColumn = classColumn;
        this.usedFeatures[classColumn] = true;
        this.fullDataSet = dataSet;
        this.testSet = testSet;
        this.unfinishedNodes = new LinkedList<Node>();

        ArrayList<String> classes = new ArrayList<String>();
        //Create a unique list of all the possible classes.
        for (Data<?>[] data : dataSet) {
            if(!classes.contains((String) data[classColumn].value())) {
                classes.add((String) data[classColumn].value());
            }
        }
        this.classes = new String[classes.size()];
        classes.toArray(this.classes);
        this.generateTree();
    }

    public void generateTree() throws Exception {
        //Create root node and mark the feature as used.
        int bestCol = findHighestGainColumn();
        Node root = new Node(bestCol);
        root.data = this.fullDataSet;
        this.usedFeatures[bestCol] = true;
        //Get the dataType of this feature to create the children.
        DataType dataType = this.fullDataSet.get(0)[bestCol].type();
        createChildrenForFeature(root, dataType);
        this.root = root;

        while(!unfinishedNodes.isEmpty() && !noMoreFeatures()) {
            Node parent = unfinishedNodes.peek();
            int nextColumn = findHighestGainColumn(parent);
            if(nextColumn == -1) {
                //Can't make anymore improvements.
                parent.notImprovable = true;
                unfinishedNodes.remove(parent);
            }
            else {
                //Create the new feature node
                Node nextNode = new Node(nextColumn);
                parent.children.add(nextNode);
                parent.children.get(0).data = parent.data;
                nextNode.parent = parent;
                this.usedFeatures[nextColumn] = true;
                //Get datatype and create children.
                DataType dt = this.fullDataSet.get(0)[nextColumn].type();
                createChildrenForFeature(nextNode, dt);
                unfinishedNodes.remove(parent);
            }
        }
    }

    public String classify(Data<?>[] search) {
        if(this.root == null) {
            return "FAIL";
        }
        Node current = this.root;
        Boolean traversed = false;
        while(true) {
            if(current.notImprovable || isPure(current)) {
                //Done
                break;
            } else {
                if(current.isFeature) {
                    //Search for the child node to go to.
                    for (Node child: current.children) {
                        //Check for a matching column.
                        if(child.data.get(0)[child.column].value().equals(search[child.column].value())) {
                            current = child;
                            traversed = true;
                            break;
                        }
                    }
                    if(!traversed) {
                        //Couldn't find where to go. Guess we stop here.
                        break;
                    }
                    //Keep going.
                    traversed = false;
                }
                else {
                    if(current.children.size() > 0) {
                        current = current.children.get(0);
                    }
                    else {
                        break;
                    }
                }
            }
        }
        if(current.isFeature && current != root) {
            current = current.parent;
        }
        if(isPure(current)) {
            return (String) current.data.get(0)[classColumn].value();
        } else {
            //Make the highest probability guess.
            int[] classCounts = nodeClassCount(current);
            int max = Integer.MIN_VALUE;
            int maxCol = -1;
            for(int i = 0; i < classCounts.length; i++) {
                if(max < classCounts[i]) {
                    max = classCounts[i];
                    maxCol = i;
                }
            }
            return this.classes[maxCol];
        }
    }

    public boolean noMoreFeatures() {
        for (boolean col : this.usedFeatures) {
            if(!col) {
                return false;
            }
        }
        return true;
    }

    public void createChildrenForFeature(Node parent, DataType dataType) {
        if(dataType == DataType.Boolean) {
            Boolean[] values = getBooleanValues(parent.column);
            //Make a new node for each value appearing under that node.
            for(Boolean value : values) {
                ArrayList<Integer> indexes = new ArrayList<Integer>();
                //Grab each item that's value matches the one we're searching for.
                for (int i = 0; i < parent.data.size(); i++) {
                    if(((Boolean) parent.data.get(i)[parent.column].value()).equals(value)) {
                        indexes.add(i);
                    }
                }
                if(indexes.size() > 0) {
                    Node newNode = new Node(parent.data.createSubset(indexes.toArray(new Integer[indexes.size()])), parent, parent.column);
                    parent.children.add(newNode);
                    if(!isPure(newNode)) {
                        this.unfinishedNodes.add(newNode);
                    }
                }
            }
        } else if (dataType == DataType.String) {
            String[] values = getStringValues(parent.column);
            //Make a new node for each value appearing under that node.
            for(String value : values) {
                ArrayList<Integer> indexes = new ArrayList<Integer>();
                //Grab each item that's value matches the one we're searching for.
                for (int i = 0; i < parent.data.size(); i++) {
                    if(((String) parent.data.get(i)[parent.column].value()).equals(value)) {
                        indexes.add(i);
                    }
                }
                if(indexes.size() > 0) {
                    Node newNode = new Node(parent.data.createSubset(indexes.toArray(new Integer[indexes.size()])), parent, parent.column);
                    parent.children.add(newNode);
                    if(!isPure(newNode)) {
                        this.unfinishedNodes.add(newNode);
                    }
                }
            }
        } else if (dataType == DataType.Integer) {
            Integer[] values = getIntegerValues(parent.column);
            //Make a new node for each value appearing under that node.
            for(Integer value : values) {
                ArrayList<Integer> indexes = new ArrayList<Integer>();
                //Grab each item that's value matches the one we're searching for.
                for (int i = 0; i < parent.data.size(); i++) {
                    if(((Integer) parent.data.get(i)[parent.column].value()).equals(value)) {
                        indexes.add(i);
                    }
                }
                if(indexes.size() > 0) {
                    Node newNode = new Node(parent.data.createSubset(indexes.toArray(new Integer[indexes.size()])), parent, parent.column);
                    parent.children.add(newNode);
                    if(!isPure(newNode)) {
                        this.unfinishedNodes.add(newNode);
                    }
                }
            }
        } else {
            //Double
            Double[] values = getDoubleValues(parent.column);
            //Make a new node for each value appearing under that node.
            for(Double value : values) {
                ArrayList<Integer> indexes = new ArrayList<Integer>();
                //Grab each item that's value matches the one we're searching for.
                for (int i = 0; i < parent.data.size(); i++) {
                    if(((Double) parent.data.get(i)[parent.column].value()).equals(value)) {
                        indexes.add(i);
                    }
                }
                if(indexes.size() > 0) {
                    Node newNode = new Node(parent.data.createSubset(indexes.toArray(new Integer[indexes.size()])), parent, parent.column);
                    parent.children.add(newNode);
                    if(!isPure(newNode)) {
                        this.unfinishedNodes.add(newNode);
                    }
                }
            }
        }
    }

    public boolean isPure(Node node) {
        DataSet dataset = node.data;
        String first = (String) dataset.get(0)[classColumn].value();
        for (Data<?>[] data : dataset) {
            if(!((String) data[classColumn].value()).equals(first)) {
                return false;
            }
        }
        return true;
    }

    public int findHighestGainColumn() throws Exception {
        double highestValue = Integer.MIN_VALUE;
        int highestColumn = -1;
        //Only evaluate unused columns.
        for (int j = 0; j < fullDataSet.get(0).length; j++) {
            if(!usedFeatures[j]) {
                double gain = gain(j);
                if (highestValue < gain) {
                    highestValue = gain;
                    highestColumn = j;
                }
            }
        }
        return highestColumn;
    }

    public int findHighestGainColumn(Node relativeTo) throws Exception {
        double highestValue = Double.MIN_VALUE;
        int highestColumn = -1;
        for (int j = 0; j < relativeTo.data.get(0).length; j++) {
            if(!usedFeatures[j]) {
                double gain = gain(j);
                if (highestValue < gain) {
                    highestValue = gain;
                    highestColumn = j;
                }
            }
        }
        return highestColumn;
    }

    public double gain(int column) throws Exception {
        int[] classOccurences = getClassOccurrencesForFeature(column);
        double[] classProbabilities = occurrencesToProbability(classOccurences);
        double featureEntropy = entropy(classProbabilities); //Get the entropy for all the values of this feature/column.
        //Count up the feature's value counts
        DataSet data = this.fullDataSet;
        if(root != null) {
            data = unfinishedNodes.peek().data;
        }

        DataType dataType = data.get(0)[column].type();

        Double[] valueEntropies;
        int[] subsetSizes;

        if(dataType == DataType.Boolean) {
            Boolean[] dataValues = getBooleanValues(column);
            valueEntropies = new Double[dataValues.length];
            subsetSizes = new int[dataValues.length];

            for (int i = 0; i < dataValues.length; i++) {
                int[] occurrences = getClassOccurrences(column, dataValues[i]);
                for(int o : occurrences) {
                    subsetSizes[i]+= o;
                }
                valueEntropies[i] = entropy(occurrencesToProbability(occurrences))*subsetSizes[i]/data.size();
            }
        } else if (dataType == DataType.String) {
            String[] dataValues = getStringValues(column);
            valueEntropies = new Double[dataValues.length];
            subsetSizes = new int[dataValues.length];

            for (int i = 0; i < dataValues.length; i++) {
                int[] occurrences = getClassOccurrences(column, dataValues[i]);
                for(int o : occurrences) {
                    subsetSizes[i]+= o;
                }
                valueEntropies[i] = entropy(occurrencesToProbability(occurrences))*subsetSizes[i]/data.size();
            }
        } else if (dataType == DataType.Integer) {
            Integer[] dataValues = getIntegerValues(column);
            valueEntropies = new Double[dataValues.length];
            subsetSizes = new int[dataValues.length];

            for (int i = 0; i < dataValues.length; i++) {
                int[] occurrences = getClassOccurrences(column, dataValues[i]);
                for(int o : occurrences) {
                    subsetSizes[i]+= o;
                }
                valueEntropies[i] = entropy(occurrencesToProbability(occurrences))*subsetSizes[i]/data.size();
            }
        } else {
            Double[] dataValues = getDoubleValues(column);
            valueEntropies = new Double[dataValues.length];
            subsetSizes = new int[dataValues.length];

            for (int i = 0; i < dataValues.length; i++) {
                int[] occurrences = getClassOccurrences(column, dataValues[i]);
                for(int o : occurrences) {
                    subsetSizes[i]+= o;
                }
                valueEntropies[i] = entropy(occurrencesToProbability(occurrences))*subsetSizes[i]/data.size();
            }
        }

        double gain = featureEntropy;

        for(double e : valueEntropies) {
            gain -= e;
        }
        return gain;
    }

    public Boolean[] getBooleanValues(int column) {
        DataSet data = this.fullDataSet;
        if(root != null) {
            data = unfinishedNodes.peek().data;
        }
        ArrayList<Boolean> values = new ArrayList<Boolean>();
        // Build a list of all values for a feature.
        for(int i = 0; i < data.size(); i++) {
            if(!values.contains((Boolean) data.get(i)[column].value())) {
                values.add((Boolean) data.get(i)[column].value());
                if(values.size() == 2) {
                    break;
                }
            }
        }
        return values.toArray(new Boolean[values.size()]);
    }

    public String[] getStringValues(int column) {
        DataSet data = this.fullDataSet;
        if(root != null) {
            data = unfinishedNodes.peek().data;
        }
        ArrayList<String> values = new ArrayList<String>();
        // Build a list of all values for a feature.
        for(int i = 0; i < data.size(); i++) {
            if(!values.contains((String) data.get(i)[column].value())) {
                values.add((String) data.get(i)[column].value());
            }
        }
        return values.toArray(new String[values.size()]);
    }

    public Integer[] getIntegerValues(int column) {
        DataSet data = this.fullDataSet;
        if(root != null) {
            data = unfinishedNodes.peek().data;
        }
        ArrayList<Integer> values = new ArrayList<Integer>();
        // Build a list of all values for a feature.
        for(int i = 0; i < data.size(); i++) {
            if(!values.contains((Integer) data.get(i)[column].value())) {
                values.add((Integer) data.get(i)[column].value());
            }
        }
        return values.toArray(new Integer[values.size()]);
    }

    public Double[] getDoubleValues(int column) {
        DataSet data = this.fullDataSet;
        if(root != null) {
            data = unfinishedNodes.peek().data;
        }
        ArrayList<Double> values = new ArrayList<Double>();
        // Build a list of all values for a feature.
        for(int i = 0; i < data.size(); i++) {
            if(!values.contains((Double) data.get(i)[column].value())) {
                values.add((Double) data.get(i)[column].value());
            }
        }
        return values.toArray(new Double[values.size()]);
    }


    public int[] getClassOccurrences(int column, Boolean value) {
        //Get the available data for the node we want to expand on.
        DataSet data = this.fullDataSet;
        if(root != null) {
            data = unfinishedNodes.peek().data;
        }

        int classOccurrences[] = new int[classes.length];

        for(int i = 0; i < data.size(); i++) {
            //Filter out datasets that don't have this specific value.
            if(((Boolean) data.get(i)[column].value()).equals(value)) {
                for (int j = 0; j < classes.length; j++) {
                    //Look for the class index corresponding to the class of this data point.
                    if (((String) data.get(i)[classColumn].value()).equals(classes[j])) {
                        //Increment the number of occurrences for this node.
                        classOccurrences[j]++;
                        break;
                    }
                }
            }
        }
        return classOccurrences;
    }

    public int[] getClassOccurrences(int column, String value) {
        //Get the available data for the node we want to expand on.
        DataSet data = this.fullDataSet;
        if(root != null) {
            data = unfinishedNodes.peek().data;
        }

        int classOccurrences[] = new int[classes.length];

        for(int i = 0; i < data.size(); i++) {
            //Filter out datasets that don't have this specific value.
            if(((String) data.get(i)[column].value()).equals(value)) {
                for (int j = 0; j < classes.length; j++) {
                    //Look for the class index corresponding to the class of this data point.
                    if (((String) data.get(i)[classColumn].value()).equals(classes[j])) {
                        //Increment the number of occurrences for this node.
                        classOccurrences[j]++;
                        break;
                    }
                }
            }
        }
        return classOccurrences;
    }

    public int[] getClassOccurrences(int column, Integer value) {
        //Get the available data for the node we want to expand on.
        DataSet data = this.fullDataSet;
        if(root != null) {
            data = unfinishedNodes.peek().data;
        }

        int classOccurrences[] = new int[classes.length];

        for(int i = 0; i < data.size(); i++) {
            //Filter out datasets that don't have this specific value.
            if(((Integer) data.get(i)[column].value()).equals(value)) {
                for (int j = 0; j < classes.length; j++) {
                    //Look for the class index corresponding to the class of this data point.
                    if (((String) data.get(i)[classColumn].value()).equals(classes[j])) {
                        //Increment the number of occurrences for this node.
                        classOccurrences[j]++;
                        break;
                    }
                }
            }
        }
        return classOccurrences;
    }

    public int[] getClassOccurrences(int column, Double value) {
        //Get the available data for the node we want to expand on.
        DataSet data = this.fullDataSet;
        if(root != null) {
            data = unfinishedNodes.peek().data;
        }

        int classOccurrences[] = new int[classes.length];

        for(int i = 0; i < data.size(); i++) {
            //Filter out datasets that don't have this specific value.
            if(((Double) data.get(i)[column].value()).equals(value)) {
                for (int j = 0; j < classes.length; j++) {
                    //Look for the class index corresponding to the class of this data point.
                    if (((String) data.get(i)[classColumn].value()).equals(classes[j])) {
                        //Increment the number of occurrences for this node.
                        classOccurrences[j]++;
                        break;
                    }
                }
            }
        }
        return classOccurrences;
    }

    public double[] occurrencesToProbability(int[] occurrences) {
        double[] probabilities = new double[occurrences.length];
        int points = 0;
        for(int i : occurrences) {
            points += i;
        }
        for(int i = 0; i < probabilities.length; i++) {
            probabilities[i] = (double) occurrences[i]/points;
        }
        return probabilities;
    }

    public int[] getClassOccurrencesForFeature(int column) {
        //Get the available data for the node we want to expand on.
        DataSet data = this.fullDataSet;
        if(root != null) {
            data = unfinishedNodes.peek().data;
        }

        int classOccurrences[] = new int[classes.length];

        for(int i = 0; i < data.size(); i++) {
            for(int j = 0; j < classes.length; j++) {
                //Look for the class index corresponding to the class of this data point.
                if(((String) data.get(i)[classColumn].value()).equals(classes[j])) {
                    //Increment the number of occurrences for this node.
                    classOccurrences[j]++;
                    break;
                }
            }
        }
        return classOccurrences;
    }

    public int[] nodeClassCount(Node node) {
        DataSet data = node.data;

        int classOccurrences[] = new int[classes.length];

        for(int i = 0; i < data.size(); i++) {
            for(int j = 0; j < classes.length; j++) {
                //Look for the class index corresponding to the class of this data point.
                if(((String) data.get(i)[classColumn].value()).equals(classes[j])) {
                    //Increment the number of occurrences for this node.
                    classOccurrences[j]++;
                    break;
                }
            }
        }

        return classOccurrences;
    }

    public String[] getNodeClassValues(Node node) {
        DataSet data = node.data;
        ArrayList<String> values = new ArrayList<String>();
        // Build a list of all values for a feature.
        for(int i = 0; i < data.size(); i++) {
            if(!values.contains((String) data.get(i)[classColumn].value())) {
                values.add((String) data.get(i)[classColumn].value());
            }
        }
        return values.toArray(new String[values.size()]);
    }

    public double entropy(double[] probabilities) {
        double result = 0;
        for(double p : probabilities) {
            /*
             * Logarithm explodes and give NaN if you hand it a 0.
             */
            Double exp = p * Math.log(p) / Math.log(2); //Uses change of base for logarithms since Math.log is base 10.
            if(!Double.isNaN(exp)) {
                result -= exp;
            }
        }
        return result;
    }

}
