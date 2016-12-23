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
		this.dateOfReception = new Date(System.currentTimeMillis());
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

	public Date getDateOfPayment() {
		return dateOfPayment;
	}

	public int getId() {
		return id;
	}

	public SanctionHolder getSanctionHolder() {
		return sanctionHolder;
	}

	public void setSanctionHolder(SanctionHolder sanctionHolder) {
		this.sanctionHolder = sanctionHolder;
	}

	public void pay() {
		this.dateOfPayment = new Date(System.currentTimeMillis());
	}
}
