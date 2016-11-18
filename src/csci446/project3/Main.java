package csci446.project3;

import csci446.project3.DataSets.BreastCancer;
import csci446.project3.DataSets.Glass;
import csci446.project3.DataSets.HouseVotes;
import csci446.project3.DataSets.Soybean;
import csci446.project3.DataSets.Iris;
import csci446.project3.ID3.ID3;
import csci446.project3.Util.DataParser;
import csci446.project3.Util.DataSet;

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
        DataSet houseVotes = DataParser.parseData(HouseVotes.filename, HouseVotes.columnNames, HouseVotes.dataTypes, HouseVotes.ignoreColumns, HouseVotes.classColumn, HouseVotes.discretizeColumns);
        DataSet breastCancer = DataParser.parseData(BreastCancer.filename, BreastCancer.columnNames, BreastCancer.dataTypes, BreastCancer.ignoreColumns, BreastCancer.classColumn, HouseVotes.discretizeColumns);
        DataSet glass = DataParser.parseData(Glass.filename, Glass.columnNames, Glass.dataTypes, Glass.ignoreColumns, Glass.classColumn, Glass.discretizeColumns);
        DataSet iris = DataParser.parseData(Iris.filename, Iris.columnNames, Iris.dataTypes, Iris.ignoreColumns, Iris.classColumn, Iris.discretizeColumns);
        DataSet soybean = DataParser.parseData(Soybean.filename, Soybean.columnNames, Soybean.dataTypes, Soybean.ignoreColumns, Soybean.classColumn, Soybean.discretizeColumns);
        
        /*
         * The contents of the DataSet are not always random.
         * You can shuffle them using Collections.shuffle()
         */
        
        Collections.shuffle(houseVotes);
        Collections.shuffle(breastCancer);
        Collections.shuffle(glass);
        Collections.shuffle(iris);
        Collections.shuffle(soybean);
        /*
         * Lastly, you want to split the data into a regular dataset and a testing set.
         * DataSet has a function for this, since it gets a little weird.
         * This grabs 10% of the data in the dataset and sets pulls it out to make the testing set.
         * This also means that the remaining 90% in DataSet can serve as our training set.
         */

        DataSet houseVotesTestingSet = houseVotes.getTestingSet(.1);
        DataSet breastCancerTestingSet = breastCancer.getTestingSet(.1);
        DataSet glassTestingSet = glass.getTestingSet(.1);
        DataSet irisTestingSet = iris.getTestingSet(.1);
        DataSet soybeanTestingSet = soybean.getTestingSet(.1);
        
         //KNN
         //House Votes
        System.out.println(HouseVotes.class.getSimpleName());
        KNN knn = new KNN(houseVotes, houseVotesTestingSet, HouseVotes.classColumn, 3);
        String[] knnHouseVotes = new String[houseVotesTestingSet.size()];
        for(int i = 0; i < houseVotesTestingSet.size(); i++) {
            knnHouseVotes[i] = knn.classify(houseVotesTestingSet.get(i));
        }
        for(int i = 0; i < houseVotesTestingSet.size(); i++) {
            if(knnHouseVotes[i].equals(houseVotesTestingSet.get(i)[HouseVotes.classColumn].value())) {
                System.out.println("KNN: Correct (" + knnHouseVotes[i] + ")");
            } else {
                System.out.println("KNN: Incorrect (" + knnHouseVotes[i] + ", actually " + houseVotesTestingSet.get(i)[HouseVotes.classColumn].value() + ")");
            }
        }
        
        //Breast Cancer
        System.out.println(BreastCancer.class.getSimpleName());
        knn = new KNN(breastCancer, breastCancerTestingSet, BreastCancer.classColumn, 3);
        String[] knnBreastCancer = new String[breastCancerTestingSet.size()];
        for(int i = 0; i < breastCancerTestingSet.size(); i++) {
            knnBreastCancer[i] = knn.classify(breastCancerTestingSet.get(i));
        }
        for(int i = 0; i < breastCancerTestingSet.size(); i++) {
            if(knnBreastCancer[i].equals(breastCancerTestingSet.get(i)[BreastCancer.classColumn].value())) {
                System.out.println("KNN: Correct (" + knnBreastCancer[i] + ")");
            } else {
                System.out.println("KNN: Incorrect (" + knnBreastCancer[i] + ", actually " + breastCancerTestingSet.get(i)[BreastCancer.classColumn].value() + ")");
            }
        }
        
        //Glass
        System.out.println(Glass.class.getSimpleName());
        knn = new KNN(glass, glassTestingSet, Glass.classColumn, 3);
        String[] knnGlass = new String[glassTestingSet.size()];
        for(int i = 0; i < glassTestingSet.size(); i++) {
            knnGlass[i] = knn.classify(glassTestingSet.get(i));
        }
        for(int i = 0; i < glassTestingSet.size(); i++) {
            if(knnGlass[i].equals(glassTestingSet.get(i)[Glass.classColumn].value())) {
                System.out.println("KNN: Correct (" + knnGlass[i] + ")");
            } else {
                System.out.println("KNN: Incorrect (" + knnGlass[i] + ", actually " + glassTestingSet.get(i)[Glass.classColumn].value() + ")");
            }
        }
        
        //Iris
        System.out.println(Iris.class.getSimpleName());
        knn = new KNN(iris, irisTestingSet, Iris.classColumn, 3);
        String[] knnIris = new String[irisTestingSet.size()];
        for(int i = 0; i < irisTestingSet.size(); i++) {
            knnIris[i] = knn.classify(irisTestingSet.get(i));
        }
        for(int i = 0; i < irisTestingSet.size(); i++) {
            if(knnIris[i].equals(irisTestingSet.get(i)[Iris.classColumn].value())) {
                System.out.println("KNN: Correct (" + knnIris[i] + ")");
            } else {
                System.out.println("KNN: Incorrect (" + knnIris[i] + ", actually " + irisTestingSet.get(i)[Iris.classColumn].value() + ")");
            }
        }
        
        //Soybean
        System.out.println(Soybean.class.getSimpleName());
        knn = new KNN(soybean, soybeanTestingSet, Soybean.classColumn, 3);
        String[] knnSoybean = new String[soybeanTestingSet.size()];
        for(int i = 0; i < soybeanTestingSet.size(); i++) {
            knnSoybean[i] = knn.classify(soybeanTestingSet.get(i));
        }
        for(int i = 0; i < soybeanTestingSet.size(); i++) {
            if(knnSoybean[i].equals(soybeanTestingSet.get(i)[Soybean.classColumn].value())) {
                System.out.println("KNN: Correct (" + knnSoybean[i] + ")");
            } else {
                System.out.println("KNN: Incorrect (" + knnSoybean[i] + ", actually " + soybeanTestingSet.get(i)[Soybean.classColumn].value() + ")");
            }
        }
        
        
        /*
         * Lets setup ID3:
         * DataSet, TestSet, column with the class categorization. (republican, democrat in this case)
         */

        System.out.println(HouseVotes.class.getSimpleName());
        ID3 id3 = new ID3(houseVotes, houseVotesTestingSet, HouseVotes.classColumn);
        String[] id3HouseVotes = new String[houseVotesTestingSet.size()];
        for(int i = 0; i < houseVotesTestingSet.size(); i++) {
            id3HouseVotes[i] = id3.classify(houseVotesTestingSet.get(i));
        }
        for(int i = 0; i < houseVotesTestingSet.size(); i++) {
            if(id3HouseVotes[i].equals(houseVotesTestingSet.get(i)[HouseVotes.classColumn].value())) {
                System.out.println("ID3: Correct (" + id3HouseVotes[i] + ")");
            } else {
                System.out.println("ID3: Incorrect (" + id3HouseVotes[i] + ", actually " + houseVotesTestingSet.get(i)[HouseVotes.classColumn].value() + ")");
            }
        }

        System.out.println(BreastCancer.class.getSimpleName());
        id3 = new ID3(breastCancer, breastCancerTestingSet, BreastCancer.classColumn);
        String[] id3BreastCancer = new String[breastCancerTestingSet.size()];
        for(int i = 0; i < breastCancerTestingSet.size(); i++) {
            id3BreastCancer[i] = id3.classify(breastCancerTestingSet.get(i));
        }
        for(int i = 0; i < breastCancerTestingSet.size(); i++) {
            if(id3BreastCancer[i].equals(breastCancerTestingSet.get(i)[BreastCancer.classColumn].value())) {
                System.out.println("ID3: Correct (" + id3BreastCancer[i] + ")");
            } else {
                System.out.println("ID3: Incorrect (" + id3BreastCancer[i] + ", actually " + breastCancerTestingSet.get(i)[BreastCancer.classColumn].value() + ")");
            }
        }

        System.out.println(Glass.class.getSimpleName());
        id3 = new ID3(glass, glassTestingSet, Glass.classColumn);
        String[] id3Glass = new String[glassTestingSet.size()];
        for(int i = 0; i < glassTestingSet.size(); i++) {
            id3Glass[i] = id3.classify(glassTestingSet.get(i));
        }
        for(int i = 0; i < glassTestingSet.size(); i++) {
            if(id3Glass[i].equals(glassTestingSet.get(i)[Glass.classColumn].value())) {
                System.out.println("ID3: Correct (" + id3Glass[i] + ")");
            } else {
                System.out.println("ID3: Incorrect (" + id3Glass[i] + ", actually " + glassTestingSet.get(i)[Glass.classColumn].value() + ")");
            }
        }

        System.out.println(Iris.class.getSimpleName());
        id3 = new ID3(iris, irisTestingSet, Iris.classColumn);
        String[] id3Iris = new String[irisTestingSet.size()];
        for(int i = 0; i < irisTestingSet.size(); i++) {
            id3Iris[i] = id3.classify(irisTestingSet.get(i));
        }
        for(int i = 0; i < irisTestingSet.size(); i++) {
            if(id3Iris[i].equals(irisTestingSet.get(i)[Iris.classColumn].value())) {
                System.out.println("ID3: Correct (" + id3Iris[i] + ")");
            } else {
                System.out.println("ID3: Incorrect (" + id3Iris[i] + ", actually " + irisTestingSet.get(i)[Iris.classColumn].value() + ")");
            }
        }

        System.out.println(Soybean.class.getSimpleName());
        id3 = new ID3(soybean, soybeanTestingSet, Soybean.classColumn);
        String[] id3Soybean = new String[soybeanTestingSet.size()];
        for(int i = 0; i < soybeanTestingSet.size(); i++) {
            id3Soybean[i] = id3.classify(soybeanTestingSet.get(i));
        }
        for(int i = 0; i < soybeanTestingSet.size(); i++) {
            if(id3Soybean[i].equals(soybeanTestingSet.get(i)[Soybean.classColumn].value())) {
                System.out.println("ID3: Correct (" + id3Soybean[i] + ")");
            } else {
                System.out.println("ID3: Incorrect (" + id3Soybean[i] + ", actually " + soybeanTestingSet.get(i)[Soybean.classColumn].value() + ")");
            }
        }
    }
}
