package gub.foysal.employerslocation.Method;

public class Users {
    private String name;
    private String phone;
    private String password;
    private String image;
    private String address;
    private String prk;

    public Users() {

    }

    public Users(String name, String phone, String password, String image, String address, String prk) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.image = image;
        this.address = address;
        this.prk=prk;
    }

    public String getPrk() {
        return prk;
    }

    public void setPrk(String prk) {
        this.prk = prk;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}