package edu.uclm.esi.iso2.multas.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table
public class Sanction {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column
	private double amount;
	@Column
	private int points;
	@Column
	private Date dateOfReception;
	@Column
	private Date dateOfPayment;
	@OneToOne
	private SanctionHolder sanctionHolder;
	
	public Sanction() {
		this.dateOfReception=new Date(System.currentTimeMillis());
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public Date getDateOfReception() {
		return dateOfReception;
	}

	public void setDateOfReception(Date dateOfReception) {
		this.dateOfReception = dateOfReception;
	}

	public Date getDateOfPayment() {
		return dateOfPayment;
	}

	public void setDateOfPayment(Date dateOfPayment) {
		this.dateOfPayment = dateOfPayment;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setSanctionHolder(SanctionHolder sanctionHolder) {
		this.sanctionHolder = sanctionHolder;
	}
	
	public SanctionHolder getSanctionHolder() {
		return sanctionHolder;
	}

	public void pay() {
		this.dateOfPayment=new Date(System.currentTimeMillis());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		Sanction other = (Sanction) obj;
		if (id != other.id)
			return false;
		return true;
	}

	
}
