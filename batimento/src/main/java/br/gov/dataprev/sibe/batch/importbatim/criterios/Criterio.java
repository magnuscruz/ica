package br.gov.dataprev.sibe.batch.importbatim.criterios;

import java.util.List;

import br.gov.dataprev.sibe.batch.importbatim.to.BeneficioTO;
import br.gov.dataprev.sibe.batch.importbatim.to.RemuneracaoTO;
import br.gov.dataprev.sibe.batch.importbatim.to.VinculoTO;

public interface Criterio {
	
	boolean aplicarCriterio(VinculoTO vinculoTO, BeneficioTO beneficioTO, List<RemuneracaoTO> remuneracoes);

}
