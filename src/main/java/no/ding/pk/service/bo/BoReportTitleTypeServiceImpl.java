package no.ding.pk.service.bo;

import no.ding.pk.domain.bo.TitleType;
import no.ding.pk.repository.bo.TitleTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static no.ding.pk.repository.specifications.TitleTypeSpecification.withTitleType;

@Service
public class BoReportTitleTypeServiceImpl implements BoReportTitleTypeService {

    private static final Logger log = LoggerFactory.getLogger(BoReportTitleTypeServiceImpl.class);

    private final TitleTypeRepository repository;

    @Autowired
    public BoReportTitleTypeServiceImpl(TitleTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TitleType> getAllTitleTypes(String type) {
        log.debug("Getting list of TitleType");
        return repository.findAll(Specification.where(withTitleType(type)));
    }

    @Override
    public TitleType save(TitleType titleType) {
        return repository.save(titleType);
    }
}
