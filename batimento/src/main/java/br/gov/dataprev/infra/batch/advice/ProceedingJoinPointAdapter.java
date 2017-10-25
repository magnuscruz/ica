package br.gov.dataprev.infra.batch.advice;

import java.lang.reflect.Method;
import java.util.Map;

import javax.interceptor.InvocationContext;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import br.gov.dataprev.infra.batch.exception.BatchRuntimeException;

/**
 * Adaptador para o uso do DtpInfraCache no SpringBatch.
 *
 * @author DATAPREV/DIT/DEAT
 */
class ProceedingJoinPointAdapter implements InvocationContext {
    private final ProceedingJoinPoint pjp;

    /**
     * Adapta o ProceedingJoinPoint do aspectj para o CacheInterceptor.
     * @param pjp ProceedingJoinPoint do AspectJ a ser adaptado.
     */
    public ProceedingJoinPointAdapter(final ProceedingJoinPoint pjp) {
        this.pjp = pjp;
    }

    /**
     * @see javax.interceptor.InvocationContext#getMethod()
     */
    public Method getMethod() {
        final MethodSignature signature = (MethodSignature) pjp.getSignature();

        // Obtém o método. No entanto, esse método pode ser de
        // uma interface (e não de uma classe concreta)
        Method m = signature.getMethod();

        // Como as anotações podem estar presentes apenas na classe concreta,
        // o procedimento abaixo extrai o método adequado.
        try {
            m = pjp.getTarget().getClass().getDeclaredMethod(m.getName(),
                    m.getParameterTypes());
        } catch (final Exception e) {
            // Em teoria seria impossivel cair aqui.
            throw new BatchRuntimeException("Erro inesperado obtendo método", e);
        }

        return m;
    }

    /**
     * @see javax.interceptor.InvocationContext#getParameters()
     */
    public Object[] getParameters() {
        return pjp.getArgs();
    }

    /**
     * @see javax.interceptor.InvocationContext#getTarget()
     */
    public Object getTarget() {
        return pjp.getTarget();
    }

    /**
     * @see javax.interceptor.InvocationContext#proceed()
     */
    public Object proceed() throws Exception {
        try {
            return pjp.proceed();
        } catch (final Exception e) {
            throw e;
        } catch (final Throwable e) {
            // Findbugs reclama, mas está ok.
            // O erro throwable não faz parte da assinatura de
            // javax.interceptor.InvocationContext#proceed().
            throw new Exception("Erro inesperado.", e);
        }
    }

    /**
     * @see javax.interceptor.InvocationContext#setParameters(java.lang.Object[])
     */
    public void setParameters(final Object[] arg0) {
        throw new UnsupportedOperationException("Método não foi adaptado");
    }

    /**
     * @see javax.interceptor.InvocationContext#getContextData()
     */
    public Map<String, Object> getContextData() {
        throw new UnsupportedOperationException("Método não foi adaptado");
    }
}
