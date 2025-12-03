package auca.ac.rw.AgriStock1.services;


import auca.ac.rw.AgriStock1.model.Buyer;
import auca.ac.rw.AgriStock1.model.Village;
import auca.ac.rw.AgriStock1.repositories.BuyerRepository;
import auca.ac.rw.AgriStock1.repositories.VillageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BuyerService {

    private final BuyerRepository buyerRepository;
    private final VillageRepository villageRepository;

    // ==================== CREATE ====================
    public Buyer createBuyer(Buyer buyer) {
        // Validate email uniqueness
        if (buyerRepository.existsByEmail(buyer.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        return buyerRepository.save(buyer);
    }

    // ==================== READ ====================
    public Buyer getBuyerById(Long id) {
        return buyerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buyer not found with id: " + id));
    }

    public List<Buyer> getAllBuyers() {
        return buyerRepository.findAll();
    }

    public Page<Buyer> getAllBuyersPaginated(Pageable pageable) {
        return buyerRepository.findAll(pageable);
    }

    public Buyer getBuyerByEmail(String email) {
        return buyerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Buyer not found with email: " + email));
    }

    // ==================== UPDATE ====================
    public Buyer updateBuyer(Long id, Buyer buyerDetails) {
        Buyer buyer = getBuyerById(id);

        // Update fields
        buyer.setBuyerName(buyerDetails.getBuyerName());
        buyer.setPhone(buyerDetails.getPhone());
        buyer.setBusinessName(buyerDetails.getBusinessName());

        // Update email only if changed and not duplicate
        if (!buyer.getEmail().equals(buyerDetails.getEmail())) {
            if (buyerRepository.existsByEmail(buyerDetails.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            buyer.setEmail(buyerDetails.getEmail());
        }
        return buyerRepository.save(buyer);
    }

    // ==================== DELETE ====================
    public void deleteBuyer(Long id) {
        if (!buyerRepository.existsById(id)) {
            throw new RuntimeException("Buyer not found with id: " + id);
        }
        buyerRepository.deleteById(id);
    }

    // Search buyers by name
    public List<Buyer> searchBuyersByName(String searchTerm) {
        return buyerRepository.findByBuyerNameContainingIgnoreCase(searchTerm);
    }

    // Search buyers by business name
    public List<Buyer> searchBuyersByBusinessName(String businessName) {
        return buyerRepository.findByBusinessNameContainingIgnoreCase(businessName);
    }

    // Check if email exists
    public boolean emailExists(String email) {
        return buyerRepository.existsByEmail(email);
    }
}
