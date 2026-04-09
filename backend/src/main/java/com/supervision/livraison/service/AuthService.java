package com.supervision.livraison.service;

import com.supervision.livraison.dto.LoginRequest;
import com.supervision.livraison.dto.LoginResponse;
import com.supervision.livraison.dto.RegisterRequest;
import com.supervision.livraison.entity.Personnel;
import com.supervision.livraison.entity.Postes;
import com.supervision.livraison.repository.PersonnelRepository;
import com.supervision.livraison.repository.PostesRepository;
import com.supervision.livraison.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Authentication service — handles login and JWT token generation.
 */
@Service
public class AuthService {

    private final PersonnelRepository personnelRepository;
    private final PostesRepository postesRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(PersonnelRepository personnelRepository, PostesRepository postesRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.personnelRepository = personnelRepository;
        this.postesRepository = postesRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticate user and return JWT token with user info.
     */
    public LoginResponse login(LoginRequest request) {
        Personnel personnel = personnelRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new RuntimeException("Identifiants invalides"));

        // In production, compare hashed passwords. For now, we accept plain text for testing.
        // If passwords are BCrypt hashed in DB, use: passwordEncoder.matches(request.getMotP(), personnel.getMotP())
        if (!request.getMotP().equals(personnel.getMotP()) &&
            !passwordEncoder.matches(request.getMotP(), personnel.getMotP())) {
            throw new RuntimeException("Identifiants invalides");
        }

        String role = personnel.getRole();
        String token = jwtUtil.generateToken(personnel.getLogin(), role, personnel.getIdpers());

        return new LoginResponse(
                token,
                role,
                personnel.getIdpers(),
                personnel.getNompers() + " " + personnel.getPrenompers(),
                personnel.getLogin()
        );
    }

    /**

     * Register a new personnel user.
     */
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (personnelRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new RuntimeException("L'identifiant existe déjà");
        }

        Personnel personnel = new Personnel();
        personnel.setNompers(request.getNom());
        personnel.setPrenompers(request.getPrenom());
        personnel.setLogin(request.getLogin());
        
        // Hash password
        personnel.setMotP(passwordEncoder.encode(request.getPassword()));
        
        personnel.setDEmbauche(new Date());

        // Assign poste based on role
        Long codePoste = "LIVREUR".equalsIgnoreCase(request.getRole()) ? 1L : 2L;
        Postes poste = postesRepository.findById(codePoste)
                .orElseThrow(() -> new RuntimeException("Poste non trouvé"));
        personnel.setPoste(poste);

        // If ID is not auto-generated, we might need to set it manually for now
        // personnel.setIdpers(System.currentTimeMillis()); // Temporary fix if @GeneratedValue is missing

        Personnel saved = personnelRepository.save(personnel);

        // Auto-login after registration
        String token = jwtUtil.generateToken(saved.getLogin(), saved.getRole(), saved.getIdpers());

        return new LoginResponse(
                token,
                saved.getRole(),
                saved.getIdpers(),
                saved.getNompers() + " " + saved.getPrenompers(),
                saved.getLogin()
        );
    }
}
