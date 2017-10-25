package br.gov.dataprev.sibe.batch.importbatim.criterios.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import br.gov.dataprev.sibe.batch.importbatim.criterios.AbstractCriterio;
import br.gov.dataprev.sibe.batch.importbatim.to.BeneficioTO;
import br.gov.dataprev.sibe.batch.importbatim.to.RemuneracaoTO;
import br.gov.dataprev.sibe.batch.importbatim.to.VinculoTO;

/**
 * Crit�rio de Pagamento de Res�duo: s�o desprezadas as remunera��es que
 * representem apenas at� duas compet�ncias consecutivas e que tenham ocorrido
 * na compet�ncia da DIB ou na primeira compet�ncia subsequente, porque s�o, em
 * geral, pagamentos de res�duos feitos pelo empregador referente a compet�ncias
 * anteriores (per�odo laboral), como diferen�as de diss�dio coletivo, horas
 * extras, comiss�es e assemelhados ou, ainda, resultado de decis�o judicial.
 * Pelo mesmo motivo, s�o desprezadas tamb�m as remunera��es que representem at�
 * o m�ximo de duas compet�ncias consecutivas, independentemente da DIB. Com
 * tr�s ou mais consecutivas, � considerado "suspeito" e deve ser enviado para o
 * Monitor Operacional (Observa��o: essa �ltima condi��o torna dispens�vel a
 * primeira j� que � necess�rio pelo menos 3 remunera��es consecutivas
 * independente da DIB).
 * 
 * @author magnus
 *
 */
public class CriterioPagamentoResiduo extends AbstractCriterio {

	private Integer quantidadeMesesDesconsiderar = 2;

	public CriterioPagamentoResiduo() {
		quantidadeMesesDesconsiderar = Integer.valueOf(getBatchConfig().get(
				"batch.criterios.pagamento.residuo.meses.desconsiderar").toString());
		// Engloba tambem o Crit�rio de Prescri��o em 5 Anos
		setCriterioAssociado(new CriterioPrescricao5Anos());
	}

	@Override
	public boolean aplicarCriterioEspecifico(VinculoTO vinculoTO,
			BeneficioTO beneficioTO, List<RemuneracaoTO> remuneracoes) {
		boolean criterioResiduo = false;
		Date dibDate = beneficioTO.getDibDate();
		// Ordena para verificar se existem 2 remunera��es sequenciais
		Collections.sort(remuneracoes, new Comparator<RemuneracaoTO>() {
			@Override
			public int compare(RemuneracaoTO o1, RemuneracaoTO o2) {
				return o1.getDataCompetencia().compareTo(
						o2.getDataCompetencia());
			}
		});
		Date dataAnterior = null;
		int contRemuneracoes = 0;
		// Crit�rio de Pagamento de Res�duo
		for (RemuneracaoTO remuneracaoTO : remuneracoes) {
			Date data = remuneracaoTO.getDataCompetencia();
			if (data.compareTo(dibDate) >= 0) {
				contRemuneracoes++;
				if (dataAnterior == null) {
					dataAnterior = data;
				} else if (data != null) {
					// Verifica se s�o 3 meses posteriores a DIB
					if (DateUtils.addMonths(dataAnterior, 1).equals(data)
							&& contRemuneracoes > quantidadeMesesDesconsiderar) {
						criterioResiduo = true;
						break;
					} else {
						dataAnterior = data;
					}
				}
			}
		}
		return criterioResiduo;
	}
}
