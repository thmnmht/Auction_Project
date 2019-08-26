package com.rahnemacollege.test;

import junit.extensions.ActiveTestSuite;
import junit.extensions.RepeatedTest;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestSuite;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public final class MainTest {

    @Test
    public void main() {
        TestSuite mySuite = new ActiveTestSuite();

        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener(System.out));

        mySuite.addTest(new RepeatedTest(new JUnit4TestAdapter(AuctionControllerTest.class), 1));
        mySuite.addTest(new RepeatedTest(new JUnit4TestAdapter(UserControllerTest.class), 1));
        mySuite.addTest(new RepeatedTest(new JUnit4TestAdapter(HomeControllerTest.class), 1));

        junit.run(mySuite);
    }

}
