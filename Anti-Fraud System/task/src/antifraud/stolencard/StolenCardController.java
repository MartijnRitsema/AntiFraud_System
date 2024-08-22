package antifraud.stolencard;

import antifraud.StatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/antifraud")
public class StolenCardController {

    @Autowired
    private StolenCardService stolenCardService;

    @PostMapping("/stolencard")
    public ResponseEntity<StolenCard> addStolenCard(@RequestBody StolenCardDto dto) {
        String number = dto.getNumber();
        if (!isValidCardNumber(number)) {
            return ResponseEntity.badRequest().body(null);
        }
        if (!isValidLuhn(number)) {
            return ResponseEntity.badRequest().body(null);
        }
        StolenCard stolenCard = stolenCardService.addStolenCard(number);
        if (stolenCard == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.ok(stolenCard); // Return 200 OK instead of 201 Created
    }

    @DeleteMapping("/stolencard/{number}")
    public ResponseEntity<StatusResponse> deleteStolenCard(@PathVariable String number) {
        if (!isValidCardNumber(number)) {
            return ResponseEntity.badRequest().body(new StatusResponse("Invalid card number format"));
        }

        if (!stolenCardService.exists(number)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StatusResponse("Card " + number + " not found"));
        }

        stolenCardService.deleteStolenCard(number);
        StatusResponse response = new StatusResponse("Card " + number + " successfully removed!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stolencard")
    public ResponseEntity<List<StolenCard>> getStolenCards() {
        List<StolenCard> stolenCards = (List<StolenCard>) stolenCardService.getAll();
        return ResponseEntity.ok(stolenCards);
    }

    private boolean isValidCardNumber(String number) {
        return number != null && number.matches("\\d{16}");
    }

    private boolean isValidLuhn(String number) {
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(number.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }
}
