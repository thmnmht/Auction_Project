package com.rahnemacollege.test;

import com.rahnemacollege.model.User;
import com.rahnemacollege.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertEquals;

/*
@RunWith(SpringJUnit4ClassRunner.class)

@ContextConfiguration(
        classes = {UserTestJpaConfig.class},
        loader = AnnotationConfigContextLoader.class)
*/

@RunWith(SpringRunner.class)
//@ActiveProfiles("test")
//@Transactional
public class InMemoryDBTest {

//    @Autowired
//    private TestEntityManager entityManager;

    /*
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    */

    @Autowired
    private UserRepository userRepository;

    @Test
    public void givenStudent_whenSave_thenGetOk() {
        User student = new User("John", "john@yahoo.com", "123456");
        userRepository.save(student);

        User student2 = userRepository.findByEmail("john@yahoo.com").get();
        assertEquals("John", student2.getName());
    }


}