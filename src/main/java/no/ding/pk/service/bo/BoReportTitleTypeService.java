package no.ding.pk.service.bo;

import no.ding.pk.domain.bo.TitleType;

import java.util.List;

public interface BoReportTitleTypeService {

    List<TitleType> getAllTitleTypes(String type);

    TitleType save(TitleType titleType);
}
