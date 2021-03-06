package service;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

import java.io.*;

import java.util.*;

/**
 * Created by Irene on 15/07/2016.
 */
public class TextIntelligenceServiceImpl implements TextIntelligenceService {

    private POSModel model;
    private String topKeywordsForAllDoc = " ";


    public TextIntelligenceServiceImpl() {
        InputStream is = null;
        try {
            is = this.getClass().getClassLoader().getResourceAsStream("en_pos_maxent.bin");
            this.model = new POSModel(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Map<String, List<String>> getNegativeScore(String inputText) throws IOException {
        int score = 0;

        PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
        POSTaggerME tagger = new POSTaggerME(model);
        ObjectStream<String> lineStream = new PlainTextByLineStream(
                new StringReader(inputText));
        perfMon.start();
        String whitespaceTokenizerLine[] = null;

        List<String> companyList = getInterestCompanyList();
        Map<String, List<String>> companyWordMap = new HashMap<String, List<String>>();
        for(String companyName: companyList){
            companyWordMap.put(companyName, new ArrayList<String>());
        }
        List<String> includeWords = getIncludeWordList();
        String[] tags = null;
        while ((lineStream.read()) != null) {
            whitespaceTokenizerLine = WhitespaceTokenizer.INSTANCE
                    .tokenize(inputText);
            tags = tagger.tag(whitespaceTokenizerLine);
            POSSample posTags = new POSSample(whitespaceTokenizerLine, tags);
            String textString = posTags.toString();
           for(String companyKey: companyList){
               if(textString.toLowerCase().indexOf(companyKey.toLowerCase()) != -1){
                   textString = textString.replaceAll("[^A-Za-z0-9_]", " ");
                   String[] words = textString.split("\\s+");

                   for (int i = 0; i < words.length; i++) {
                       String[] splitWord = words[i].split("_");
                       // only select noun and verb
                       if (includeWords.contains(splitWord[0])) {
                           companyWordMap.get(companyKey).add(splitWord[0]);
                       }
                   }
               }
           }

            perfMon.incrementCounter();
        }

        return companyWordMap;
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

        String[] wordOrder = new String[size];
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
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("EnStopWordFile.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String[] stopWords = br.readLine().split(",");
        for (String s : stopWords) {
            stopWordList.add(s);
        }
        br.close();
        return stopWordList;
    }

    public List<String> getIncludeWordList() throws IOException {
        List<String> includeWordList = new ArrayList<String>();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("EnIncludeWordFile.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String[] stopWords = br.readLine().split(",");
        for (String s : stopWords) {
            includeWordList.add(s);
        }
        br.close();
        return includeWordList;
    }

    public List<String> getInterestCompanyList() throws IOException {
        List<String> includeWordList = new ArrayList<String>();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("InterestCompanyList.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

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
            String textString = posTags.toString().replaceAll("[^A-Za-z0-9_]", " ");
            String[] words = textString.split("\\s+");
            for (int i = 0; i < words.length; i++) {
                String[] splitWord = words[i].split("_");
                List<String> stopWord = this.getStopWordList();
                if (!stopWord.contains(splitWord[0])) {

                        if (usefulWords.get(splitWord[0]) == null) {
                            usefulWords.put(splitWord[0], 1);
                        } else {
                            usefulWords.put(splitWord[0],
                                    usefulWords.get(splitWord[0]) + 1);
                        }
                }
            }
            perfMon.incrementCounter();
        }

        return getWordInDesFrequencyOrder(usefulWords, size);
    }


}
