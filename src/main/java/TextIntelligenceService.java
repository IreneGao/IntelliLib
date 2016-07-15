import java.io.IOException;

/**
 * Created by Irene on 15/07/2016.
 */
public interface TextIntelligenceService {
    int getNegativeScore(String target, String inputText) throws IOException;

    String[] getTopWords(String inputText, int size) throws IOException;
}
