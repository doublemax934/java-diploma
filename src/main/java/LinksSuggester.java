import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LinksSuggester {
    public static List<Suggest> mainSuggestList = new ArrayList<>();

    public LinksSuggester(File file) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        Scanner scanner = null;
        int i = 0;

        while ((line = reader.readLine()) != null) {
            Suggest suggest = new Suggest();
            scanner = new Scanner(line);
            scanner.useDelimiter("\t");
            while (scanner.hasNext()) {
                String data = scanner.next();
                if (i == 0)
                    suggest.setKeyWord(data);
                else if (i == 1)
                    suggest.setTitle(data);
                else if (i == 2)
                    suggest.setUrl(data);
                else {
                    throw new WrongLinksFormatException("Данные не верные, в строке должно быть три части: ключевое слово, заголовок  и ссылка");
                }
                i++;
            }
            i = 0;
            mainSuggestList.add(suggest);
        }
        reader.close();
    }

    public List<Suggest> suggest(String text, List<Suggest> remainingSuggests) {

        List<Suggest> suggestOnFirstPage = new ArrayList<>();

        for (Suggest s : remainingSuggests) {
            if (text.toLowerCase().contains(s.getKeyWord())) {
                suggestOnFirstPage.add(s);
            }
        }
        return suggestOnFirstPage;
    }
}
