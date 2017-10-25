package br.gov.dataprev.sibe.batch.importbatim.to;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class VinculoTO {

	private Integer id;
	private String inicioVinculo;
	private String fimVinculo;
	private Integer numeroRemuneracoes;
	private String dataUltimaRemuneracao;
	private Integer classificadorEmpregador;
	private Integer tipoContribuinte;
	private Integer origemVinculo;
	private Integer especieBeneficio;
	private Integer situacaoBeneficio;
	private String identificadorEmpregador;
	private String nomeEmpregador;
	private String cepEmpregador;
	private String municipioEmpregador;
	private String bairroEmpregador;
	private String logradouroEmpregador;
	private String ufEmpregador;
	private List<RemuneracaoTO> remuneracoes;



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getNumeroRemuneracoes() {
		return numeroRemuneracoes;
	}

	public void setNumeroRemuneracoes(Integer numeroRemuneracoes) {
		this.numeroRemuneracoes = numeroRemuneracoes;
	}

	public Integer getTipoContribuinte() {
		return tipoContribuinte;
	}

	public void setTipoContribuinte(Integer tipoContribuinte) {
		this.tipoContribuinte = tipoContribuinte;
	}

	public Integer getOrigemVinculo() {
		return origemVinculo;
	}

	public void setOrigemVinculo(Integer origemVinculo) {
		this.origemVinculo = origemVinculo;
	}

	public Integer getEspecieBeneficio() {
		return especieBeneficio;
	}

	public void setEspecieBeneficio(Integer especieBeneficio) {
		this.especieBeneficio = especieBeneficio;
	}

	public Integer getSituacaoBeneficio() {
		return situacaoBeneficio;
	}

	public void setSituacaoBeneficio(Integer situacaoBeneficio) {
		this.situacaoBeneficio = situacaoBeneficio;
	}

	public String getIdentificadorEmpregador() {
		return identificadorEmpregador;
	}

	public void setIdentificadorEmpregador(String identificadorEmpregador) {
		this.identificadorEmpregador = identificadorEmpregador;
	}

	public String getNomeEmpregador() {
		return nomeEmpregador;
	}

	public void setNomeEmpregador(String nomeEmpregador) {
		this.nomeEmpregador = nomeEmpregador;
	}

	public String getCepEmpregador() {
		return cepEmpregador;
	}

	public void setCepEmpregador(String cepEmpregador) {
		this.cepEmpregador = cepEmpregador;
	}

	public String getMunicipioEmpregador() {
		return municipioEmpregador;
	}

	public void setMunicipioEmpregador(String municipioEmpregador) {
		this.municipioEmpregador = municipioEmpregador;
	}

	public String getBairroEmpregador() {
		return bairroEmpregador;
	}

	public void setBairroEmpregador(String bairroEmpregador) {
		this.bairroEmpregador = bairroEmpregador;
	}

	public String getLogradouroEmpregador() {
		return logradouroEmpregador;
	}

	public void setLogradouroEmpregador(String logradouroEmpregador) {
		this.logradouroEmpregador = logradouroEmpregador;
	}

	public String getUfEmpregador() {
		return ufEmpregador;
	}

	public void setUfEmpregador(String ufEmpregador) {
		this.ufEmpregador = ufEmpregador;
	}

	public List<RemuneracaoTO> getRemuneracoes() {
		return remuneracoes;
	}

	public void setRemuneracoes(List<RemuneracaoTO> remuneracoes) {
		this.remuneracoes = remuneracoes;
	}

	public String getInicioVinculo() {
		return inicioVinculo;
	}

	public void setInicioVinculo(String inicioVinculo) {
		this.inicioVinculo = inicioVinculo;
		
	}

	public String getFimVinculo() {
		return fimVinculo;
	}

	public void setFimVinculo(String fimVinculo) {
		this.fimVinculo = fimVinculo;
	}

	public String getDataUltimaRemuneracao() {
		return dataUltimaRemuneracao;
	}

	public void setDataUltimaRemuneracao(String dataUltimaRemuneracao) {
		this.dataUltimaRemuneracao = dataUltimaRemuneracao;
	}

	public Integer getClassificadorEmpregador() {
		return classificadorEmpregador;
	}

	public void setClassificadorEmpregador(Integer classificadorEmpregador) {
		this.classificadorEmpregador = classificadorEmpregador;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((identificadorEmpregador == null) ? 0
						: identificadorEmpregador.hashCode());
		result = prime * result
				+ ((inicioVinculo == null) ? 0 : inicioVinculo.hashCode());
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
		VinculoTO other = (VinculoTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (identificadorEmpregador == null) {
			if (other.identificadorEmpregador != null)
				return false;
		} else if (!identificadorEmpregador
				.equals(other.identificadorEmpregador))
			return false;
		if (inicioVinculo == null) {
			if (other.inicioVinculo != null)
				return false;
		} else if (!inicioVinculo.equals(other.inicioVinculo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
