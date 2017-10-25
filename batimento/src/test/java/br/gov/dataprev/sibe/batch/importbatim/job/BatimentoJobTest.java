package br.gov.dataprev.sibe.batch.importbatim.job;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.ErrorMode;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.gov.dataprev.infra.batch.listener.SkipCheckingListener;
import br.gov.dataprev.infra.batch.support.DtpJobStarter;
import br.gov.dataprev.sibe.batch.importbatim.testes.BaseTestes;

/**
 * Testes de integracao para o {@link Job} ImportacaoBatimentoJob.
 *
 * @author DATAPREV/DIT/DEAT
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SqlGroup({
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:spring-batch-config.sql", config=@SqlConfig(errorMode=ErrorMode.CONTINUE_ON_ERROR))
})
@ContextConfiguration(locations = { "/job-runner-context.xml", "/config-layer.xml", "/data-source-pool.xml" })
public class BatimentoJobTest extends BaseTestes {

	/**
	 * Usando o JobStarter da infra. Para um teste mais unitario.
	 */
	@Autowired
	private DtpJobStarter jobStarter;

	public BatimentoJobTest() {
		super();
	}

	@Before
	public void before() throws Exception {
		super.initTestes(".*\\.ZIP");
	}
	@Test
	public void testLaunchJob() throws Exception {
		SkipCheckingListener.resetProcessSkips();

		final int returncode = runSimpleTest();
		assertEquals(0, returncode);
		assertEquals(0, SkipCheckingListener.getProcessSkips());
	}

	private int runSimpleTest() throws Exception {
		return this.jobStarter.start("importacaoBatimentoJob", "processo=0");
	}
}
