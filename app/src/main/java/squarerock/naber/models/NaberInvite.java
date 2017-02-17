package squarerock.naber.models;

/**
 * Created by pranavkonduru on 2/9/17.
 */

public class NaberInvite {

    private String invitedBy;
    private String invitedVia;
    private String contact;
    private String isAccepted;

    public String getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(String invitedBy) {
        this.invitedBy = invitedBy;
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

    public String getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(String isAccepted) {
        this.isAccepted = isAccepted;
    }
}
