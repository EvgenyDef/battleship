package by.cats.repository;

import by.cats.entity.ShipLayout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipLayoutsRepository extends JpaRepository<ShipLayout, Integer> {
    List<ShipLayout> findByUserId(Integer userId);
}
