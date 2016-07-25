package Main_Engine;

import Algorithm.Heap;
import Algorithm.Restaurant;

import java.io.*;

/**
 * Created by dwk89 on 07/23/2016.
 */
public class Wisdom {
    private int recommendSize;
    public String[] list;

    public Wisdom(int size) {
        list = new String[size];
        recommendSize = size;
    }

    public String[] Recommend() throws IOException {
        String filename = "C:/Users/dwk89/IdeaProjects/W/data/Trained_Data/trainedData.json";
        String line = null;
        int i = 0;
        Wisdom L = new Wisdom(recommendSize);
        Heap H = new Heap();
        Restaurant current = null;
        Restaurant root = null;

        FileReader reader = new FileReader(filename);
        BufferedReader bReader = new BufferedReader(reader);
        while ((line = bReader.readLine()) != null) {
            if(i < recommendSize) {
                L.list[i] = line;
                i++;
            }
            else if (i == recommendSize) {
                H.buildHeap(L.list, 0);
                i++;
            }
            else {
                current = new Restaurant(line);
                root = new Restaurant(L.list[0]);

                if (current.is_better_than(root)) {
                    L.list[0] = line;
                    H.heapify(L.list, 0);
                }
            }
        }

        bReader.close();

        String result = "";
        int k1, k2;

        for (int j = 0; j < L.list.length; j++) {
            k1 = L.list[j].indexOf('{');
            k2 = L.list[j].lastIndexOf('}');
            L.list[j] = L.list[j].substring(k1, k2 + 1);

            if (j == 0) {
                result += '[' + L.list[j] + ',' + '\n';
            }
            else if (j == L.list.length - 1) {
                result += L.list[j] + ']';
            }
            else {
                result += L.list[j] + ',' + '\n';
            }
        }

        FileWriter writer = new FileWriter("C:/Users/dwk89/IdeaProjects/W/data/Other_Data/results.json");
        BufferedWriter bWriter = new BufferedWriter(writer);

        bWriter.write(result);
        bWriter.close();

        return L.list;
    }

    public static void main(String[] args) throws Exception {
        Wisdom L = new Wisdom(8);
        String result[] = L.Recommend();
        System.out.println(result[0]);
    }
}