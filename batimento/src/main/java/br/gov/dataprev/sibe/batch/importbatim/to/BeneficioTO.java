package br.gov.dataprev.sibe.batch.importbatim.to;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.time.DateUtils;

import br.gov.dataprev.sibe.batch.importbatim.processor.ImportacaoBatimentoProcessor;

public class BeneficioTO {

	private String numeroBeneficio;
	private String tipoEmpregador;
	private String identificadorEmpregador;
	private String orgaoLocal;
	private String orgaoPagador;
	private String fonteNit;
	private String grupoEspecies;
	private String especieBeneficio;
	private String dib;
	private String dcb;
	private String ddb;
	private String rendaAtual;
	private String camposFuturos;
	private Date dibDate;

	public String getNumeroBeneficio() {
		return numeroBeneficio;
	}

	public void setNumeroBeneficio(String numeroBeneficio) {
		this.numeroBeneficio = numeroBeneficio;
	}

	public String getTipoEmpregador() {
		return tipoEmpregador;
	}

	public void setTipoEmpregador(String tipoEmpregador) {
		this.tipoEmpregador = tipoEmpregador;
	}

	public String getOrgaoLocal() {
		return orgaoLocal;
	}

	public void setOrgaoLocal(String orgaoLocal) {
		this.orgaoLocal = orgaoLocal;
	}

	public String getOrgaoPagador() {
		return orgaoPagador;
	}

	public void setOrgaoPagador(String orgaoPagador) {
		this.orgaoPagador = orgaoPagador;
	}

	public String getFonteNit() {
		return fonteNit;
	}

	public void setFonteNit(String fonteNit) {
		this.fonteNit = fonteNit;
	}

	public String getGrupoEspecies() {
		return grupoEspecies;
	}

	public void setGrupoEspecies(String grupoEspecies) {
		this.grupoEspecies = grupoEspecies;
	}

	public String getEspecieBeneficio() {
		return especieBeneficio;
	}

	public void setEspecieBeneficio(String especieBeneficio) {
		this.especieBeneficio = especieBeneficio;
	}

	public String getDib() {
		return dib;
	}

	public void setDib(String dib) {
		this.dib = dib;
	}

	public String getDcb() {
		return dcb;
	}

	public void setDcb(String dcb) {
		this.dcb = dcb;
	}

	public String getDdb() {
		return ddb;
	}

	public void setDdb(String ddb) {
		this.ddb = ddb;
	}

	public String getRendaAtual() {
		return rendaAtual;
	}

	public void setRendaAtual(String rendaAtual) {
		this.rendaAtual = rendaAtual;
	}

	public String getCamposFuturos() {
		return camposFuturos;
	}

	public void setCamposFuturos(String camposFuturos) {
		this.camposFuturos = camposFuturos;
	}

	public Date getDibDate() {
		if (dibDate == null) {
			try {
				dibDate = DateUtils.parseDate(dib,
						ImportacaoBatimentoProcessor.DATE_PATTERNS);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return dibDate;
	}

	public Date setDibDate(Date dibDate) {
		if (dibDate != null) {
			dib = new SimpleDateFormat(
					ImportacaoBatimentoProcessor.DATE_PATTERNS[0])
					.format(dibDate);
		}
		return dibDate;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getIdentificadorEmpregador() {
		return identificadorEmpregador;
	}

	public void setIdentificadorEmpregador(String codigoEmpregador) {
		this.identificadorEmpregador = codigoEmpregador;
	}
}
