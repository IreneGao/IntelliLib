import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Irene on 15/07/2016.
 */
public class TextIntelligenceServiceImpl implements TextIntelligenceService {

    private POSModel model;
    private String topKeywordsForAllDoc = " ";


    public TextIntelligenceServiceImpl(String allURL, String userInput) {
        InputStream is = null;
        try {
            is = new FileInputStream("en_pos_maxent.bin");
            this.model = new POSModel(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

public int getNegativeScore(String target, String inputText) throws IOException {
        int score = 0;

        List<String> includeList = getIncludeWordList();
        String wordContent;
        wordContent = inputText.replaceAll("[^A-Za-z0-9]", " ")
                .toLowerCase();
        String[] webKeywords=this.getTopWords(wordContent, 10);
        return score;
    }


    public String[] getWordInDesFrequencyOrder(Map<String, Integer> words, int size) {

        // Convert map to list of <String,Integer> entries
        List<Map.Entry<String, Integer>> wordList = new ArrayList<Map.Entry<String, Integer>>(
                words.entrySet());

        // Sort list by integer values
        Collections.sort(wordList,
                new Comparator<Map.Entry<String, Integer>>() {
                    public int compare(Map.Entry<String, Integer> e1,
                                       Map.Entry<String, Integer> e2) {
                        return (e2.getValue()).compareTo(e1.getValue());
                    }
                });

        String[] wordOrder = new String[wordList.size()];
        int i = 0;
        for (Map.Entry<String, Integer> wordEntry : wordList) {
            wordOrder[i] = wordEntry.getKey();
            i++;
            if(i>=size){
                return wordOrder;
            }
        }


        return wordOrder;

    }

    public List<String> getStopWordList() throws IOException {
        List<String> stopWordList = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(
                "EnStopWordFile.txt"));

        String[] stopWords = br.readLine().split(",");
        for (String s : stopWords) {
            stopWordList.add(s);
        }
        br.close();
        return stopWordList;
    }

    public List<String> getIncludeWordList() throws IOException {
        List<String> includeWordList = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(
                "EnIncludeWordFile.txt"));

        String[] stopWords = br.readLine().split(",");
        for (String s : stopWords) {
            includeWordList.add(s);
        }
        br.close();
        return includeWordList;
    }

public String[] getTopWords(String inputText, int size) throws IOException {
        PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
        POSTaggerME tagger = new POSTaggerME(model);
        ObjectStream<String> lineStream = new PlainTextByLineStream(
                new StringReader(inputText));
        perfMon.start();
        String whitespaceTokenizerLine[] = null;

        Map<String, Integer> usefulWords = new HashMap<String, Integer>();

        String[] tags = null;
        while ((lineStream.read()) != null) {
            whitespaceTokenizerLine = WhitespaceTokenizer.INSTANCE
                    .tokenize(inputText);
            tags = tagger.tag(whitespaceTokenizerLine);
            POSSample posTags = new POSSample(whitespaceTokenizerLine, tags);

            String[] words = posTags.toString().split("\\s+");
            for (int i = 0; i < words.length; i++) {
                String[] splitWord = words[i].split("_");
                // only select noun and verb
                if (!this.getStopWordList().contains(splitWord[0])) {

                    // only return noun
                    if (splitWord[1].startsWith("N")) {
                        if (usefulWords.get(splitWord[0]) == null) {
                            usefulWords.put(splitWord[0], 1);
                        } else {
                            usefulWords.put(splitWord[0],
                                    usefulWords.get(splitWord[0]) + 1);
                        }
                    }
                }
            }
            perfMon.incrementCounter();
        }

        int i = 0;

        return getWordInDesFrequencyOrder(usefulWords, size);
    }


}



