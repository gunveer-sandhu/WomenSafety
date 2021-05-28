package gunveer.codes.womensafety;

public class Contact {
    Long contactNumber;
    String contactNickname;

    public Contact(String contactNickname, Long contactNumber) {

        this.contactNickname = contactNickname;
        this.contactNumber = contactNumber;

    }

    public Long getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(Long contactNumber) {
        this.contactNumber = contactNumber;
    }


    public String getContactNickname() {
        return contactNickname;
    }

    public void setContactNickname(String contactNickname) {
        this.contactNickname = contactNickname;
    }
}
