package softuni.exam.models.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Positive;

@Entity
@Table(name = "towns")
public class Town extends BaseEntity{

    @Column(name = "town_name", nullable = false, unique = true)
    private String townName;

    @Column(name = "population", nullable = false)
    @Positive
    private Integer population;

    public Town() {
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }
}
