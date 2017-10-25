package br.gov.dataprev.sibe.batch.importbatim.criterios.impl;

public enum CriterioEnum {
	LICITUDE_REMUNERACOES(1,"Crit�rio de Licitude das Remunera��es"),
	MULTIPLA_ATIVIDADE(2,"Crit�rio de m�ltipla atividade"),
	PAGAMENTO_RESIDUO(3,"Crit�rio de Pagamento de Res�duo"),
	PRESCRICAO_5ANOS(4,"Crit�rio de Prescri��o em 5 Anos"),
	SALARIO_MATERNIDADE(5,"Crit�rio de Sal�rio maternidade");	

	private Integer codigo;
	private String descricao;
	
	CriterioEnum(Integer codigo, String descricao) {
		this.setCodigo(codigo);
		this.setDescricao(descricao);
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
}
