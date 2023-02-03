package no.ding.pk.web.controllers.v1;

import com.itextpdf.text.DocumentException;
import no.ding.pk.domain.offer.PriceOffer;
import no.ding.pk.service.converters.PdfService;
import no.ding.pk.service.offer.PriceOfferService;
import no.ding.pk.service.template.HandlebarsTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;

@RestController
@RequestMapping(path = "/api/v1/pdf")
public class PdfServiceController {

    private static final Logger log = LoggerFactory.getLogger(PdfServiceController.class);
    
    private final PdfService pdfService;

    private final PriceOfferService priceOfferService;

    private final HandlebarsTemplateService templateService;

    @Autowired
    public PdfServiceController(PdfService pdfService, PriceOfferService priceOfferService, HandlebarsTemplateService templateService) {
        this.pdfService = pdfService;
        this.priceOfferService = priceOfferService;
        this.templateService = templateService;
    }

    @PostMapping(path = "/create/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> createPdf(@PathVariable("id") Long priceOfferId, @RequestBody String json) throws DocumentException, com.lowagie.text.DocumentException, IOException {
        
        log.debug("Received request body: {}", json);

        Optional<PriceOffer> optPriceOffer = priceOfferService.findById(priceOfferId);

        if(optPriceOffer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PriceOffer priceOffer = optPriceOffer.get();

        String filename = priceOffer.getId() + "_Pristilbud_" + priceOffer.getCustomerName() + ".pdf";
        ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename(filename, StandardCharsets.UTF_8).build();
        HttpHeaders headers = new HttpHeaders();
//         headers.setAccessControlExposeHeaders(ACCESS_CONTROL_EXPOSE_HEADERS);
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(contentDisposition);

        String xhtml = templateService.compileTemplate(json);

        byte[] file = pdfService.generatePdfFromHTML(xhtml);

        ByteArrayResource body = new ByteArrayResource(file);

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(body);
    }
}
