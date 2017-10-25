package br.gov.dataprev.sibe.batch.importbatim.criterios;

import java.util.List;
import java.util.Properties;

import br.gov.dataprev.sibe.batch.importbatim.processor.ImportacaoBatimentoProcessor;
import br.gov.dataprev.sibe.batch.importbatim.to.BeneficioTO;
import br.gov.dataprev.sibe.batch.importbatim.to.RemuneracaoTO;
import br.gov.dataprev.sibe.batch.importbatim.to.VinculoTO;
import br.gov.dataprev.sibe.batch.importbatim.utils.CachedConfigurationManager;

public abstract class AbstractCriterio implements Criterio {

	private List<RemuneracaoTO> listaRemuneracoes;
	private VinculoTO vinculo;
	private AbstractCriterio criterioAssociado;
	private Properties batchConfig;

	public AbstractCriterio() {
		setBatchConfig(CachedConfigurationManager.getInstance()
				.getProperties(
						ImportacaoBatimentoProcessor.BATCH_CONFIG_PROPERTIES));
	}

	public List<RemuneracaoTO> getListaRemuneracoes() {
		return listaRemuneracoes;
	}

	public void setListaRemuneracoes(List<RemuneracaoTO> listaRemuneracoes) {
		this.listaRemuneracoes = listaRemuneracoes;
	}

	public VinculoTO getVinculo() {
		return vinculo;
	}

	public void setVinculo(VinculoTO vinculo) {
		this.vinculo = vinculo;
	}

	public AbstractCriterio getCriterioAssociado() {
		return criterioAssociado;
	}

	public void setCriterioAssociado(AbstractCriterio criterioAssociado) {
		this.criterioAssociado = criterioAssociado;
	}

	@Override
	public boolean aplicarCriterio(VinculoTO vinculoTO,
			BeneficioTO beneficioTO, List<RemuneracaoTO> remuneracoes) {
		boolean criterio = (criterioAssociado != null ? criterioAssociado
				.aplicarCriterioEspecifico(vinculoTO, beneficioTO, remuneracoes)
				: true);
		return criterio
				&& this.aplicarCriterioEspecifico(
						vinculoTO,
						beneficioTO,
						criterioAssociado != null ? criterioAssociado
								.getListaRemuneracoes() : remuneracoes);
	}

	protected abstract boolean aplicarCriterioEspecifico(VinculoTO vinculoTO,
			BeneficioTO beneficioTO, List<RemuneracaoTO> remuneracoes);

	public Properties getBatchConfig() {
		return batchConfig;
	}

	public void setBatchConfig(Properties batchConfig) {
		this.batchConfig = batchConfig;
	}
}
