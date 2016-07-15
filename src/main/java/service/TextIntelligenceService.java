package service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Irene on 15/07/2016.
 */
public interface TextIntelligenceService {
    Map<String, List<String>> getNegativeScore(String inputText) throws IOException;

    String[] getTopWords(String inputText, int size) throws IOException;
}
