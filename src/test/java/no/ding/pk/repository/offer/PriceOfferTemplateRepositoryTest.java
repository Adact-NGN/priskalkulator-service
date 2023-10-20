package no.ding.pk.repository.offer;

import no.ding.pk.domain.User;
import no.ding.pk.domain.offer.template.PriceOfferTemplate;
import no.ding.pk.domain.offer.template.TemplateMaterial;
import no.ding.pk.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@TestPropertySource("/h2-db.properties")
class PriceOfferTemplateRepositoryTest {

    @Autowired
    private PriceOfferTemplateRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldPersistPriceOfferTemplate() {
        User author = User.builder("Test", "Testesen", "Test Testesen", "test.testesen@testing.no", "test.testesen@testing.no")
                .build();

        author = userRepository.save(author);

        User sharedUser = User.builder("Delt", "Med", "Delt Med", "delt.med@testing.no", "delt.med@testing.no")
                .build();

        sharedUser = userRepository.save(sharedUser);

        PriceOfferTemplate template = PriceOfferTemplate.builder()
                .name("Test template")
                .author(author)
                .materials(List.of(TemplateMaterial.builder().material("111101").build()))
                .zoneBasedMaterials(List.of(TemplateMaterial.builder().material("50101").build()))
                .isShareable(true)
                .sharedWith(List.of(sharedUser))
                .build();

        template = repository.save(template);

        assertThat(template.getId(), notNullValue());
    }

    @Test
    public void shouldFindPriceOfferTemplateByAuthor() {
        User author = User.builder("Test", "Testesen", "Test Testesen", "test.testesen@testing.no", "test.testesen@testing.no")
                .build();

        author = userRepository.save(author);

        User sharedUser = User.builder("Delt", "Med", "Delt Med", "delt.med@testing.no", "delt.med@testing.no")
                .build();

        sharedUser = userRepository.save(sharedUser);

        PriceOfferTemplate template = PriceOfferTemplate.builder()
                .name("Test template")
                .author(author)
                .materials(List.of(TemplateMaterial.builder().material("111101").build()))
                .zoneBasedMaterials(List.of(TemplateMaterial.builder().material("50101").build()))
                .isShareable(true)
                .sharedWith(List.of(sharedUser))
                .build();

        repository.save(template);

        List<PriceOfferTemplate> actual = repository.findAllByAuthorEmail(author.getEmail());

        assertThat(actual, hasSize(1));
    }

    @Test
    public void shouldFindPriceOfferTemplatesSharedWithUser() {
        User author = User.builder("Test", "Testesen", "Test Testesen", "test.testesen@testing.no", "test.testesen@testing.no")
                .build();

        author = userRepository.save(author);

        User sharedUser = User.builder("Delt", "Med", "Delt Med", "delt.med@testing.no", "delt.med@testing.no")
                .build();

        sharedUser = userRepository.save(sharedUser);

        User anotherUser = User.builder("Another", "User", "Another User", "another.user@testing.no", "another.user@testing.no")
                .build();

        anotherUser = userRepository.save(anotherUser);

        PriceOfferTemplate template = PriceOfferTemplate.builder()
                .name("Test template")
                .author(author)
                .materials(List.of(TemplateMaterial.builder().material("111101").build()))
                .zoneBasedMaterials(List.of(TemplateMaterial.builder().material("50101").build()))
                .isShareable(true)
                .sharedWith(List.of(sharedUser))
                .build();

        repository.save(template);

        PriceOfferTemplate anotherTemplate = PriceOfferTemplate.builder()
                .name("Test template 2")
                .author(anotherUser)
                .materials(List.of(TemplateMaterial.builder().material("119901").build()))
                .zoneBasedMaterials(List.of(TemplateMaterial.builder().material("50103").build()))
                .build();

        repository.save(anotherTemplate);

        PriceOfferTemplate yetAnotherTemplate = PriceOfferTemplate.builder()
                .name("Test template 3")
                .author(anotherUser)
                .sharedWith(List.of(sharedUser))
                .isShareable(true)
                .materials(List.of(TemplateMaterial.builder().material("119901").build()))
                .zoneBasedMaterials(List.of(TemplateMaterial.builder().material("50103").build()))
                .build();

        repository.save(yetAnotherTemplate);


        List<PriceOfferTemplate> allBySharedWith = repository.findAllBySharedWith(sharedUser);

        assertThat(allBySharedWith, hasSize(2));
    }

}