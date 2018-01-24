package org.transmart.plugin.auth0

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.userdetails.NoStackUsernameNotFoundException
import grails.transaction.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.codehaus.groovy.grails.commons.ControllerArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsClass
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.validation.FieldError
import org.springframework.web.context.request.RequestContextHolder
import org.transmart.searchapp.AuthUser
import org.transmart.searchapp.Role

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.lang.reflect.Method

/**
 * @author <a href='mailto:burt_beckwith@hms.harvard.edu'>Burt Beckwith</a>
 */
@CompileStatic
@Slf4j('logger')
class AuthService implements InitializingBean {

	protected static final GrantedAuthority PUBLIC_USER = new SimpleGrantedAuthority(Roles.PUBLIC_USER.authority)

	@Autowired private Auth0Config auth0Config
	@Autowired private GrailsApplication grailsApplication
	@Autowired private List<LogoutHandler> logoutHandlers
	@Autowired private MessageSource messageSource
	@Autowired private SpringSecurityService springSecurityService

	private Map<String, Map<String, UserLevel>> levels = [:].withDefault { [:] }
	private Map<String, String> controllerDefaultActions = [:]

	def mailService

	/**
	 * @return the current auth if authenticated
	 */
	Authentication authentication() {
		springSecurityService.getAuthentication()
	}

	/**
	 * @return the current auth principal if authenticated
	 */
	AuthUserDetails principal() {
		loggedIn() ? (AuthUserDetails) springSecurityService.getPrincipal() : null
	}

	/**
	 * Find the <code>AuthUser</code> with the specified unique id and build
	 * an <code>AuthUserDetails</code> instance from it.
	 */
	@Transactional(readOnly = true, noRollbackFor = [IllegalArgumentException, UsernameNotFoundException])
	AuthUserDetails loadAuthUserDetailsByUniqueId(String uniqueId) throws UsernameNotFoundException {
		AuthUser authUser = findBy('uniqueId', uniqueId)
		if (authUser) {
			userDetails authUser
		}
		else {
			logger.warn 'No AuthUser found with uniqueId/subject "{}"', uniqueId
			throw new NoStackUsernameNotFoundException()
		}
	}

	/**
	 * Find the <code>AuthUser</code> with the specified username and build
	 * an <code>AuthUserDetails</code> instance from it.
	 */
	@Transactional(readOnly = true, noRollbackFor = [IllegalArgumentException, UsernameNotFoundException])
	AuthUserDetails loadAuthUserDetails(String username) throws UsernameNotFoundException {
		AuthUser authUser = authUser(username)
		if (authUser) {
			userDetails authUser
		}
		else {
			logger.warn 'No AuthUser found with username "{}"', username
			throw new NoStackUsernameNotFoundException()
		}
	}

	@CompileDynamic // TODO use GrailsCompileStatic
	protected AuthUser findBy(String name, String value) {
		AuthUser.createCriteria().get {
			eq name, value
			cache true
		}
	}

	protected AuthUserDetails userDetails(AuthUser authUser) {
		List<GrantedAuthority> authorities = []
		for (Role role in authUser.authorities) {
			authorities << new SimpleGrantedAuthority(role.authority)
		}
		if (!authorities) {
			authorities << PUBLIC_USER // TODO
		}

		new AuthUserDetails(authUser.username, authUser.passwd, authUser.enabled, true /*!user.accountExpired*/,
				true, true /*!user.accountLocked*/, authorities,
				authUser.id, authUser.userRealName)
	}

	/**
	 * @return the current auth username if authenticated
	 */
	String currentUsername() {
		principal()?.username
	}

	/**
	 * @return the <code>AuthUser</code> instance for the currently authenticated user.
	 */
	AuthUser currentAuthUser() {
		authUser currentUsername()
	}

	/**
	 * Retrieve the <code>AuthUser</code> for the specified username.
	 * The query cache is used to reduce unnecessary database calls.
	 */
	AuthUser authUser(String username) {
		findBy 'username', username
	}

	/**
	 * @return the id of the AuthUser instance for the currently authenticated user.
	 */
	long currentUserId() {
		(long) springSecurityService.getCurrentUserId()
	}

	/**
	 * @return true if authenticated and not anonymous.
	 */
	boolean loggedIn() {
		springSecurityService.isLoggedIn()
	}

	/**
	 * Get the user level for the current user.
	 *
	 * @return UNREGISTERED if not authenticated,
	 *         ADMIN if the user has ROLE_ADMIN,
	 *         TWO if the user has ROLE_DATASET_EXPLORER_ADMIN,
	 *         ONE if the user has ROLE_STUDY_OWNER,
	 *         ZERO otherwise
	 */
	UserLevel currentUserLevel() {
		if (loggedIn()) {
			userLevel principal().authorities.collect { GrantedAuthority a -> a.authority }
		}
		else {
			UserLevel.UNREGISTERED
		}
	}

	/**
	 * Get the user level for the specified user. Do not call this if the user is
	 * currently authenticated as it would result in unnecessary database calls;
	 * use currentUserLevel() instead.
	 *
	 * @return ADMIN if the user has ROLE_ADMIN,
	 *         TWO if the user has ROLE_DATASET_EXPLORER_ADMIN,
	 *         ONE if the user has ROLE_STUDY_OWNER,
	 *         ZERO otherwise
	 */
	UserLevel userLevel(AuthUser authUser) {
		userLevel authUser.authorities*.authority
	}

	/**
	 * Get the user level for the given role names.
	 *
	 * @return ADMIN if the user has ROLE_ADMIN,
	 *         TWO if the user has ROLE_DATASET_EXPLORER_ADMIN,
	 *         ONE if the user has ROLE_STUDY_OWNER,
	 *         ZERO otherwise
	 */
	UserLevel userLevel(Collection<String> roleNames) {
		if (roleNames.contains(Roles.ADMIN.authority)) {
			UserLevel.ADMIN
		}
		else if (roleNames.contains(Roles.DATASET_EXPLORER_ADMIN.authority)) {
			UserLevel.TWO
		}
		else if (roleNames.contains(Roles.STUDY_OWNER.authority)) {
			UserLevel.ONE
		}
		else {
			UserLevel.ZERO
		}
	}

	void logout() {
		Authentication authentication = SecurityContextHolder.context.authentication
		if (authentication) {
			HttpServletRequest request = currentRequest()
			HttpServletResponse response = currentResponse()
			for (LogoutHandler handler in logoutHandlers) {
				handler.logout request, response, authentication
			}
		}
	}

	Map currentUserInfo(String username = null) {
		AuthUser user
		UserLevel level
		if (!username) {
			user = currentAuthUser()
			level = currentUserLevel()
		}
		else {
			user = authUser(username)
			level = userLevel(user)
		}

		if (!user) {
			// TODO
		}

		Map details = user.description ? (Map) JSON.parse(user.description) : [:]
		details + [
				email   : user.email ?: user.username ?: '',
				id      : user.id,
				level   : level,
				type    : user.type,
				username: user.username]
	}

	@Transactional
	void updateUserDescription(Map data) {
		AuthUser user = currentAuthUser()
		user.description = ((currentUserInfo() + data) as JSON).toString()
		user.name = user.userRealName = (data.firstname ?: '').toString() + ' ' + (data.lastname ?: '')
		user.save()
	}

	void checkUserLevelAccess(String controller, String action) {
		String username = currentUsername()
		logger.debug 'checkUserLevelAccess() controller "{}" action "{}" user "{}"', controller, action, username
		if (controller && levels.containsKey(controller)) {
			if (!action) {
				action = controllerDefaultActions[controller]
			}
			UserLevel minLevel = levels[controller][action]
			if (!minLevel) {
				logger.trace 'checkUserLevelAccess() no RequiresLevel annotation for /{}/{}', controller, action
				return
			}

			UserLevel userLevel = currentUserLevel()
			if (userLevel < minLevel) {
				logger.error 'checkUserLevelAccess() {} < {}, access denied for /{}/{} user {}',
						userLevel, minLevel, controller, action, username
				throw new AccessDeniedException('You are not authorized to perform this action')
			}

			logger.debug 'checkUserLevelAccess() {} >= {}, access allowed for /{}/{} user {}',
					userLevel, minLevel, controller, action, username
		}
	}

	/**
	 * Get a <code>Settings</code> instance for the currently authenticated user.
	 */
	Settings userSetting(String name, long userId = currentUserId()) {
		findSetting name, userId
	}

	/**
	 * Get a shared <code>Settings</code> instance.
	 */
	Settings setting(String name) {
		findSetting name, 0
	}

	@CompileDynamic // TODO use GrailsCompileStatic
	private Settings findSetting(String name, long userId) {
		Settings.createCriteria().get {
			eq 'fieldname', name
			eq 'userid', userId
			cache true
		}
	}

	@Transactional
	void grantRoles(AuthUser user, Roles... roles) {
		for (Roles role in roles) {
			grantRole user, role
		}
	}

	@CompileDynamic // TODO use GrailsCompileStatic
	protected void grantRole(AuthUser user, Roles role) {
		Role.createCriteria().get {
			eq 'authority', role.authority
			cache true
			lock true
		}.addToPeople user
	}

	/**
	 * Build an <code>Authentication</code> for the given username and register
	 * it in the security context.
	 */
	void authenticateAs(String username) {
		springSecurityService.reauthenticate username
	}

	/**
	 * @param o a domain class instance
	 * @return resolved error strings
	 */
	@CompileDynamic
	Map<String, List<String>> errorStrings(o) {
		Locale locale = Locale.getDefault()
		Map<String, List<String>> stringsByField = [:].withDefault { [] }
		for (fieldErrors in o.errors) {
			for (error in fieldErrors.allErrors) {
				String message = messageSource.getMessage(error, locale)
				stringsByField[((FieldError) error).field] << message
			}
		}
		[:] + stringsByField
	}

	protected HttpServletRequest currentRequest() {
		currentRequestAttributes().request
	}

	protected HttpServletResponse currentResponse() {
		currentRequestAttributes().response
	}

	protected GrailsWebRequest currentRequestAttributes() {
		(GrailsWebRequest) RequestContextHolder.currentRequestAttributes()
	}

	void afterPropertiesSet() {
		findControllerUserLevels()
	}

	protected void findControllerUserLevels() {
		levels.clear()
		for (GrailsClass gc in grailsApplication.getArtefacts(ControllerArtefactHandler.TYPE)) {
			String controllerName = gc.logicalPropertyName
			controllerDefaultActions[controllerName] = ((GrailsControllerClass) gc).defaultAction

			for (Method method in gc.clazz.methods) {
				RequiresLevel annotation = method.getAnnotation(RequiresLevel)
				if (annotation) {
					levels[controllerName][method.name] = annotation.value()
				}
			}
		}
		logger.debug 'levels {}, controllerDefaultActions {}', levels, controllerDefaultActions
	}
}
