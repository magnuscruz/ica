package br.gov.dataprev.sibe.batch.importbatim.to;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class RemuneracaoTO {

	private Integer idVinculo;
	private Date dataCompetencia;
	private String fonteInformacao;
	private Double remuneracao;

	public Integer getIdVinculo() {
		return idVinculo;
	}

	public void setIdVinculo(Integer idVinculo) {
		this.idVinculo = idVinculo;
	}

	public Date getDataCompetencia() {
		return dataCompetencia;
	}

	public void setDataCompetencia(Date dataCompetencia) {
		this.dataCompetencia = dataCompetencia;
	}

	public String getFonteInformacao() {
		return fonteInformacao;
	}

	public void setFonteInformacao(String fonteInformacao) {
		this.fonteInformacao = fonteInformacao;
	}

	public Double getRemuneracao() {
		return remuneracao;
	}

	public void setRemuneracao(Double remuneracao) {
		this.remuneracao = remuneracao;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
