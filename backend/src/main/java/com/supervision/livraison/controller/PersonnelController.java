package com.supervision.livraison.controller;

import com.supervision.livraison.dto.LivreurDTO;
import com.supervision.livraison.entity.GpsPosition;
import com.supervision.livraison.entity.Personnel;
import com.supervision.livraison.repository.GpsPositionRepository;
import com.supervision.livraison.repository.PersonnelRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Personnel controller — manages driver (livreur) information.
 */
@RestController
@RequestMapping("/api/personnel")
@CrossOrigin(origins = "*")
public class PersonnelController {

    private final PersonnelRepository personnelRepository;
    private final GpsPositionRepository gpsPositionRepository;

    public PersonnelController(PersonnelRepository personnelRepository, GpsPositionRepository gpsPositionRepository) {
        this.personnelRepository = personnelRepository;
        this.gpsPositionRepository = gpsPositionRepository;
    }

    /**
     * GET /api/personnel/livreurs — Get all drivers with their latest GPS position.
     */
    @GetMapping("/livreurs")
    public ResponseEntity<List<LivreurDTO>> getAllLivreurs() {
        List<Personnel> livreurs = personnelRepository.findAllLivreurs();

        List<LivreurDTO> dtoList = livreurs.stream().map(p -> {
            LivreurDTO dto = new LivreurDTO();
            dto.setId(p.getIdpers());
            dto.setNom(p.getNompers());
            dto.setPrenom(p.getPrenompers());
            dto.setTelephone(p.getTelpers());
            dto.setVille(p.getVillepers());

            // Get latest GPS position
            List<GpsPosition> positions = gpsPositionRepository.findByLivreurIdOrderByRecordedAtDesc(p.getIdpers());
            if (!positions.isEmpty()) {
                GpsPosition latest = positions.get(0);
                dto.setLatitude(latest.getLatitude());
                dto.setLongitude(latest.getLongitude());
                dto.setLastUpdate(latest.getRecordedAt().toString());
            }

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }
}
