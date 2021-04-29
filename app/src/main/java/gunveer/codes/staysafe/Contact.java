package gunveer.codes.staysafe;

public class Contact {
    Long contactNumber;
    String contactEmail;
    String contactNickname;

    public Contact(String contactNickname, Long contactNumber, String contactEmail) {

        this.contactNickname = contactNickname;
        this.contactNumber = contactNumber;
        this.contactEmail = contactEmail;

    }

    public Long getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(Long contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactNickname() {
        return contactNickname;
    }

    public void setContactNickname(String contactNickname) {
        this.contactNickname = contactNickname;
    }
}
