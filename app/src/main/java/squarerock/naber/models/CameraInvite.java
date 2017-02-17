package squarerock.naber.models;

/**
 * Created by pranavkonduru on 2/9/17.
 */

public class CameraInvite {
    private String invitedBy;           // Who invited
    private String invitedTo;           // Which camera is the invitation for
    private String invitedVia;          // Phone number or email - does it matter?
    private String contact;             // Actual contact of person whom this invite is supposed to be
    private boolean isAccepted;         // False initially. After user accepts, will be updated to true. Dont know the use of it, yet.

    public boolean getAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public String getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(String invitedBy) {
        this.invitedBy = invitedBy;
    }

    public String getInvitedTo() {
        return invitedTo;
    }

    public void setInvitedTo(String invitedTo) {
        this.invitedTo = invitedTo;
    }

    public String getInvitedVia() {
        return invitedVia;
    }

    public void setInvitedVia(String invitedVia) {
        this.invitedVia = invitedVia;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
