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
 * Critério de Pagamento de Resíduo: são desprezadas as remunerações que
 * representem apenas até duas competências consecutivas e que tenham ocorrido
 * na competência da DIB ou na primeira competência subsequente, porque são, em
 * geral, pagamentos de resíduos feitos pelo empregador referente a competências
 * anteriores (período laboral), como diferenças de dissídio coletivo, horas
 * extras, comissões e assemelhados ou, ainda, resultado de decisão judicial.
 * Pelo mesmo motivo, são desprezadas também as remunerações que representem até
 * o máximo de duas competências consecutivas, independentemente da DIB. Com
 * três ou mais consecutivas, é considerado "suspeito" e deve ser enviado para o
 * Monitor Operacional (Observação: essa última condição torna dispensável a
 * primeira já que é necessário pelo menos 3 remunerações consecutivas
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
		// Engloba tambem o Critério de Prescrição em 5 Anos
		setCriterioAssociado(new CriterioPrescricao5Anos());
	}

	@Override
	public boolean aplicarCriterioEspecifico(VinculoTO vinculoTO,
			BeneficioTO beneficioTO, List<RemuneracaoTO> remuneracoes) {
		boolean criterioResiduo = false;
		Date dibDate = beneficioTO.getDibDate();
		// Ordena para verificar se existem 2 remunerações sequenciais
		Collections.sort(remuneracoes, new Comparator<RemuneracaoTO>() {
			@Override
			public int compare(RemuneracaoTO o1, RemuneracaoTO o2) {
				return o1.getDataCompetencia().compareTo(
						o2.getDataCompetencia());
			}
		});
		Date dataAnterior = null;
		int contRemuneracoes = 0;
		// Critério de Pagamento de Resíduo
		for (RemuneracaoTO remuneracaoTO : remuneracoes) {
			Date data = remuneracaoTO.getDataCompetencia();
			if (data.compareTo(dibDate) >= 0) {
				contRemuneracoes++;
				if (dataAnterior == null) {
					dataAnterior = data;
				} else if (data != null) {
					// Verifica se são 3 meses posteriores a DIB
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
