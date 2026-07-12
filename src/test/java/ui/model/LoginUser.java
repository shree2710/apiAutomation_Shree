package ui.model;

/**
 * One row of JSON-driven UI login data
 * ({@code src/test/resources/testdata/ui-login-users.json}).
 */
public class LoginUser {

    private String description;
    private String username;
    private String password;
    private String expectedError;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getExpectedError() {
        return expectedError;
    }

    public void setExpectedError(String expectedError) {
        this.expectedError = expectedError;
    }

    @Override
    public String toString() {
        return description;
    }
}
