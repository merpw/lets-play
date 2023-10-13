package pw.mer.letsplay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("User")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @Id
    @JsonProperty("id")
    @Getter
    private String id;

    @JsonProperty("name")
    @Getter
    @Setter
    private String name;

    @Indexed(unique = true)
    @JsonProperty("email")
    @Getter
    @Setter
    private String email;

    @Setter
    private String encodedPassword;

    @Getter
    @Setter
    @JsonProperty("role")
    private ERole role;

    @JsonIgnore
    @Field("password")
    public String getEncodedPassword() {
        return encodedPassword;
    }

    public User(String name, String email, String encodedPassword, ERole role) {
        this.name = name;
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.role = role;
    }
}
