package br.gov.dataprev.sibe.batch.importbatim.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.springframework.batch.core.annotation.BeforeProcess;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.gov.dataprev.infra.batch.reader.LineNumberEntity;
import br.gov.dataprev.sibe.batch.importbatim.criterios.AbstractCriterio;
import br.gov.dataprev.sibe.batch.importbatim.criterios.Criterio;
import br.gov.dataprev.sibe.batch.importbatim.to.BatimentoTO;
import br.gov.dataprev.sibe.batch.importbatim.to.BeneficioTO;
import br.gov.dataprev.sibe.batch.importbatim.to.RemuneracaoTO;
import br.gov.dataprev.sibe.batch.importbatim.to.VinculoTO;
import br.gov.dataprev.sibe.batch.importbatim.utils.CachedConfigurationManager;

/**
 * Classe responsavel por processar os dados obtidos do mapper.
 * <p/>
 * Utiliza o framework SpringBatch.
 * 
 * @author magnus.cruz@dataprev.gov.br
 */
@Component("importacaoBatimentoProcessor")
@Scope("step")
public class ImportacaoBatimentoProcessor implements
		ItemProcessor<LineNumberEntity, BatimentoTO> {
	private static final String BATCH_CRITERIOS_CLASSES = "batch.criterios.classes";

	private static final String BATCH_CRITERIOS_PACKAGE = "batch.criterios.package";

	public static final String BATCH_CONFIG_PROPERTIES = "/home/magnus/hackathon/MonitorBatch/batch-config/batch-config.properties";

	private static final Logger LOG = Logger
			.getLogger(ImportacaoBatimentoProcessor.class);

	public static String[] DATE_PATTERNS = { "yyyyMMdd" };

	private Set<Class<? extends AbstractCriterio>> listaCriterios = new HashSet<Class<? extends AbstractCriterio>>();

	/**
	 * Efetua log da linha sendo processada no momento.
	 * 
	 * @param obj
	 *            Objeto em processamento
	 */
	@SuppressWarnings({ "unchecked" })
	@BeforeProcess
	public void init(final LineNumberEntity obj) {
		Properties batchConfig = CachedConfigurationManager.getInstance()
				.getProperties(BATCH_CONFIG_PROPERTIES);
		String classesName = (String) batchConfig.get(BATCH_CRITERIOS_CLASSES);
		if (StringUtils.isBlank(classesName)) {
			String packageName = (String) batchConfig
					.get(BATCH_CRITERIOS_PACKAGE);
			if (StringUtils.isBlank(packageName)) {
				throw new RuntimeException(
						"É obrigatório a presença de uma das propriedades 'batch.criterios.classes' ou 'batch.criterios.package' no arquivo de configuração.");
			}
			Reflections reflections = new Reflections(packageName);
			listaCriterios = reflections.getSubTypesOf(AbstractCriterio.class);
		} else {
			String[] classes = classesName.split(",");
			for (String name : classes) {
				try {
					Class<? extends AbstractCriterio> loadClass = (Class<? extends AbstractCriterio>) ClassLoader
							.getSystemClassLoader().loadClass(name);
					listaCriterios.add(loadClass);
				} catch (Exception e) {
					LOG.error("ERRO NA CRIAÇÃO DA INSTANCIA DE " + name, e);
				}
			}
		}

		if (obj instanceof BatimentoTO) {
			//LOG.info(ToStringBuilder.reflectionToString(obj));
		}
	}

	public BatimentoTO process(final LineNumberEntity to) throws Exception {
		BatimentoTO bat = null;
		try {
			if (to instanceof BatimentoTO) {
				bat = new BatimentoTO();
				BeanUtils.copyProperties(bat, to);

				BeneficioTO beneficioTO = new BeneficioTO();
				BeanUtils.copyProperties(beneficioTO, to);

				List<VinculoTO> listaVinculos = getVinculos(bat);
				// LOG.debug(ToStringBuilder.reflectionToString(listaVinculos));
				Map<Integer, List<RemuneracaoTO>> mapRemuneracoes = getRemuneracoes(bat);
				// LOG.debug(ToStringBuilder.reflectionToString(mapRemuneracoes));
				boolean criterio = aplicandoCriterios(beneficioTO,
						listaVinculos, mapRemuneracoes);
				if (!criterio) {
					bat = null;
				}
			}
		} catch (Exception e) {
			LOG.error("ERRO AO POVOAR BATIMENTO ENTITY", e);
		}

		return bat;
	}

	/**
	 * Aplicando os critérios cadastrados no arquivo de configuração.
	 *
	 * @param beneficioTO
	 *            .getDibDate()
	 * @param listaVinculos
	 * @param mapRemuneracoes
	 * @return
	 */
	private boolean aplicandoCriterios(BeneficioTO beneficioTO,
			List<VinculoTO> listaVinculos,
			Map<Integer, List<RemuneracaoTO>> mapRemuneracoes) {

		boolean resultado = false;

		for (VinculoTO vinculoTO : listaVinculos) {
			List<RemuneracaoTO> remuneracoes = mapRemuneracoes.get(vinculoTO
					.getId());
			for (Class<? extends AbstractCriterio> criterioClass : listaCriterios) {
				try {
					Criterio criterio = criterioClass.newInstance();
					resultado = criterio.aplicarCriterio(vinculoTO,
							beneficioTO, remuneracoes);
					if (resultado) {
						return true;
					}
				} catch (Exception e) {
					LOG.error(
							"ERRO AO APLICAR CRITERIO "
									+ criterioClass.getName(), e);
				}
			}
		}
		return resultado;
	}

	private List<VinculoTO> getVinculos(BatimentoTO bat) {
		String vinculos = bat.getVinculos();
		int endIndex = 205;
		int count = 1;
		List<String> listaVinculos = new ArrayList<String>();
		for (; count <= 100; count++) {
			listaVinculos.add(vinculos.substring((endIndex * (count - 1)),
					endIndex * count));
		}
		List<VinculoTO> listaVinculoTO = new ArrayList<VinculoTO>();
		for (String strVinculo : listaVinculos) {
			VinculoTO to = BatimentoBuilder.extrairVinculo(strVinculo);
			if (to.getId() != null && to.getId() == 1) {
				listaVinculoTO.add(to);
			}
		}
		return listaVinculoTO;
	}

	private Map<Integer, List<RemuneracaoTO>> getRemuneracoes(BatimentoTO bat) {
		String remuneracoes = bat.getRemuneracoes();
		int endIndex = 24;
		int count = 1;
		List<String> listaRemuneracoes = new ArrayList<String>();
		for (; count <= 600; count++) {
			listaRemuneracoes.add(remuneracoes.substring(
					(endIndex * (count - 1)), endIndex * count));
		}
		Map<Integer, List<RemuneracaoTO>> mapRemuneracoesTO = new HashMap<Integer, List<RemuneracaoTO>>();
		for (String str : listaRemuneracoes) {
			RemuneracaoTO to = BatimentoBuilder.extrairRemuneracao(str);
			if (to.getIdVinculo() != null && to.getIdVinculo() != 0) {
				List<RemuneracaoTO> listaRemuneracoesTO = mapRemuneracoesTO
						.get(to.getIdVinculo());
				if (listaRemuneracoesTO == null) {
					listaRemuneracoesTO = new ArrayList<RemuneracaoTO>();
					mapRemuneracoesTO.put(to.getIdVinculo(),
							listaRemuneracoesTO);
				}
				listaRemuneracoesTO.add(to);
			}
		}

		return mapRemuneracoesTO;
	}
//
//
//	public void verificaMunicipio(final int idMunicipioPrev, final String nome)
//			throws BatchException {
//		try {
//			final MunicipioTO municipio = this.sdcDao
//					.obterMunicipioPeloCodigoPrevidencia(idMunicipioPrev);
//
//			if (municipio == null) {
//				throw new BatchException("IdMunicipioPrev inexistente");
//			} else {
//				if (!nome.contains(municipio.getNmMunicipio())) {
//					throw new BatchException(
//							"Nome do municipio inconsistente com o SDC");
//				}
//			}
//		} catch (final SDCException e) {
//			LOG.error("Erro acessando o SDC", e);
//			throw new BatchException("Erro obtendo dados do SDC", e);
//		}
//	}
}
