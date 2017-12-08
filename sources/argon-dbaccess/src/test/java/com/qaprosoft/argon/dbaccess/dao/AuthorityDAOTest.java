package com.qaprosoft.argon.dbaccess.dao;

import static org.testng.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.qaprosoft.argon.dbaccess.dao.mysql.AuthorityDAO;
import com.qaprosoft.argon.models.db.Authority;
import com.qaprosoft.argon.models.db.Authority.AuthorityType;

/**
 * @author asemenkov
 * @since 07 Dec 2017
 */
@Test
@ContextConfiguration("classpath:com/qaprosoft/argon/dbaccess/dbaccess-test.xml")
public class AuthorityDAOTest extends AbstractTestNGSpringContextTests
{

	private static final boolean ENABLED = true;

	private static final Authority AUTHORITY = new Authority();
	{
		AUTHORITY.setAuthority(AuthorityType.TEST_USER);
	}

	@Autowired
	private AuthorityDAO authorityDAO;

	@Test(enabled = ENABLED)
	public void createAuthority()
	{
		authorityDAO.createAuthority(AUTHORITY);
		assertNotEquals(AUTHORITY.getId(), 0, "Authority ID must be set up by autogenerated keys.");
	}

	@Test(enabled = ENABLED, dependsOnMethods = "createAuthority", expectedExceptions = DuplicateKeyException.class)
	public void createAuthorityFail()
	{
		authorityDAO.createAuthority(AUTHORITY);
	}

	@Test(enabled = ENABLED, dependsOnMethods = "createAuthority")
	public void getAllAuthorities()
	{
		assertFalse(authorityDAO.getAllAuthorities().isEmpty(), "List of all authorities is empty.");
	}

	@Test(enabled = ENABLED, dependsOnMethods = "createAuthority")
	public void getAuthorityById()
	{
		checkAuthority(authorityDAO.getAuthorityById(AUTHORITY.getId()));
	}

	@Test(enabled = ENABLED, dependsOnMethods = "createAuthority")
	public void getAuthorityByAuthority()
	{
		checkAuthority(authorityDAO.getAuthorityByAuthority(AUTHORITY.getAuthority()));
	}

	@Test(enabled = ENABLED, dependsOnMethods = "createAuthority")
	public void updateAuthority()
	{
		AUTHORITY.setAuthority(AuthorityType.TEST_ADMIN);
		authorityDAO.updateAuthority(AUTHORITY);
		checkAuthority(authorityDAO.getAuthorityById(AUTHORITY.getId()));
	}

	// Toggle to delete by ID/AUTHORITY
	private static final boolean DELETE_USER_BY_ID = false;

	@Test(enabled = ENABLED && DELETE_USER_BY_ID, dependsOnMethods =
	{ "createAuthority", "createAuthorityFail", "getAuthorityById",
			"getAuthorityByAuthority", "getAllAuthorities", "updateAuthority" })
	public void deleteAuthorityById()
	{
		authorityDAO.deleteAuthorityById(AUTHORITY.getId());
		assertNull(authorityDAO.getAuthorityById(AUTHORITY.getId()));
	}

	@Test(enabled = ENABLED && !DELETE_USER_BY_ID, dependsOnMethods =
	{ "createAuthority", "createAuthorityFail", "getAuthorityById",
			"getAuthorityByAuthority", "getAllAuthorities", "updateAuthority" })
	public void deleteAuthorityByAuthority()
	{
		authorityDAO.deleteAuthorityByAuthority(AUTHORITY.getAuthority());
		assertNull(authorityDAO.getAuthorityByAuthority(AUTHORITY.getAuthority()));
	}

	private void checkAuthority(Authority authority)
	{
		assertEquals(authority.getAuthority(), AUTHORITY.getAuthority(), "Authority is not as expected.");
	}
}