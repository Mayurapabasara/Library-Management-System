package com.mayura.librarymanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

	
	package com.mayura.SimpleWebApp.controller;

	import java.util.List;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.web.bind.annotation.*;

	import com.mayura.SimpleWebApp.model.Product;
	import com.mayura.SimpleWebApp.repository.ProductRepository;
	import com.mayura.SimpleWebApp.service.ProductService;

	@RestController
	@RequestMapping("/api/products")
	@CrossOrigin(origins="http://localhost:5222")
	public class ProductController {

	    @Autowired
	    private ProductRepository repo;
	    
	    @Autowired
	    private ProductService service;

	    @GetMapping
	    public List<Product> getAllProducts() {
	        return repo.findAll();
	    }

	    @GetMapping("/{id}")
	    public Product getProduct(@PathVariable Long id) {
	        return service.getProduct(id); // connect to the service user -> controlller -> service -> repository -> databse
	    }
	    

	    @PostMapping
	    public Product addProduct(@RequestBody Product product) {
	    	product.setProdId(null);
	        return repo.save(product);
	    }

	    @PutMapping("/{id}")
	    public Product updateProduct(@PathVariable Long id, @RequestBody Product updated) {
	        Product existing = repo.findById(id).orElse(null);
	        if (existing != null) {
	            existing.setProdName(updated.getProdName());
	            existing.setPrice(updated.getPrice());
	            return repo.save(existing);
	        }
	        return null;
	    }

	    @DeleteMapping("/products/   {id}")
	    public void deleteProduct(@PathVariable Long id) {
	        repo.deleteById(id);
	    }
	}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String paymentType; // e.g. "Fine", "Membership", "BookPurchase"

    @Column(nullable = false)
    private String paymentMethod; // e.g. "Cash", "Card", "Online"

    private String referenceNumber; // for card/online payments

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    private String description;

    // relationships
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // optional: track which librarian processed the payment
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff processedBy;

    // Constructors
    public Payment() {}

    public Payment(Double amount, String paymentType, String paymentMethod, String referenceNumber,
                   String description, Member member, Staff processedBy) {
        this.amount = amount;
        this.paymentType = paymentType;
        this.paymentMethod = paymentMethod;
        this.referenceNumber = referenceNumber;
        this.description = description;
        this.member = member;
        this.processedBy = processedBy;
        this.paymentDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Staff getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(Staff processedBy) {
        this.processedBy = processedBy;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", amount=" + amount +
                ", paymentType='" + paymentType + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentDate=" + paymentDate +
                ", member=" + (member != null ? member.getMemberId() : "N/A") +
                ", processedBy=" + (processedBy != null ? processedBy.getStaffId() : "N/A") +
                '}';
    }
}
