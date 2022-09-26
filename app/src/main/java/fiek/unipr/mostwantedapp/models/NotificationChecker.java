package fiek.unipr.mostwantedapp.models;

public class NotificationChecker {
    String docId, docOldType, docNewType;

    public NotificationChecker(String docId, String docOldType, String docNewType) {
        this.docId = docId;
        this.docOldType = docOldType;
        this.docNewType = docNewType;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocOldType() {
        return docOldType;
    }

    public void setDocOldType(String docOldType) {
        this.docOldType = docOldType;
    }

    public String getDocNewType() {
        return docNewType;
    }

    public void setDocNewType(String docNewType) {
        this.docNewType = docNewType;
    }
}
