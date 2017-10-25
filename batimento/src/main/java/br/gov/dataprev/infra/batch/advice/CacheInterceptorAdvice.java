package br.gov.dataprev.infra.batch.advice;

import org.springframework.cache.interceptor.CacheInterceptor;

/**
 * Advice para integrar o DtpInfraCache com o AspectJ.
 * <p>
 * As aplica��es devem usar a classe apenas indiretamente atrav�s do c�digo abaixo:
 * <p>
 * <pre>
 * {@code
 *  <aop:config>
 *    <aop:aspect id="cacheInterceptor" ref="DtpCacheInterceptorAdvice">
 *      <aop:around pointcut="execution( * br.gov.dataprev.refappbatch.dao.impl.SdcDaoImpl.*(..))"
 *                  method="intercept" />
 *      </aop:aspect>
 *  </aop:config>
 * }
 * </pre>
 *
 *  @author DATAPREV/DIT/DEAT
 */
public class CacheInterceptorAdvice {
    // Verificar se existe problema de concorr�ncia
    private static CacheInterceptor cache = new CacheInterceptor();

    /**
     * Invoca o DtpInfraCache ap�s adaptar o ProceedingJoinPoint do AspectJ.
     * @param pjp "Interceptor" do aspectJ.
     * @return Objeto em cache.
     * @throws Exception se houver erro invocando servi�o de cache.
    
    public Object intercept(final ProceedingJoinPoint pjp) throws Exception {
        final ProceedingJoinPointAdapter adapter = new ProceedingJoinPointAdapter(pjp);

        return cache.invoke(adapter.getMethod());
    } */
}
