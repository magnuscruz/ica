package br.gov.dataprev.infra.batch.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class DtpJobParametersBean {

	private String jobName;
	
	private String arranque;
	
	private Map<String, String> jobParameters;
	
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Map<String, String> getJobParameters() {
		return jobParameters;
	}

	public void setJobParameters(Map<String, String> jobParameters) {
		this.jobParameters = jobParameters;
	}
	

	public String getArranque() {
		return arranque;
	}

	public void setArranque(String arranque) {
		this.arranque = arranque;
	}

	/**
	 * Converte os parâmetros definidos no bean para um array de argumentos na ordem:
	 * <job> <param1=value1> ... <paramN=valueN> -<arranque>
	 * @return Array de argumentos na ordem esperada
	 */
	public String[] getParametersAsArgs() {
		List<String> args = new ArrayList<String>(); //importante ser array para garantir ordem
		
		args.add(jobName);
		
		for (String key : jobParameters.keySet()) {
			args.add(key + "=" + jobParameters.get(key));
		}
		
		args.add("-" + arranque);
		
		return args.toArray(new String[args.size()]);
	}
	
	/**
	 * Garante que o Bean foi configurado com os parâmetros obrigatórios
	 */
	public Boolean validaConfig() {
		if (arranque == null || "".equals(arranque))
			return false;
	
		return true;
	}
	
}
