package co.mvpmatch.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Map;

import static java.util.Map.entry;

/**
 * A user.
 */
@Entity
@Table(name = "user")
@TypeDef(name = "json", typeClass = JsonType.class)
//@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
//    @Pattern(regexp = Constants.LOGIN_REGEX)
//    @Size(min = 4, max = 50)
    @Size(min = 1, max = 50)
    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;

    /**
     * password hash
     * @Size(min = 60, max = 60)
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @NotNull
//    @Size(min = 1, max = 50)
//    @Size(min = 0, max = 50)
    @Column(name = "password", length = 60, nullable = false)
    private String password;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private Map<Integer, Integer> deposit = Map.ofEntries(
                                                    entry(5, 0),
                                                    entry(10, 0),
                                                    entry(20, 0),
                                                    entry(50, 0),
                                                    entry(100, 0)
    );

//    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//    @NotNull
//    @Size(min = 1, max = 50)
    @Column(name = "role", length = 60, nullable = false)
    private String role;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Transient
    private boolean rememberMe;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Map<Integer, Integer> getDeposit() {
        return deposit;
    }

    public void setDeposit(Map<Integer, Integer> deposit) {
        this.deposit = deposit;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public Long getMoney() {
//        deposit.entrySet().stream().reduce(0, (subtotal, coin) -> subtotal + coin.getKey() *
        Long result  = 0L;
        for (Map.Entry<Integer, Integer> coin: deposit.entrySet()) {
            result += coin.getKey() * coin.getValue();
        }
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", role='" + role + '\'' +
            ", rememberMe=" + rememberMe +
            '}';
    }
}
