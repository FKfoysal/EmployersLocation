package gub.foysal.employerslocation.Method;

public class Fingerprint {
    private String name;
    private String phone;
    private String address;
    private String prk;
    private String date;
    private String time;
    private String attendance;

    public Fingerprint() {

    }

    public Fingerprint(String name, String phone, String address, String prk, String date, String time, String attendance) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.prk = prk;
        this.date = date;
        this.time = time;
        this.attendance = attendance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrk() {
        return prk;
    }

    public void setPrk(String prk) {
        this.prk = prk;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }
}
