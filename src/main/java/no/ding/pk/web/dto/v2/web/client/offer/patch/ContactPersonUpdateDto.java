package no.ding.pk.web.dto.v2.web.client.offer.patch;

import org.openapitools.jackson.nullable.JsonNullable;

public class ContactPersonUpdateDto {
    private JsonNullable<Long> id;
    private JsonNullable<String> firstName;
    private JsonNullable<String> lastName;
    private JsonNullable<String> mobileNumber;
    private JsonNullable<String> emailAddress;

    public JsonNullable<Long> getId() {
        return id;
    }

    public void setId(JsonNullable<Long> id) {
        this.id = id;
    }

    public JsonNullable<String> getFirstName() {
        return firstName;
    }

    public void setFirstName(JsonNullable<String> firstName) {
        this.firstName = firstName;
    }

    public JsonNullable<String> getLastName() {
        return lastName;
    }

    public void setLastName(JsonNullable<String> lastName) {
        this.lastName = lastName;
    }

    public JsonNullable<String> getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(JsonNullable<String> mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public JsonNullable<String> getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(JsonNullable<String> emailAddress) {
        this.emailAddress = emailAddress;
    }
}
