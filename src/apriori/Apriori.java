package apriori;

import au.com.bytecode.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static util.Output.p;

/**
 * Created by mingwei on 5/1/14.
 */
public class Apriori {
    String[] fields;
    Integer[] fieldCount;
    List<String[]> data;

    double support;
    double confidence;

    public Apriori(double support, double confidence) {
        this.support = support;
        this.confidence = confidence;

        this.readInputFile("data/FoodMart.csv");
        this.oneItemFrequency();
        p(fieldCount);
    }

    public static void main(String[] args) {
        Apriori apr = new Apriori(0.1, 0.8);
    }

    private void readInputFile(String filename) {

        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<String[]> myEntries = null;
        try {
            myEntries = reader.readAll();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert myEntries != null;

        // get field names from the first line.
        this.fields = myEntries.get(0);
        this.data = myEntries.subList(1, myEntries.size());

        fieldCount = new Integer[fields.length];

        for (int i = 0; i < fields.length; i++)
            fieldCount[i] = 0;

        p(fields);

//        Output.p(myEntries);
    }

    private void oneItemFrequency() {
        for (String[] strList : this.data) {
            for (int i = 0; i < strList.length; i++)
                if (strList[i].equals("1"))
                    fieldCount[i]++;
        }

        // descending order http://edivad.wordpress.com/2008/03/21/java-reverse-ordering-a-treemap/
        TreeMap<Integer, Integer> treeMap = new TreeMap<Integer, Integer>(Collections.reverseOrder());

        for (int i = 0; i < fieldCount.length; i++) {
            treeMap.put(fieldCount[i], i);
        }


        p("minimum support: %f\n", data.size() * this.support);
        for (Map.Entry<Integer, Integer> entry : treeMap.entrySet()) {
            if (entry.getKey() > data.size() * this.support)
                p("%s:\t%d\n", fields[entry.getValue()], entry.getKey());
        }
    }
}
