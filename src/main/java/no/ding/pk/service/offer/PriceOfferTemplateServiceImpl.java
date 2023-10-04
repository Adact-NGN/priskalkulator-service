package no.ding.pk.service.offer;

import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.template.PriceOfferTemplate;
import no.ding.pk.repository.offer.PriceOfferTemplateRepository;
import no.ding.pk.service.UserService;
import no.ding.pk.web.handlers.PriceOfferTemplateNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class PriceOfferTemplateServiceImpl implements PriceOfferTemplateService {

    private static final Logger log = LoggerFactory.getLogger(PriceOfferTemplateServiceImpl.class);

    private final PriceOfferTemplateRepository repository;
    private final UserService userService;

    @Autowired
    public PriceOfferTemplateServiceImpl(PriceOfferTemplateRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public PriceOfferTemplate save(PriceOfferTemplate newTemplate) {
        return repository.save(newTemplate);
    }

    @Override
    public List<PriceOfferTemplate> findAll() {
        return repository.findAll();
    }

    @Override
    public PriceOfferTemplate findById(Long id) {
        Optional<PriceOfferTemplate> byId = repository.findById(id);

        if(byId.isEmpty()) {
            throw new PriceOfferTemplateNotFound();
        }

        return byId.get();
    }

    @Override
    public List<PriceOfferTemplate> findAllByAuthor(String userEmail) {
        return repository.findAllByAuthorEmail(userEmail);
    }

    @Override
    public List<PriceOfferTemplate> findAllSharedWithUser(String userEmail) {

        User user = userService.findByEmail(userEmail);

        if(user == null) {
            log.debug("Could not find user with mail {} when looking for shared price offer templates.", userEmail);
            return new ArrayList<>();
        }

        return repository.findAllBySharedWith(user);
    }
}
