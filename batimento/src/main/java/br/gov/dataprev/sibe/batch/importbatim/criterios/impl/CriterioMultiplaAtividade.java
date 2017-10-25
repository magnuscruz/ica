package br.gov.dataprev.sibe.batch.importbatim.criterios.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import br.gov.dataprev.sibe.batch.importbatim.criterios.AbstractCriterio;
import br.gov.dataprev.sibe.batch.importbatim.processor.ImportacaoBatimentoProcessor;
import br.gov.dataprev.sibe.batch.importbatim.to.BeneficioTO;
import br.gov.dataprev.sibe.batch.importbatim.to.RemuneracaoTO;
import br.gov.dataprev.sibe.batch.importbatim.to.VinculoTO;
import br.gov.dataprev.sibe.batch.importbatim.utils.CachedConfigurationManager;

/**
 * Crit�rio de m�ltipla atividade: a regra de verifica��o no CNIS � a mesma para
 * todos os Grupos / Esp�cies, verificando v�nculos com qualquer empregador,
 * exceto para casos de Aux�lio-Doen�a SEM m�ltipla atividade, cuja verifica��o
 * se restringe ao v�nculo espec�fico que deu origem ao benef�cio e exclui a
 * verifica��o dos demais v�nculos. Aux�lio-Doen�a (verifica��o no CNIS pelo NIT
 * do Titular): Esp�cies 10, 13, 31, 35 e 91: somente para benef�cios sem
 * m�ltipla atividade e cujo CNPJ do empregador seja o mesmo do gerador do
 * aux�lio. O monitoramento das esp�cies 36, 94 e 95, de Aux�lio-Acidente, n�o
 * foi solicitado pelo Cliente. O arquivo de entrada recebido do Batimento CNIS
 * j� cont�m a informa��o de CNPJ espec�fico (que n�o faz parte do layout dos
 * vetores de v�nculos) para indicar o empregador relacionado com o benef�cio de
 * Aux�lio-Doen�a; este campo s� vir� preenchido nos casos apropriados de
 * Aux�lio-Doen�a (isso j� foi resolvido nos processos anteriores) - se estiver
 * preenchido, a verifica��o de remunera��es s� dever� ser feita para o
 * empregador constante neste campo; se N�O estiver preenchido, a verifica��o de
 * remunera��es s� dever� ser feita para todos os empregadores encontrados.
 * Observar que, na pr�tica, um identificador de empregador � composto de DOIS
 * campos, um tipo (CNPJ, CEI, CPF, ...) e outro com o n�mero propriamente dito.
 * 
 * @author magnus
 *
 */
public class CriterioMultiplaAtividade extends AbstractCriterio {

	private static final String BATCH_CRITERIOS_ESPECIES_AUXILIO_DOENCA_ACIDENTE = "batch.criterios.especies.auxilio.doenca.acidente";
	private ArrayList<String> especiesAuxilioDoencaAcidente;

	public CriterioMultiplaAtividade() {
		// Engloba tambem o Criterio de Prescri��o em 5 Anos
		setCriterioAssociado(new CriterioPagamentoResiduo());
		Properties batchConfig = CachedConfigurationManager.getInstance()
				.getProperties(ImportacaoBatimentoProcessor.BATCH_CONFIG_PROPERTIES);
		String codigosEspecies = (String) batchConfig.get(BATCH_CRITERIOS_ESPECIES_AUXILIO_DOENCA_ACIDENTE);
		if (StringUtils.isBlank(codigosEspecies)) {
			throw new RuntimeException(
					"� obrigat�rio a presen�a de uma da propriedade 'batch.criterios.especies.auxilio.doenca.acidente' no arquivo de configura��o.");
		}
		String[] strings = codigosEspecies.split(",");
		if (strings.length == 0) {
			throw new RuntimeException(
					"� obrigat�rio a presen�a de uma da propriedade 'batch.criterios.especies.auxilio.doenca.acidente' no arquivo de configura��o.");
		}
		especiesAuxilioDoencaAcidente = new ArrayList<String>();
		for (String especie : strings) {
			especiesAuxilioDoencaAcidente.add(especie);
		}
	}

	@Override
	public boolean aplicarCriterioEspecifico(VinculoTO vinculoTO, BeneficioTO beneficioTO,
			List<RemuneracaoTO> remuneracoes) {
		boolean criterioResiduo = false;
		// Se n�o � Auxilio Doen�a ou Acidente n�o precisa verificar
		if (!especiesAuxilioDoencaAcidente.contains(beneficioTO.getEspecieBeneficio())) {
			return false;
		}
		String identificadorEmpregador = beneficioTO.getIdentificadorEmpregador();
		/*
		 * se estiver preenchido, a verifica��o de remunera��es s� dever� ser feita para
		 * o empregador constante neste campo.
		 */
		if (identificadorEmpregador != null && !identificadorEmpregador.equals("00000000000000")) {
			// Se n�o for o empregador gerador do aux�lio n�o verifica as remuneracoes
			if (!vinculoTO.getIdentificadorEmpregador().equals(identificadorEmpregador)) {
				return false;
			}
			/*
			 * se N�O estiver preenchido, a verifica��o de remunera��es dever� ser feita
			 * para todos os empregadores encontrados.
			 */
		} else {

		}
		// Ordena as remunera��es
		Collections.sort(remuneracoes, new Comparator<RemuneracaoTO>() {
			@Override
			public int compare(RemuneracaoTO o1, RemuneracaoTO o2) {
				return o1.getDataCompetencia().compareTo(o2.getDataCompetencia());
			}
		});
		for (RemuneracaoTO remuneracaoTO : remuneracoes) {
			Date data = remuneracaoTO.getDataCompetencia();
			// Verifica se houve pagamento depois da dib
			if (data != null && data.compareTo(DateUtils.addMonths(beneficioTO.getDibDate(), 1)) > 0) {
				criterioResiduo = true;
				break;
			}
		}
		return criterioResiduo;
	}
}
