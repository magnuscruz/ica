package br.gov.dataprev.sibe.batch.importbatim.criterios.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.gov.dataprev.sibe.batch.importbatim.criterios.AbstractCriterio;
import br.gov.dataprev.sibe.batch.importbatim.to.BeneficioTO;
import br.gov.dataprev.sibe.batch.importbatim.to.RemuneracaoTO;
import br.gov.dataprev.sibe.batch.importbatim.to.VinculoTO;

/**
 * Crit�rio de Prescri��o em 5 Anos: s� devem se consideradas as remunera��es
 * cujas compet�ncias estejam dentro dos �ltimos 60 meses contados do m�s
 * anterior ao da data de batimento, inclusive, para tr�s - por lei, os
 * anteriores a esse prazo est�o prescritos, n�o havendo mais possibilidade de
 * cobran�a.
 * 
 * @author magnus
 *
 */
public class CriterioPrescricao5Anos extends AbstractCriterio {

	/**
	 * A data 60 meses atras.
	 */
	private Date data60MesesAtras;

	public CriterioPrescricao5Anos() {
		// Criando uma data 60 meses atras
		Calendar hoje = Calendar.getInstance();
		hoje.add(Calendar.MONTH, -60);
		data60MesesAtras = hoje.getTime();
		//Engloba tambem o Crit�rio de Licitude das Remunera��es
		setCriterioAssociado(new CriterioLicitudeRemuneracoes());
	}

	@Override
	public boolean aplicarCriterioEspecifico(VinculoTO vinculoTO,
			BeneficioTO beneficioTO, List<RemuneracaoTO> remuneracoes) {
		boolean criterio5Anos = false;
		setListaRemuneracoes(new ArrayList<RemuneracaoTO>());
		for (RemuneracaoTO remuneracaoTO : remuneracoes) {
			if (remuneracaoTO.getDataCompetencia().compareTo(
					beneficioTO.getDibDate()) > 0
					&& remuneracaoTO.getDataCompetencia().compareTo(
							data60MesesAtras) > 0) {
				criterio5Anos = true;
				getListaRemuneracoes().add(remuneracaoTO);
			}
		}
		return criterio5Anos;
	}
}
