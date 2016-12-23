package edu.uclm.esi.iso2.multas.domain;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import edu.uclm.esi.iso2.multas.dao.DriverDao;
import edu.uclm.esi.iso2.multas.dao.GeneralDao;
import edu.uclm.esi.iso2.multas.dao.OwnerDao;

@Entity
@Table
public class Inquiry {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(nullable = false, updatable = false)
	private Date dateOfIssue;
	@Column(nullable = false, updatable = false)
	private String location;
	@ManyToOne(targetEntity = Owner.class)
	private Owner owner;
	@Column(nullable = false, updatable = false)
	private double speed;
	@Column(nullable = false, updatable = false)
	private double maxSpeed;
	@OneToOne(fetch = FetchType.LAZY, targetEntity = Sanction.class, cascade = { CascadeType.PERSIST,
			CascadeType.REMOVE })
	private Sanction sanction;

	public Inquiry() {

	}

	public Inquiry(String license, double speed, String location, double maxSpeed) {
		this();
		this.dateOfIssue = new Date(System.currentTimeMillis());
		this.speed = speed;
		this.maxSpeed = maxSpeed;
		this.location = location;
		this.owner = findOwner(license);
	}

	private Owner findOwner(String license) {
		OwnerDao dao = new OwnerDao();
		return dao.findByLicense(license);
	}

	public Sanction createSanctionFor(String dni) {
		int points = calculatePoints();
		int amount = calculateAmount();
		Sanction sanction = new Sanction();
		DriverDao dao = new DriverDao();
		Driver driver = dao.findByDni(dni);
		sanction.setSanctionHolder(driver);
		sanction.setPoints(points);
		sanction.setAmount(amount);
		GeneralDao<Sanction> daoSanction = new GeneralDao();
		daoSanction.insert(sanction);
		setSanction(sanction);
		GeneralDao<Inquiry> daoInquiry = new GeneralDao();
		daoInquiry.update(this);
		return sanction;
	}

	public int getId() {
		return id;
	}

	public double getSpeed() {
		return speed;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public Sanction getSanction() {
		return sanction;
	}

	public void setSanction(Sanction sanction) {
		this.sanction = sanction;
	}
	
	private int calculatePoints() {
		if (getMaxSpeed() <= 50)
			return calcularPuntos(getMaxSpeed() + 20, getMaxSpeed() + 30);
		else
			return calcularPuntos(getMaxSpeed() + 30, getMaxSpeed() + 50);
	}

	private int calculateAmount() {
		if (getMaxSpeed() <= 50)
			return calcularPrecio(getMaxSpeed() + 20, getMaxSpeed() + 30);
		else
			return calcularPrecio(getMaxSpeed() + 30, getMaxSpeed() + 50);
	}

	private int calcularPuntos(double primerLimiteSuperior, double segundoLimiteSuperior) {
		if (getSpeed() >= (getMaxSpeed() + 1) && getSpeed() <= primerLimiteSuperior)
			return 0;
		else if (getSpeed() >= (primerLimiteSuperior + 1) && getSpeed() <= segundoLimiteSuperior)
			return 2;
		else if (getSpeed() >= (segundoLimiteSuperior + 1) && getSpeed() <= (segundoLimiteSuperior + 10))
			return 4;
		else if (getSpeed() >= (segundoLimiteSuperior + 11))
			return 6;
		else
			return 0;
	}

	private int calcularPrecio(double primerLimiteSuperior, double segundoLimiteSuperior) {
		if (getSpeed() >= (getMaxSpeed() + 1) && getSpeed() <= primerLimiteSuperior)
			return 100;
		else if (getSpeed() >= (primerLimiteSuperior + 1) && getSpeed() <= segundoLimiteSuperior)
			return 300;
		else if (getSpeed() >= (segundoLimiteSuperior + 1) && getSpeed() <= (segundoLimiteSuperior + 10))
			return 400;
		else if (getSpeed() >= (segundoLimiteSuperior + 11) && getSpeed() <= (segundoLimiteSuperior + 20))
			return 500;
		else if (getSpeed() >= (segundoLimiteSuperior + 21))
			return 600;
		else
			return 0;
	}

	
	 
}
