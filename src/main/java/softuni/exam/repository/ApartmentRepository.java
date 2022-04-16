package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entity.Apartment;
import softuni.exam.models.entity.Town;

import java.util.Optional;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {

    @Query("SELECT a from Apartment a where a.town.townName = :townName and a.area = :area")
    Optional<Apartment> findByTownAndArea(String townName, Double area);

    Town findByTown(String town);

    Optional<Apartment> findById(Long id);
}
