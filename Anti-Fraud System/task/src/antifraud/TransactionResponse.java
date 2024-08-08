package antifraud;

// Model class to represent the transaction response
public class TransactionResponse {
    private String result;

    // Constructor to initialize the result field
    public TransactionResponse(String result) {
        this.result = result;
    }

    // Getter for the result field
    public String getResult() {
        return result;
    }

    // Setter for the result field
    public void setResult(String result) {
        this.result = result;
    }
}
