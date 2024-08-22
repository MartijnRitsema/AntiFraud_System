package antifraud.suspiciousip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuspiciousIPService {

    @Autowired
    private SuspiciousIPRepository suspiciousIPRepository;

    public SuspiciousIP addSuspiciousIP(String ip) {
        if (suspiciousIPRepository.existsByIp(ip)) {
            return null; // IP already exists
        }
        SuspiciousIP suspiciousIP = new SuspiciousIP();
        suspiciousIP.setIp(ip);
        return suspiciousIPRepository.save(suspiciousIP);
    }

    public void deleteSuspiciousIP(String ip) {
        SuspiciousIP suspiciousIP = suspiciousIPRepository.findByIp(ip);
        if (suspiciousIP != null) {
            suspiciousIPRepository.delete(suspiciousIP);
        }
    }

    // New method to check if an IP exists
    public boolean exists(String ip) {
        return suspiciousIPRepository.existsByIp(ip);
    }

    // Method to get all suspicious IPs
    public List<SuspiciousIP> getAll() {
        return (List<SuspiciousIP>) suspiciousIPRepository.findAll();
    }
}
