package edu.miu.cs.cs544.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String name; 
	
	private String description;
	
	private String excerpt;

	@Enumerated(EnumType.STRING)
	private ProductType type;

	private double nightlyRate;

	private int maxCapacity;

	@Embedded
	private AuditData auditData;
	
}