package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportAgentsDto;
import softuni.exam.models.entity.Agent;
import softuni.exam.repository.AgentRepository;
import softuni.exam.service.AgentService;
import softuni.exam.service.TownService;
import softuni.exam.util.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

@Service
public class AgentServiceImpl implements AgentService {

    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final AgentRepository agentRepository;
    private final TownService townService;

    private static final String JSON_AGENTS_FILE_PATH = "src/main/resources/files/json/agents.json";

    public AgentServiceImpl(Gson gson, ModelMapper modelMapper, ValidationUtil validationUtil, AgentRepository agentRepository, TownService townService) {
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.agentRepository = agentRepository;
        this.townService = townService;
    }

    @Override
    public boolean areImported() {
        return agentRepository.count() > 0;
    }

    @Override
    public String readAgentsFromFile() throws IOException {
        return Files.readString(Path.of(JSON_AGENTS_FILE_PATH));
    }

    @Override
    public String importAgents() throws IOException {
        StringBuilder sb = new StringBuilder();

        ImportAgentsDto[] importAgents = gson.fromJson(readAgentsFromFile(), ImportAgentsDto[].class);

        Arrays.stream(importAgents)
                .filter(currentAgent -> {
                    boolean isValid = validationUtil.isValid(currentAgent);
                    Optional<Agent> agentName = agentRepository.findByFirstName(currentAgent.getFirstName());
                    if (agentName.isPresent()) {
                        isValid = false;
                    }

                        sb.append(isValid ? String.format("Successfully imported agent - %s %s",
                                currentAgent.getFirstName(), currentAgent.getLastName())
                                : "Invalid agent");
                        sb.append(System.lineSeparator());

                    return isValid;
                })
                .map(currentAgent -> {
                    Agent agent = modelMapper.map(currentAgent, Agent.class);
                    agent.setTown(townService.findByName(currentAgent.getTown()));

                    return agent;
                })

                .forEach(agentRepository::save);


        return sb.toString().trim();
    }
}
