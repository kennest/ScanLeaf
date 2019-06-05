package wesicknessdect.example.org.wescanleaf.models;

import com.google.gson.annotations.SerializedName;

public class Credential {
    @SerializedName(value = "username")
    String email;
    @SerializedName(value = "password")
    String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
