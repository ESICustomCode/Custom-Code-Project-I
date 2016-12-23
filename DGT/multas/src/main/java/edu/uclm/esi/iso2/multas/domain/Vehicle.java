package edu.uclm.esi.iso2.multas.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table
public class Vehicle {
	@Id
	private String license;
	@OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Owner owner;

	public Vehicle() {

	}

	public Vehicle(String license) {
		this();
		this.license = license;
	}
	
	public String getLicense(){
		return license;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}
}
