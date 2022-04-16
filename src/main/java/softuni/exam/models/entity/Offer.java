package softuni.exam.models.entity;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "offers")
public class Offer extends BaseEntity{

    @Column(name = "price", nullable = false)
    @Positive
    private BigDecimal price;


    @Column(name = "published_on", nullable = false)
    private LocalDate publishedOn;


    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    public Apartment getApartment() {
        return apartment;
    }


    @OneToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;

    public Offer() {
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(LocalDate publishedOn) {
        this.publishedOn = publishedOn;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }
}
