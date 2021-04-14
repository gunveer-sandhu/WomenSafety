package gunveer.codes.womensafety;

public class Contact {
    int contactNumber;
    String contactEmail;
    String contactNickname;

    public Contact(String contactNickname, int contactNumber, String contactEmail) {

        this.contactNickname = contactNickname;
        this.contactNumber = contactNumber;
        this.contactEmail = contactEmail;

    }

    public int getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(int contactNumber) {
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
