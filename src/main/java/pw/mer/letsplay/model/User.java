package pw.mer.letsplay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("User")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @Id
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @Indexed(unique = true)
    @JsonProperty("email")
    private String email;

    private final String password;

    private final ERole role;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty("role")
    public String getRoleString() {
        return role.toString().toLowerCase();
    }

    public ERole getRole() {
        return role;
    }

    public User(String name, String email, String password, ERole role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
