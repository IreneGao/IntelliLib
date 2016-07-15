package service;

import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Irene on 15/07/2016.
 */
public class TextIntelligenceServiceTest {

  /*  Map<String, List<String>> getNegativeScore(String target, String inputText) throws IOException;

    String[] getTopWords(String inputText, int size) throws IOException;*/
TextIntelligenceServiceImpl textIntelligenceService = new TextIntelligenceServiceImpl();


    @Test
    public void test_getNegativeScore(){
        String inputText = "Facebook stock fall, price drop, CEO resign";

        try {
            Map<String, List<String>> companyWordMap = textIntelligenceService.getNegativeScore(inputText);
            assertEquals(companyWordMap.get("Facebook").size(),3);
            assertEquals(companyWordMap.get("Facebook").get(0),"fall");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_getTopWords(){
        String inputText = "Facebook stock drop, price drop, CEO resign, reputation drop";

        try {
            String[] topWords = textIntelligenceService.getTopWords(inputText, 5);
            assertEquals(topWords[0], "drop");
            assertEquals(topWords.length, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
