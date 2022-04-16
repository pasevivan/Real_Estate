package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportTownsDto;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.TownService;
import softuni.exam.util.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class TownServiceImpl implements TownService {

    private final ModelMapper modelMapper;
    private final Gson gson;
    private final TownRepository townRepository;
    private final ValidationUtil validationUtil;

    private static final String JSON_TOWNS_FILE_PATH = "src/main/resources/files/json/towns.json";

    public TownServiceImpl(ModelMapper modelMapper, Gson gson, TownRepository townRepository, ValidationUtil validationUtil) {
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.townRepository = townRepository;
        this.validationUtil = validationUtil;
    }

    @Override
    public boolean areImported() {
        return townRepository.count() > 0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        return Files.readString(Path.of(JSON_TOWNS_FILE_PATH));

    }

    @Override
    public String importTowns() throws IOException {
        StringBuilder sb = new StringBuilder();

        ImportTownsDto[] importTownsDtos = gson.fromJson(readTownsFileContent(), ImportTownsDto[].class);

        Arrays.stream(importTownsDtos)
                .filter(currentTown -> {
                    boolean isValid = validationUtil.isValid(currentTown);

                    sb.append(isValid ? String.format("Successfully imported town %s - %d",
                            currentTown.getTownName(),
                            currentTown.getPopulation())
                            : "Invalid town");
                    sb.append(System.lineSeparator());

                    return isValid;

                })
                .map(currentTown -> modelMapper.map(currentTown, Town.class))
                .forEach(townRepository::save);

        return sb.toString().trim();
    }

    @Override
    public Town findByName(String town) {
        return townRepository.findByTownName(town);
    }
}
