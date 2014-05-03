package tests;

import au.com.bytecode.opencsv.CSVReader;
import util.Output;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by mingwei on 5/1/14.
 */
public class testApriori {
    public static void main(String[] args) throws IOException {
        CSVReader reader = new CSVReader(new FileReader("data/FoodMart.csv"));
        List myEntries = reader.readAll();
        Output.p(myEntries);
    }
}
