package br.gov.dataprev.sibe.batch.importbatim.processor;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import br.gov.dataprev.sibe.batch.importbatim.to.RemuneracaoTO;
import br.gov.dataprev.sibe.batch.importbatim.to.VinculoTO;

public class BatimentoBuilder {
	private static final Logger LOG = Logger.getLogger(BatimentoBuilder.class);

	public static VinculoTO extrairVinculo(String strVinculo) {
		VinculoTO to = new VinculoTO();
		parseVinculo(strVinculo, to);
		return to;
	}

	private static void parseVinculo(String strVinculo, VinculoTO to) {
		int index = 0;
		String strTemp = strVinculo.substring(index, index = index + 3);
		to.setId(new Integer(strTemp));
		strTemp = strVinculo.substring(index, index = index + 8);
		to.setInicioVinculo(strTemp);
		strTemp = strVinculo.substring(index, index = index + 8);
		to.setFimVinculo(strTemp);
		strTemp = strVinculo.substring(index, index = index + 3);
		to.setNumeroRemuneracoes(new Integer(strTemp));
		strTemp = strVinculo.substring(index, index = index + 8);
		to.setDataUltimaRemuneracao(strTemp);
		strTemp = strVinculo.substring(index, index = index + 1);
		to.setClassificadorEmpregador(new Integer(strTemp));
		strTemp = strVinculo.substring(index, index = index + 1);
		to.setTipoContribuinte(new Integer(strTemp));
		strTemp = strVinculo.substring(index, index = index + 1);
		to.setOrigemVinculo(new Integer(strTemp));
		strTemp = strVinculo.substring(index, index = index + 2);
		to.setEspecieBeneficio(new Integer(strTemp));
		strTemp = strVinculo.substring(index, index = index + 2);
		to.setSituacaoBeneficio(new Integer(strTemp));
		strTemp = strVinculo.substring(index, index = index + 14);
		to.setIdentificadorEmpregador(strTemp);
		strTemp = strVinculo.substring(index, index = index + 55);
		to.setNomeEmpregador(strTemp);
		strTemp = strVinculo.substring(index, index = index + 8);
		to.setCepEmpregador(strTemp);
		strTemp = strVinculo.substring(index, index = index + 7);
		to.setMunicipioEmpregador(strTemp);
		strTemp = strVinculo.substring(index, index = index + 27);
		to.setBairroEmpregador(strTemp);
		strTemp = strVinculo.substring(index, index = index + 55);
		to.setLogradouroEmpregador(strTemp);
		strTemp = strVinculo.substring(index, index = index + 2);
		to.setUfEmpregador(strTemp);
	}

	public static RemuneracaoTO extrairRemuneracao(String str) {
		RemuneracaoTO to = new RemuneracaoTO();
		parseRemuneracao(str, to);
		return to;
	}

	private static void parseRemuneracao(String str, RemuneracaoTO to) {
		int index = 0;
		String strTemp = str.substring(index, index = index + 3);

		to.setIdVinculo(new Integer(strTemp));
		strTemp = str.substring(index, index = index + 6);

		// Construção da data de competencia com data de 8 digitos
		String comp = strTemp + "01";
		Date competencia = null;
		try {
			competencia = DateUtils.parseDate(comp,
					ImportacaoBatimentoProcessor.DATE_PATTERNS);
		} catch (ParseException e) {
			LOG.debug("ERRO NO CONVERT DE STR PARA DATA", e);
		}
		to.setDataCompetencia(competencia);

		strTemp = str.substring(index, index = index + 1);
		to.setFonteInformacao(strTemp);

		strTemp = str.substring(index, index = index + 14);
		to.setRemuneracao(new Double(strTemp) / 100);
	}

}
