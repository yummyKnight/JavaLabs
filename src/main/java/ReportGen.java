import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ReportGen {


    static void createHTML(String srcXmlFile, String srcXsltFile, String outputFileName) throws TransformerException, IOException {
        // creating html from xml with xslt
        /* Create a TransformerFactory object */
        FileOutputStream out = new FileOutputStream(outputFileName);
        TransformerFactory tFactory = TransformerFactory.newInstance();
        /* Get the incoming XSLT file */
        Transformer transformer = tFactory.newTransformer(new StreamSource(srcXsltFile));
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes(StandardCharsets.UTF_8));
        /* Get the XML file and apply the XSLT transformation to convert to HTML */
        transformer.transform(new StreamSource(srcXmlFile), new StreamResult(out));

    }

    static void createPDF(String srcXmlFile, String srcXsltFile, String outputFileName) throws TransformerException, IOException, DocumentException {
        createHTML(srcXmlFile, srcXsltFile, outputFileName);
        String url = new File(outputFileName).toURI().toURL().toString();
        System.out.println("" + url);
        String HTML_TO_PDF = "ConvertedFile.pdf";
        OutputStream os = new FileOutputStream(HTML_TO_PDF);
        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver().addFont("Fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        renderer.setDocument(url);
        renderer.layout();
        renderer.createPDF(os);
        os.close();

//        createHTML(srcXmlFile,srcXsltFile,outputFileName);
//        Document document = new Document();
//        // Создаем writer для записи в pdf
//        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("output.pdf"));
//        // Открываем для чтения html страничку
//        document.open();
//        // Парсим её и записываем в PDF
//        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new FileInputStream(outputFileName));
//        document.close();
    }

    public static void main(String[] args) throws TransformerException, IOException, DocumentException {
        //unicode????
        createPDF("table.xml", "template.xslt", "sample.html");

    }
}
