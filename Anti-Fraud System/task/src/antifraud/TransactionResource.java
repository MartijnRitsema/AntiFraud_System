package antifraud;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/antifraud")
public class TransactionResource {

    @PostMapping("/transaction")
    public ResponseEntity<TransactionDto> performTransaction(@RequestBody TransactionData transaction) {
        if (transaction == null || transaction.getAmount() == null || transaction.getAmount() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        TransactionDto transactionResult = new TransactionDto();
        transactionResult.setResult(TransactionStatus.ALLOWED.toString());

        if (needsManualProcessing(transaction)) {
            transactionResult.setResult(TransactionStatus.MANUAL_PROCESSING.toString());
        }
        if (isProhibited(transaction)) {
            transactionResult.setResult(TransactionStatus.PROHIBITED.toString());
        }

        return ResponseEntity.ok(transactionResult);
    }

    private boolean needsManualProcessing(TransactionData transaction) {
        return transaction.getAmount() > 200 && transaction.getAmount() <= 1500;
    }

    private static boolean isProhibited(TransactionData transaction) {
        return transaction.getAmount() > 1500;
    }
}