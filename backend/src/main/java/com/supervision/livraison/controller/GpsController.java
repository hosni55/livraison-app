package com.supervision.livraison.controller;

import com.supervision.livraison.dto.GpsPositionRequest;
import com.supervision.livraison.dto.LivreurDTO;
import com.supervision.livraison.entity.GpsPosition;
import com.supervision.livraison.entity.Personnel;
import com.supervision.livraison.repository.GpsPositionRepository;
import com.supervision.livraison.repository.PersonnelRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * GPS controller — handles GPS position tracking for livreurs.
 */
@RestController
@RequestMapping("/api/gps")
@CrossOrigin(origins = "*")
public class GpsController {

    private final GpsPositionRepository gpsPositionRepository;
    private final PersonnelRepository personnelRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public GpsController(GpsPositionRepository gpsPositionRepository,
                         PersonnelRepository personnelRepository,
                         SimpMessagingTemplate messagingTemplate) {
        this.gpsPositionRepository = gpsPositionRepository;
        this.personnelRepository = personnelRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * POST /api/gps/position — Record a GPS position (livreur only).
     * Also broadcasts the position via WebSocket for real-time map updates.
     */
    @PostMapping("/position")
    public ResponseEntity<GpsPosition> recordPosition(
            @AuthenticationPrincipal Personnel livreur,
            @RequestBody GpsPositionRequest request) {

        GpsPosition position = new GpsPosition();
        position.setLivreur(livreur);
        position.setLatitude(request.getLatitude());
        position.setLongitude(request.getLongitude());

        GpsPosition saved = gpsPositionRepository.save(position);

        // Broadcast position to controleurs via WebSocket
        LivreurDTO dto = new LivreurDTO();
        dto.setId(livreur.getIdpers());
        dto.setNom(livreur.getNompers());
        dto.setPrenom(livreur.getPrenompers());
        dto.setLatitude(request.getLatitude());
        dto.setLongitude(request.getLongitude());

        messagingTemplate.convertAndSend("/topic/gps-updates", dto);

        return ResponseEntity.ok(saved);
    }

    /**
     * GET /api/gps/livreurs — Get latest GPS positions for all livreurs (controleur only).
     */
    @GetMapping("/livreurs")
    public ResponseEntity<List<LivreurDTO>> getAllLivreursPositions() {
        List<GpsPosition> latestPositions = gpsPositionRepository.findLatestPositions();

        List<LivreurDTO> dtoList = latestPositions.stream().map(pos -> {
            LivreurDTO dto = new LivreurDTO();
            dto.setId(pos.getLivreur().getIdpers());
            dto.setNom(pos.getLivreur().getNompers());
            dto.setPrenom(pos.getLivreur().getPrenompers());
            dto.setLatitude(pos.getLatitude());
            dto.setLongitude(pos.getLongitude());
            dto.setLastUpdate(pos.getRecordedAt().toString());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }
}
