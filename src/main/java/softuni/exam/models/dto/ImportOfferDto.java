package softuni.exam.models.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name = "offer")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportOfferDto {

    @XmlElement(name = "price")
    @Positive
    @NotNull
    private BigDecimal price;

    @XmlElement(name = "agent")
    @NotNull
    private AgentDto agent;

    @XmlElement(name = "apartment")
    @NotNull
    private ApartmentDto apartment;

    @XmlElement(name = "publishedOn")
    @NotNull
    private String publishedOn;

    public ImportOfferDto() {
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public AgentDto getAgent() {
        return agent;
    }

    public void setAgent(AgentDto agent) {
        this.agent = agent;
    }

    public ApartmentDto getApartment() {
        return apartment;
    }

    public void setApartment(ApartmentDto apartment) {
        this.apartment = apartment;
    }

    public String getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(String publishedOn) {
        this.publishedOn = publishedOn;
    }
}
