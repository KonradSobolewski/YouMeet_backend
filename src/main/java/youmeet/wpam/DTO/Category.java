package youmeet.wpam.DTO;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Long id;

    @Column(name = "type")
    @NotNull
    private String type;

    public Category() {
    }

    public Category(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
