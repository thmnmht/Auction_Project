package com.rahnemacollege.tests;

import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.Assert.*;
@RunWith(SpringRunner   .class)
@SpringBootTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Before
    public void setUp() throws Exception {
        User user1= new User();
        user1.setName("Alice");
        User user2= new User();
        user2.setName("Bob");
        user2.setEmail("bob@rahnema.com");
        //save user, verify has ID value after save
        assertNull(user1.getId());
        assertNull(user2.getId());//null before save
        this.userRepository.save(user1);
        this.userRepository.save(user2);
        assertNotNull(user1.getId());
        assertNotNull(user2.getId());
    }
    @Test
    public void testFetchData(){
        User userA = userRepository.findByName("Bob");
        assertNotNull(userA);
        assertEquals("bob@rahnema.com", userA.getEmail());
        Iterable<User> users = userRepository.findAll();
        int count = 0;
        for(User p : users){
            count++;
        }
        assertEquals(count, 2);
    }
}
