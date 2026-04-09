import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class TestPwd {
    public static void main(String[] args) {
        BCryptPasswordEncoder e = new BCryptPasswordEncoder();
        String h = "$2a$10$X7ZkP9qR3mN5vL8wY2tA.eO4bC6dE1fG3hI5jK7lM9nO0pQ2rS4tU";
        String[] w = {"test", "admin123", "password123", "livreur", "controleur", "livraison", "12345678"};
        for(String s : w) { if(e.matches(s, h)) System.out.println("FOUND: " + s); }
    }
}
