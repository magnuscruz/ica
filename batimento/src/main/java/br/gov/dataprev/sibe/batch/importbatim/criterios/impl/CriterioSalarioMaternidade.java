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
 * Para casos da Esp�cie 80 - Sal�rio maternidade, s�o IRREGULARES (1) e devem
 * ser inclu�das no arquivo / lista para fornecimento � CMOBEN aqueles que
 * tiverem, para QUALQUER v�nculo empregat�cio, remunera��o referente a m�s
 * INTEIRO cuja compet�ncia (m�s de refer�ncia) mm/aaaa esteja compreendida
 * entre a DIB e a DCB (isto �, dentro do per�odo do benef�cio). Lembrando que
 * um benef�cio de Sal�rio Maternidade sempre dura quatro meses, entende-se por
 * m�s INTEIRO aquele que � coberto pelo per�odo do benef�cio desde o seu dia 01
 * at� o seu �ltimo dia (28, 29, 30 ou 31, conforme o m�s); ent�o, s� podem
 * ocorrer meses N�O INTEIROS no primeiro e no �ltimo (4o.) m�s do benef�cio; o
 * segundo e terceiro meses de benef�cio sempre ser�o INTEIROS; O primeiro m�s
 * de benef�cio pode n�o ser INTEIRO porque a DIB pode ocorrer depois do dia 01
 * e, nesse caso, haver� uma remunera��o parcial V�LIDA / REGULAR referente ao
 * per�odo efetivamente trabalhado entre o dia 01 e a DIB; mas se a DIB cai no
 * dia 01, trata-se de m�s INTEIRO e ent�o n�o pode haver remunera��o para a
 * compet�ncia desse primeiro m�s de benef�cio; O �ltimo m�s de benef�cio pode
 * n�o ser INTEIRO porque a DCB e o retorno ao trabalho podem ocorrer antes do
 * �ltimo dia do m�s e, nesse caso, haver� uma remunera��o parcial V�LIDA /
 * REGULAR referente ao per�odo efetivamente trabalhado entre a DCB e o �ltimo
 * dia do m�s; mas se a DCB cai no dia �ltimo dia do m�s, trata-se de m�s
 * INTEIRO e ent�o n�o pode haver remunera��o para a compet�ncia desse �ltimo
 * m�s de benef�cio.
 * 
 * @author magnus
 *
 */
public class CriterioSalarioMaternidade extends AbstractCriterio {

	private static final String BATCH_CRITERIOS_ESPECIE = "batch.criterios.especie.salario.maternidade";
	private ArrayList<String> especies;

	public CriterioSalarioMaternidade() {
		// Engloba tambem o Criterio de Prescri��o em 5 Anos
		setCriterioAssociado(new CriterioPagamentoResiduo());
		String codigosEspecies = (String) getBatchConfig()
				.get(BATCH_CRITERIOS_ESPECIE);
		if (StringUtils.isBlank(codigosEspecies)) {
			throw new RuntimeException(
					"� obrigat�rio a presen�a de uma da propriedade 'batch.criterios.especies.auxilio.doenca.acidente' no arquivo de configura��o.");
		}
		String[] strings = codigosEspecies.split(",");
		if (strings.length == 0) {
			throw new RuntimeException(
					"� obrigat�rio a presen�a de uma da propriedade 'batch.criterios.especies.auxilio.doenca.acidente' no arquivo de configura��o.");
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
		// Se n�o � Salario Maternidade
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
		 * Lembrando que um benef�cio de Sal�rio Maternidade sempre dura quatro
		 * meses, entende-se por m�s INTEIRO aquele que � coberto pelo per�odo
		 * do benef�cio desde o seu dia 01 at� o seu �ltimo dia (28, 29, 30 ou
		 * 31, conforme o m�s); ent�o, s� podem ocorrer meses N�O INTEIROS no
		 * primeiro e no �ltimo (4o.) m�s do benef�cio; o segundo e terceiro
		 * meses de benef�cio sempre ser�o INTEIROS;
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

		// Ordena para verificar se existem 2 remunera��es sequenciais
		Collections.sort(remuneracoes, new Comparator<RemuneracaoTO>() {
			@Override
			public int compare(RemuneracaoTO o1, RemuneracaoTO o2) {
				return o1.getDataCompetencia().compareTo(
						o2.getDataCompetencia());
			}
		});
		// Crit�rio de somente considera mes cheio
		for (RemuneracaoTO remuneracaoTO : remuneracoes) {
			Date data = remuneracaoTO.getDataCompetencia();
			if (data.compareTo(dataInicioConsiderar)>-1&&data.compareTo(dataFimConsiderar)<1) {
				return true;
			}
		}
		return criterio;
	}
}
