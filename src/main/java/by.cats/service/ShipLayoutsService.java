package by.cats.service;

import by.cats.Model.ShipPlacemant;
import by.cats.entity.ShipLayout;
import by.cats.repository.ShipLayoutsRepository;
import by.cats.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipLayoutsService {
    private final ShipLayoutsRepository shipLayoutsRepository;
    private final UserRepository userRepository;

    // save layouts
    public ShipLayout saveShipLayout(Integer userId, String layoutName, List<ShipPlacemant> ships) {
        ShipLayout shipLayout = ShipLayout.builder()
                .layoutName(layoutName)
                .ships(ships)
                .user(userRepository.findUserById(userId)
                        .orElseThrow())
                .build();

        return shipLayoutsRepository.save(shipLayout);
    }

    //get all user's layouts from database
    public List<ShipLayout> getUserLayouts(Integer userId) {
        return shipLayoutsRepository.findByUserId(userId);
    }

    @Autowired
    public ShipLayoutsService(ShipLayoutsRepository shipLayoutsRepository,
                              UserRepository userRepository) {
        this.shipLayoutsRepository = shipLayoutsRepository;
        this.userRepository = userRepository;
    }
}
