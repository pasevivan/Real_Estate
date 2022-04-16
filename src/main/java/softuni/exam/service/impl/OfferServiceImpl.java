package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportOffersDto;
import softuni.exam.models.entity.Agent;
import softuni.exam.models.entity.Apartment;
import softuni.exam.models.entity.ApartmentType;
import softuni.exam.models.entity.Offer;
import softuni.exam.repository.AgentRepository;
import softuni.exam.repository.ApartmentRepository;
import softuni.exam.repository.OfferRepository;
import softuni.exam.service.OfferService;
import softuni.exam.util.util.ValidationUtil;
import softuni.exam.util.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class OfferServiceImpl implements OfferService {

    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final OfferRepository offerRepository;
    private final AgentRepository agentRepository;
    private final ApartmentRepository apartmentRepository;

    private static final String XML_OFFERS_FILE_PATH = "src/main/resources/files/xml/offers.xml";

    public OfferServiceImpl(XmlParser xmlParser, ModelMapper modelMapper, ValidationUtil validationUtil, OfferRepository offerRepository, AgentRepository agentRepository, ApartmentRepository apartmentRepository) {
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.offerRepository = offerRepository;
        this.agentRepository = agentRepository;

        this.apartmentRepository = apartmentRepository;
    }

    @Override
    public boolean areImported() {
        return offerRepository.count() > 0;
    }

    @Override
    public String readOffersFileContent() throws IOException {
        return Files.readString(Path.of(XML_OFFERS_FILE_PATH));
    }

    @Override
    public String importOffers() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();

        ImportOffersDto importOffersDto = xmlParser.fromFile("src/main/resources/files/xml/offers.xml", ImportOffersDto.class);

        importOffersDto.getImportOfferDto()
                .stream()
                .filter(currentOffer -> {
                    boolean isValid = validationUtil.isValid(currentOffer);

                    Agent agent = agentRepository.findByFirstName(currentOffer.getAgent().getName()).orElse(null);

                    if (agent == null) {
                        isValid = false;
                    }

                    sb.append(isValid ? String.format("Successfully imported offer %.2f", currentOffer.getPrice())
                            : "Invalid offer");
                    sb.append(System.lineSeparator());

                    return isValid;
                })
                .map(currentOffer -> {
                    Offer offer = modelMapper.map(currentOffer, Offer.class);
                    offer.setAgent(agentRepository.findByFirstName(currentOffer.getAgent().getName()).orElse(null));
                    offer.setApartment(apartmentRepository.getById(currentOffer.getApartment().getId()));
                    return offer;

                })
                .forEach(offerRepository::save);


        return sb.toString();
    }

    @Override
    public String exportOffers() {
        StringBuilder sb = new StringBuilder();

        List<Offer> allOffers = offerRepository.findAllByTypeThreeRoomsOrderedByAreaDescThenOrderedByPriceAsc();

        allOffers.forEach(offer -> {
            sb.append(String.format("Agent %s %s with offer №%d:\n" +
                    "   \t\t-Apartment area: %.2f\n" +
                    "   \t\t--Town: %s\n" +
                    "   \t\t---Price: %.2f$\n",
                    offer.getAgent().getFirstName(),
                    offer.getAgent().getLastName(),
                    offer.getId(),
                    offer.getApartment().getArea(),
                    offer.getApartment().getTown().getTownName(),
                    offer.getPrice()
            ))
                    .append(System.lineSeparator());


        });

        return sb.toString();
    }
}
