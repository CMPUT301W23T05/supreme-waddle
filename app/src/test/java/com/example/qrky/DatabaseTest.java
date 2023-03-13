package com.example.qrky;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DatabaseTest {
    @Test
    public void testGetScore() {

        Database mockDb = mock(Database.class);
        assertEquals(0, mockDb.getScore("90236548d872e262cf06bc671bf3e7424e7f8e5deb37ed59fe0e89dbf064114d"));
        System.out.println("testGetScore " + mockDb.getScore("90236548d872e262cf06bc671bf3e7424e7f8e5deb37ed59fe0e89dbf064114d"));
        when(mockDb.getScore("90236548d872e262cf06bc671bf3e7424e7f8e5deb37ed59fe0e89dbf064114d")).thenReturn(0);
        assertEquals(0, mockDb.getScore("90236548d872e262cf06bc671bf3e7424e7f8e5deb37ed59fe0e89dbf064114d"));

    }
}