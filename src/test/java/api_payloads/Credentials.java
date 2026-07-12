package api_payloads;

/**
 * Login payload for the auth API. Serialized by the shared JsonUtil mapper, so
 * tests no longer hand-build JSON strings.
 */
public class Credentials {

    private String username;
    private String password;

    public Credentials() {
        // for Jackson
    }

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
