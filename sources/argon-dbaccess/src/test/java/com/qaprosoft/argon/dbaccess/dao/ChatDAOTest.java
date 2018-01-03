package com.qaprosoft.argon.dbaccess.dao;

import com.qaprosoft.argon.dbaccess.dao.mysql.ChatDAO;
import com.qaprosoft.argon.dbaccess.dao.mysql.StatusDAO;
import com.qaprosoft.argon.dbaccess.dao.mysql.UserDAO;
import com.qaprosoft.argon.dbaccess.utils.KeyGenerator;
import com.qaprosoft.argon.models.db.Chat;
import com.qaprosoft.argon.models.db.Status;
import com.qaprosoft.argon.models.db.User;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNull;


/**
 * @author kbugrim
 * @since 10 Dec 2017
 */
@Test
@ContextConfiguration("classpath:com/qaprosoft/argon/dbaccess/dbaccess-test.xml")
public class ChatDAOTest extends AbstractTestNGSpringContextTests {

    private static final boolean ENABLED = false;

    @Autowired
    private ChatDAO chatDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private StatusDAO statusDAO;

    private static final Chat CHAT = new Chat();
    {
        CHAT.setName("chat" + KeyGenerator.getKey());
        CHAT.setPrivateEnabled(false);
    }

    private final static User USER = new User();

    {
        USER.setEmail(KeyGenerator.getKey() + "@test-mail.com");
        USER.setEnabled(true);
        USER.setFirstName("Boris");
        USER.setLastName("The Blade");
        USER.setPassword("pass" + KeyGenerator.getKey().toString());
        USER.setDob(DateTime.now().withTime(0, 0, 0, 0).minusYears(18).toDate());
        USER.setUsername("user" + KeyGenerator.getKey());
        USER.setVerified(true);
    }


    @Test(enabled = ENABLED)
    public void createChat()
    {
        chatDAO.createChat(CHAT);
        assertNotEquals(CHAT.getId(), 0, "Chat ID must be set up by autogenerated keys.");
    }

    @Test(enabled = ENABLED, dependsOnMethods = "createChat", expectedExceptions = DuplicateKeyException.class)
    public void createChatFail()
    {
        chatDAO.createChat(CHAT);
    }

    @Test(enabled = ENABLED, dependsOnMethods = "createChat")
    public void getChatById()
    {
        checkChat(chatDAO.getChatById(CHAT.getId()));
    }

    @Test(enabled = ENABLED, dependsOnMethods = "createChat")
    public void getChatByName()
    {
        checkChat(chatDAO.getChatByName(CHAT.getName()));
    }

    @Test(enabled = ENABLED, dependsOnMethods = {"createChat", "getChatByName", "getChatById"})
    public void updateChat()
    {
        CHAT.setName("chat" + KeyGenerator.getKey());
        CHAT.setPrivateEnabled(true);
        chatDAO.updateChat(CHAT);
        checkChat(chatDAO.getChatById(CHAT.getId()));
    }

    @Test(enabled = ENABLED, dependsOnMethods = {"createChat", "getChatByName", "getChatById", "updateChat"})
    public void addUserToChat()
    {
        USER.setStatus(statusDAO.getStatusByType(Status.Type.OFFLINE));
        userDAO.createUser(USER);
        chatDAO.addUserToChat(USER.getId(), CHAT.getId());
        CHAT.getUsersId().add(USER.getId());
        checkChat(chatDAO.getChatById(CHAT.getId()));
    }

    @Test(enabled = ENABLED, dependsOnMethods = {"createChat", "getChatByName", "getChatById", "updateChat", "addUserToChat"})
    public void deleteUserFromChat()
    {
        chatDAO.removeUserFromChat(USER.getId(), CHAT.getId());
        CHAT.getUsersId().remove(USER.getId());
        userDAO.deleteUserById(USER.getId());
        checkChat(chatDAO.getChatById(CHAT.getId()));
    }

    // Toggle to delete by ID/STATUS
    private static final boolean DELETE_CHAT_BY_ID = true;

    @Test(enabled = ENABLED && DELETE_CHAT_BY_ID, dependsOnMethods =
            { "createChat", "createChatFail", "getChatById",
                    "getChatByName", "updateChat","addUserToChat", "deleteUserFromChat"})
    public void deleteChatById()
    {
        chatDAO.deleteChatById(CHAT.getId());
        assertNull(chatDAO.getChatById(CHAT.getId()));
    }

    @Test(enabled = ENABLED && !DELETE_CHAT_BY_ID, dependsOnMethods =
            { "createChat", "createChatFail", "getChatById",
                    "getChatByName", "updateChat","addUserToChat", "deleteUserFromChat" })
    public void deleteChatByName()
    {
        chatDAO.deleteChatByName(CHAT.getName());
        assertNull(chatDAO.getChatByName(CHAT.getName()));
    }

    private void checkChat(Chat chat)
    {
        assertEquals(chat.getName(), CHAT.getName(), "Chat name is not as expected.");
        assertEquals(chat.getPrivateEnabled(), CHAT.getPrivateEnabled(), "Chat private is not as expected.");
        assertEquals(chat.getUsersId().size(), CHAT.getUsersId().size(), "Chat size of user is not as expected.");
    }
}
