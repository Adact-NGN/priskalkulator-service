package no.ding.pk.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsArrayContaining.hasItemInArray;

class UserTest {

    @Test
    public void shouldAddSalesOfficeToUser() {
        User user = User.builder("Kjetil", "Minde", "Kjetil Torvund Minde", "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        user.addSalesOffice("100");

        assertThat(user.getSalesOffices(), hasSize(1));
    }

    @Test
    public void shouldAddListOfSalesOfficesToUser() {
        User user = User.builder("Kjetil", "Minde", "Kjetil Torvund Minde", "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no").build();

        user.setSalesOffices(List.of("100", "104", "127"));

        assertThat(user.getSalesOffices(), hasSize(3));
    }

    @Test
    public void shouldRemoveSalesOfficeFromUser() {
        User user = User.builder("Kjetil", "Minde", "Kjetil Torvund Minde", "kjetil.torvund.minde@ngn.no", "kjetil.torvund.minde@ngn.no")
                .salesOffices("100,104,127")
                .build();

        user.removeSalesOffice("104");

        assertThat(user.getSalesOffices(), hasSize(2));
        assertThat(user.getSalesOffices().toArray(), not(hasItemInArray("104")));
    }

}