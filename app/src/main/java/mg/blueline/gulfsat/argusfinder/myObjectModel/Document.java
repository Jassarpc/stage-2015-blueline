package mg.blueline.gulfsat.argusfinder.myObjectModel;

/**
 * Created by hamidullah on 9/25/15.
 */
public class Document {
    private String name;
    private String creator;
    private String date;
    private String extension;

    public String getExtension() {
        return extension;
    }

    public void setExtension() {
        String filenameArray[] = name.split("\\.");
        this.extension = filenameArray[filenameArray.length-1];
       // this.extension =name.substring((name.lastIndexOf(".") + 1), name.length());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
