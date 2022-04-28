import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {
        LinksSuggester linksSuggester = new LinksSuggester(new File("data\\config"));
        var dir = new File("data\\pdfs");
        for (var fileIn : dir.listFiles()) {
            List<Suggest> suggestsList = new ArrayList<>(LinksSuggester.mainSuggestList);
            String path = "data\\converted\\New_";
            File file = new File(path + fileIn.getName());
            var doc = new PdfDocument(new PdfReader(fileIn), new PdfWriter(file));
            for (int t = 1; t < doc.getNumberOfPages() + 1; t++) {
                var text = PdfTextExtractor.getTextFromPage(doc.getPage(t));
                List<Suggest> remainingSuggests = suggestsList;
                List<Suggest> suggest = linksSuggester.suggest(text, remainingSuggests).stream().distinct().collect(Collectors.toList());
                for (Suggest s : suggest) {
                    remainingSuggests.remove(s);
                }
                if (!suggest.isEmpty()) {
                    var newPage = doc.addNewPage(t + 1);
                    var rect = new Rectangle(newPage.getPageSize()).moveRight(10).moveDown(10);
                    Canvas canvas = new Canvas(newPage, rect);
                    Paragraph paragraph = new Paragraph("Suggestions:\n");
                    paragraph.setFontSize(25);
                    suggest.forEach((e) -> {
                        PdfLinkAnnotation annotation = new PdfLinkAnnotation(rect);
                        PdfAction action = PdfAction.createURI(e.getUrl());
                        annotation.setAction(action);
                        Link link = new Link(e.getTitle(), annotation);
                        paragraph.add(link.setUnderline());
                        paragraph.add("\n");
                    });
                    canvas.add(paragraph);
                    t++;
                }
            }
            doc.close();
        }
    }
}
