package com.qaprosoft.argon.dbaccess.dao;

import com.qaprosoft.argon.dbaccess.dao.mysql.GroupMapper;
import com.qaprosoft.argon.dbaccess.dao.mysql.UserMapper;
import com.qaprosoft.argon.dbaccess.dao.mysql.search.UserSearchCriteria;
import com.qaprosoft.argon.dbaccess.utils.KeyGenerator;
import com.qaprosoft.argon.models.db.Group;
import com.qaprosoft.argon.models.db.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

@Test
@ContextConfiguration("classpath:com/qaprosoft/argon/dbaccess/dbaccess-test.xml")
public class UserMapperTest extends AbstractTestNGSpringContextTests {
	/**
	 * Turn this on to enable this test
	 */
	private static final boolean ENABLED = false;

	private static final Group GROUP = new Group() {
		private static final long serialVersionUID = 1L;
		{
			setName("n1" + KeyGenerator.getKey());
			setRole(Role.ROLE_USER);
		}
	};

	private static final User USER = new User() {
		private static final long serialVersionUID = 1L;
		{
			setUsername("elton" + KeyGenerator.getKey());
			setFirstName("Elton");
			setLastName("John");
			setEmail("e.jhon@gmail.com");
		}
	};

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private GroupMapper groupMapper;

	@Test(enabled = ENABLED)
	public void createUser() {
		userMapper.createUser(USER);

		assertNotEquals(USER.getId(), 0, "User ID must be set up by autogenerated keys");
	}

	@Test(enabled = ENABLED, dependsOnMethods = { "createUser" }, expectedExceptions = { DuplicateKeyException.class })
	public void createUserFail() {
		userMapper.createUser(USER);
	}

	@Test(enabled = ENABLED, dependsOnMethods = { "createUser" })
	public void getUserById() {
		checkUser(userMapper.getUserById(USER.getId()));
	}

	@Test(enabled = ENABLED, dependsOnMethods = { "createUser" })
	public void getUserByUserName() {
		checkUser(userMapper.getUserByUserName(USER.getUsername()));
	}

	@Test(enabled = ENABLED, dependsOnMethods = { "createUser" })
	public void updateUser() {
		USER.setUsername("eric" + KeyGenerator.getKey());
		USER.setFirstName("Eric");
		USER.setLastName("Clapton");
		USER.setEmail("e.clapton@gmail.com");

		userMapper.updateUser(USER);

		checkUser(userMapper.getUserById(USER.getId()));
	}

	/**
	 * Turn this in to delete user after all tests
	 */
	private static final boolean DELETE_ENABLED = true;

	/**
	 * If true, then <code>deleteUser</code> will be used to delete user after all
	 * tests, otherwise - <code>deleteUserById</code>
	 */
	private static final boolean DELETE_BY_USER = false;

	@Test(enabled = ENABLED && DELETE_ENABLED && DELETE_BY_USER, dependsOnMethods = { "createUser", "createUserFail",
			"getUserById", "getUserByUserName", "updateUser" })
	public void deleteUser() {
		userMapper.deleteUser(USER);

		assertNull(userMapper.getUserById(USER.getId()));
	}

	@Test(enabled = ENABLED && DELETE_ENABLED && !DELETE_BY_USER, dependsOnMethods = { "createUser", "createUserFail",
			"getUserById", "getUserByUserName", "updateUser" })
	public void deleteUserById() {
		userMapper.deleteUserById((USER.getId()));

		assertNull(userMapper.getUserById(USER.getId()));
	}

	@Test(enabled = ENABLED, dependsOnMethods = { "createUser" })
	public void addUserToGroup() {
		UserSearchCriteria userSearchCriteria = new UserSearchCriteria();
		userSearchCriteria.setGroupName(GROUP.getName());
		USER.setUsername("n1" + KeyGenerator.getKey());
		userMapper.createUser(USER);
		groupMapper.createGroup(GROUP);
		userMapper.addUserToGroup(USER.getId(), GROUP.getId());
		List<User> userList = userMapper.searchUsers(userSearchCriteria);
		Assert.assertEquals(userList.get(0).getGroups().get(0).getId(), GROUP.getId());
		Assert.assertEquals(userList.get(0).getId(), USER.getId(), "");
	}

	@Test(enabled = ENABLED && DELETE_ENABLED, dependsOnMethods = { "createUser",
			"addUserToGroup" }, expectedExceptions = { IndexOutOfBoundsException.class })
	public void deleteUserFromGroup() {
		UserSearchCriteria userSearchCriteria = new UserSearchCriteria();
		userSearchCriteria.setGroupName(GROUP.getName());
		userMapper.deleteUserFromGroup(USER.getId(), GROUP.getId());
		userMapper.searchUsers(userSearchCriteria).get(0);
	}

	private void checkUser(User user) {
		assertEquals(user.getUsername(), USER.getUsername(), "User name must match");
		assertEquals(user.getFirstName(), USER.getFirstName(), "First name must match");
		assertEquals(user.getLastName(), USER.getLastName(), "Last name must match");
		assertEquals(user.getEmail(), USER.getEmail(), "Email must match");
	}
}
