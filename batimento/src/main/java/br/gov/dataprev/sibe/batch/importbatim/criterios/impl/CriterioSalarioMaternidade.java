package br.gov.dataprev.sibe.batch.importbatim.criterios.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import br.gov.dataprev.sibe.batch.importbatim.criterios.AbstractCriterio;
import br.gov.dataprev.sibe.batch.importbatim.to.BeneficioTO;
import br.gov.dataprev.sibe.batch.importbatim.to.RemuneracaoTO;
import br.gov.dataprev.sibe.batch.importbatim.to.VinculoTO;

/**
 * Para casos da Espécie 80 - Salário maternidade, são IRREGULARES (1) e devem
 * ser incluídas no arquivo / lista para fornecimento à CMOBEN aqueles que
 * tiverem, para QUALQUER vínculo empregatício, remuneração referente a mês
 * INTEIRO cuja competência (mês de referência) mm/aaaa esteja compreendida
 * entre a DIB e a DCB (isto é, dentro do período do benefício). Lembrando que
 * um benefício de Salário Maternidade sempre dura quatro meses, entende-se por
 * mês INTEIRO aquele que é coberto pelo período do benefício desde o seu dia 01
 * até o seu último dia (28, 29, 30 ou 31, conforme o mês); então, só podem
 * ocorrer meses NÃO INTEIROS no primeiro e no último (4o.) mês do benefício; o
 * segundo e terceiro meses de benefício sempre serão INTEIROS; O primeiro mês
 * de benefício pode não ser INTEIRO porque a DIB pode ocorrer depois do dia 01
 * e, nesse caso, haverá uma remuneração parcial VÁLIDA / REGULAR referente ao
 * período efetivamente trabalhado entre o dia 01 e a DIB; mas se a DIB cai no
 * dia 01, trata-se de mês INTEIRO e então não pode haver remuneração para a
 * competência desse primeiro mês de benefício; O último mês de benefício pode
 * não ser INTEIRO porque a DCB e o retorno ao trabalho podem ocorrer antes do
 * último dia do mês e, nesse caso, haverá uma remuneração parcial VÁLIDA /
 * REGULAR referente ao período efetivamente trabalhado entre a DCB e o último
 * dia do mês; mas se a DCB cai no dia último dia do mês, trata-se de mês
 * INTEIRO e então não pode haver remuneração para a competência desse último
 * mês de benefício.
 * 
 * @author magnus
 *
 */
public class CriterioSalarioMaternidade extends AbstractCriterio {

	private static final String BATCH_CRITERIOS_ESPECIE = "batch.criterios.especie.salario.maternidade";
	private ArrayList<String> especies;

	public CriterioSalarioMaternidade() {
		// Engloba tambem o Criterio de Prescrição em 5 Anos
		setCriterioAssociado(new CriterioPagamentoResiduo());
		String codigosEspecies = (String) getBatchConfig()
				.get(BATCH_CRITERIOS_ESPECIE);
		if (StringUtils.isBlank(codigosEspecies)) {
			throw new RuntimeException(
					"É obrigatório a presença de uma da propriedade 'batch.criterios.especies.auxilio.doenca.acidente' no arquivo de configuração.");
		}
		String[] strings = codigosEspecies.split(",");
		if (strings.length == 0) {
			throw new RuntimeException(
					"É obrigatório a presença de uma da propriedade 'batch.criterios.especies.auxilio.doenca.acidente' no arquivo de configuração.");
		}
		especies = new ArrayList<String>();
		for (String especie : strings) {
			especies.add(especie);
		}
	}

	@Override
	public boolean aplicarCriterioEspecifico(VinculoTO vinculoTO,
			BeneficioTO beneficioTO, List<RemuneracaoTO> remuneracoes) {
		boolean criterio = false;
		String especieBeneficio = beneficioTO.getEspecieBeneficio();
		// Se não é Salario Maternidade
		if (!especies.contains(especieBeneficio)) {
			return criterio;
		}		
		
		Date dibDate = beneficioTO.getDibDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dibDate);
		int diaMesDIB = calendar.get(Calendar.DAY_OF_MONTH);
		Date dataInicioConsiderar = calendar.getTime();
		Date dataFimConsiderar = calendar.getTime();
		/*
		 * Lembrando que um benefício de Salário Maternidade sempre dura quatro
		 * meses, entende-se por mês INTEIRO aquele que é coberto pelo período
		 * do benefício desde o seu dia 01 até o seu último dia (28, 29, 30 ou
		 * 31, conforme o mês); então, só podem ocorrer meses NÃO INTEIROS no
		 * primeiro e no último (4o.) mês do benefício; o segundo e terceiro
		 * meses de benefício sempre serão INTEIROS;
		 */
		if (diaMesDIB!=1) {
			//Posiciona a data para o primeiro dia do mes seguinte
			dataInicioConsiderar = DateUtils.addMonths(dataInicioConsiderar, 1);
			dataInicioConsiderar = DateUtils.setDays(dataInicioConsiderar, 1);
			//Posiciona a data para o primeiro dia do terceiro mes subsequente
			dataFimConsiderar = DateUtils.addMonths(dataFimConsiderar, 3);
			dataFimConsiderar = DateUtils.setDays(dataFimConsiderar, 1);
		} else {
			//Posiciona a data para o primeiro dia do terceiro mes subsequente
			dataFimConsiderar = DateUtils.addMonths(dataFimConsiderar, 3);
		}

		// Ordena para verificar se existem 2 remunerações sequenciais
		Collections.sort(remuneracoes, new Comparator<RemuneracaoTO>() {
			@Override
			public int compare(RemuneracaoTO o1, RemuneracaoTO o2) {
				return o1.getDataCompetencia().compareTo(
						o2.getDataCompetencia());
			}
		});
		// Critério de somente considera mes cheio
		for (RemuneracaoTO remuneracaoTO : remuneracoes) {
			Date data = remuneracaoTO.getDataCompetencia();
			if (data.compareTo(dataInicioConsiderar)>-1&&data.compareTo(dataFimConsiderar)<1) {
				return true;
			}
		}
		return criterio;
	}
}
