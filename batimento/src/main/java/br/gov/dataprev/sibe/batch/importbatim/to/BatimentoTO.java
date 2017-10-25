package br.gov.dataprev.sibe.batch.importbatim.to;

import java.io.Serializable;

import br.gov.dataprev.infra.batch.reader.LineNumberEntity;

/**
 * Mapeia as colunas do arquivo do Batimento.
 * 
 * @author DATAPREV/DIT/DEAT
 */
public class BatimentoTO extends LineNumberEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5488833013507674713L;
	private String nit;
	private String cpf;
	private String nome;
	private String nomeMae;
	private String dataNascimento;
	private String rg;
	private String ctps;
	private String tituloEleitor;
	private String cnh;
	private String docEstrageiro;
	private String carteiraMaritimo;
	private String passaporte;
	private String sexo;
	private String estadoCivil;
	private String tipoCertidao;
	private String folhaCertidao;
	private String livroCertidao;
	private String termoCertidao;
	private String dataLimite;
	private String codigoRetorno;
	private String cpfCnis;
	private String nomeCpfCnis;
	private String dataNascCnis;
	private String totalTrabalhadoresCnis;
	private String validacaoCpfReceita;
	private String dataObito;
	private String quantidadeVinculos;
	private String vinculos;
	private String remuneracoes;

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

	public String getRemuneracoes() {
		return remuneracoes;
	}

	public void setRemuneracoes(String remuneracoes) {
		this.remuneracoes = remuneracoes;
	}

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

	public String getCamposFuturos() {
		return camposFuturos;
	}

	public void setCamposFuturos(String camposFuturos) {
		this.camposFuturos = camposFuturos;
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

	public String getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public String getCtps() {
		return ctps;
	}

	public void setCtps(String ctps) {
		this.ctps = ctps;
	}

	public String getTituloEleitor() {
		return tituloEleitor;
	}

	public void setTituloEleitor(String tituloEleitor) {
		this.tituloEleitor = tituloEleitor;
	}

	public String getCnh() {
		return cnh;
	}

	public void setCnh(String cnh) {
		this.cnh = cnh;
	}

	public String getDocEstrageiro() {
		return docEstrageiro;
	}

	public void setDocEstrageiro(String docEstrageiro) {
		this.docEstrageiro = docEstrageiro;
	}

	public String getCarteiraMaritimo() {
		return carteiraMaritimo;
	}

	public void setCarteiraMaritimo(String carteiraMaritimo) {
		this.carteiraMaritimo = carteiraMaritimo;
	}

	public String getPassaporte() {
		return passaporte;
	}

	public void setPassaporte(String passaporte) {
		this.passaporte = passaporte;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(String estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public String getTipoCertidao() {
		return tipoCertidao;
	}

	public void setTipoCertidao(String tipoCertidao) {
		this.tipoCertidao = tipoCertidao;
	}

	public String getFolhaCertidao() {
		return folhaCertidao;
	}

	public void setFolhaCertidao(String folhaCertidao) {
		this.folhaCertidao = folhaCertidao;
	}

	public String getLivroCertidao() {
		return livroCertidao;
	}

	public void setLivroCertidao(String livroCertidao) {
		this.livroCertidao = livroCertidao;
	}

	public String getTermoCertidao() {
		return termoCertidao;
	}

	public void setTermoCertidao(String termoCertidao) {
		this.termoCertidao = termoCertidao;
	}

	public String getDataLimite() {
		return dataLimite;
	}

	public void setDataLimite(String dataLimite) {
		this.dataLimite = dataLimite;
	}

	public String getCodigoRetorno() {
		return codigoRetorno;
	}

	public void setCodigoRetorno(String codigoRetorno) {
		this.codigoRetorno = codigoRetorno;
	}

	public String getCpfCnis() {
		return cpfCnis;
	}

	public void setCpfCnis(String cpfCnis) {
		this.cpfCnis = cpfCnis;
	}

	public String getNomeCpfCnis() {
		return nomeCpfCnis;
	}

	public void setNomeCpfCnis(String nomeCpfCnis) {
		this.nomeCpfCnis = nomeCpfCnis;
	}

	public String getDataNascCnis() {
		return dataNascCnis;
	}

	public void setDataNascCnis(String dataNascCnis) {
		this.dataNascCnis = dataNascCnis;
	}

	public String getTotalTrabalhadoresCnis() {
		return totalTrabalhadoresCnis;
	}

	public void setTotalTrabalhadoresCnis(String totalTrabalhadoresCnis) {
		this.totalTrabalhadoresCnis = totalTrabalhadoresCnis;
	}

	public String getValidacaoCpfReceita() {
		return validacaoCpfReceita;
	}

	public void setValidacaoCpfReceita(String validacaoCpfReceita) {
		this.validacaoCpfReceita = validacaoCpfReceita;
	}

	public String getDataObito() {
		return dataObito;
	}

	public void setDataObito(String dataObito) {
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

	public String getRendaAtual() {
		return rendaAtual;
	}

	public void setRendaAtual(String rendaAtual) {
		this.rendaAtual = rendaAtual;
	}

	public String getDdb() {
		return ddb;
	}

	public void setDdb(String ddb) {
		this.ddb = ddb;
	}

	public String getIdentificadorEmpregador() {
		return identificadorEmpregador;
	}

	public void setIdentificadorEmpregador(String identificadorEmpregador) {
		this.identificadorEmpregador = identificadorEmpregador;
	}

}
