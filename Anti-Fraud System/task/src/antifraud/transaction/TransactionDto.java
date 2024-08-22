package antifraud.transaction;

public class TransactionDto {
    private String result;
    private String info;

    public TransactionDto(String result, String info) {
        this.result = result;
        this.info = info;
    }

    // Getters and setters
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
