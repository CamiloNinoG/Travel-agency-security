package cng.ms_security.Services;
import java.security.SecureRandom;
import org.springframework.stereotype.Service;

@Service
public class FAService {

    private final SecureRandom random = new SecureRandom();

    public String generateCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
