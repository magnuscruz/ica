package br.gov.dataprev.sibe.batch.importbatim.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Mapeia as colunas do arquivo do Batimento.
 * 
 * @author DATAPREV/DIT/DEAT
 */
@Entity
public class Batimento {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	@Column
	private String nit;
	@Column
	private String cpf;
	@Column
	private String nome;
	@Column
	private String nomeMae;
	@Column
	@Temporal(TemporalType.DATE)
	private Date dataNascimento;
	@Column
	private String rg;
	@Column
	private String codigoRetorno;
	@Column
	@Temporal(TemporalType.DATE)
	private Date dataObito;
	@Column
	private String quantidadeVinculos;
	@Column
	private String vinculos;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNit() {
		return nit;
	}

	public void setNit(String nit) {
		this.nit = nit;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public String getCodigoRetorno() {
		return codigoRetorno;
	}

	public void setCodigoRetorno(String codigoRetorno) {
		this.codigoRetorno = codigoRetorno;
	}

	public Date getDataObito() {
		return dataObito;
	}

	public void setDataObito(Date dataObito) {
		this.dataObito = dataObito;
	}

	public String getQuantidadeVinculos() {
		return quantidadeVinculos;
	}

	public void setQuantidadeVinculos(String quantidadeVinculos) {
		this.quantidadeVinculos = quantidadeVinculos;
	}

	public String getVinculos() {
		return vinculos;
	}

	public void setVinculos(String vinculos) {
		this.vinculos = vinculos;
	}

}
