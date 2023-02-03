package no.ding.pk.service.converters;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.itextpdf.text.DocumentException;
import com.lowagie.text.BadElementException;

public interface PdfService {
    byte[] generatePdfFromHTML(String htmlString) throws DocumentException, IOException, com.lowagie.text.DocumentException;
    byte[] generateFlyingSaucerPdf(String inputHtmlPath, String outputPdfPath);

    byte[] generateJsoupPdf(String inputHtmlPath, String outputPdfPath) throws IOException;

    void generatePdfWithOpenPdf(String inputHtmlPath, String outputPdfPath) throws IOException, BadElementException;
}
