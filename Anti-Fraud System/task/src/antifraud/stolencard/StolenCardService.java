package antifraud.stolencard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StolenCardService {

    @Autowired
    private StolenCardRepository stolenCardRepository;

    public StolenCard addStolenCard(String number) {
        if (stolenCardRepository.existsByNumber(number)) {
            return null; // Card already exists
        }
        StolenCard stolenCard = new StolenCard();
        stolenCard.setNumber(number);
        return stolenCardRepository.save(stolenCard);
    }

    public void deleteStolenCard(String number) {
        StolenCard stolenCard = stolenCardRepository.findByNumber(number);
        if (stolenCard != null) {
            stolenCardRepository.delete(stolenCard);
        }
    }

    // New method to check if a card exists
    public boolean exists(String number) {
        return stolenCardRepository.existsByNumber(number);
    }

    // Add this method to get all stolen cards
    public List<StolenCard> getAll() {
        return (List<StolenCard>) stolenCardRepository.findAll();
    }
}
