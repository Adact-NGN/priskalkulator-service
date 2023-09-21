package no.ding.pk.service.converters;

import com.lowagie.text.DocumentException;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import no.ding.pk.service.template.HandlebarsTemplateService;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Service
public class PdfServiceImpl implements PdfService {

    private final static Logger log = LoggerFactory.getLogger(PdfServiceImpl.class);

    @Override
    public byte[] generatePdfFromHTML(String htmlString) throws IOException, com.lowagie.text.DocumentException {
        org.jsoup.nodes.Document doc;
        String cleanHtml = removeHtmlComments(htmlString); //Jsoup.clean(htmlString, Whitelist.relaxed());
        if(htmlString.startsWith("<html>")) {
            doc = Jsoup.parse(cleanHtml);
        } else {
            doc = Jsoup.parseBodyFragment(cleanHtml);
        }

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(W3CDom.convert(doc), "Test.pdf");
        renderer.layout();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        renderer.createPDF(baos);

        baos.close();

        return baos.toByteArray();
    }

    private String removeHtmlComments(String htmlString) {
        String test = htmlString.replaceAll("<!--.*?-->", "");

      return test.replaceAll("_ngcontent-gif-c95=\"\"", "");
    }

    @Override
    public byte[] generateFlyingSaucerPdf(String inputHtmlPath, String outputPdfPath) {
        try {
            String url = new File(inputHtmlPath).toURI().toURL().toString();
            System.out.println("URL: " + url);

            log.debug("Output path {}", outputPdfPath);
            File outputFile = new File(outputPdfPath);

            if(!outputFile.exists()) {
                if(outputFile.createNewFile()) {
                    log.debug("New PDF output file created.");
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
    
            //Flying Saucer part
            ITextRenderer renderer = new ITextRenderer();
    
            renderer.setDocument(url);
            renderer.layout();
            renderer.createPDF(out);
    
            out.close();

            return out.toByteArray();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public byte[] generateJsoupPdf(String inputHtmlPath, String outputPdfPath) throws IOException {
        File inputHtml = new File(inputHtmlPath);

        org.jsoup.nodes.Document xhtmlDocument = createXhtmlDocument(inputHtml);

        return getPdfBytes(xhtmlDocument);
    }

    private byte[] getPdfBytes(Document xhtmlDocument) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            SharedContext sharedContext = renderer.getSharedContext();
            sharedContext.setPrint(true);
            sharedContext.setInteractive(false);
            renderer.setDocumentFromString(xhtmlDocument.html());
            renderer.layout();
            renderer.createPDF(outputStream);

            outputStream.close();

            return outputStream.toByteArray();
        } catch (com.lowagie.text.DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] generateJsoupPdf(String htmlString) throws IOException {
        org.jsoup.nodes.Document xhtmlDocument = createXhtmlDocumentFromString(htmlString);

        return getPdfBytes(xhtmlDocument);
    }
    
    @Override
    public void generatePdfWithOpenPdf(String inputHtmlPath, String outputPdfPath) {

        org.jsoup.nodes.Document doc = new org.jsoup.nodes.Document(inputHtmlPath);

        File outputPdf = new File(outputPdfPath);
        try (OutputStream os = new FileOutputStream(outputPdf)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withUri(outputPdfPath);
            builder.toStream(os);
            builder.withW3cDocument(new W3CDom().fromJsoup(doc), "/");
            builder.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static org.jsoup.nodes.Document createXhtmlDocument(File inputHtml) throws IOException {
        org.jsoup.nodes.Document document = Jsoup.parse(inputHtml, StandardCharsets.UTF_8.toString());
        document.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);

        return document;
    }

    private static org.jsoup.nodes.Document createXhtmlDocumentFromString(String htmlString) {
        org.jsoup.nodes.Document document = Jsoup.parse(htmlString);
        document.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);

        return document;
    }

}
