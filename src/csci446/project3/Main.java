package csci446.project3;

import csci446.project3.DataSets.BreastCancer;
import csci446.project3.DataSets.Glass;
import csci446.project3.DataSets.HouseVotes;
import csci446.project3.DataSets.Soybean;
import csci446.project3.DataSets.Iris;
import csci446.project3.ID3.ID3;
import csci446.project3.NB.NaiveBayes;
import csci446.project3.Util.DataParser;
import csci446.project3.Util.DataSet;
import static java.lang.Math.sqrt;

import java.util.Collections;

public class Main {
    
    public static void main(String[] args) throws Exception {
        /*
         * Columns Need to be named manually.
         *
         * Data-types for each column need to be specified. You'll find all that in the names file.
         *
         * To keep this class clean, csci446.project3.DataSets has the all the basic info to pass to the algorithm.
         */
        DataSet unfilteredHouseVotes = DataParser.parseData(HouseVotes.filename, HouseVotes.columnNames, HouseVotes.dataTypes, HouseVotes.ignoreColumns, HouseVotes.classColumn, HouseVotes.discretizeColumns);
        DataSet unfilteredBreastCancer = DataParser.parseData(BreastCancer.filename, BreastCancer.columnNames, BreastCancer.dataTypes, BreastCancer.ignoreColumns, BreastCancer.classColumn, HouseVotes.discretizeColumns);
        DataSet unfilteredGlass = DataParser.parseData(Glass.filename, Glass.columnNames, Glass.dataTypes, Glass.ignoreColumns, Glass.classColumn, Glass.discretizeColumns);
        DataSet unfilteredIris = DataParser.parseData(Iris.filename, Iris.columnNames, Iris.dataTypes, Iris.ignoreColumns, Iris.classColumn, Iris.discretizeColumns);
        DataSet unfilteredSoybean = DataParser.parseData(Soybean.filename, Soybean.columnNames, Soybean.dataTypes, Soybean.ignoreColumns, Soybean.classColumn, Soybean.discretizeColumns);
        
        /*
         * The contents of the DataSet are not always random.
         * You can shuffle them using Collections.shuffle()
         */

        Collections.shuffle(unfilteredHouseVotes);
        Collections.shuffle(unfilteredBreastCancer);
        Collections.shuffle(unfilteredGlass);
        Collections.shuffle(unfilteredIris);
        Collections.shuffle(unfilteredSoybean);

        /*
         * Each dataset is being split into 10 equal-ish size parts.
         */
        int crossVerSize = 10;
        DataSet[] houseVotesSets = unfilteredHouseVotes.splitSet(crossVerSize);
        DataSet[] breastCancerSets = unfilteredBreastCancer.splitSet(crossVerSize);
        DataSet[] glassSets = unfilteredGlass.splitSet(crossVerSize);
        DataSet[] irisSets = unfilteredIris.splitSet(crossVerSize);
        DataSet[] soybeanSets = unfilteredSoybean.splitSet(crossVerSize);

        /*
         * Begin Tests!
         */
        //KNN
        int k;
        //House Votes
        //Store test results in array. PercentageOfCorrect;
        double[] testResults = new double[crossVerSize];
        System.out.println(HouseVotes.class.getSimpleName());
        for (int test = 0; test < crossVerSize; test++) {
            //Prepare the datasets.
            DataSet houseVotesTestingSet = houseVotesSets[test];
            DataSet houseVotes = houseVotesTestingSet.setupEmptySet();
            for (int i = 0; i < crossVerSize; i++) {
                if (i != test) {
                    houseVotes.addAll(houseVotesSets[i]);
                }
            }
            //Test Data storage.
            //Run the test
            k = (int) sqrt(houseVotes.get(0).length);    //standard k=sqrt(features)
            KNN knn = new KNN(houseVotes, houseVotesTestingSet, HouseVotes.classColumn, k); //Old value of K: 3
            String[] knnHouseVotes = new String[houseVotesTestingSet.size()];
            for (int i = 0; i < houseVotesTestingSet.size(); i++) {
                knnHouseVotes[i] = knn.classify(houseVotesTestingSet.get(i));
            }
            int incorrect = 0;
            int correct = 0;
            for (int i = 0; i < houseVotesTestingSet.size(); i++) {
                if (knnHouseVotes[i].equals(houseVotesTestingSet.get(i)[HouseVotes.classColumn].value())) {
                    correct = correct + 1;
                } else {
                    incorrect = incorrect + 1;
                }
            }
            testResults[test] = (double) correct / ((double)(correct + incorrect)) * 100d;
        }
        double total = 0;
        System.out.print("\tKNN: Success Rates: ");
        for (double result : testResults) {
            total += result;
            System.out.print(result + "%, ");
        }
        total = total/testResults.length;
        System.out.println();
        System.out.println("\tKNN: Avg. Success Rate: " + total);
        System.out.println();


        //Breast Cancer
        testResults = new double[crossVerSize];
        System.out.println(BreastCancer.class.getSimpleName());
        for (int test = 0; test < crossVerSize; test++) {
            //Prepare the datasets.
            DataSet breastCancerTestingSet = breastCancerSets[test];
            DataSet breastCancer = breastCancerTestingSet.setupEmptySet();
            for (int i = 0; i < crossVerSize; i++) {
                if (i != test) {
                    breastCancer.addAll(breastCancerSets[i]);
                }
            }
            //Run the test
            k = (int) sqrt(breastCancer.get(0).length);    //standard k=sqrt(features)
            KNN knn = new KNN(breastCancer, breastCancerTestingSet, BreastCancer.classColumn, k); //Old value of K: 3
            String[] knnBreastCancer = new String[breastCancerTestingSet.size()];
            for (int i = 0; i < breastCancerTestingSet.size(); i++) {
                knnBreastCancer[i] = knn.classify(breastCancerTestingSet.get(i));
            }
            int incorrect = 0;
            int correct = 0;
            for (int i = 0; i < breastCancerTestingSet.size(); i++) {
                if (knnBreastCancer[i].equals(breastCancerTestingSet.get(i)[BreastCancer.classColumn].value())) {
                    correct = correct + 1;
                } else {
                    incorrect = incorrect + 1;
                }
            }
            testResults[test] = (double) correct / ((double)(correct + incorrect)) * 100d;
        }
        total = 0;
        System.out.print("\tKNN: Success Rates: ");
        for (double result : testResults) {
            total += result;
            System.out.print(result + "%, ");
        }
        total = total/testResults.length;
        System.out.println();
        System.out.println("\tKNN: Avg. Success Rate: " + total);
        System.out.println();

        
        //Glass
        testResults = new double[crossVerSize];
        System.out.println(Glass.class.getSimpleName());
        for (int test = 0; test < crossVerSize; test++) {
            //Prepare the datasets.
            DataSet glassTestingSet = glassSets[test];
            DataSet glass = glassTestingSet.setupEmptySet();
            for (int i = 0; i < crossVerSize; i++) {
                if (i != test) {
                    glass.addAll(glassSets[i]);
                }
            }
            //Test Data storage.
            //Run the test
            k = (int) sqrt(glass.get(0).length);    //standard k=sqrt(features)
            KNN knn = new KNN(glass, glassTestingSet, Glass.classColumn, k); //Old value of K: 3
            String[] knnGlass = new String[glassTestingSet.size()];
            for (int i = 0; i < glassTestingSet.size(); i++) {
                knnGlass[i] = knn.classify(glassTestingSet.get(i));
            }
            int incorrect = 0;
            int correct = 0;
            for (int i = 0; i < glassTestingSet.size(); i++) {
                if (knnGlass[i].equals(glassTestingSet.get(i)[Glass.classColumn].value())) {
                    correct = correct + 1;
                } else {
                    incorrect = incorrect + 1;
                }
            }
            testResults[test] = (double) correct / ((double)(correct + incorrect)) * 100d;
        }
        total = 0;
        System.out.print("\tKNN: Success Rates: ");
        for (double result : testResults) {
            total += result;
            System.out.print(result + "%, ");
        }
        total = total/testResults.length;
        System.out.println();
        System.out.println("\tKNN: Avg. Success Rate: " + total);
        System.out.println();
        
        //Iris
        testResults = new double[crossVerSize];
        System.out.println(Iris.class.getSimpleName());
        for (int test = 0; test < crossVerSize; test++) {
            //Prepare the datasets.
            DataSet irisTestingSet = irisSets[test];
            DataSet iris = irisTestingSet.setupEmptySet();
            for (int i = 0; i < crossVerSize; i++) {
                if (i != test) {
                    iris.addAll(irisSets[i]);
                }
            }
            //Test Data storage.
            //Run the test
            k = (int) sqrt(iris.get(0).length);    //standard k=sqrt(features)
            KNN knn = new KNN(iris, irisTestingSet, Iris.classColumn, k); //Old value of K: 3
            String[] knnIris = new String[irisTestingSet.size()];
            for (int i = 0; i < irisTestingSet.size(); i++) {
                knnIris[i] = knn.classify(irisTestingSet.get(i));
            }
            int incorrect = 0;
            int correct = 0;
            for (int i = 0; i < irisTestingSet.size(); i++) {
                if (knnIris[i].equals(irisTestingSet.get(i)[Iris.classColumn].value())) {
                    correct = correct + 1;
                } else {
                    incorrect = incorrect + 1;
                }
            }
            testResults[test] = (double) correct / ((double)(correct + incorrect)) * 100d;
        }
        total = 0;
        System.out.print("\tKNN: Success Rates: ");
        for (double result : testResults) {
            total += result;
            System.out.print(result + "%, ");
        }
        total = total/testResults.length;
        System.out.println();
        System.out.println("\tKNN: Avg. Success Rate: " + total);
        System.out.println();
        
        //Soybean
        testResults = new double[crossVerSize];
        System.out.println(Soybean.class.getSimpleName());
        for (int test = 0; test < crossVerSize; test++) {
            //Prepare the datasets.
            DataSet soybeanTestingSet = soybeanSets[test];
            DataSet soybean = soybeanTestingSet.setupEmptySet();
            for (int i = 0; i < crossVerSize; i++) {
                if (i != test) {
                    soybean.addAll(soybeanSets[i]);
                }
            }
            //Run the test
            k = (int) sqrt(soybean.get(0).length);    //standard k=sqrt(features)
            KNN knn = new KNN(soybean, soybeanTestingSet, Soybean.classColumn, k); //Old value of K: 3
            String[] knnSoybean = new String[soybeanTestingSet.size()];
            for (int i = 0; i < soybeanTestingSet.size(); i++) {
                knnSoybean[i] = knn.classify(soybeanTestingSet.get(i));
            }
            int incorrect = 0;
            int correct = 0;
            for (int i = 0; i < soybeanTestingSet.size(); i++) {
                if (knnSoybean[i].equals(soybeanTestingSet.get(i)[Soybean.classColumn].value())) {
                    correct = correct + 1;
                } else {
                    incorrect = incorrect + 1;
                }
            }
            testResults[test] = (double) correct / ((double)(correct + incorrect)) * 100d;
        }
        total = 0;
        System.out.print("\tKNN: Success Rates: ");
        for (double result : testResults) {
            total += result;
            System.out.print(result + "%, ");
        }
        total = total/testResults.length;
        System.out.println();
        System.out.println("\tKNN: Avg. Success Rate: " + total);
        System.out.println();

        
        /*
         * Lets setup ID3:
         * DataSet, TestSet, column with the class categorization. (republican, democrat in this case)
         */
        //HouseVotes
        testResults = new double[crossVerSize];
        System.out.println(HouseVotes.class.getSimpleName());
        for (int test = 0; test < crossVerSize; test++) {
            //Prepare the datasets.
            DataSet houseVotesTestingSet = houseVotesSets[test];
            DataSet houseVotes = houseVotesTestingSet.setupEmptySet();
            for (int i = 0; i < crossVerSize; i++) {
                if (i != test) {
                    houseVotes.addAll(houseVotesSets[i]);
                }
            }
            //Run the test
            ID3 id3 = new ID3(houseVotes, houseVotesTestingSet, HouseVotes.classColumn); //Old value of K: 3
            String[] id3HouseVotes = new String[houseVotesTestingSet.size()];
            for (int i = 0; i < houseVotesTestingSet.size(); i++) {
                id3HouseVotes[i] = id3.classify(houseVotesTestingSet.get(i));
            }
            int incorrect = 0;
            int correct = 0;
            for (int i = 0; i < houseVotesTestingSet.size(); i++) {
                if (id3HouseVotes[i].equals(houseVotesTestingSet.get(i)[HouseVotes.classColumn].value())) {
                    correct = correct + 1;
                } else {
                    incorrect = incorrect + 1;
                }
            }
            testResults[test] = (double) correct / ((double)(correct + incorrect)) * 100d;
        }
        total = 0;
        System.out.print("\tID3: Success Rates: ");
        for (double result : testResults) {
            total += result;
            System.out.print(result + "%, ");
        }
        total = total/testResults.length;
        System.out.println();
        System.out.println("\tID3: Avg. Success Rate: " + total);
        System.out.println();

        //BreastCancer
        testResults = new double[crossVerSize];
        System.out.println(BreastCancer.class.getSimpleName());
        for (int test = 0; test < crossVerSize; test++) {
            //Prepare the datasets.
            DataSet breastCancerTestingSet = breastCancerSets[test];
            DataSet breastCancer = breastCancerTestingSet.setupEmptySet();
            for (int i = 0; i < crossVerSize; i++) {
                if (i != test) {
                    breastCancer.addAll(breastCancerSets[i]);
                }
            }
            //Run the test
            ID3 id3 = new ID3(breastCancer, breastCancerTestingSet, BreastCancer.classColumn); //Old value of K: 3
            String[] id3BreastCancer = new String[breastCancerTestingSet.size()];
            for (int i = 0; i < breastCancerTestingSet.size(); i++) {
                id3BreastCancer[i] = id3.classify(breastCancerTestingSet.get(i));
            }
            int incorrect = 0;
            int correct = 0;
            for (int i = 0; i < breastCancerTestingSet.size(); i++) {
                if (id3BreastCancer[i].equals(breastCancerTestingSet.get(i)[BreastCancer.classColumn].value())) {
                    correct = correct + 1;
                } else {
                    incorrect = incorrect + 1;
                }
            }
            testResults[test] = (double) correct / ((double)(correct + incorrect)) * 100d;
        }
        total = 0;
        System.out.print("\tID3: Success Rates: ");
        for (double result : testResults) {
            total += result;
            System.out.print(result + "%, ");
        }
        total = total/testResults.length;
        System.out.println();
        System.out.println("\tID3: Avg. Success Rate: " + total);
        System.out.println();


        //Glass
        testResults = new double[crossVerSize];
        System.out.println(Glass.class.getSimpleName());
        for (int test = 0; test < crossVerSize; test++) {
            //Prepare the datasets.
            DataSet glassTestingSet = glassSets[test];
            DataSet glass = glassTestingSet.setupEmptySet();
            for (int i = 0; i < crossVerSize; i++) {
                if (i != test) {
                    glass.addAll(glassSets[i]);
                }
            }
            //Run the test
            ID3 id3 = new ID3(glass, glassTestingSet, Glass.classColumn); //Old value of K: 3
            String[] id3Glass = new String[glassTestingSet.size()];
            for (int i = 0; i < glassTestingSet.size(); i++) {
                id3Glass[i] = id3.classify(glassTestingSet.get(i));
            }
            int incorrect = 0;
            int correct = 0;
            for (int i = 0; i < glassTestingSet.size(); i++) {
                if (id3Glass[i].equals(glassTestingSet.get(i)[Glass.classColumn].value())) {
                    correct = correct + 1;
                } else {
                    incorrect = incorrect + 1;
                }
            }
            testResults[test] = (double) correct / ((double)(correct + incorrect)) * 100d;
        }
        total = 0;
        System.out.print("\tID3: Success Rates: ");
        for (double result : testResults) {
            total += result;
            System.out.print(result + "%, ");
        }
        total = total/testResults.length;
        System.out.println();
        System.out.println("\tID3: Avg. Success Rate: " + total);
        System.out.println();



        //Soybean
        testResults = new double[crossVerSize];
        System.out.println(Soybean.class.getSimpleName());
        for (int test = 0; test < crossVerSize; test++) {
            //Prepare the datasets.
            DataSet soybeanTestingSet = soybeanSets[test];
            DataSet soybean = soybeanTestingSet.setupEmptySet();
            for (int i = 0; i < crossVerSize; i++) {
                if (i != test) {
                    soybean.addAll(soybeanSets[i]);
                }
            }
            //Run the test
            ID3 id3 = new ID3(soybean, soybeanTestingSet, Soybean.classColumn); //Old value of K: 3
            String[] id3Soybean = new String[soybeanTestingSet.size()];
            for (int i = 0; i < soybeanTestingSet.size(); i++) {
                id3Soybean[i] = id3.classify(soybeanTestingSet.get(i));
            }
            int incorrect = 0;
            int correct = 0;
            for (int i = 0; i < soybeanTestingSet.size(); i++) {
                if (id3Soybean[i].equals(soybeanTestingSet.get(i)[Soybean.classColumn].value())) {
                    correct = correct + 1;
                } else {
                    incorrect = incorrect + 1;
                }
            }
            testResults[test] = (double) correct / ((double)(correct + incorrect)) * 100d;
        }
        total = 0;
        System.out.print("\tID3: Success Rates: ");
        for (double result : testResults) {
            total += result;
            System.out.print(result + "%, ");
        }
        total = total/testResults.length;
        System.out.println();
        System.out.println("\tID3: Avg. Success Rate: " + total);
        System.out.println();



        /*
         * Naive Bayes
         */
        //HouseVotes
        testResults = new double[crossVerSize];
        System.out.println(HouseVotes.class.getSimpleName());
        for (int test = 0; test < crossVerSize; test++) {
            //Prepare the datasets.
            DataSet houseVotesTestingSet = houseVotesSets[test];
            DataSet houseVotes = houseVotesTestingSet.setupEmptySet();
            for (int i = 0; i < crossVerSize; i++) {
                if (i != test) {
                    houseVotes.addAll(houseVotesSets[i]);
                }
            }
            //Run the test
            NaiveBayes nb = new NaiveBayes(houseVotes, houseVotesTestingSet, HouseVotes.classColumn); //Old value of K: 3
            String[] nbHouseVotes = nb.getResults();
            int incorrect = 0;
            int correct = 0;
            for (int i = 0; i < houseVotesTestingSet.size(); i++) {
                if (nbHouseVotes[i].equals(houseVotesTestingSet.get(i)[HouseVotes.classColumn].value())) {
                    correct = correct + 1;
                } else {
                    incorrect = incorrect + 1;
                }
            }
            testResults[test] = (double) correct / ((double)(correct + incorrect)) * 100d;
        }
        total = 0;
        System.out.print("\tNB: Success Rates: ");
        for (double result : testResults) {
            total += result;
            System.out.print(result + "%, ");
        }
        total = total/testResults.length;
        System.out.println();
        System.out.println("\tNB: Avg. Success Rate: " + total);
        System.out.println();



        //BreastCancer
        testResults = new double[crossVerSize];
        System.out.println(BreastCancer.class.getSimpleName());
        for (int test = 0; test < crossVerSize; test++) {
            //Prepare the datasets.
            DataSet breastCancerTestingSet = breastCancerSets[test];
            DataSet breastCancer = breastCancerTestingSet.setupEmptySet();
            for (int i = 0; i < crossVerSize; i++) {
                if (i != test) {
                    breastCancer.addAll(breastCancerSets[i]);
                }
            }
            //Run the test
            NaiveBayes nb = new NaiveBayes(breastCancer, breastCancerTestingSet, BreastCancer.classColumn); //Old value of K: 3
            String[] nbBreastCancer = nb.getResults();
            int incorrect = 0;
            int correct = 0;
            for (int i = 0; i < breastCancerTestingSet.size(); i++) {
                if (nbBreastCancer[i].equals(breastCancerTestingSet.get(i)[BreastCancer.classColumn].value())) {
                    correct = correct + 1;
                } else {
                    incorrect = incorrect + 1;
                }
            }
            testResults[test] = (double) correct / ((double)(correct + incorrect)) * 100d;
        }
        total = 0;
        System.out.print("\tNB: Success Rates: ");
        for (double result : testResults) {
            total += result;
            System.out.print(result + "%, ");
        }
        total = total/testResults.length;
        System.out.println();
        System.out.println("\tNB: Avg. Success Rate: " + total);
        System.out.println();



        //Iris
        testResults = new double[crossVerSize];
        System.out.println(Iris.class.getSimpleName());
        for (int test = 0; test < crossVerSize; test++) {
            //Prepare the datasets.
            DataSet irisTestingSet = irisSets[test];
            DataSet iris = irisTestingSet.setupEmptySet();
            for (int i = 0; i < crossVerSize; i++) {
                if (i != test) {
                    iris.addAll(irisSets[i]);
                }
            }
            //Run the test
            NaiveBayes nb = new NaiveBayes(iris, irisTestingSet, Iris.classColumn); //Old value of K: 3
            String[] nbIris = nb.getResults();
            int incorrect = 0;
            int correct = 0;
            for (int i = 0; i < irisTestingSet.size(); i++) {
                if (nbIris[i].equals(irisTestingSet.get(i)[Iris.classColumn].value())) {
                    correct = correct + 1;
                } else {
                    incorrect = incorrect + 1;
                }
            }
            testResults[test] = (double) correct / ((double)(correct + incorrect)) * 100d;
        }
        total = 0;
        System.out.print("\tNB: Success Rates: ");
        for (double result : testResults) {
            total += result;
            System.out.print(result + "%, ");
        }
        total = total/testResults.length;
        System.out.println();
        System.out.println("\tNB: Avg. Success Rate: " + total);
        System.out.println();



        //Soybean
        testResults = new double[crossVerSize];
        System.out.println(Soybean.class.getSimpleName());
        for (int test = 0; test < crossVerSize; test++) {
            //Prepare the datasets.
            DataSet soybeanTestingSet = soybeanSets[test];
            DataSet soybean = soybeanTestingSet.setupEmptySet();
            for (int i = 0; i < crossVerSize; i++) {
                if (i != test) {
                    soybean.addAll(soybeanSets[i]);
                }
            }
            //Run the test
            NaiveBayes nb = new NaiveBayes(soybean, soybeanTestingSet, Soybean.classColumn); //Old value of K: 3
            String[] nbSoybean = nb.getResults();
            int incorrect = 0;
            int correct = 0;
            for (int i = 0; i < soybeanTestingSet.size(); i++) {
                if (nbSoybean[i].equals(soybeanTestingSet.get(i)[Soybean.classColumn].value())) {
                    correct = correct + 1;
                } else {
                    incorrect = incorrect + 1;
                }
            }
            testResults[test] = (double) correct / ((double)(correct + incorrect)) * 100d;
        }
        total = 0;
        System.out.print("\tNB: Success Rates: ");
        for (double result : testResults) {
            total += result;
            System.out.print(result + "%, ");
        }
        total = total/testResults.length;
        System.out.println();
        System.out.println("\tNB: Avg. Success Rate: " + total);
        System.out.println();



    }
}
