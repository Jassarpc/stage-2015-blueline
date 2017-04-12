package mg.blueline.gulfsat.argusfinder.myObjectModel;

/**
 * Created by hamidullah on 9/26/15.
 */
public class User {
    private String username;
    private String name;

    private Double longitude;
    private String lastLocation;
    private String hd;
    private String mail;
    private String image;
    private Double latitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


    public String getHd() {
        return hd;
    }

    public void setHd(String hd) {

       // hd=hd.substring(0,18);

        this.hd = hd;}

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public String getName() {return name;}

    public void setName(String name) {
        this.name = name;
    }


    public void setMail(String mail) {
        this.mail = mail;
    }



    public String getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(String lastLocation) {
        this.lastLocation = lastLocation;
    }
}
