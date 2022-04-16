package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportApartmentsDto;
import softuni.exam.models.entity.Apartment;
import softuni.exam.repository.ApartmentRepository;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.ApartmentService;
import softuni.exam.util.util.ValidationUtil;
import softuni.exam.util.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApartmentServiceImpl implements ApartmentService {

    private final XmlParser xmlParser;
    private final ValidationUtil validationUtil;
    private final ApartmentRepository apartmentRepository;
    private final ModelMapper modelMapper;
    private final TownRepository townRepository;

    public ApartmentServiceImpl(XmlParser xmlParser, ValidationUtil validationUtil, ApartmentRepository apartmentRepository, ModelMapper modelMapper, TownRepository townRepository) {
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
        this.apartmentRepository = apartmentRepository;
        this.modelMapper = modelMapper;
        this.townRepository = townRepository;
    }


    @Override
    public boolean areImported() {
        return apartmentRepository.count() > 0;
    }

    @Override
    public String readApartmentsFromFile() throws IOException {
        return Files.readString(Path.of("src/main/resources/files/xml/apartments.xml"));
    }

    @Override
    public String importApartments() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();

        ImportApartmentsDto importApartmentsDto = xmlParser.fromFile("src/main/resources/files/xml/apartments.xml", ImportApartmentsDto.class);

        importApartmentsDto.getApartmentImportDtos().stream()
                .filter(currentApartment -> {
                    boolean isValid = validationUtil.isValid(currentApartment);

                    Apartment apartmentCheck = apartmentRepository.findByTownAndArea(currentApartment.getTownName(), currentApartment.getArea()).orElse(null);

                    if (apartmentCheck != null) {
                        isValid = false;
                    }

                    sb.append(isValid ? String.format("Successfully imported apartment %s - %.2f",
                            currentApartment.getApartmentType(),
                            currentApartment.getArea())
                            : "Invalid apartment");
                    sb.append(System.lineSeparator());


                    return isValid;
                })
                .map(apartmentImportDto -> {
                  Apartment apartment =  modelMapper.map(apartmentImportDto, Apartment.class);
                  apartment.setTown(townRepository.findByTownName(apartmentImportDto.getTownName()));
                  return apartment;
                })
                .forEach(apartmentRepository::save);

        return sb.toString().trim();
    }
}
