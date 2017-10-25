package br.gov.dataprev.sibe.batch.importbatim.criterios.impl;

public enum CriterioEnum {
	LICITUDE_REMUNERACOES(1,"Critério de Licitude das Remunerações"),
	MULTIPLA_ATIVIDADE(2,"Critério de múltipla atividade"),
	PAGAMENTO_RESIDUO(3,"Critério de Pagamento de Resíduo"),
	PRESCRICAO_5ANOS(4,"Critério de Prescrição em 5 Anos"),
	SALARIO_MATERNIDADE(5,"Critério de Salário maternidade");	

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
