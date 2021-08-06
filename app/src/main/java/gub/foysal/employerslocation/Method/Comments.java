package gub.foysal.employerslocation.Method;

public class Comments {
    public String comment, location, date, prk, time,name;

    public Comments() {

    }

    public Comments(String comment, String location, String date, String prk, String time,String name) {
        this.comment = comment;
        this.location = location;
        this.date = date;
        this.prk = prk;
        this.time = time;
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrk() {
        return prk;
    }

    public void setPrk(String prk) {
        this.prk = prk;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
