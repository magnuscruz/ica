package br.gov.dataprev.sibe.batch.importbatim.criterios.impl;

import java.util.ArrayList;
import java.util.List;

import br.gov.dataprev.sibe.batch.importbatim.criterios.AbstractCriterio;
import br.gov.dataprev.sibe.batch.importbatim.to.BeneficioTO;
import br.gov.dataprev.sibe.batch.importbatim.to.RemuneracaoTO;
import br.gov.dataprev.sibe.batch.importbatim.to.VinculoTO;

/**
 * Crit�rio de Licitude das Remunera��es: desprezar as remunera��es cujas
 * compet�ncias sejam anteriores � DIB, porque s� interessam as auferidas no
 * per�odo do benef�cio. Observa��o: essa situa��o j� ser� contemplada de
 * imediato, j� que s� ser�o aplicados os crit�rios em situa��es onde exista
 * v�nculo juntamente com benef�cio.
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
