package br.gov.dataprev.sibe.batch.importbatim.to;

import java.io.Serializable;

import br.gov.dataprev.infra.batch.reader.LineNumberEntity;

/**
 * Mapeia as colunas HEADER do arquivo.
 * 
 * @author DATAPREV/DIT/DEAT
 */
public class BatimentoHeaderTO  extends LineNumberEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5142338687520592381L;
	private String id;
	private String dataSolicitacao;
	private String orientacaoLimite;
	private String parteVariavel;
	private String tamanhoInformacao;
	private String tipoDispo;
	private String totalRegistros;

	public String getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(String dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public String getOrientacaoLimite() {
		return orientacaoLimite;
	}

	public void setOrientacaoLimite(String orientacaoLimite) {
		this.orientacaoLimite = orientacaoLimite;
	}

	public String getParteVariavel() {
		return parteVariavel;
	}

	public void setParteVariavel(String parteVariavel) {
		this.parteVariavel = parteVariavel;
	}

	public String getTamanhoInformacao() {
		return tamanhoInformacao;
	}

	public void setTamanhoInformacao(String tamanhoInformacao) {
		this.tamanhoInformacao = tamanhoInformacao;
	}

	public String getTipoDispo() {
		return tipoDispo;
	}

	public void setTipoDispo(String tipoDispo) {
		this.tipoDispo = tipoDispo;
	}

	public String getTotalRegistros() {
		return totalRegistros;
	}

	public void setTotalRegistros(String totalRegistros) {
		this.totalRegistros = totalRegistros;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
