package no.ding.pk.web.enums;

public enum SalesRoleName {
    Kundeveileder("Kundeveileder", "KV"),
    Salgskonsulent("Salgskonsulent", "SK"),
    KAM_lokalt("KAM lokalt", "KL"),
    KAM_nasjonalt("KAM nasjonalt", "KN"),
    Salgsleder_sjef_lokalt("Salgsleder/salgssjef lokalt", "SL"),
    Salgsjef_divisjon("Salgssjef divisjon", "SD"),
    Distriktssjef("Distriktssjef", "DR"),
    Superadmin("Superadmin", "Admin");
    private String name;
    private String description;

    SalesRoleName(String description, String name) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
