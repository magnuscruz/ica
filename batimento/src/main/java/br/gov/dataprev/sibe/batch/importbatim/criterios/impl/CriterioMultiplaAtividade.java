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
 * Critério de múltipla atividade: a regra de verificação no CNIS é a mesma para
 * todos os Grupos / Espécies, verificando vínculos com qualquer empregador,
 * exceto para casos de Auxílio-Doença SEM múltipla atividade, cuja verificação
 * se restringe ao vínculo específico que deu origem ao benefício e exclui a
 * verificação dos demais vínculos. Auxílio-Doença (verificação no CNIS pelo NIT
 * do Titular): Espécies 10, 13, 31, 35 e 91: somente para benefícios sem
 * múltipla atividade e cujo CNPJ do empregador seja o mesmo do gerador do
 * auxílio. O monitoramento das espécies 36, 94 e 95, de Auxílio-Acidente, não
 * foi solicitado pelo Cliente. O arquivo de entrada recebido do Batimento CNIS
 * já contém a informação de CNPJ específico (que não faz parte do layout dos
 * vetores de vínculos) para indicar o empregador relacionado com o benefício de
 * Auxílio-Doença; este campo só virá preenchido nos casos apropriados de
 * Auxílio-Doença (isso já foi resolvido nos processos anteriores) - se estiver
 * preenchido, a verificação de remunerações só deverá ser feita para o
 * empregador constante neste campo; se NÃO estiver preenchido, a verificação de
 * remunerações só deverá ser feita para todos os empregadores encontrados.
 * Observar que, na prática, um identificador de empregador é composto de DOIS
 * campos, um tipo (CNPJ, CEI, CPF, ...) e outro com o número propriamente dito.
 * 
 * @author magnus
 *
 */
public class CriterioMultiplaAtividade extends AbstractCriterio {

	private static final String BATCH_CRITERIOS_ESPECIES_AUXILIO_DOENCA_ACIDENTE = "batch.criterios.especies.auxilio.doenca.acidente";
	private ArrayList<String> especiesAuxilioDoencaAcidente;

	public CriterioMultiplaAtividade() {
		// Engloba tambem o Criterio de Prescrição em 5 Anos
		setCriterioAssociado(new CriterioPagamentoResiduo());
		Properties batchConfig = CachedConfigurationManager.getInstance()
				.getProperties(ImportacaoBatimentoProcessor.BATCH_CONFIG_PROPERTIES);
		String codigosEspecies = (String) batchConfig.get(BATCH_CRITERIOS_ESPECIES_AUXILIO_DOENCA_ACIDENTE);
		if (StringUtils.isBlank(codigosEspecies)) {
			throw new RuntimeException(
					"É obrigatório a presença de uma da propriedade 'batch.criterios.especies.auxilio.doenca.acidente' no arquivo de configuração.");
		}
		String[] strings = codigosEspecies.split(",");
		if (strings.length == 0) {
			throw new RuntimeException(
					"É obrigatório a presença de uma da propriedade 'batch.criterios.especies.auxilio.doenca.acidente' no arquivo de configuração.");
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
		// Se não é Auxilio Doença ou Acidente não precisa verificar
		if (!especiesAuxilioDoencaAcidente.contains(beneficioTO.getEspecieBeneficio())) {
			return false;
		}
		String identificadorEmpregador = beneficioTO.getIdentificadorEmpregador();
		/*
		 * se estiver preenchido, a verificação de remunerações só deverá ser feita para
		 * o empregador constante neste campo.
		 */
		if (identificadorEmpregador != null && !identificadorEmpregador.equals("00000000000000")) {
			// Se não for o empregador gerador do auxílio não verifica as remuneracoes
			if (!vinculoTO.getIdentificadorEmpregador().equals(identificadorEmpregador)) {
				return false;
			}
			/*
			 * se NÃO estiver preenchido, a verificação de remunerações deverá ser feita
			 * para todos os empregadores encontrados.
			 */
		} else {

		}
		// Ordena as remunerações
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
