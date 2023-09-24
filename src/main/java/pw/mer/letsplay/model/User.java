package pw.mer.letsplay.model;

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

    public String getId() {
        return id;
    }

    @JsonProperty("name")
    private String name;

    @Indexed(unique = true)
    @JsonProperty("email")
    private String email;

    private String password;

    @JsonProperty("role")
    private final ERole role;

    public String getRole() {
        return role.toString().toLowerCase();
    }

    public User(String name, String email, String password, ERole role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
