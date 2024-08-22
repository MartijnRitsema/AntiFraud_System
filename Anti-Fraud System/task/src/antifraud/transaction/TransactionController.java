package antifraud.transaction;

import antifraud.stolencard.StolenCardRepository;
import antifraud.suspiciousip.SuspiciousIPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {

    @Autowired
    private SuspiciousIPRepository suspiciousIPRepository;

    @Autowired
    private StolenCardRepository stolenCardRepository;

    @PostMapping("/transaction")
    public ResponseEntity<TransactionDto> performTransaction(@RequestBody TransactionRequest transaction) {
        if (transaction == null || transaction.getAmount() == null || transaction.getIp() == null || transaction.getNumber() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        // Add validation for negative or zero amounts
        if (transaction.getAmount() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        boolean isSuspiciousIP = suspiciousIPRepository.existsByIp(transaction.getIp());
        boolean isStolenCard = stolenCardRepository.existsByNumber(transaction.getNumber());

        StringBuilder info = new StringBuilder();

        // Check for amount condition
        if (transaction.getAmount() > 1500) {
            info.append("amount");
        }

        // Check for stolen card condition
        if (isStolenCard) {
            if (info.length() > 0) {
                info.append(", ");
            }
            info.append("card-number");
        }

        // Check for suspicious IP condition
        if (isSuspiciousIP) {
            if (info.length() > 0) {
                info.append(", ");
            }
            info.append("ip");
        }

        if (info.length() > 0) {
            return ResponseEntity.ok(new TransactionDto("PROHIBITED", info.toString()));
        }

        // Logic for manual processing
        if (transaction.getAmount() > 200 && transaction.getAmount() <= 1500) {
            return ResponseEntity.ok(new TransactionDto("MANUAL_PROCESSING", "amount"));
        }

        // Default response
        return ResponseEntity.ok(new TransactionDto("ALLOWED", "none"));
    }
}
