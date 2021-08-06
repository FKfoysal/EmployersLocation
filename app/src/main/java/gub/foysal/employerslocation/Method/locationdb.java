package gub.foysal.employerslocation.Method;

public class locationdb {
    private String lat,lon,address,finger;

    public locationdb() {
    }

    public locationdb(String lat, String lon, String address, String finger) {
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.finger = finger;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFinger() {
        return finger;
    }

    public void setFinger(String finger) {
        this.finger = finger;
    }
}
