package ru.netology

import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ChatServiceTest {
    var chatService = ChatService

    @Before
    fun setUp() {
        chatService.clear()
    }


    @Test
    fun startChatShouldReturnChat() {
        assertEquals(
            Chats(1, 1, messages = mutableListOf()),
            chatService.startChat(1)
        )
    }

    @Test
    fun deleteChatShouldDelete() {
        chatService.startChat(1)
        chatService.startChat(2)
        chatService.startChat(3)
        chatService.deleteChat(1)
        assertEquals(2, chatService.chatsCount())
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteChatShouldThrowError() {
        chatService.startChat(1)
        chatService.startChat(2)
        chatService.startChat(3)
        chatService.deleteChat(4)

    }

    @Test
    fun addMessageShouldStarNewCht() {
        chatService.addMessage(1, "Test text")
        assertEquals(1, chatService.chatsCount())
    }

    @Test
    fun addMessageShouldAddNewMessage() {
        chatService.addMessage(1, "Test text")
        assertEquals(1, chatService.messagesCount(1))
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteMessageShouldThrowChatError() {
        chatService.addMessage(1, "Test text1")
        chatService.addMessage(1, "Test text2")
        chatService.addMessage(1, "Test text3")
        chatService.deleteMessage(2, 1)
    }

    @Test(expected = MessageNotFoundException::class)
    fun deleteMessageShouldThrowMessageError() {
        chatService.addMessage(1, "Test text1")
        chatService.addMessage(1, "Test text2")
        chatService.addMessage(1, "Test text3")
        chatService.deleteMessage(1, 4)
        assertEquals(2, chatService.messagesCount(1))
    }

    @Test
    fun editMessageShouldEdit() {
        chatService.addMessage(1, "Test text")
        chatService.editMessage(1, 1, "Edited text")
        assertEquals("Edited text", chatService.getChats().elementAt(0).messages[0].text)
    }

    @Test(expected = ChatNotFoundException::class)
    fun editMessageShouldThrowChatError() {
        chatService.addMessage(1, "Test text")
        chatService.editMessage(2, 1, "Edited text")
    }

    @Test(expected = MessageNotFoundException::class)
    fun editMessageShouldThrowMessageError() {
        chatService.addMessage(1, "Test text")
        chatService.editMessage(1, 2, "Edited text")
    }

    @Test
    fun getUnreadChatCountShouldCount1() {
        chatService.addMessage(1, "Test text1")
        assertEquals(1, chatService.getUnreadChatCount())
    }

    @Test
    fun getUnreadChatCountShouldCount0() {
        assertEquals(0, chatService.getUnreadChatCount())
    }

    @Test
    fun getTestsShouldReturnTests() {
        chatService.addMessage(1, "Test text1")
        chatService.addMessage(2, "Test text1")
        chatService.addMessage(3, "Test text1")
        assertEquals(3, chatService.getChats().size)
    }

    @Test
    fun getLastMessagesShouldReturnList() {
        chatService.addMessage(1, "Test text1")
        chatService.addMessage(1, "Test text2")
        chatService.addMessage(1, "Test text3")
        val expected = mutableListOf("Test text1", "Test text2", "Test text3")
        assertEquals(expected, chatService.getLastMessages(1))
    }

    @Test(expected = ChatNotFoundException::class)
    fun getLastMessagesShouldThrowError() {
        chatService.addMessage(1, "Test text1")
        chatService.getLastMessages(2)
    }

    @Test
    fun getLastMessagesShouldReturnEmptyList() {
        chatService.startChat(1)
        val expected = mutableListOf<String>()
        assertEquals(expected, chatService.getLastMessages(1))
    }

    @Test
    fun getLastMessagesShouldPrintNoeMessages() {
        val originalOut = System.out
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))
        try {
            chatService.startChat(1)
            chatService.getLastMessages(1)
            assertTrue(outputStream.toString().trim().contains("Нет сообщений"))
        } finally {
            System.setOut(originalOut)
        }
    }

    @Test
    fun getListOfMessagesToReadShouldReturn7messages() {
        chatService.addMessage(1, "Test text1")
        chatService.addMessage(1, "Test text2")
        chatService.addMessage(1, "Test text3")
        chatService.addMessage(1, "Test text4")
        chatService.addMessage(1, "Test text5")
        chatService.addMessage(1, "Test text6")
        chatService.addMessage(1, "Test text7")
        chatService.addMessage(1, "Test text8")
        chatService.addMessage(1, "Test text9")
        assertEquals(
            7,
            chatService.getListOfMessagesToRead(1, 7).size
        )
    }

    @Test
    fun getListOfMessagesToReadShouldMarkAsRead() {
        chatService.addMessage(1, "Test text3")
        chatService.getListOfMessagesToRead(1, 1)
        assertFalse(chatService.getChats().elementAt(0).messages[0].unread)
    }

    @Test(expected = ChatNotFoundException::class)
    fun getListOfMessagesToReadShouldThrowError() {
        chatService.addMessage(1, "Test text")
        chatService.getListOfMessagesToRead(2, 1)
    }
}