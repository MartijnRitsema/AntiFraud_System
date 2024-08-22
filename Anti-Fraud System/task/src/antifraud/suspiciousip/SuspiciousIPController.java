package antifraud.suspiciousip;

import antifraud.StatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/antifraud")
public class SuspiciousIPController {

    @Autowired
    private SuspiciousIPService suspiciousIPService;

    @PostMapping("/suspicious-ip")
    public ResponseEntity<?> addSuspiciousIP(@RequestBody SuspiciousIPDto dto) {
        String ip = dto.getIp();
        if (!isValidIP(ip)) {
            return ResponseEntity.badRequest().body("Invalid IP format");
        }
        SuspiciousIP suspiciousIP = suspiciousIPService.addSuspiciousIP(ip);
        if (suspiciousIP == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("IP already exists");
        }
        return ResponseEntity.ok(suspiciousIP); // Return 200 OK instead of 201 Created
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<StatusResponse> deleteSuspiciousIP(@PathVariable String ip) {
        if (!isValidIP(ip)) {
            return ResponseEntity.badRequest().body(new StatusResponse("Invalid IP format"));
        }

        if (!suspiciousIPService.exists(ip)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StatusResponse("IP " + ip + " not found"));
        }

        suspiciousIPService.deleteSuspiciousIP(ip);
        StatusResponse response = new StatusResponse("IP " + ip + " successfully removed!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/suspicious-ip")
    public ResponseEntity<List<SuspiciousIP>> getSuspiciousIPs() {
        List<SuspiciousIP> suspiciousIPs = suspiciousIPService.getAll();
        return ResponseEntity.ok(suspiciousIPs);
    }

    private boolean isValidIP(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return false;
        for (String part : parts) {
            try {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
}
