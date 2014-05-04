package apriori;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static util.Output.p;

/**
 * Apriori algorithm for data mining class homework.
 *
 * Created by mingwei on 5/1/14.
 */
public class Apriori {
    String[] fields;
    Integer[] fieldCount;
    List<String[]> data;

    double support;
    double confidence;

    Set<Set<Integer>> frequentSet;
    Set<Set<Integer>> infrequentSet;

    Map<Integer, Set<Set<Integer>>> resultMap;


    /**
     * Constructor function.
     *
     * @param support    minimum support for frequent item set.
     * @param confidence minimum confidence
     */
    public Apriori(double support, double confidence) {
        this.support = support;
        this.confidence = confidence;
        this.frequentSet = new HashSet<Set<Integer>>();
        this.infrequentSet = new HashSet<Set<Integer>>();
        this.resultMap = new TreeMap<Integer, Set<Set<Integer>>>();
    }

    public static void main(String[] args) {
        Apriori apr = new Apriori(0.1, 0.8);
        apr.mine("data/FoodMart.csv");
    }

    /**
     * Main entry point for mine certain input file.
     *
     * @param filename the input file path.
     */
    public void mine(String filename) {
        this.readInputFile(filename);
        this.oneItemFrequency();
        this.findFrequentSets();
        this.generateRules();
        p(fieldCount);
    }

    private void generateRules() {

        List<Triple<Set<Integer>, Set<Integer>, Double>> rules = new ArrayList<Triple<Set<Integer>, Set<Integer>, Double>>();
        for (Map.Entry<Integer, Set<Set<Integer>>> entry : resultMap.entrySet()) {
            if (entry.getKey() == 1)
                continue;
            for (Set<Integer> idset : entry.getValue()) {
                // get support for the whole set.
                int support_whole = getSupport(idset);
                // generate subsets
                ICombinatoricsVector<Integer> initialSet = Factory.createVector(new ArrayList<Integer>(idset));
                Generator<Integer> gen = Factory.createSubSetGenerator(initialSet);
                // loop through every subset
                for (ICombinatoricsVector<Integer> subSet : gen) {
                    if (subSet.getSize() != 0 && subSet.getSize() != entry.getKey()) {
//                        System.out.println(subSet);

                        Set<Integer> subset1 = new HashSet<Integer>();
                        subset1.addAll(subSet.getVector());

                        Set<Integer> subset2 = new HashSet<Integer>(idset);
                        subset2.removeAll(subset1);

                        int support_sub = getSupport(subset1);

                        double confidence = (double) support_whole / support_sub;

                        if (confidence > this.confidence) {
                            rules.add(new ImmutableTriple(subset1, subset2, confidence));
                            p("%s : %s [%d/%d = %.2f]\n", subset1.toString(), subset2.toString(), support_whole, support_sub, confidence);
                        }

                    }
                }
            }
        }

    }

    private int getSupport(Set<Integer> set) {
        int support = 0;
        for (String[] strList : this.data) {
            boolean contains = true;
            for (int i : set)
                if (strList[i].equals("0"))
                    contains = false;
            if (contains)
                support++;
        }
        return support;
    }

    /**
     * read file and extract frequency information.
     * @param filename the path of the input file.
     */
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
    }

    /**
     * extract 1-item frequent time sets.
     */
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


        Set<Integer> tmp;
        p("minimum support: %f\n", data.size() * this.support);
        for (Map.Entry<Integer, Integer> entry : treeMap.entrySet()) {
            if (entry.getKey() > data.size() * this.support){
                p("%s:\t%d\n", fields[entry.getValue()], entry.getKey());
                tmp = new HashSet<Integer>();
                tmp.add(entry.getValue());
                frequentSet.add(tmp);
            }
        }
        resultMap.put(1, frequentSet);
        frequentSet = null;
    }

    private void findFrequentSets() {
        int currentRound = 1;
        while (true) {
            Set<Set<Integer>> currentSets = resultMap.get(currentRound);
            // generate candidate set
            Set<Set<Integer>> candidateSets = new HashSet<Set<Integer>>();
            for (Set<Integer> s1 : currentSets) {
                for (Set<Integer> s2 : currentSets) {
                    if (!s1.equals(s2)) {
                        HashSet<Integer> union = new HashSet<Integer>(s1);
                        union.addAll(s2);
                        if (union.size() == currentRound + 1) {
                            boolean ignore = false;
                            for (Set<Integer> s : infrequentSet) {
                                if (union.contains(s)) {
                                    ignore = true;
                                    break;
                                }
                            }
                            if (ignore)
                                continue;
                            candidateSets.add(union);
                        }
                    }
                }
            }
            if (candidateSets.size() == 0)
                break;
            infrequentSet = new HashSet<Set<Integer>>();
            frequentSet = new HashSet<Set<Integer>>();
            // count frequency of candidate items
            Integer[] count = new Integer[candidateSets.size()];
            // use the following hashMap to count the appearance of every set.
            HashMap<Set<Integer>, Integer> hashMap = new HashMap<Set<Integer>, Integer>();
            for (Set<Integer> s : candidateSets) {
                hashMap.put(s, 0);
            }
            for (String[] strList : this.data) {
                for (Set<Integer> s : candidateSets) {
                    boolean contains = true;
                    for (int i : s)
                        if (strList[i].equals("0"))
                            contains = false;
                    if (contains)
                        hashMap.put(s, hashMap.get(s) + 1);
                }
            }

            for (Map.Entry<Set<Integer>, Integer> entry : hashMap.entrySet()) {
                if (entry.getValue() > data.size() * support)
                    frequentSet.add(entry.getKey());
                else
                    infrequentSet.add(entry.getKey());
            }

            if (frequentSet.size() == 0)
                break;
            resultMap.put(currentRound + 1, frequentSet);
            currentRound++;
        }
    }
}
