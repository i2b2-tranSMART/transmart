package org.transmart.authorization

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.aop.TargetSource
import org.springframework.aop.framework.AopInfrastructureBean
import org.springframework.aop.framework.ProxyFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.web.context.request.RequestContextHolder
import org.transmartproject.core.users.User

/**
 * Creates a proxy bean that provides the "current user" appropriate for context
 * of the caller.
 *
 * The current user has to be available to transmart services in several
 * distinct contexts. Probably, it would be preferable to have it
 * available only in a request thread and do the all the necessary checks
 * against the user in the request thread. But, as it stands, we also need to
 * have the user available in other contexts, like the quartz thread used in
 * exporting.
 *
 * Consequently, the bean currentUserBean is now implemented using this proxy
 * factory bean hat returns a proxy that delegates to other beans, trying them
 * in order (typically each one is bound to a different scope).
 *
 * The currentUserBean returned proxy tries to delegate, in order, to the
 * request scoped bean (see {@link CurrentUserBeanFactoryBean}, then to the
 * quartz bean, and then to whatever other beans happen to be registered with
 * {@link CurrentUserBeanProxyFactory#registerBeanToTry(java.lang.String)}.
 */
@CompileStatic
class CurrentUserBeanProxyFactory implements FactoryBean<User>, BeanFactoryAware {

    // Don't change this bean name. Rmodules depends on this bean name
    public final static String BEAN_BAME = 'currentUserBean'
    public final static String SUB_BEAN_REQUEST = 'currentUserBeanRequestScoped'
    public final static String SUB_BEAN_QUARTZ = 'currentUserBeanQuartzScope'

    private User object

    private List<String> extraBeansToTry = [SUB_BEAN_QUARTZ]

    void registerBeanToTry(String beanName) {
        extraBeansToTry << beanName
    }

    User getObject() {
        object
    }

    Class<User> getObjectType() {
        User
    }

    boolean isSingleton() {
        true
    }

    @CompileStatic
    @Slf4j('logger')
    class CurrentUserBeanTargetSource implements TargetSource {

        ConfigurableBeanFactory cbf

	CurrentUserBeanTargetSource(ConfigurableBeanFactory cbf) {
	    this.cbf = cbf
	}

        final Class<?> targetClass = User

        final boolean isStatic() {
            false
        }

	def getTarget() {
            if (RequestContextHolder.requestAttributes) {
                // request context is active
                cbf.getBean SUB_BEAN_REQUEST
            }
            else {
                for (beanName in extraBeansToTry) {
                    try {
                        return cbf.getBean(beanName)
                    }
		    catch (BeansException ignored) {
			logger.debug 'BeansException for bean {}', beanName
                    }
                }

		throw new IllegalStateException("Tried to fetch current user, but it's not available")
            }
        }

	void releaseTarget(target) {
            // not really anything to do
        }
    }

    void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory

        ProxyFactory pf = new ProxyFactory([User] as Class[])
	pf.targetSource = new CurrentUserBeanTargetSource(cbf)
        // also expose the thing as a ScopedObject
        //pf.addAdvice(new DelegatingIntroductionInterceptor(scopedObject))
	pf.addInterface AopInfrastructureBean

	object = (User) pf.getProxy(cbf.getBeanClassLoader())
    }
}
