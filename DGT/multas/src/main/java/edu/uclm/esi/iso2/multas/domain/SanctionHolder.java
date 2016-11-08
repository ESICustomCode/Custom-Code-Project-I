package edu.uclm.esi.iso2.multas.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SanctionHolder {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int id;
	@Column(length=10, nullable=false, unique=true)
	private String dni;
	@Column(length=50, nullable=false)
	private String name;
	@Column(length=50, nullable=false)
	private String lastName;
	@Column(length=255, nullable=false)
	private String fullAddress;
	
	public SanctionHolder() {
		
	}
	
	public SanctionHolder(String dni, String name, String lastName, String fullAddress) {
		super();
		this.dni = dni;
		this.name = name;
		this.lastName = lastName;
		this.fullAddress = fullAddress;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dni == null) ? 0 : dni.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SanctionHolder other = (SanctionHolder) obj;
		if (dni == null) {
			if (other.dni != null)
				return false;
		} else if (!dni.equals(other.dni))
			return false;
		return true;
	}

	
}
