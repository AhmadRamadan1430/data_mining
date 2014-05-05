package tests;

import apriori.Apriori;
import au.com.bytecode.opencsv.CSVReader;
import util.Output;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by mingwei on 5/1/14.
 */
public class testApriori {

    public static void testCSV(String filename) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(filename));
        List myEntries = reader.readAll();
        Output.p(myEntries);
    }


    public static void main(String[] args) throws IOException {
        Apriori apr = new Apriori(0.001, 0.02);
        apr.mine("data/FoodMart.csv");
    }
}
