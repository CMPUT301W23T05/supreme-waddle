package com.example.qrky;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Test;


/**
 * Database Unit Test.
 * This class is used to test the Database class
 * @author Ahmed
 */
public class DatabaseTest {
    @Test
    public void testGetScore() {

        Database mockDb = mock(Database.class);
        doReturn(8).when(mockDb).getScore("90236548d872e262cf06bc671bf3e7424e7f8e5deb37ed59fe0e89dbf064114d");
        assertEquals(8, mockDb.getScore("90236548d872e262cf06bc671bf3e7424e7f8e5deb37ed59fe0e89dbf064114d"));

    }

    @Test
    public void testMakeName() {
        Database mockDb = mock(Database.class);
        doReturn("Quiet Far Fast Empty Old Strong ").when(mockDb).makeName("90236548d872e262cf06bc671bf3e7424e7f8e5deb37ed59fe0e89dbf064114d");
        System.out.println("testGetUsername " + mockDb.makeName("90236548d872e262cf06bc671bf3e7424e7f8e5deb37ed59fe0e89dbf064114d"));
        assertEquals("Quiet Far Fast Empty Old Strong ", mockDb.makeName("90236548d872e262cf06bc671bf3e7424e7f8e5deb37ed59fe0e89dbf064114d"));
    }


}