package br.gov.dataprev.sibe.batch.importbatim.criterios.impl;

import java.util.ArrayList;
import java.util.List;

import br.gov.dataprev.sibe.batch.importbatim.criterios.AbstractCriterio;
import br.gov.dataprev.sibe.batch.importbatim.to.BeneficioTO;
import br.gov.dataprev.sibe.batch.importbatim.to.RemuneracaoTO;
import br.gov.dataprev.sibe.batch.importbatim.to.VinculoTO;

/**
 * Critério de Licitude das Remunerações: desprezar as remunerações cujas
 * competências sejam anteriores à DIB, porque só interessam as auferidas no
 * período do benefício. Observação: essa situação já será contemplada de
 * imediato, já que só serão aplicados os critérios em situações onde exista
 * vínculo juntamente com benefício.
 * 
 * @author magnus
 *
 */
public class CriterioLicitudeRemuneracoes extends AbstractCriterio {

	@Override
	public boolean aplicarCriterioEspecifico(VinculoTO vinculoTO,
			BeneficioTO beneficioTO, List<RemuneracaoTO> remuneracoes) {
		boolean criterio = false;
		setListaRemuneracoes(new ArrayList<RemuneracaoTO>());
		for (RemuneracaoTO remuneracaoTO : remuneracoes) {
			if (remuneracaoTO.getDataCompetencia().compareTo(
					beneficioTO.getDibDate()) > 0) {
				criterio = true;
				getListaRemuneracoes().add(remuneracaoTO);
			}
		}
		return criterio;
	}
}
