package no.ding.pk.service.converters;

import com.itextpdf.text.DocumentException;
import no.ding.pk.service.template.HandlebarsTemplateService;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class PdfServiceImplTest {

    private final PdfService pdfService = new PdfServiceImpl();

    @Test
    void testGeneratePdfFromHTML() throws IOException, DocumentException, com.lowagie.text.DocumentException {
        ClassLoader classLoader = getClass().getClassLoader();
        
        File file = new File(classLoader.getResource("templates/priceOfferTemplate.html").getFile());

        assertThat(file.exists(), is(true));

        String absolutePath = file.getAbsolutePath();
        System.out.println(absolutePath);
        
        String html = IOUtils.toString(new FileInputStream(file), StandardCharsets.UTF_8);

        byte[] generatePdfFromHTML = pdfService.generatePdfFromHTML(html);

        assertThat(generatePdfFromHTML, notNullValue());

        Path resourceDirectory = Paths.get("src", "test", "resources");
        String absolutePathOut = resourceDirectory.toFile().getAbsolutePath();
        
        File outputPdfFile = new File(absolutePathOut + "/fromHtmlout.pdf");

        FileOutputStream outputStream = new FileOutputStream(outputPdfFile);
        byte[] strToBytes = generatePdfFromHTML.toString().getBytes();
        outputStream.write(strToBytes);

        outputStream.close();

    }

    @Test
    void testFlyingSaucerPdfGenerator() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        
        File file = new File(classLoader.getResource("templates/priceOfferTemplate.html").getFile());

        assertThat(file.exists(), is(true));

        String absoluteHtmlPath = file.getAbsolutePath();

        Path resourceDirectory = Paths.get("src", "test", "resources");
        String absolutePathOut = resourceDirectory.toFile().getAbsolutePath() + "/outFlyingSaucer.pdf";

        File outputPdfFile = new File(absolutePathOut);

        FileOutputStream outputStream = new FileOutputStream(outputPdfFile);

        byte[] generateFlyingSaucerPdf = pdfService.generateFlyingSaucerPdf(absoluteHtmlPath, absolutePathOut);
        outputStream.write(generateFlyingSaucerPdf);

        outputStream.close();
    }

    @Test
    void testJsoupPdfGenerator() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(classLoader.getResource("templates/priceOfferTemplate.html").getFile());

        assertThat(file.exists(), is(true));

        String absoluteHtmlPath = file.getAbsolutePath();

        Path resourceDirectory = Paths.get("src", "test", "resources");
        String absolutePathOut = resourceDirectory.toFile().getAbsolutePath() + "/outJsoup.pdf";

        File outputPdfFile = new File(absolutePathOut);

        FileOutputStream outputStream = new FileOutputStream(outputPdfFile);

        byte[] generateJsoupPdf = pdfService.generateJsoupPdf(absoluteHtmlPath, absolutePathOut);

        outputStream.write(generateJsoupPdf);

        outputStream.close();
    }
}
