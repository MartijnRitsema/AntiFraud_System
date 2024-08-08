package antifraud;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// Controller class to handle the transaction endpoints
@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {

    // Endpoint to handle POST requests for transaction evaluation
    @PostMapping("/transaction")
    @ResponseStatus(HttpStatus.OK) // Default response status
    public TransactionResponse handleTransaction(@RequestBody TransactionRequest transactionRequest) {
        // Validate the transaction amount
        if (transactionRequest.getAmount() == null || transactionRequest.getAmount() <= 0) {
            // Return error message with HTTP 400 Bad Request status
            throw new InvalidTransactionException();
        }

        long amount = transactionRequest.getAmount();
        String result;

        // Determine the result based on the amount
        if (amount <= 200) {
            result = "ALLOWED";
        } else if (amount <= 1500) {
            result = "MANUAL_PROCESSING";
        } else {
            result = "PROHIBITED";
        }

        // Return the result in the response body
        return new TransactionResponse(result);
    }
}
